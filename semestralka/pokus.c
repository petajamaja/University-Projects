#define DEV_ENABLE "/sys/bus/pci/devices/0000:03:00.0/enable"
#define DEV_ADDRESS 0xfe8f0000

#define CTRL 0x8020
#define DATA 0x8040
#define DATARD 0x8060

#define byte unsigned char

#include <stdio.h>
#include <sys/mman.h>
#include <stdlib.h>
#include <string.h>
#include <fcntl.h>
#include <time.h>
#include <ctype.h>
#include <sys/sysctl.h>
#include <sys/time.h>
#include <unistd.h>
#include "mmaped_8bit_bus_kbd-headers-2013/kbd_hw.h"
#include "mmaped_8bit_bus_kbd-headers-2013/chmod_lcd.h"

volatile unsigned char *base;

void clear_LCD();
void beep3x();
void short_beep();
void display_disk_stats();
void display_memory_stats();
void display_cpuinfo();
void display_load_avg();
int read_keyboard();
void write_on_LCD(char *string, int line);
void initialize();
void wrbyte(int adresa,byte b);
byte rdbyte(int adresa);
void get_rid_of_beeping();

int pciEnable(int isEnable) {
	char cen = isEnable != 0 ? '1' : '0';
	int enable = open(DEV_ENABLE, O_WRONLY);
	if (enable == -1)
		return 0;
	write(enable, &cen, 1);
	close(enable);
	return 1;
}

int main(){

	int soubor = open("/dev/mem", O_RDWR | O_SYNC);
	if (soubor == -1)
		return 1;

	base = mmap(NULL, 0x10000, PROT_WRITE | PROT_READ,
			MAP_SHARED, soubor, DEV_ADDRESS);

	if (base == MAP_FAILED )
		return 2;

	if (pciEnable(1)) {

		*(base + CTRL) = 0x00;  // zapni napajeni

        get_rid_of_beeping();

		sleep(1); 
        
        initialize();
		
        write_on_LCD("**  WELCOME!  **",0);
        beep_3x();
        
        // can be from 0 to 3 (because we need to display 4 types of data)
        int listing_index = -1;
        int key_states[14] = {124,124,124,124,124,124,124,124,124,124,124,124,124,124};
        
      
        // main operation cycle
        while(1){
            
            int beep;
            beep = read_keyboard();

            // this is our universal off button
            if (beep == 12){
                 
                 printf("User pressed key 12!\n");
                 beep_3x();
                 clear_LCD();
                 write_on_LCD("**  GOODBYE!  **",1);
                 sleep(8);
                 *(base + CTRL) = 0x00;  // vypni napajeni
                 break;
                 
            }

            if(beep == 14) usleep(100000);
            if(beep == 13) usleep(100000);

            if(beep!=0 && (key_states[beep-1]==124 || beep == 14 || beep == 13)){
 
                //make all other keys freed
                int i;
                for(i = 1; i <= 14; i++){
                    if(i!=beep) key_states[i-1]=124;
                }

                short_beep();

            	if(beep == 13){
            		printf("User pressed arrow down!\n");
            		// listing down means incrementing listing index
            		if(listing_index == 3) listing_index = 0;
            		else listing_index++;
            		
            	}else if (beep == 14){
            		printf("User pressed arrow up!\n");
            		// listing up means decrementing listing index
                        // the first if is intended to check that we are not
                        // decrementing the listing index if it is belo zero.
                       if(listing_index >=0){
            		  if(listing_index == 0) listing_index = 3;
            		  else listing_index--;
            	       }
            	
                }else{
            		// right now, these keys don't matter anything
                        clear_LCD();
                        printf("User pressed key %d!\n",beep);
            	}

            	//choose info block to be displayed according to listing index
            	switch(listing_index){
            	  case 0: display_load_avg();break;
            	  case 1: display_cpuinfo(); break;
            	  //case 2: display_memory_stats();break;
            	  //case 3: display_disk_stats(); break;
            	  default: break;
            	}
                      
        }
               
            // keys multiple presses error handling
            if(beep!=0){
                 if(key_states[beep-1]<=130) key_states[beep-1]++;
                 else if(key_states[beep-1]>=124)key_states[beep-1]--;
            }
     

        }
      
        *(base + CTRL) = 0x00;  // power off
	}
	pciEnable(0);

	return (0);
}

