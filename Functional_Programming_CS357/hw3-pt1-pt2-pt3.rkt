#lang racket
;;Tristin Glunt
;; CS 357 - HW 3
;; tglunt@unm.edu

;;I ran the program and pasted the testoutput for
;;HW3 in the command line and everything outputted with
;;no errors.

;;I did it once at the end of the program and it broke the
;;whole program with random errors all over the place. I'm
;;using DrRacket, not sure if that is why.

;;Posting in the command line after compiling should work
;;great though!


;; Part 1 - Book exercises


;;7.2
;;compose3
;;takes as arguments three procedures f, g, h
;;returns the composition k of x = f(g(h(x)))
(define compose3
  (lambda (f g h)
    (lambda (x)
      (f (g (h x))))))

;;7.3
;;compose-many
;;use the unrestricted lambda to define
;;composition of arbitrary number of procedures on one argument
;;((compose-many add1 add1 add1 add1) 3) => 7
;;((compose-many sqrt abs sub1 (lambda (n) (* n n))) 0.6) => 0.8
(define compose-many
  (lambda f
    (lambda (x)
      (letrec
          ((loop
            (lambda (f x)
              (if (null? f)
                  x
                  (loop (cdr f) ((car f) x))))))
        (loop (helper-reverse f) x)))))

(define helper-reverse
  (lambda (ls)
    (if (null? ls)
        '()
        (append (helper-reverse (cdr ls)) (list (car ls))))))


