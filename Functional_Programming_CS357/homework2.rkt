#lang racket
;;Tristin Glunt
;;CS 357
;;Homework 2
;;tglunt@unm.edu

;;4.4
;;'(a b d) => ((a) (b) (d))
;;takes top level elements of a list and wraps them all individually by another list
;;making them all 1 lvl deeper
(define deepen-1
  (lambda (ls)
    (cond
      ((null? ls) '())
      ((not (pair? ls)) (cons ls '()))
      (else
       (cons (deepen-1 (car ls)) (deepen-1 (cdr ls)))))))

;;4.6
;;(insert-left-all 'z 'a '(a (b a ) ((a (c))))) => (z a ((b z a) (z a (c))))
(define insert-left-all
  (lambda (newItem oldItem ls)
    (cond
      ((null? ls) '())
      ((equal? oldItem (car ls)) (append (list newItem (car ls)) (insert-left-all newItem oldItem (cdr ls))))
      ((pair? (car ls)) (cons (insert-left-all newItem oldItem (car ls)) (insert-left-all newItem oldItem (cdr ls))))
      (else
       (cons (car ls) (insert-left-all newItem oldItem (cdr ls)))))))

;;4.10
;;(leftmost '((a b) (c (d e)))) => a
;;(leftmost '((((c ((e f) g) h))))) => c
;;returns leftmost atomic item in the list
(define leftmost
  (lambda (ls)
    (cond
      ((null? ls) '())
      ((not (pair? (car ls))) (car ls))
      (else
       (leftmost (car ls))))))

;;4.11
;;(rightmost '((a b) (d (c d (f (g h) i) m n) u) v)) => v
;;(rightmost '((((((b (c)))))))) => c
;;(rightmost '(a ())) => ()
(define rightmost
  (lambda (ls)
    (cond
      ((null? ls) '())
      ((not (pair? (car (my-reverse ls)))) (car (my-reverse ls)))
      (else
       (rightmost (car (my-reverse ls)))))))

(define my-reverse
  (lambda (ls)
    (reverse-it ls '())))

(define reverse-it
  (lambda (ls acc)
    (if (null? ls)
        acc
        (reverse-it (cdr ls) (cons (car ls) acc)))))


;;4.18
(define length-it
  (lambda (ls acc)
    (if (null? ls)
        acc
      (length-it (cdr ls) (+ acc 1)))))

(define length
  (lambda (ls)
    (length-it ls 0)))

;;4.19
(define mk-asc-list-of-ints
  (lambda (n acc)
    (if (= n 0)
        acc
       (mk-asc-list-of-ints (sub1 n) (cons n acc)))))

(define mk-desc-list-of-ints
  (lambda (n acc)
    (if (= n 0)
        (my-reverse acc)
       (mk-desc-list-of-ints (sub1 n) (cons n acc)))))

;;4.20
(define occurs
  (lambda (item ls)
    (if (null? ls)
        0
        (if (equal? (car ls) item)
            (+ 1 (occurs item (cdr ls)))
            (occurs item (cdr ls))))))

(define occurs-it
  (lambda (item ls acc)
    (cond
      ((null? ls) acc)
      ((equal? item (car ls)) (occurs-it item (cdr ls)(add1 acc)))
      (else
       (occurs-it item (cdr ls) acc)))))


;;2
;;(calculator 42) => 42
;;(calculator '(1 + 2)) => 3
;;(calculator '(1 + (2 * 8))) => 17
;;(calculator '((((2 + 3) * 2) / 5) + (17 - 1)))) => 18
(define calculator
  (lambda (ls)
    (cond
      ((number? ls) ls)
      ((and (not (pair? (car ls))) (not (pair? (cdr (cdr ls)))))
       (my-eval (car (cdr ls)) (car ls) (car (cdr (cdr ls)))))
      (else
       (my-eval (car (cdr ls)) (calculator (car ls)) (calculator (caddr ls)))))))

(define my-eval
  (lambda (item num1 num2)
    (cond
      ((equal? item '+) (+ num1 num2))
      ((equal? item '-) (- num1 num2))
      ((equal? item '/) (/ num1 num2))
      (else
       (* num1 num2)))))

;;3
;;(infix->prefix 42) => 42
;;(infix->prefix '(1 + 2)) => (+ 1 2)
;;(infix->prefix '(1 + (2 * 8))) => (+ 1 (* 2 8))
;;(infix->prefix '((((2 + 3) * 2) / 5) + (17 - 1)) => (+ (/ (* (+ 2 3) 2) 5) (- 17 1))

;;The recursive call calls on the car of the list no matter what even if it is not the pair,
;;since the base case will return the number if it wasn't a pair and the caddr will handle the
;;actual pair of the list
(define infix->prefix
  (lambda (ls)
    (cond
      ((number? ls) ls)
      ((and (not (pair? (car ls))) (not (pair? (caddr ls))))
       (cons (car (cdr ls)) (cons (car ls) (cddr ls))))
      (else
       (cons (car (cdr ls)) (cons (infix->prefix (car ls)) (cons (infix->prefix (caddr ls)) '())))))))

;;4
;;(iota-iota 1) => ((1 . 1))
;;(iota-iota 2) => ((1 . 1) (1 . 2) (2 . 1) (2 .2))
;;(iota-iota 3) => ((1 . 1) (1 . 2) (1 . 3) (2 . 1) (2 . 2) (2 . 3) (3 . 1) (3 . 2) (3 . 3))
;;All helper functions should be tail-recursive and should be defined within the body of iota-iota using letrec.
(define iota-iota
  (lambda (n)
    (letrec
        ((loop
          (lambda (x z acc)
                     (cond
                       ((= x 0) acc)
                       ((= z 0) (loop (sub1 x) n acc))
                       (else
                        (loop x (sub1 z) (cons (cons x z) acc)))))))
          (loop n n '()))))


;;5
;;(digits->number '(7 6 1 5)) => 7615
;; tail-recursive and any helper functions declared with letrec
(define digits->number
  (lambda (ls)
    (letrec
        ((loop
          (lambda (ls n acc)
            (if (null? ls)
                acc
               (loop (cdr ls) (sub1 n) (+ (* (expt 10 (sub1 n)) (car ls)) acc))))))
      (loop ls (length ls) 0))))

;;6
;; (cond->if '(cond ((> x y) (- x y)) ((< x y) (- y x)) (else 0))) ===>
;; (if (> x y) (- x y) (if (< x y) (- y x) 0))
;; takes cond statement and transforms it into a set of nested ifs
(define cond->if
  (lambda (ls)
    (letrec
        ((loop
          (lambda (ls)
            (if (equal? (caar ls) 'else)
                (cadar ls)
                (append (list 'if) (car ls) (list (loop (cdr ls))))))))
      (loop (cdr ls)))))

;;maybe change to condition statement and if car is equal to cond then pass the
;;cdr instead of possibly destroying a list
;;obviously has lots of test conditions not being tested for...



;;7
;; (cos 2)
;; sum first 100 terms of the taylor series
;; tail recursively
;; any helper functions defined with letrec
;; not using expt or fact
;; multiplies acc by 1.0 to conver to floating point
(define cos
  (lambda (x)
    (letrec
        ((loop
          (lambda (x sign exp expacc fact factacc acc)
            (if (= 98 exp)
                (* 1.0 acc) 
              (loop x (- sign) (+ 2 exp) (* expacc x x) (+ 2 fact) (* (* fact (- fact 1)) factacc)
                    (+ acc (* sign (/ (* (* x x) expacc) (* (* fact (- fact 1)) factacc)))))))))
      (loop x -1 2 1 2 1 1))))