/**
 *  Get rid of that ANNOYING beeping at the beginning
 *  (it didn't let me go further for such a long time)
 */
void get_rid_of_beeping(){
	*(base+0x8040)=0x00;
    *(base+0x8020)=0xE3;
	*(base+0x8020)=0xA3;
	usleep(10*1000);
}

void short_beep(){
	wrbyte(BUS_KBD_WR_o,0xFF);
	usleep(100000);
	wrbyte(BUS_KBD_WR_o,0x00);
}

void beep_3x(){

        short_beep();
        usleep(30000);
        short_beep();
        usleep(30000);
        short_beep();
}
/**
 *   Writing instructions into LCD dispatcher
 *   Needed sequence :  10100000 | 3 bits of address - moment of writing
 *   Needed sequence 2: 11100000 | 3 bits of address - end of writing
 */
void wrbyte(int adresa,byte b){

	*(base+CTRL)=0xF0;
	//posilani dat na datovou sbernici
	*(base+DATA)=b;
	//actiavte signal write on two bytes0x8040
	// but bit wr is still 0!
	*(base+CTRL)=0xE0 | (adresa & 3);
	// chip select, all others high
	// 1010 0000
	*(base+CTRL)=0xA0 | (adresa & 3);
	//sleep 1 millisecond
	usleep(1);
	// chip unselect0x8040
	// 0xE0 - 1110 0000
	// stop passing the signal
	*(base+CTRL)=0xE0 | (adresa & 3);

	// deactivation of writing
	// 1111 0000
	*(base+CTRL)=0xF0;
}

/**
 *    Reading data from LCD 
 *    Needed sequence :   10010000 | 3 bits address - moment of reading, periferie on
 *    Needed sequence 2 : 11010000 | 3 bits of address - end of reading
 */
byte rdbyte(int adresa){

    // activating signal read
	// 1011 0000
    *(base+CTRL)=0xD0 | (adresa & 3);
    *(base+CTRL)=0x90 | (adresa & 3);
    usleep(1);
    byte ret = *(base+DATARD);
    // end of reading
    *(base+CTRL)=0xD0 | (adresa & 3);
    // control sequence to null
    *(base+CTRL)=0xF0;

    return ret;
}

void initialize(){
	        // LCD inicialisation
			wrbyte(BUS_LCD_INST_o,CHMOD_LCD_MOD);
			usleep(1000);
			wrbyte(BUS_LCD_INST_o,CHMOD_LCD_MOD);
			usleep(1000);
			wrbyte(BUS_LCD_INST_o,CHMOD_LCD_CLR);
			usleep(1000);
	        wrbyte(BUS_LCD_INST_o,CHMOD_LCD_DON);
	        usleep(1000);
	        wrbyte(BUS_LCD_INST_o,CHMOD_LCD_MOD);
	        usleep(1000);
	        wrbyte(BUS_LCD_INST_o,CHMOD_LCD_DON);
	        usleep(1000);

            while(1){
	            byte read = rdbyte(BUS_LCD_STAT_o);
	            if ((read & CHMOD_LCD_BF)!=0){
	            	continue;
	            }
	            break;
	        }
}
/**
 *    A function to write on the LCD display.
 *    @param line - either 0 or 1, means choice of the line
 *    @param string - a string to be displayed
 */
void write_on_LCD(char *string, int line){
 
                
	        int i;
	        int len = strlen(string);

	   
	        for(i=0;i<len;i++){
	        	wrbyte(BUS_LCD_INST_o,CHMOD_LCD_POS+i+(line*0x40));
	        	wrbyte(BUS_LCD_WDATA_o,string[i]);
	        }
}

/**
 *  A function to write on LCD a long sentence
 *  which will be automatically distributed betweeen two rows.
 *  @param string - a string to be displayed
 */
void write_whole_on_LCD(char *string){
	
	initialize();
	int i;
	int line = 0;
	int len = strlen(string);

    for(i=0;i<len;i++){
    	 if(i >= 16) line = 1;
		 wrbyte(BUS_LCD_INST_o,CHMOD_LCD_POS+i+(line*0x30));
		 wrbyte(BUS_LCD_WDATA_o,string[i]);
    }
	
}

