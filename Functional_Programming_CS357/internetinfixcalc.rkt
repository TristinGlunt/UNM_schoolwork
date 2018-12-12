#lang racket
;; usage: (reduce (tokenize))

;; tokenizing function
(define (tokenize)
  (define (-tokenize-operator first)
    (cond
      ([equal? first #\+] (list + (read-char)))
      ([equal? first #\-] (list - (read-char)))
      ([equal? first #\*] (list * (read-char)))
      ([equal? first #\/] (list / (read-char)))))

  (define (-tokenize-number first)
    (define (--char->number char)
      (let ([ascii-zero (char->integer #\0)])
        (- (char->integer char) ascii-zero)))

    (define (--read-number initial)
      (let ([char (read-char)])
        (if (char-numeric? char)
            (--read-number (+ (--char->number char) (* initial 10)))
            (list initial char))))

    (if (char-numeric? first)
        (--read-number (--char->number first))
        '()))

  (define (-tokenize-openparen first)
    (if (equal? first #\()
        (list #\( (read-char))
        '()))

  (define (-tokenize first endchar)
    (if (equal? first endchar)
        '()
        (let ([operator (-tokenize-operator first)]
              [number (-tokenize-number first)]
              [openparen (-tokenize-openparen first)])
          (cond
            ([pair? operator] (cons (car operator) (-tokenize (cadr operator) endchar)))
            ([pair? number] (cons (car number) (-tokenize (cadr number) endchar)))
            ([pair? openparen] (list (-tokenize (cadr openparen) #\))))
            (else (tokenize))))))

  (let ([first (read-char)])
    (-tokenize first #\newline)))

;; parsing and evaluation function
(define (reduce tokens)
  (define (-operator-priority op)
    (cond
      ([ormap (lambda (p) (equal? p op)) (list + -)] 1)
      ([ormap (lambda (p) (equal? p op)) (list * /)] 2)))

  (define (-rvalue list max-priority)
    (define (--lower-parentheses parenthesed-expression next-tokens)
          (let ([paren-result (car (-rvalue parenthesed-expression 0))])
            (-rvalue (cons paren-result next-tokens) max-priority)))

    (define (--reduce-rvalue lvalue next-tokens)
      (let* ([operator (car next-tokens)]
             [priority (-operator-priority operator)]
             [then (cdr next-tokens)])
        (if (> priority max-priority)
            (let* ([rvalue (-rvalue then priority)]
                   [value (operator lvalue (car rvalue))])
              (-rvalue (cons value (cdr rvalue)) max-priority))
            list)))

    (let ([lvalue (car list)]
          [next-tokens (cdr list)])
      (if (pair? lvalue)
          (--lower-parentheses lvalue next-tokens)
          (if (pair? next-tokens)
              (--reduce-rvalue lvalue next-tokens)
              list))))

  (car (-rvalue tokens 0)))