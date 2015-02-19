#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <errno.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h> 
#include <sys/time.h>

#include "config.h"
#include "window.h"
#include "arithmetics.h"

/*
 *   function:     create_socket
 *   description:  create an UDP socket
 *  
 */
void create_socket(int* socket_descriptor){
    
    *socket_descriptor = socket(AF_INET, SOCK_DGRAM, IPPROTO_UDP);
     if (*socket_descriptor == INVALID_SOCKET) 
        error("ERROR creating socket");
     else printf("Socket creating successful\n");
}

/*
 *   function:      check_cmd_arguments
 *   description:   check if all the needed arguments are provided,
 *                  and assign their values to variables.
 *                  also control of host validity.
 *
 */
void check_cmd_arguments(int argc, char *argv[], int* port, struct hostent *hp){
    
    if (argc != 3) {
       fprintf(stderr,"usage %s hostname port\n", argv[0]);
       exit(EXIT_FAILURE);  
    }
    
    *port = atoi(argv[2]);
    
    if ((hp = gethostbyname(argv[1]))==0) {
       printf("Invalid or unknown host\n");
       exit(EXIT_FAILURE);
      
    } else printf("Host accepted\n");
 
    
    printf("Command line arguments OK\n");
}

/*
 *    function:      setup_server_address_characteristics
 *    description:   specify server address properties,
 *                   fill in the adress structure that 
 *                   will be used later to contact the server.
 *
 */
void setup_server_address_characteristics(struct sockaddr_in *server, 
                                  struct hostent *hp, int port){
    
     (*server).sin_family = PF_INET;
     (*server).sin_addr.s_addr = htonl(INADDR_ANY);
     
     // Convert an IP port number in host byte order 
     // to the IP port number in network byte order
     (*server).sin_port = htons(port);
     
     
    
}

void print_usage(){
    
    printf("\n");
    printf("----------------------------"
            "USAGE INFORMATION----------"
            "---------------------");
    printf("-\n- You are supposed to type messages. "
            "This is a message sending application.\n");
    printf("- Your message must consist of at most "
            "256 symbols, but don't count:\n");
    printf("- if you are above the limit, your message "
            "will be just cut to fit in.\n");
    printf("- \n- To send the message, press enter or "
            "type a newline character.\n");
    printf("- To end connection and "
            "close the program, type EOF character.");
    printf("-\n");
    printf("-----------------------------------------"
            "------------------------------------");
    
    int i;
    for (i = 0; i < 5; i++){
        printf("\n");
    }
    
}


static void send_frame(int descriptor, int order_num, Window *client_window) {
    
    int will_message_be_delivered;
    
    // an array that represents a frame sample - so far empty
    char frame_pattern[SEQUENCE_BYTE_LENGTH + MESSAGE_LENGTH];
    ssize_t bytes_sent;

    // if the order number of frame fits in current window position,
    // prepare the new frame to be sent
    if (is_in_window(client_window, order_num)) {
        
       // write order number of the frame into the frame sample
       write_number_dec(frame_pattern, order_num);
       
       printf("message size : %d\n", get_message_size(client_window, order_num));
       
       // write actual message text after the order number 
       memcpy(frame_pattern + SEQUENCE_BYTE_LENGTH, 
              get_message(client_window, order_num),
              get_message_size(client_window, order_num));
    
       assign_behaviour_to_message(&will_message_be_delivered);
       
       if(will_message_be_delivered == SUCCESS) {
            
            // sending ready frame to the remote server socket
            bytes_sent = write(descriptor, frame_pattern, 
                    SEQUENCE_BYTE_LENGTH + get_message_size(client_window, order_num));
            
            printf("bytes sent: %d\n",bytes_sent);
            if (bytes_sent == -1)  error("ERROR writing to remote socket: ");
            else {
                fprintf(stderr,"Frame number %d sent to server\n",order_num);
                fprintf(stderr,"Message size is %d\n",get_message_size(client_window, order_num) );
            }
            
       } else fprintf(stderr,"Frame number %d lost\n",order_num);    
         
       
    }
}


