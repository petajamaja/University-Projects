#include <stdio.h>
#include <stdlib.h>
#include <time.h>

#include "config.h"
#include "arithmetics.h"

 
#define MODULO 100
#define HALF 50


/**
 *   function:     get_number_dec
 *   description:  turn sequence number 
 *                 into decimal
 *                (in integers)
 * 
 */
int get_number_dec(const char *data) {
    int i;
    int order_num = 0;
    for (i = 0; i < SEQUENCE_BYTE_LENGTH; i++) {
        if (data[i] < '0' || data[i] > '9') {
            return -1; 
        }
        order_num *= 10;
        order_num += data[i] - '0';
    }
    return order_num;
}

/**
 *   function:     is_less
 *   description:  compare two sequence numbers
 *                 and decide if first is smaller.
 * 
 */
int is_less(int a, int b) {
    int d = a - b;
    if ((d < 0 && d > -HALF) || (d > HALF)) {
        return 1;
    } else {
        return 0;
    }
}

/**
 *   function:    write_number_dec
 *   description:  turn number into decimal
 *                (in integers), and write 
 *                into specified array.
 * 
 */
void write_number_dec(char *array, int order_num) {
    int i;
    for (i = SEQUENCE_BYTE_LENGTH - 1; i >= 0; i--) {
        array[i] = (order_num % 10) + '0';
        order_num /= 10;
    }
}

/**
 *   function:    assign_behaviour_to_message
 *   description:  according to probability,
 *                determine if the message will
 *                be sent or lost by setting the
 *                behaviour variable to SUCCESS
 *                (90% probability) or DROPOUT
 *                (10% probablity).          
 * 
 */
void assign_behaviour_to_message( int *behaviour){
    
    int random;
    srand ( time(NULL) );
    random = rand()%10;
    if (random < 9)
        *behaviour = SUCCESS;
    else 
        *behaviour = DROPOUT;
    
}

/**
 *   function:    is_greater
 *   description: compare two sequence numbers
 *                and decide if first is greater.
 * 
 */
int is_greater(int a, int b) {
    int d = a - b;
    if ((d > 0 && d < HALF) || (d < -HALF)) {
        return 1;
    } else {
        return 0;
    }
}

/**
 *   function:     is_greater
 *   description:  compare two sequence numbers
 *                 and decide if first is greateror equal.
 * 
 */
int is_greater_or_equal(int a, int b) {
    if (a == b) {
        return 1;
    }
    return is_greater(a, b);
}


int increment_number(int order_num) {
    return (order_num + 1) % MODULO;
}

/**
 *   function    :  print_line
 *   description :  make server and client stderr/stdout design more readable
 *                  by printing a separation line.
 */
void print_line(){
    
    printf("\n--------------------------------------------------------------------------\n\n");
    
}

void error(const char *message){
    
    perror(message);
    exit(EXIT_FAILURE);
}