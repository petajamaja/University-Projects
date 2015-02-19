
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <errno.h>
#include <sys/types.h> 
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>

#include "config.h"
#include "window.h"
#include "arithmetics.h"


void create_socket(int* socket_descriptor){
    
    *socket_descriptor = socket(AF_INET, SOCK_DGRAM, IPPROTO_UDP);
     if (*socket_descriptor < 0) 
        error("ERROR opening socket");
     else printf("Socket opening successful\n");
}

void check_cmd_arguments(int argc, char *argv[], int* port){
     
     if (argc != 2) {
         error("ERROR, no port provided\n");
     } else fprintf(stderr,"Command line arguments OK\n");
     
     // Converting char input symbols into numerical raw
     *port = atoi(argv[1]);
     
}

void setup_server_address_characteristics(struct sockaddr_in *server, int port){
    
     (*server).sin_family = PF_INET;
     (*server).sin_addr.s_addr = htonl(INADDR_ANY);
     
     // Convert an IP port number in host byte order 
     // to the IP port number in network byte order
     (*server).sin_port = htons(port);
    
}

void attempt_to_bind(struct sockaddr_in *server,int *socket_descriptor){
    
     if (bind(*socket_descriptor, (struct sockaddr *) server,
              sizeof(*server)) < 0) {
              error("ERROR on binding");
              close(*socket_descriptor);
              
     }
     else printf("Binding successful\n");
     
}

void check_socket_name(struct sockaddr_in server,int socket_descriptor){
    
     socklen_t length = sizeof( server );
     if (getsockname(socket_descriptor, (struct sockaddr *) &server, &length)<0) {
        error("Error getsockname\n");
     }

     printf("The server UDP port number is %d\n",ntohs(server.sin_port));
     print_line();
}

void send_acknowledgement(int next_frame, ssize_t bytes_received, int descriptor, struct sockaddr_in *addr){
    
     int will_ack_be_sent;
     assign_behaviour_to_message(&will_ack_be_sent);
     
     char acknowledge_msg[SEQUENCE_BYTE_LENGTH];
     
     write_number_dec(acknowledge_msg, next_frame);

     if(will_ack_be_sent == SUCCESS) {
         
          fprintf(stderr,"Acknowledgement sent\n");
          bytes_received = sendto(descriptor, acknowledge_msg, sizeof(acknowledge_msg), 
                                     0, (struct sockaddr*) addr, sizeof(*addr));
          
     } else fprintf(stderr,"Acknowledgement lost\n");
        
     print_line();
}

int receive_frame( ssize_t *bytes,int descriptor, char* frame_received, 
                   int* order_num, struct sockaddr_in *addr, socklen_t *addr_len){
    
     
     int to_ret = 0;
     *bytes = recvfrom(descriptor, frame_received, BUFFER_LENGTH, 
                                  0, (struct sockaddr*) addr, addr_len);
     
     // error is here! 
     printf("bytes: %d\n",*bytes);  
     
     *order_num = get_number_dec(frame_received);
     if (*order_num == -1) {
            fprintf(stderr, "ERROR, invalid sequence number\n");
            to_ret = 1;
     } else fprintf(stderr, "Received frame %d\n", *order_num);
     
     return(to_ret);
     
}

void run(int descriptor){
      
    int next_frame_to_receive = 0;

    Window server_window;
    create_window(&server_window);

    while(1) {
        
        
        char frame_received[SEQUENCE_BYTE_LENGTH + MESSAGE_LENGTH + 1];
        int order_num;
        
        ssize_t bytes_received;
        struct sockaddr_in addr;
        socklen_t address_len = sizeof(addr);
        
        
        // receive next frame
       
       int ctrl = receive_frame(&bytes_received,descriptor,frame_received,
                                 &order_num, &addr,&address_len);
        
       printf("bytes received: %d\n",bytes_received);
        // if sequence number is invalid, skip to the end of cycle - no ack sending!
        if( ctrl == 1) continue;
           
        // if number of frame just received fits in the server window
        if (is_greater_or_equal(order_num, next_frame_to_receive) &&
            is_less(order_num, next_frame_to_receive + WINDOW_SIZE)) {
            
            // but the window doesn't contain this frame yet 
            if (! is_in_window(&server_window, order_num)) {
                
                // store frame in server window
                fprintf(stderr, "Stored frame number %d in server window\n", order_num);
                put_into_window(&server_window, order_num, frame_received + SEQUENCE_BYTE_LENGTH, 
                              bytes_received - SEQUENCE_BYTE_LENGTH);
            }
        }

        // print all the current window frames in order
        while (is_in_window(&server_window, next_frame_to_receive)) {
            
            fprintf(stdout, "MESSAGE TEXT: \n");
            print_message_from_frame(&server_window, next_frame_to_receive, stdout);
            
            // if EOF encountered, close stdout
            if (get_message_size(&server_window, next_frame_to_receive) == 0) {
                fclose(stdout);
            }
            
            next_frame_to_receive = increment_number(next_frame_to_receive);
        }
        
        // complete all stdout operations
        fflush(stdout);

        send_acknowledgement(next_frame_to_receive, bytes_received, descriptor, &addr);
        
    }
    
}

int  main(int argc, char *argv[]){
     
     int socket_descriptor,port_number;
     struct sockaddr_in server_address;
     
     print_line();
     
     check_cmd_arguments(argc,argv,&port_number);
     
     create_socket(&socket_descriptor);
     
     setup_server_address_characteristics(&server_address,port_number);
     
     attempt_to_bind(&server_address, &socket_descriptor);

     check_socket_name(server_address,socket_descriptor);
  
     run(socket_descriptor);
     
     exit(EXIT_SUCCESS);
     
    }
     