void run(int descriptor){
    
    int msg_waited_to_be_sent = 0;
    int msg_on_client_queue = 0;
    
    int end_of_input = 0;
    struct timeval last_time;

    Window client_window;
    create_window(&client_window);
    
    
    while(1) {
        
        int matching_descriptors;
        int max_descriptor_num = 0;
        
        // Pointers to sets of descriptors that should be 
        // checked to see if they are ready for reading(resp. writing).
        fd_set read_set, write_set;
        
        struct timeval waiting_time;
        
        FD_ZERO(&read_set);
        FD_ZERO(&write_set);

        // adding server socket descriptor to read_set,
        // wait for network data to be sent
        
        FD_SET(descriptor, &read_set);
        max_descriptor_num = max(max_descriptor_num, descriptor);

        // wait for stdin in case window is still not full with frames
        if (!end_of_input && is_less(msg_on_client_queue, msg_waited_to_be_sent + WINDOW_SIZE)) {
            
            FD_SET(0, &read_set);
            max_descriptor_num = max(max_descriptor_num, 0);
        }

        // Timer settings
        waiting_time.tv_sec = TIMER / 1000;
        waiting_time.tv_usec = (TIMER % 1000) * 1000;
        matching_descriptors = select(max_descriptor_num + 1, &read_set, &write_set, NULL, &waiting_time);

        if (matching_descriptors == -1) error("ERROR timeout reached\n"); 
        
        // if window is still not full with frames (see above)
        if (FD_ISSET(0, &read_set)) {
            
            // read more stdin, insert it in window, and send
            
            char message_text[MESSAGE_LENGTH];
            int n = read(0, message_text, sizeof(message_text));
                
            if (n == -1) error("ERROR reading from stdin\n");
            if (n == 0)  end_of_input = 1;
            

            put_into_window(&client_window, msg_on_client_queue, message_text, n);
            
            send_frame(descriptor, msg_on_client_queue, &client_window);
            
            // set packet timer off
            gettimeofday(&last_time, NULL);
            
            // go to next message
            msg_on_client_queue = increment_number(msg_on_client_queue);
            
            
        }

        // if, on the contrary,the window is full with frames
        if (FD_ISSET(descriptor, &read_set)) {
                        
            char buf[SEQUENCE_BYTE_LENGTH];
            
            // trying to read the acknowledgment
            int bytes_read = read(descriptor, buf, sizeof(buf));
            
            if (bytes_read == -1) error("ERROR reading acknowledgement\n");
            
            // if the datagram recieved has correct ack format
            if (bytes_read == SEQUENCE_BYTE_LENGTH) {
                
                int order_num = get_number_dec(buf);
                fprintf(stderr,"Got acknowledgement for frame %d\n",order_num-1);
                
                print_line();
                
                // if there are unsent frames before order_num frame
                if (is_greater(order_num, msg_waited_to_be_sent)) {
                    
                    // moving the server window to match the sent message with received acks.
                    // (not exactly moving,but changing value that afffects future movement).
                    while (is_greater(order_num, msg_waited_to_be_sent)) {
                        
                        msg_waited_to_be_sent = increment_number(msg_waited_to_be_sent);
                        
                    }
                    
                    // check if all the frames have been sent correctly
                    if (end_of_input && msg_waited_to_be_sent == msg_on_client_queue) break;
                
                // server sends ack for the first frame for the second time
                } else if (order_num == msg_waited_to_be_sent) {
                   
                    fprintf(stderr,"Resending  frame number 0\n");
                    send_frame(descriptor, &client_window, msg_waited_to_be_sent);
                    // timer off
                    gettimeofday(&last_time, NULL);
                    
                }
            }else fprintf(stderr,"No acknowledgement received\n");

            
            
        }
        // if there is something to be sent
        if (msg_waited_to_be_sent != msg_on_client_queue) {  
            
            struct timeval now;
            gettimeofday(&now, NULL);
            int delta = (now.tv_sec - last_time.tv_sec) * 1000; 
            delta += (now.tv_usec - last_time.tv_usec) / 1000;
            
            // if timeout for acknowledgement receiving reached
            // but no acknowledgement is received
            if (delta > TIMER) {
                
                // resending frame
                printf("Resending frame number %d\n",msg_waited_to_be_sent);
                send_frame(descriptor, msg_waited_to_be_sent, &client_window);
                gettimeofday(&last_time, NULL);
            }
            
            
        }

    }
}

void connect_try(int *socket_descriptor, struct sockaddr_in *server){
    
    if (connect(*socket_descriptor, (struct sockaddr*)server, sizeof(*server)) == -1)
    {
        error("ERROR connecting, connection failed \n");
    } else fprintf(stderr,"Connection OK\n");
}

int main(int argc, char *argv[]){
    
    int socket_descriptor;
    int port_number;
    struct sockaddr_in server_address;
    struct hostent *host;
       
    check_cmd_arguments(argc, argv,&port_number, host);
    create_socket( &socket_descriptor );  
    
    setup_server_address_characteristics(&server_address,host,port_number);
    connect_try(&socket_descriptor,&server_address);
    print_usage();
    
    run(socket_descriptor);
    
    exit(EXIT_SUCCESS);
    
}
