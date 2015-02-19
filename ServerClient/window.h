
#ifndef WINDOW_H
#define	WINDOW_H

#include "config.h"
#include <stdio.h>


struct Frame {
    int length;
    int order;
    char message[MESSAGE_LENGTH];
};


typedef struct Frame Window[WINDOW_SIZE];

extern void create_window(Window *window);

extern int is_in_window(Window *window, int order_num);

extern void put_into_window(Window *window, int order_num, char *message, int msg_len);

extern void print_message_from_frame(Window *window, int order_num, FILE *out);

extern const char* get_message(Window *window, int order_num);

extern int get_message_size(Window *window, int order_num);



#ifdef	__cplusplus
extern "C" {
#endif




#ifdef	__cplusplus
}
#endif

#endif	/* WINDOW_H */

