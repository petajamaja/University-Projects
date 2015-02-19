;;;;; UTILS - TAKEN FROM SEMINARS AND LECTURES AND OTHER SOURCES

;; is an item atomic?
(define (atom? x) (not (pair? x)))

;; returmns the same list , but applies function fn at position pos
(define (apply-at fn list pos)
  (cond ((= pos 0) (cons (fn (car list)) (cdr list)))
        (else (cons (car list) (apply-at fn (cdr list) (- pos 1))))))
  
;; substitute element in list on position pos by new
(define (subst new list pos)
  (apply-at (lambda (x) new) list pos))

;; replace one list with another - modified
(define (replace list new-list i)
  (if (= i 0)
      (cons new-list (cdr list))
      (cons (car list) (replace (cdr list) new-list (- i 1)))))

;; the same apply-at, but for matrix
(define (apply-at-at fn x y list-of-lists)
  (apply-at (lambda (line)
              (apply-at fn line x)
             ) list-of-lists y))

;; this one is obvious =)
(define (get-symbol x y list-of-lists)
  (list-ref (list-ref list-of-lists y) x))

(define (search-procedure-name f llist)
  (if (null? llist)
      #f
      (if (null? (car llist))
          #f
          (if (f (car llist))
              (car llist)
              (search-procedure-name f (cdr llist)))
      )))

;; function fold
 (define (fold fn a llist)
  (cond ((null? llist) a)
        (else (fn (car llist) (fold fn a (cdr llist))))
  ))
 

 
(define (abs-value x)
  (if (> 0 x)
      (- x)
      x))

;; sorting function
(define (sort f a)
  (if (null? a)
      '()
      (sort-inner f (car a) (sort f (cdr a)))))
      
(define (sort-inner f val a)
  (if (null? a)
      (list val)
      (if (f val (car a))
          (append (list val) a)
          (append (list (car a)) (sort-inner f val (cdr a))))))


(define (take listt index)
  (if (= index 0)
      '()
      (append (list (car listt)) (take (cdr listt) (- index 1)))))

