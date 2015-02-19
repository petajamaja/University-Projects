
#ifndef ARITHMETICS_H
#define	ARITHMETICS_H


#ifdef	__cplusplus
extern "C" {
#endif

void assign_behaviour_to_message( int *behaviour);
int get_number_dec(const char *data);
void write_number_dec(char *buffer, int order_num);
int is_less(int a, int b);
void print_line();
int is_greater(int a, int b);
void error(const char *message);
int is_greater_or_equal(int a, int b);
int increment_number(int);



#ifdef	__cplusplus
}
#endif

#endif	/* ARITHMETICS_H */