;;7.6
;; if there are < 2 elemets in the list, return empty list
;; map proc to the first two arguments of the list
;;(map-first-two + '(2 3 4 5 7)) => (5 7 9 12)
;;(map-first-two max '(2 4 3 5 4 1)) => (4 4 5 5 4)
(define map-first-two
  (lambda (proc ls)
    (if (null? (cdr ls))
        '()
        (cons (proc (car ls) (cadr ls)) (map-first-two proc (cdr ls))))))


;;7.7
;;reduce
;;(3 5 7 9) -> (8 7 9) -> (15 9) -> 24
;;(reduce + '(1 3 5 7 9)) => 25
;;(reduce (lambda (x y) (and x y)) '(#t #t #t #t)) => #t
(define reduce
  (lambda (proc ls)
    (if (null? (cddr ls))
        (proc (car ls) (cadr ls))
        (reduce proc (append (list (proc (car ls) (cadr ls))) (cddr ls))))))


;;7.8
;;(andmap positive? '(3 4 6 9)) => #t
;;(andmap positive? '(3 -1 4 8)) => #f
;;(let ((not-null? (compose not null?)))
;;(andmap not-null? '((a b) (c) (c d e)))) => #t


(define andmap
  (lambda (proc ls)
    (letrec
        ((ttable (map proc ls))
         (loop
          (lambda (proc othtable acc)
                (cond
                  ((null? othtable) acc)
                  ((eq? (car othtable) #f) #f)
                  ((eq? (car othtable) #t) (loop proc (cdr othtable) #t))
                  (else
                   (loop proc (cdr othtable)))))))
      (loop proc ttable #f))))    


;;7.12
;;((curried* 25) 5) => 125
;; curry the procedure *
;;(timeslO 125) => 1250
(define curried*
  (lambda (n)
    (lambda (m)
      (* m n))))

(define times10
  (lambda (n)
    ((curried* n) 10)))

;;7.18
;;first write between? that takes in 3 numbers, a, b, and c
;; returns true when b is between a and c; a < b < c
;; then write between?-c a curried version of between where
;; each procedure has only one parameter
;; a returns b which returns c which checks whether x is between z
;;(((between?-c 5) 6) 7) => #t
(define between?
  (lambda (a b c)
    (cond
      ((not (and (number? a) (number? b) (number? c))) #f)
      ((and (< a b) (< b c)) #t)
      (else
       #f))))

(define between?-c
  (lambda (a)
    (lambda (b)
      (lambda (c)
        (if (< b c)
            (if (< a b)
                #t
                #f)
            #f)))))

;;7.22
;;using flat-recur define mult-by-scalar that takes
;;as its argumentsa number c and returns a procedure
;;that takes as its arguments an ntpl and multiplies
;;each component of ntpl by the number c
;;((mult-by-scalar 3) '(1 -2 3 -4)) => (3 -6 9 -12)
;;((mult-by-scalar 5) '()) => ()

(define mult-by-scalar
  (lambda (n)
    (flat-recur '() (lambda (x y) (cons (* n x) y)))))


(define flat-recur
  (lambda (seed list-proc)
    (letrec
        ((helper
          (lambda (ls)
            (if (null? ls)
                seed
                (list-proc (car ls) (helper (cdr ls)))))))
      helper)))

;;7.30
;;define procedure reverse-all using deep-recur
;;also defined a helper reverse function
;;that act as the procs in the deep-reverse
(define deep-recur
  (lambda (seed item-proc list-proc)
    (letrec
        ((helper
          (lambda (ls)
            (if (null? ls)
                seed
                (let ((a (car ls)))
                  (if (or (pair? a) (null? a))
                          (list-proc (helper a) (helper (cdr ls)))
                          (item-proc a (helper (cdr ls)))))))))
      helper)))

(define reverse-all
  (lambda (ls)
    ((deep-recur '() other-reverse other-reverse) ls)))

(define other-reverse
  (lambda (x y)
    (append y (list x))))


;;7.31
;;define flat-recur using deep-recur :(
(define my-flat-recur
  (lambda (seed list-proc)
    (deep-recur seed list-proc list-proc)))

;;testing exer-flat-recur
(define test-mult-by-scalar
  (lambda (n)
    (my-flat-recur '() (lambda (x y) (cons (* n x) y)))))

;;Part 2
;;1.a
;;(define fact (tail-recur zero? sub1 * 1))
;;(fact 10) => 3268800
(define tail-recur-it
  (lambda (bpred xproc aproc acc0)
    (letrec
        ((helper
          (lambda (x acc0)
            (if (bpred x)
                acc0
                (helper (xproc x) (aproc x acc0))))))
      helper)))

(define tail-recur
  (lambda (bpred xproc aproc acc0)
    (lambda (x)
      ((tail-recur-it bpred xproc aproc acc0) x acc0))))

;;b. tail-recur for reverse
(define reverse
  (tail-recur null? cdr (lambda (x y) (cons (car x) y)) '()))

;;c. tail-recur for iota
(define iota
  (tail-recur zero? sub1 cons '()))

;;2.
;;((disjunction2 symbol? procedure?) +) => #t
;;((disjunction2 symbol? procedure?) (quote +)) => #f
;;(filter (disjunction2 even? (lambda (x) (< x 4))) (iota 8))
;; => (1 2 3 4 6 8)
(define disjunction2
  (lambda (pred1? pred2?)
    (lambda (x)
      (if (or (pred1? x) (pred2? x))
          #t
          #f))))


(define filter
  (lambda (pred ls)
    ((fold (lambda (x y) (if (pred x) (cons x y) y)) '()) ls)))

(define fold
  (lambda (proc seed)
    (lambda (ls)
      (if (null? ls)
          seed
          (proc (car ls)
                ((fold proc seed) (cdr ls)))))))

;;3.
;;((disjunction symbol? procedure? even?) +) => #t
(define disjunction
  (lambda pred
   (lambda (x)
    (letrec
        ((loop
          (lambda (ls)
            (cond
              ((null? ls) #f)
              (((car ls) x) #t)
              (else
               (loop (cdr ls)))))))
      (loop pred)))))

;;4
;;(matrix-map (lambda (x) (* x x)) '((1 2) (3 4)))
;;=> ((1 4) (9 16))

(define matrix-map
  (lambda (proc ls)
    (cond
      ((not (and (pair? (car ls)) (pair? (cdr ls)))) '())
      (else
       (my-deep-map proc ls)))))

(define my-deep-map
  (lambda (proc ls)
    (cond
      ((null? ls) '())
      ((pair? (car ls))
       (cons (my-deep-map proc (car ls)) (my-deep-map proc (cdr ls))))
      (else
       (cons (proc (car ls)) (my-deep-map proc (cdr ls)))))))

;;5
(define fold5
  (lambda (seed proc)
    (letrec
        ((pattern
          (lambda (ls)
            (if (null? ls)
                seed
                (proc (car ls)
                      (pattern (cdr ls)))))))
      pattern)))

;;a.
;; (delete-duplicates '(a b a b a b a b)) => (a b)
;; (delete-duplicates '(1 2 3 4))
(define delete-duplicates
    (fold5 '()
           (lambda (x y)
             (if (not (member? x y))
                 (cons x y)
                 (cdr y)))))

(define member?
  (lambda (item ls)
    (cond
      ((null? ls) #f)
      ((eq? item (car ls)) #t)
      (else
       (member item (cdr ls))))))

;;b.
;;(assoc 'b '((a 1) (b 2))) => '(b 2)
;;(assoc 'c '((a 1) (b 2))) => #f
(define assoc
  (lambda (item ls)
    ((fold5 #f
           (lambda (x y)
             (if (eq? item (car x))
                 x
                 y)))
     ls)))

;;Pt.3
(define select
  (lambda (pred ls0 ls1)
    (cond ((null? ls0) '())
          ((pred (car ls0))
           (cons (car ls1) (select pred (cdr ls0) (cdr ls1))))
          (else
           (select pred (cdr ls0) (cdr ls1))))))

;;1. length - returns the length of a list of numbersw.
(define length
  (lambda (ls)
    (apply + (map (lambda (x) 1) ls))))

;;2. sum-of-squares
;;returns the sum of the squares of its arguments.
(define sum-of-squares
  (lambda ls
    (apply + (map (lambda (x) (* x x)) ls))))

;;3. avg - returns the average of its arguments
(define avg
  (lambda ls
    (/ (apply + ls) (length ls))))

;;4. avg-odd - returns the average of its odd arguments.
(define avg-odd
  (lambda ls
    (let ((select-odds (select odd? (iota (length ls)) ls)))
      (/ (apply + select-odds) (length select-odds)))))

;;5. shortest - returns the shortest of its list arguments
;; beacause of the output given it looked like you wanted
;; shortest to take in a list of arguments, not an
;;arbirary number
;;(shortest '(1 2) '(1 1 1) '(1 2))  => '((1 2) (1 2))

;;
(define shortest
  (lambda ls
    ((fold5 (car ls) (lambda (x y) (cond
                              ((< (length x) (length y)) x)
                              (else
                               y))))
    ls)))

;; 6. avg-fact - returns the average of the factorials of its arguments.
;; (avg-fact 1 2 3) => 3
(define avg-fact
  (lambda n
    (/ (apply + (map iota-fact n)) (length n))))

(define iota-fact
  (lambda (n)
    (apply * (iota n))))

;; 7. tally - takes a predicate and a list and returns the number of list elements which satisfy the predicate.
;; (tally even? '(1 2 2 3 4 5 5)) => 3
(define tally
  (lambda (pred ls)
      (apply +
             (map (lambda (x) (if (pred x) 1 0)) ls))))

;; 8. list-ref - takes a list and an integer, n, and returns the n-th element of the list
;; (list-ref '(1 2 3 4) 3) => 3
;; (list-ref '(4 3 2 1) 2) => 2
(define list-ref
  (lambda (ls n)
      (car (select (lambda (x)
                (= x n)) (iota n) ls))))