;; definition of random numbers generator, taken from SICP
(define (congruential-rng seed)
  (let ((a 16807 #|(expt 7 5)|#)
        (m 2147483647 #|(- (expt 2 31) 1)|#))
    (let ((m-1 (- m 1)))
      (let ((seed (+ (remainder seed m-1) 1)))
        (lambda (b)
          (let ((n (remainder (* a seed) m)))
            (set! seed n)
            (quotient (* (- n 1) b) m-1)))))))

(define random (congruential-rng 12345))

;;;;; END OF UTILS MODULE

;;;;; PART 1 - SIMULATOR

;;; main robot movement functions section
 
;; a map containing current position and the position that will be next 
;; after the robot turns left
(define turn-left-map
  (list (cons 'west 'southwest)
        (cons 'southwest 'southeast)
        (cons 'southeast 'east)
        (cons 'east 'northeast)
        (cons 'northeast 'northwest)
        (cons 'northwest 'west)))

;; creating actual bind between the key and value of map above
(define (eval-left orientation)
  (cdr (assoc orientation turn-left-map)))

;; function turn-left - using mapping defined upwards
(define (turn-left state expression program limit steps current-maze x y current-orientation) 
  (list '(OK 1) (list current-maze (list x y) (eval-left current-orientation))))
  
;; function step odd, steps where starting position is odd
(define (step-odd state expression program limit steps current-maze x y current-orientation)
       (cond
         ((equal? current-orientation 'west)
                   (list '(OK 1) (list current-maze (list (- x 1) y) current-orientation))) ; [x,y] -> [x-1,y]
         ((equal? current-orientation 'southwest)
                   (list '(OK 1) (list current-maze (list x (+ y 1)) current-orientation))) ; [x,y] -> [x,y+1]
         ((equal? current-orientation 'southeast)
                   (list '(OK 1) (list current-maze (list (+ x 1) (+ y 1)) current-orientation)))  ; [x,y] -> [x+1,y+1]
         ((equal? current-orientation 'east)
                   (list '(OK 1) (list current-maze (list (+ x 1)  y) current-orientation)))  ; [x,y] -> [x+1,y]
         ((equal? current-orientation 'northeast)
                   (list '(OK 1) (list current-maze (list (+ x 1)  (- y 1)) current-orientation)))  ; [x,y] -> [x+1,y-1]
         ((equal? current-orientation 'northwest)
                   (list '(OK 1) (list current-maze (list x  (- y 1)) current-orientation)))   ; [x,y] -> [x,y-1]
         (else (list '(ERROR 0) state))))

;; function step even, steps where starting position is even
(define (step-even  state expression program limit steps current-maze x y current-orientation)  
       (cond
         ((equal? current-orientation 'west)
                   (list '(OK 1) (list current-maze (list (- x 1) y) current-orientation))) ; [x,y] -> [x-1,y]
         ((equal? current-orientation 'southwest)
                   (list '(OK 1) (list current-maze (list (- x 1) (+ y 1)) current-orientation))) ; [x,y] -> [x-1,y+1]
         ((equal? current-orientation 'southeast)
                   (list '(OK 1) (list current-maze (list  x  (+ y 1)) current-orientation)))  ; [x,y] -> [x,y+1]
         ((equal? current-orientation 'east)
                   (list '(OK 1) (list current-maze (list (+ x 1)  y) current-orientation)))  ; [x,y] -> [x+1,y]
         ((equal? current-orientation 'northeast)
                   (list '(OK 1) (list current-maze (list x (- y 1)) current-orientation)))  ; [x,y] -> [x,y-1]
         ((equal? current-orientation 'northwest)
                   (list '(OK 1) (list current-maze (list (- x 1)  (- y 1)) current-orientation)))   ; [x,y] -> [x-1,y-1]
         (else (list '(ERROR 0) state))))
                    

;; actual function step
(define (step state expression program limit steps current-maze x y current-orientation)
  (if (or (even? y)(= y 0)) (step-even state expression program limit steps current-maze x y current-orientation)
      (step-odd state expression program limit steps current-maze x y current-orientation)))
;; function put-mark


;; function get-mark
(define (get-mark state expression program limit steps current-maze x y current-orientation)
 
  (if (> (list-ref (list-ref current-maze y) x) 0)
      (list '(OK 1) (list 
                         (append (take current-maze y)
                                 (list (append (take (list-ref current-maze y) x)
                                               (list (- (list-ref (list-ref current-maze y) x) 1))
                                               (cdr (list-tail (list-ref current-maze y) x))))
                                 (cdr (list-tail current-maze y)))
                         (list x y)
                         current-orientation))
      (list '(ERROR 0) state)
  ))
      

;; function put-mark
(define (put-mark state expression program limit steps current-maze x y current-orientation)
    (list '(OK 1) (list (append (take current-maze y) 
                  (list (append (take (list-ref current-maze y) x)
                                    (list (+ (list-ref (list-ref current-maze y) x) 1))
                                           (cdr (list-tail (list-ref current-maze y) x))))
                             (cdr (list-tail current-maze y)))
                     (list x y)
                     current-orientation)))

;;; end of main movement section


;;; section of boolean functions 

;; function mark?
 (define ( mark? x y current-maze)
    (>= (get-symbol x y current-maze) 1))
        

 ;; function west?
 (define (west? current-orientation)
     (equal? 'west current-orientation))
 
 
 ;; function wall?
 (define (wall? x y current-maze current-orientation)
     (if (even? y) (equal? (even-wall? x y current-maze current-orientation) #t)
      (equal? (odd-wall? x y current-maze current-orientation) #t)))
   
   
 (define (odd-wall? x y current-maze current-orientation)
  
     (cond  
           ((equal? current-orientation 'west)
                  (equal? (equal? (get-symbol (- x 1)  y current-maze) 'w) #t)) ; is [x-1,y] a wall?
           ((equal? current-orientation 'southwest)
                  (equal? (equal? (get-symbol x (+ 1 y) current-maze) 'w) #t)) ; is [x,y+1] a wall?
           ((equal? current-orientation 'southeast)
                  (equal?(equal?(get-symbol  (+ x 1) (+ y 1) current-maze) 'w) #t)) ; is [x+1,y+1] a wall?
           ((equal? current-orientation 'east)
                  (equal? (equal? (get-symbol (+ x 1) y current-maze) 'w) #t)) ; is [x+1,y] a wall? 
           ((equal? current-orientation 'northeast)
                  (equal? (equal?(get-symbol  (+ x 1) (- y 1) current-maze) 'w) #t)) ; is [x+1,y-1] a wall?   
           ((equal? current-orientation 'northwest)
                  (equal? (equal? (get-symbol x (- y 1) current-maze) 'w) #t))) ; is [x,y-1] a ?     
)
 
 (define (even-wall? x y current-maze current-orientation)
     (cond  
           ((equal? current-orientation 'west)
                  (equal? (get-symbol (- x 1)  y current-maze) 'w)) ; is [x-1,y] a wall?
           ((equal? current-orientation 'southwest)
                  (equal? (get-symbol (- x 1) (+ 1 y) current-maze) 'w)) ; is [x-1,y+1] a wall?
           ((equal? current-orientation 'southeast)
                  (equal?(get-symbol  x  (+ y 1) current-maze) 'w)) ; is [x,y+1] a wall?
           ((equal? current-orientation 'east)
                  (equal? (get-symbol (+ x 1) y current-maze) 'w)) ; is [x+1,y] a wall? 
           ((equal? current-orientation 'northeast)
                  (equal?(get-symbol  x (- y 1) current-maze) 'w)) ; is [x,y-1] a wall?   
           ((equal? current-orientation 'northwest)
                  (equal? (get-symbol (- x 1) (- y 1) current-maze) 'w)) ; is [x-1,y-1] a ?     
     ))
 
 ;; function valid?
 ;; returns #f if the action cannot be done
 ;; according to current state
 (define (valid? action  x y current-maze current-orientation)
   (cond 
     ((equal? action 'step) (equal? (wall? x y current-maze current-orientation) #f))
     ((equal? action 'get-mark) (equal? (mark?  x y current-maze) #t))
     (else #t)))
   
 
;; function wanted?, returns true if the element in
 ;; program list contains the needed procedure name
 ;; if the second word (name of procedure) equals the actual call
 ;; returns true, esle returns false
 (define (wanted? list-element procedure-name)
    (equal? (list-ref list-element 1) procedure-name))
 
 ;; function known?, returns true if the function name is 
 ;; one of the basic robot-language operations
 (define (known? arg)
   (cond ((equal? 'step arg) #t)
         ((equal? 'turn-left arg) #t)
         ((equal? 'put-mark arg) #t)
         ((equal? 'get-mark arg) #t)
         ((equal? 'wall? arg) #t)
         ((equal? 'mark? arg) #t)
         ((equal? 'west? arg) #t)
         ((equal? '() arg) #f)
         (else #f)))
   
 ;;; end of boolean section
 

;;; interpreter section

;; evaluate a program (procedure)
(define (simulate-procedure state expression program limit steps current-maze x y current-orientation)
  
  (let ((expression-new (list-ref
                (search-procedure-name (lambda (procedure)
                         (equal? expression (list-ref procedure 1)))
                       program)
                2)))
    (if expression-new
        (command state expression-new program (- limit 1) steps)
        (list '(ERROR 0) state))))


;; evaluate an if-clause 
(define (command-condition state expression program limit steps current-maze x y current-orientation)
  
  (case (list-ref expression 1)
    ('west?
     (if (west? current-orientation)
         (command state (list-ref expression 2) program limit steps)
         (command state (list-ref expression 3) program limit steps)))
    ('mark?
     (if (mark? x y current-maze)
         (command state (list-ref expression 2) program limit steps)
         (command state (list-ref expression 3) program limit steps)))
    ('wall?
          (if (wall? x y current-maze current-orientation)
              (command state (list-ref expression 2) program limit steps)
              (command state (list-ref expression 3) program limit steps)
          )
    )
    (else (list '(ERROR) state))))


;; evaluate single-word command.
;; it decides whether this command is known 
;; and acts according to the result.
(define (atomic-command state expression program limit steps current-maze x y current-orientation)
  
  (if (known? expression)                    ; if this is a basic robot-language operation
      (if(valid? expression  x y current-maze current-orientation)                 ; if this operation can be done in current maze state
         (begin
             (case expression
               ('step (step state expression program limit steps current-maze x y current-orientation))
               ('turn-left (turn-left state expression program limit steps current-maze x y current-orientation))
               ('put-mark (put-mark state expression program limit steps current-maze x y current-orientation))
               ('get-mark (get-mark state expression program limit steps current-maze x y current-orientation))))
         (list '(ERROR 0) state))                                                             ; negative branch - here an error in robot program occurs, return error + state
          
      (if (> limit 0)                                                     ; if this is, instead, some procedure call
          (simulate-procedure state expression program limit steps current-maze x y current-orientation)    ; positive branch - find a procedure in definitions and call its body 
          (list '(ERROR 0) state))))                                          ; negative branch - return error + current state
          
        

;; main recursive function
;; analyses expression-list parameter
;; evaluates its elements 
;; and calls itself on the cdr of the espression-list
(define (command state expression-list program limit steps)
  
  (if (or (< limit 0) (and (list? expression-list) (null? expression-list)))  ; If the limit has been riched or the program ended, return the required output
      
      (list (if (< limit 0) 
                '(ERROR 0)     ;in case it is an error, return error + current state
                '(OK 0))         ; else return OK + current state
                  
            state)
      
      (let ((current-maze (list-ref state 0))              ; else, define current state inside this function, and work with it 
            (x (list-ref (list-ref state 1) 0))
            (y (list-ref (list-ref state 1) 1))
            (current-orientation (list-ref state 2)))
        
        (if (list? expression-list)    ; if expression is a list
            
            (if (equal? (list-ref expression-list 0) 'if)   ; see if it is an if-statement
  
                (command-condition state expression-list program limit steps current-maze x y current-orientation)  ; positive branch: simulate the condition
                
                
                (let ((current-state (command state (car expression-list) program limit steps)))                           ; negative branch: work recursively
                  
                  (if (or (and (not (null? (list-ref current-state 0))) (equal? (car (list-ref current-state 0)) 'ERROR)) (> (cadar current-state) steps)) ; check if it is possible to continue
                     
                      current-state   ; positive branch : return current state of the maze and robot
            
                      (let ((next-state (command (list-ref current-state 1) (cdr expression-list) program limit steps)))    ; negative branch : continue with recursion
                        (append (list (list (caar next-state) (+ (cadar current-state) (cadar next-state)))) (list (list-ref next-state 1)))))))
            (atomic-command state expression-list program limit steps current-maze x y current-orientation)))))  ; if expression is not a list but a single command, just perform it
        

;;; main function that calls our recursive function
(define (simulate state expression program limit steps)
  ;; define what the output will be by calling the recursive evaluator
  (let ((output (command state expression program limit steps)))
    (list (cadar output) (cadr output))))

;;; end of main function

;;;;; END OF PART 1 - SIMULATOR

;;;;; PART 2 - POPULATION EVALUATOR

(define (recursive-evaluate program-list pairs threshold stack-size)
  (if (null? program-list)
      '()
      (let ((ret1 (recursive-inner-evaluate (car program-list) pairs threshold stack-size))
            (ret2 (recursive-evaluate (cdr program-list) pairs threshold stack-size)))
        (if (null? ret1)
            (if (null? ret2)
                '()
                ret2
            )
            (if (null? ret2)
                ret1
                (append ret1 ret2)
            )))))


(define (recursive-inner-evaluate program pairs threshold stack-size)
  (if (null? program)
      '()
      (let ((prg (evaluate-procedure program)))
        (if (> prg (list-ref threshold 2))
            '()
            (let ((ret (actual-evaluation program pairs threshold stack-size)))
              (if (or (> (list-ref ret 0) (list-ref threshold 0))
                      (> (list-ref ret 1) (list-ref threshold 1))
                      (> (list-ref ret 2) (list-ref threshold 3)))
                  '()
                  (list (list (list (list-ref ret 0) (list-ref ret 1) prg (list-ref ret 2)) program))))))))


(define (actual-evaluation program pairs threshold stack-size)
  (if (null? pairs)
      '(0 0 0) 
      (let ((ret1 (actual-evaluation-inner program (car pairs) threshold stack-size)))
        (if (or (> (list-ref ret1 0) (list-ref threshold 0))
                (> (list-ref ret1 1) (list-ref threshold 1))
                (> (list-ref ret1 2) (list-ref threshold 3)))
            ret1
            (let ((ret2 (actual-evaluation program (cdr pairs) threshold stack-size)))
              (list (+ (list-ref ret1 0) (list-ref ret2 0))
                    (+ (list-ref ret1 1) (list-ref ret2 1))
                    (+ (list-ref ret1 2) (list-ref ret2 2))))))))


(define (actual-evaluation-inner program pair threshold stack-size)
  (let ((ret (simulate (list-ref pair 0) 'start program stack-size (list-ref threshold 3))))
    (let ((steps (list-ref ret 0))
          (manhattan (find-manhattan-distance (list-ref (list-ref ret 1) 0) (list-ref (list-ref pair 1) 0)))
          (config (+ (evaluate-config (list-ref (list-ref ret 1) 1) (list-ref (list-ref pair 1) 1))
                     (if (equal? (list-ref (list-ref ret 1) 2) (list-ref (list-ref pair 1) 2)) 0 1))))
      (list manhattan config steps))))


;; recursive manhattan difference between the two mazes
(define (find-manhattan-distance first-maze second-maze)
  (if (null? first-maze)
      0
      (+ (find-manhattan-distance-inner (car first-maze) (car second-maze))
         (find-manhattan-distance (cdr first-maze) (cdr second-maze)))))

;; inner recursive function that actually conducts the manhattan distance counting
(define (find-manhattan-distance-inner first-maze second-maze)
  (if (null? first-maze)
      0
      (+ (if (equal? (car first-maze) 'w)
             0
             (if (>= (car first-maze) (car second-maze))
                 (- (car first-maze) (car second-maze))
                 (- (car second-maze) (car first-maze))
             )
         )
         (find-manhattan-distance-inner (cdr first-maze) (cdr second-maze)))))


(define (evaluate-config config1 config2) 0
  (let ((x (- (list-ref config1 0) (list-ref config2 0)))
        (y (- (list-ref config1 1) (list-ref config2 1))))
    (if (>= x 0)
        (if (>= y 0)
            (+ x y)
            (+ x (- y)))
        (if (>= y 0)
            (+ (- x) y)
            (+ (- x) (- y))))))

;; evaluation of a single procedure from the procedures list
(define (evaluate-procedure program)
 (if (null? program)
     0
     (if (list? program)
         (if (equal? (list-ref program 0) 'if)
             (+ (evaluate-procedure (list-ref program 2)) (evaluate-procedure (list-ref program 3)) 1)
             (if (equal? (list-ref program 0) 'procedure)
                 (+ (evaluate-procedure (list-ref program 2)) 1)
                 (+ (evaluate-procedure (car program)) (evaluate-procedure (cdr program)))))
         1
         )))


;; main evaluation function!
(define (evaluate program-list pairs threshold stack-size)
  
  ; sorting algorithm
  (sort (lambda (element-a element-b)
                   (if (< (list-ref (car element-a) 0) (list-ref (car element-b) 0))
                       #t
                       (if (= (list-ref (car element-a) 0) (list-ref (car element-b) 0))
                           (if (< (list-ref (car element-a) 1) (list-ref (car element-b) 1))
                               #t
                               (if (= (list-ref (car element-a) 1) (list-ref (car element-b) 1))
                                   (if (< (list-ref (car element-a) 2) (list-ref (car element-b) 2))
                                       #t
                                       (if (= (list-ref (car element-a) 2) (list-ref (car element-b) 2))
                                           (if (< (list-ref (car element-a) 3) (list-ref (car element-b) 3))
                                               #t
                                               #f
                                           )
                                           #f
                                       )
                                   )
                                   #f
                               )
                           )
                           #f
                       )
                   )
                 )
                 (recursive-evaluate program-list pairs threshold stack-size)))


;;;;; END OF POPULATION EVALUATOR

;;;;; PART 3 - EVOLUTION OF PROGRAMS

;; The main idea - which proved to be very useful - is to construct initial population out of nothing.
;; That is, we don't use pre-defined programs as initial population, we generate them randomly, too.

;;; generating stuff using random

;; this one is obviously generating a condition =)
(define (generate-random-condition)
  (case (random 3)
    ((0) 'west?)
    ((1) 'mark?)
    (else 'wall?)))

;; creating a program out of basic commands (+ something else, see comments)
(define (generate-command)
  
  ;; each action is done with probability of 1/10
  (case (random 10)
    ((0) '(0 ()))
    ((1) '(0 (step)))
    ((2) '(0 (start)))
    ((3) '(0 (get-mark)))
    ((4) '(0 (put-mark)))
    ((5) '(0 (turn-left)))
    ((6) '(1 (turn-right)))
    
    ;; create a random if-clause
    ((7) (let ((chunk1 (generate-command))
             (chunk2 (generate-command)))
         (list (+ (car chunk2) (car chunk1))
               (list (list 'if (generate-random-condition) (cadr chunk1) (cadr chunk2))))))
    
    ;; create a concatenation of two randomly created programs
    (else (let ((chunk1 (generate-command))
                (chunk2 (generate-command)))
            (if (null? (cadr chunk1))
                chunk2
                (if (null? (cadr chunk2))
                    chunk1
                    (list (+ (car chunk2) (car chunk1))
                          (append (cadr chunk1) (cadr chunk2)))))))))

;;; end of generating stuff using random

;;; actual mutation function - mutation of some whole expression
(define (mutate expression)  
  (if (null? expression)                ;; if we have an empty list - equivalent of the beginning - 
      (if (= (random 4) 0)        ;; with probability of 0.25 we generate a new procedure
          (generate-command)
          '(0 ()))                ;; with probability of 0.75 we return that empty list
      
      (if (list? (car expression))      ;; if the first elememnt is a list
          
        ;; with PROBABILITY_1 of 0.25 we do the following:
          
          (if (= (random 4) 0)   
              (case (random 4)
                
               ;; with PROBABILITY_2 of 0.25 return empty program with value 0
                
                ((0) '(0 ()))     
                
               ;; with PROBABILITY_2 of 0.25 return the concatenation of two programs:
                
                ((1) (let ((prg-part1 (mutate (list-ref (car expression) 2)))     ;; this is recursive creation of two programs that we will later concatenate
                         (prg-part3 (mutate (cdr expression))))
                     (list (+ (car prg-part1) (car prg-part3))                   ;; the final value of program is the sum of values of two programs above,
                           (append (cadr prg-part1) (cadr prg-part3)))))         ;; the final program is a concatenation of two previous programs
                
               ;; with PROBABILITY_2 of 0.25 return the concatenation of two other programs:
                
                ((2) (let ((prg-part2 (mutate (list-ref (car expression) 3)))     ;; the process is the same as above, only the part of current program 
                         (prg-part3 (mutate (cdr expression))))                   ;; that we are going to mutate is different
                     (list (+ (car prg-part2) (car prg-part3))                 
                           (append (cadr prg-part2) (cadr prg-part3)))))
                
               ;; with PROBABILITY_2 of 0.25 create an if-clause with the mutated parts of the initial program, and the condition is random
                
                (else (let ((prg-part1 (mutate (list-ref (car expression) 2)))
                            (prg-part2 (mutate (list-ref (car expression) 3)))
                            (prg-part3 (mutate (cdr expression))))
                        (list (+ (car prg-part3) (car prg-part2) (car prg-part1))
                              (cons (list 'if (generate-random-condition) (cadr prg-part1) (cadr prg-part2)) (cadr prg-part3))))))
              
              
              ;; with PROBABILITY_1 of 0.75 create an if-clause with the mutated parts of the initial program, with already existing condition
              
              (let ((prg-part1 (mutate (list-ref (car expression) 2)))
                    (prg-part2 (mutate (list-ref (car expression) 3)))
                    (prg-part3 (mutate (cdr expression))))
                (list (+ (car prg-part3) (car prg-part2) (car prg-part1))
                      (cons (list 'if (list-ref (car expression) 1) (cadr prg-part1) (cadr prg-part2)) (cadr prg-part3)))))
          
       
          ;; if the first element is NOT a list
          
          ;; with PROBABILITY_3 of 0.25 do the following:
          
          (if (= (random 4) 0)
              
              ;; with PROBABILITY_4 of 0.25 add a new program part to the mutated initial program
              
              (let ((prg-part1 (generate-command))         
                    (prg-part2 (mutate (cdr expression))))
                (list (+ (car prg-part2) (car prg-part1))
                      (append (cadr prg-part1) (cadr prg-part2))))
              
              ;; with PROBABILITY_4 of 0.75  mutate the initial program 
              ;; and add the first command of the initial program to the mutated program
              
              (let ((prg-part (mutate (cdr expression))))
                (list (+ (if (equal? (car expression) 'turn-right) 1 0) (car prg-part))
                      (cons (car expression) (cadr prg-part))))))))

;;; end of actual mutation function

;;; the part where the evolution is being done

;; the structure of each program is defined by a digit which says whether we have any 'turn-right' procedure calls or not
;; depending on that digit, we define the list of pre-defined procedures : one of them is 'start' - the main procedure
;; while the other one is 'turn-right'.
;; this function constructs the list of needed procedures.
(define (create-needed-procedures-list program)
  (if (> (car program) 0)
      (list (list 'procedure 'start (cadr program))
            (list 'procedure 'turn-right '(turn-left turn-left turn-left turn-left turn-left)))
      (list (list 'procedure 'start (cadr program)))))

;; preparation of the initial population of size i 
(define (evolve-prepare population-size)
  (if (> population-size 0)
      (cons (create-needed-procedures-list (mutate '(start)))
            (evolve-prepare (- population-size 1)))
      '()))


(define (evolve-get list-of-programs size)
  (if (> size 0)
      (cons (cadar list-of-programs) (evolve-get (cdr list-of-programs) (- size 1)))
      '()))
  

(define (evolve-programs list-of-programs)
  (if (null? list-of-programs)                
      '()
      (let ((program (caddar (car list-of-programs))))
        (cons (car list-of-programs)
              (cons (create-needed-procedures-list (mutate program))
                    (cons (create-needed-procedures-list (mutate program))
                          (cons (create-needed-procedures-list (mutate program))
                                (cons (create-needed-procedures-list (mutate program))
                                      (evolve-programs (cdr list-of-programs))))))))))

;; this is the inner function called when the main evolve function is called
;; it does the output and the selection
(define (evolve-inner pre-def-procedures pairs threshold stack_size)
  (let ((ret (evaluate pre-def-procedures pairs threshold stack_size)))
    (newline)
    (display (car ret))
    (evolve-inner (evolve-programs (evolve-get ret 15)) pairs threshold stack_size)))


;; the function that is called by the testing program
(define (evolve pairs threshold stack-size)
  (evolve-inner (evolve-programs (evolve-prepare 15)) pairs threshold stack-size))