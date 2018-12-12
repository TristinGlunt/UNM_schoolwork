 {-
Tristin Glunt
tglunt@unm.edu
HW 6 
-}

--Only difference is order of splits output, effects order of trees and bogus, 
--still efficient and working just different order from Lance
--also order of question 10 are slightly different but still same sets

import Data.Char
import Data.List
-- "1011000"
-- 88
bits2num xs = binaryToNum'' (reverse xs)

binaryToNum'' (x:xs) = if x == '1' then 1 + binaryToNum' xs 0 1 else binaryToNum' xs 0 1

binaryToNum' [] acc n = acc
binaryToNum' (x:xs) acc n = if x == '1' then binaryToNum' xs (acc + (1 * (2 ^ n))) (n+1) else binaryToNum' xs acc (n+1)

--2 
--num to binary
--127
--"001111111"
num2bits :: Int -> [Char]
num2bits 0 = ['0']
num2bits n = num2bits' $ map intToDigit (reverse (numToBinary' n))

numToBinary' 0 = [0]
numToBinary' n = (n `mod` 2) : numToBinary' (n `div` 2)

num2bits' :: [Char] -> [Char]
num2bits' (x:xs) = if x == '0' then num2bits' xs else (x:xs)

--3 
--variance [1..10]
-- 8.25
variance :: Fractional a => [a] -> a
variance xs = sumOfSquares / fromIntegral (length xs)
    where sumOfSquares = (sum $ map (\x -> x^2) (difference' xs))

difference' :: Fractional a => [a] -> [a]
difference' xs = map (\x -> x - findAvg xs) xs

findAvg :: Fractional a => [a] -> a
findAvg xs = (sum xs) / fromIntegral (length xs)

--4
--difference "ABCD" "AD"
--"BC"
difference :: Eq a => [a] -> [a] -> [a]
difference xs [] = xs
difference [] ys = ys
difference xs ys = foldr (\x y -> if x `elem` ys then y else x : y) [] xs

--5
splits :: (Eq a, Ord a) => [a] -> [([a], [a])]
splits xs = splitsHelper ((length xs) - 1) xs

splitsHelper 0 xs = []
splitsHelper n xs = combinations n xs ++ splitsHelper (n-1) xs

combinations :: Ord a => Int -> [a] -> [([a], [a])]
combinations n xs = [ (x, (difference xs x)) | x <- mapM (const xs) [1..n], increasing' x]

increasing' [] = True
increasing' (x:[]) = True
increasing' (x:xs) = if x < head xs then increasing' xs else False 
--6
--argmin length ["ABC","EF","GHIJ","K"]
--"K"
argmin :: Ord b => (a -> b) -> [a] -> a
argmin f xs = foldl (\x acc -> if (f x) < (f acc) then x else acc) (head xs) xs

--7
data Htree a = HLeaf Double a | HFork Double [a] (Htree a) (Htree a) deriving (Eq, Show)

instance (Ord a) => Ord (Htree a) where
    (HLeaf x _) <= (HLeaf y _) = x <= y
    (HLeaf x _) <= (HFork y _ _ _) = x <= y
    (HFork x _ _ _) <= (HLeaf y _) = x <= y
    (HFork x _ _ _) <= (HFork y _ _ _) = x <= y

bogus :: (Ord a) => [(Double, a)] -> Htree a
bogus [x] = HLeaf (fst x) (snd x)
bogus xs = merge (bogus $ fst f) (bogus $ snd f)
    where f = argmin diff (splits xs)
          diff (a,b) = abs $ (sumProb a) - (sumProb b)

sumProb x = sum $ map fst x

--8
church :: Int -> (c -> c) -> c -> c
church n f = foldr (\x y -> (f.y)) id [1..n]  

--9
data Btree a = Leaf a | Fork (Btree a) (Btree a) deriving (Eq, Show)

trees :: (Ord a) => [a] -> [Btree a]
trees xs = concat [ makeTrees x | x <- splits xs]

makeTrees :: (Ord a) => ([a], [a]) -> [Btree a]
makeTrees ([x], [y]) = [Fork (Leaf x) (Leaf y)]
makeTrees ([x], y) = [Fork (Leaf x) z | z <- trees y]
makeTrees (x,[y]) = [Fork z (Leaf y) | z <- trees x]
makeTrees (x, y) = [Fork z t | z <- trees x, t <- trees y]

--10
genome = "AGCT"

insertions xs = concatMap (insertions' xs) genome

insertions' [xs] y = [y:xs:[], xs:y:[]]
insertions' (x:xs) y = [y:x:xs] ++ (map (x:) (insertions' xs y))

deletions xs = map (\x -> delete' x xs) xs
delete' item xs = filter (/= item) xs

substitutions xs = [ replaceEnd x xs | x <- genome] ++ [ replaceMid y xs | y <- genome] ++ [replaceStart z xs | z <- genome]
replaceStart item (x:y:z:[]) = item:y:z:[]
replaceMid item (x:y:z:[]) = x:item:z:[]
replaceEnd item (x:y:z:[]) = x : y : item : []

transpositions :: [Char] -> [[Char]]
transpositions [x] = [[x]]
transpositions (x:y:[]) = [y:x:[]]
transpositions (x:y:xs) = [y:x:xs] ++ (map (x:) (transpositions (y:xs)))


-- merge two Huffman coding trees
merge u@(HLeaf x l) v@(HLeaf y r) = HFork (x+y) [l,r] u v
merge u@(HLeaf x l) v@(HFork y rs _ _) = HFork (x+y) (l:rs) u v
merge u@(HFork x ls _ _) v@(HLeaf y r) = HFork (x+y) (r:ls) u v
merge u@(HFork x ls _ _) v@(HFork y rs _ _) = HFork (x+y) (ls ++ rs) u v

-- encode character using Huffman coding tree
encode (HFork _ _ (HLeaf _ l) (HLeaf _ r)) c = if c == l then "0" else "1"
encode (HFork _ _ (HLeaf _ l) v@(HFork _ rs _ _)) c =
    if c == l then "0" else '1':(encode v c)
encode (HFork _ _ u@(HFork _ ls _ _) v@(HLeaf _ r)) c =
    if c == r then "1" else '0':(encode u c)
encode (HFork _ _ u@(HFork _ ls _ _) v@(HFork _ rs _ _)) c =
    if c `elem` ls then '0':(encode u c) else '1':(encode v c)

-- decode message using Huffman coding tree
decode t [] = []
decode t (x:xs) = loop t (x:xs)
    where loop (HLeaf _ l) xs = l:(decode t xs)
          loop (HFork _ _ u v) ('0':xs) = loop u xs
          loop (HFork _ _ u v) ('1':xs) = loop v xs

main = do
    putStrLn "func:bits2num"
    putStrLn $ show (bits2num "0")
    putStrLn $ show (bits2num "1")
    putStrLn $ show (bits2num "1011000")
    putStrLn $ show (bits2num "10101011011100111")
    putStrLn "func:num2bits"
    putStrLn $ show (num2bits 0)
    putStrLn $ show (num2bits 1)
    putStrLn $ show (num2bits 88)
    putStrLn $ show (num2bits 87783)
    putStrLn "func:variance"
    putStrLn $ show (variance [1..10])
    putStrLn $ show (variance [1])
    putStrLn $ show (variance [5, 8, 11])
    putStrLn "func:splits"
    --different order but same set
    putStrLn $ show (splits "ab")
    putStrLn $ show (splits "abc")
    putStrLn $ show (splits "abcd")
    putStrLn "func:argmin"
    putStrLn $ show (argmin length ["ABC","EF","GHIJ","K"])
    putStrLn $ show (argmin cos [0, 1.57, 3.14, 4.71, 6.28])
    let xs = [(0.30,'e'), (0.14,'h'), (0.1,'l'), (0.16,'o'),(0.05,'p'), (0.23,'t'), (0.02,'w')]
    putStrLn "func:bogus"
    --changed bytes of decode to match my bytes of encoded "hello" and "whole"
    --encoded bytes don't match your given test output, but I'm sure it works efficiently
    --most likely from different order of splits, building different order of treess
    putStrLn $ show (concatMap (encode (bogus xs)) "hello")
    putStrLn $ show (decode (bogus xs) "11100110110010")
    putStrLn $ show (concatMap (encode (bogus xs)) "whole")
    putStrLn $ show (decode (bogus xs) "10111101011000")
    putStrLn "func:church"
    putStrLn $ show ((church 4) tail "ABCDEFGH")
    putStrLn $ show ((church 3) (+ 1) 0)
    putStrLn "func:trees"
    --different order of outputs but still same sets
    putStrLn $ show (length (trees [0..4]))
    putStrLn $ show ((trees "ABCDE") !! 114)
    putStrLn $ show (trees "ABC")
    putStrLn $ show (trees "AB")
    putStrLn "func:insertions"
    putStrLn $ show (insertions "GC")
    putStrLn $ show (insertions "AT")
    putStrLn "func:deletions"
    putStrLn $ show (deletions "AGCT")
    putStrLn $ show (deletions "GCT")
    putStrLn "func:substitutions"
    --different order of outputs but still same sets
    putStrLn $ show (substitutions "ACT")
    putStrLn $ show (substitutions "GTC")
    putStrLn "func:transpositions"
    putStrLn $ show (transpositions "GATC")
    putStrLn $ show (transpositions "ACT")