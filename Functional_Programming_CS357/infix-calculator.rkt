#lang racket
;;2
;;(calculator 42) => 42
;;(calculator '(1 + 2)) => 3
;;(calculator '(1 + (2 * 8))) => 17
;;(calculator '((((2 + 3) * 2) / 5) + (17 - 1))))
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

(define pair?-all
  (lambda (ls)
    (cond
      ((null? ls) #f)
      ((number? ls) #f)
      ((pair? (car ls)) #t)
      (else
       (pair?-all (cdr ls))))))

;;3
