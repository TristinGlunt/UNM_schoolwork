
--1
-- myTakeWhile (/= ’ ’) "This is practice."
-- => "This"
myTakeWhile :: (t -> Bool) -> [t] -> [t]
myTakeWhile f [] = []
myTakeWhile f (x:xs) = if f x then x : myTakeWhile f xs else []

--2
mySpan :: (a -> Bool) -> [a] -> ([a], [a])
mySpan f (x:xs) = (myTakeWhile f (x:xs), myTakeRest f (x:xs))

myTakeRest f [] = []
myTakeRest f (x:xs) = if f x then myTakeRest f xs else x : xs

--3
--["ABC","ABD","ABE","ACD","ACE","ADE","BCD","BCE","BDE","CDE"]
combinations3 :: (Ord a) => [a] -> [[a]]
combinations3 xs = [ (x:y:z:[]) | x <- xs, y <- xs, z <- xs, (x < y && y < z) ] 

--4
--runLengthEncode [4,2,2,1,1,1,1,4,4,4,4]
-- [(4,1),(2,2),(1,4),(4,4)]
runLengthEncode :: Eq a => [a] -> [(a, Int)]
runLengthEncode xs = [ (head x, length x) | x <- group xs] 

-- group [4,2,2,1,1,1,1,4,4,4,4] = [[4], [2,2], [1,1,1,1], [4,4,4,4]]
group :: Eq a => [a] -> [[a]]
group [] = []
group (x:xs) = fst stuff : group (snd stuff)
                where stuff = mySpan (== x) (x:xs)

--5
-- runLengthDecode [(4,1),(2,2),(1,4),(4,4)]
-- [4,2,2,1,1,1,1,4,4,4,4]
-- runLengthDecode :: Eq => a -> [(a, Int)] -> [a]
runLengthDecode [] = []
runLengthDecode (x:xs) = helper (fst x) (snd x) ++ runLengthDecode xs

helper x 0 = []
helper x acc = x : helper x (acc - 1)

--6
--splitText (/= ’ ’) "This is practice."
-- ["This","is","practice."]
splitText :: Eq a => (a -> Bool) -> [a] -> [[a]]
splitText f [] = []
splitText f (x:xs) = filter (/= []) $ [(myTakeWhile f (x:xs))] ++ (splitText f (splitHelper f (x:xs)))

splitHelper :: (a -> Bool) -> [a] -> [a]
splitHelper f [] = []
splitHelper f (x:xs) = if f x then splitHelper f xs else xs

--7 
--encipher [A..Z] [a..z] "THIS"
-- this
encipher xs ys zs = [snd x |z <- zs, x <- zip xs ys, z == fst x]

--8
--goldbach :: Integral t => t -> [(t, t)]
goldbach n = [ (x,y) | x <- primes n, y <- primes n, (x + y) == n, x <= y]


factors n = [(x,y) | x <- [1..n], y <- [1..n], x*y == n]

primes n = [x | x <- [1..n], ((== 2) . length) (factors x)]

--9
--increasing "ABBD"
-- True
--increasing [100, 99..1]
-- False

increasing :: Ord a => [a] -> Bool
increasing xs = and (init [x <= (head (tail xs)) | x <- xs])

--10
select :: (t -> Bool) -> [t] -> [a] -> [a]
select f xs ys = [ t | (x,t) <- zip xs ys, (f x)]

--11
--increasing :: Ord a => [a] -> Bool
increasing' [] = True
increasing' (x:[]) = True
increasing' (x:xs) = if x < head xs then increasing' xs else False 

combinations :: Ord a => Int -> [a] -> [[a]]
combinations n xs = [ x | x <- mapM (const xs) [1..n], increasing' x]

    
--12
data ComplexInteger =\ ComplexInteger {real :: Int, imaginary :: Int}

instance Eq ComplexInteger where
    (ComplexInteger a b) == (ComplexInteger c d) = (a == c) && (b == d)

instance Show ComplexInteger where
    show (ComplexInteger a 0) = show a
    show (ComplexInteger 0 b) = show b ++ "i"
    show (ComplexInteger a b) = show a ++ "+" ++ show b ++ "i" 

instance Num ComplexInteger where
    (ComplexInteger a b) * (ComplexInteger c d) = (ComplexInteger (a*c - b*d) (a*d + b*c))
    (ComplexInteger a b) + (ComplexInteger c d) = (ComplexInteger (a+c) (b+d))   


main = do
    putStrLn "func:myTakeWhile"
    putStrLn $ show (myTakeWhile (/= ' ') "This is practice.")
    putStrLn $ show (myTakeWhile (/= ' ') " This is practice.")
    putStrLn $ show (myTakeWhile (/= 1) [2,3,4,1])
    putStrLn "func:mySpan"
    putStrLn $ show (mySpan (/= ' ') "This is practice.")
    putStrLn $ show (mySpan (/= ' ') " This is practice.")
    putStrLn $ show (mySpan (/= 1) [2,3,4,1])
    putStrLn "func:combinations3"
    putStrLn $ show (combinations3 "ABCDE")
    putStrLn $ show (combinations3 [1, 3, 5, 7])
    putStrLn "func:runLengthEncode"
    putStrLn $ show (runLengthEncode [4,2,2,1,1,1,1,4,4,4,4])
    putStrLn $ show (runLengthEncode "foo")
    putStrLn "func:runLengthDecode"
    putStrLn $ show (runLengthDecode [(4,1),(2,2),(1,4),(4,4)])
    putStrLn $ show (runLengthDecode [('f',1),('o',2)])
    putStrLn "func:splitText"
    putStrLn $ show (splitText (/= ' ') "This is practice.")
    putStrLn $ show (splitText (/= ' ') " This  is   practice. ")
    putStrLn "func:encipher"
    putStrLn $ show (encipher ['A'..'Z'] ['a'..'z'] "HELLO")
    putStrLn $ show (encipher ['b'..'z'] ['a'..'y'] "good")
    putStrLn "func:goldbach"
    putStrLn $ show (goldbach 6)
    putStrLn $ show (goldbach 100)
    putStrLn "func:increasing"
    putStrLn $ show (increasing "ABBD")
    putStrLn $ show (increasing [100,99..1])
    putStrLn "func:select"
    putStrLn $ show (select even [1..26] "abcdefghijklmnopqrstuvwxyz")
    putStrLn $ show ( select (<= 'g') "abcdefghijklmnopqrstuvwxyz" [1..26])
    putStrLn "func:combinations"
    putStrLn $ show (combinations 3 "ABCDE")
    putStrLn $ show (combinations 4 "ABCDE")
    putStrLn $ show (combinations 2 "ABCDE")
    putStrLn "func:ComplexInteger"
    putStrLn $ show (real (ComplexInteger 1 2))
    putStrLn $ show (imaginary (ComplexInteger 2 3))
    putStrLn $ show ((ComplexInteger 1 2) == (ComplexInteger 3 4))
    putStrLn $ show ((ComplexInteger 1 2) * (ComplexInteger 3 4))
    putStrLn $ show ((ComplexInteger 1 2) + (ComplexInteger 3 4))
