
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "window.h"
#include "config.h"


int is_in_window(Window *win, int order_num ) {
    if ((*win)[order_num  % WINDOW_SIZE].order  == order_num ) {
        return 1;
    } else {
        return 0;
    }
}


void put_into_window(Window *win, int order_num , char *message, int msg_len) {
    
    struct Frame *w_frame= &((*win)[order_num  % WINDOW_SIZE]);
    w_frame->order  = order_num ;
    w_frame->length = msg_len;
    memcpy(w_frame->message, message, msg_len);
}

void create_window(Window *win) {
    int i;
    for (i = 0; i < WINDOW_SIZE; i++) {
        (*win)[i].order  = -1;
    }
}

void print_message_from_frame(Window *window, int order_num , FILE *out) {
    
    struct Frame *w_frame= &((*window)[order_num  % WINDOW_SIZE]);
    if (w_frame->order != order_num ) {
        return;
    }
    
    fprintf(stderr,"message length is %d\n",w_frame->length);
    fwrite(w_frame->message, w_frame->length, 1, out);
    
    printf("\n");
}


int get_message_size(Window *window, int order_num ) {
    struct Frame *w_frame= &((*window)[order_num  % WINDOW_SIZE]);
    if (w_frame->order  != order_num ) {
        return 0;
    }
    return w_frame->length;
}

const char* get_message(Window *window, int order_num ) {
    struct Frame *w_frame= &((*window)[order_num  % WINDOW_SIZE]);
    if (w_frame->order  != order_num ) {
        return NULL;
    }
    return w_frame->message;
}