void clear_LCD(){

   int i;
   for(i=0;i<16;i++){
	wrbyte(BUS_LCD_INST_o,CHMOD_LCD_POS+i);
        wrbyte(BUS_LCD_WDATA_o,' ');
	wrbyte(BUS_LCD_INST_o,CHMOD_LCD_POS+i+0x40);
	wrbyte(BUS_LCD_WDATA_o,' ');
	}

}

void clear_row_LCD(int row){
   int i;
   for(i=0;i<16;i++){
	wrbyte(BUS_LCD_INST_o,CHMOD_LCD_POS+i+(row * 0x40));
	wrbyte(BUS_LCD_WDATA_o,' ');
	}
}


/*
 * A function that reads the keyboard
 * and determines which key is pressed
 * @return - number of pressed key
 */
int read_keyboard(){
    
	// all of the keys are not pressed

	int main_ret = 0;
	byte b,c,d;

	
	wrbyte(BUS_KBD_WR_o,3);
	b = rdbyte(BUS_KBD_RD_o);


	 switch(b & 31){
	   case(30):{
		    main_ret = 1;
		    
		    break;
	   }
	   case(29):{
		   main_ret = 4;
		   
		   break;
	   }
		   
	   case(27):{
		   main_ret = 7;
		   
		   break;
	   }
	   case(23):{
	       main_ret = 10;
	       
	       break;
	   }
	   case(15):{
		   
		   main_ret = 13;
		   break;
	   }
	   default: break;
	}

	
			wrbyte(BUS_KBD_WR_o,5);
			c = rdbyte(BUS_KBD_RD_o);

				switch(c & 31){
				   case(30): {
					   
					   main_ret = 2;
					   break;
				   }
				   case(29): {
					   main_ret = 5;
					   
					   break;
				   }
				   case(27):{
					   
					   main_ret = 8;
					   break;
				   }
				   case(23):{
					   
					   main_ret = 11;
					   break;
				   }
				   case(15):{
					   
					   main_ret = 14;
					   break;
				   }
				   default: break;
				}


    wrbyte(BUS_KBD_WR_o,6);
	d = rdbyte(BUS_KBD_RD_o);

	    switch(d & 15){
			   case(14):{
				  
				  main_ret = 3;
				  break;
			   }
			   case(13):{
				   
				   main_ret = 6;
				   break;
			   }
			   case(11):{
				   
				   main_ret = 9;
				   break;
			   }
			   case(7):{
				   
				   main_ret = 12;
				   break;
			   }
			   default: break;

	}
	 return(main_ret);

}

/**
 *  Displaying processor load values.
 *  They are displayed in the same order as 
 *  in the /proc/loadavg, the exact meaning of them
 *  is explained in the user manual.
 */
void display_load_avg(){

	 clear_LCD();
     write_on_LCD("  LOAD AVERAGE  ",0);

     FILE *fr;
	 float avg;
	 int n;

	 if( (fr = fopen("/proc/loadavg","r")) == NULL) exit(1);
	 n = fscanf(fr,"%f",&avg);
	 fclose(fr);

     char load_info[13];
     char empty[] = "    ";

     sprintf(load_info,"%f",avg);
     strcat(empty,load_info);
     write_on_LCD(empty,1);
	     
}


void display_cpuinfo(){
	         
	         // Welcome screen of CPU INFO display
             clear_LCD();
			 write_on_LCD("  CPU FREQUENCY ",0);
			 // initilizing state_key
			 
			 FILE *fr;
			 char line[256];
			 float dmhz;

			 if( (fr = fopen("/proc/cpuinfo","r")) == NULL) exit(1);
             while(fgets(line,sizeof(line),fr)){
            	 if(sscanf(line,"cpu MHz\t: %f",&dmhz) == 1){
            		 printf("%f",dmhz);
            		 break;
            	 }
             }

             fclose(fr);

             char hz_info[16];
             char empty[] = "  ";
             char mhz = " MHz";
             sprintf(hz_info,"%f",dmhz);
             strcat(empty,hz_info);
             strcat(empty,mhz);
             write_on_LCD(empty,1);
				 
}
/**
 * I used this helper function to check which key 
 * on which column is represented by which value.
 * In the final program, this function is not being used.
 */
void test_column(int row_hex){
	while(1){
	     wrbyte(BUS_KBD_WR_o,row_hex);
	     byte b = rdbyte(BUS_KBD_RD_o);
	     printf("%d\n",b);
	     wrbyte(BUS_LED_WR_o,b);
    }
}


