import Data.List
--Data List functions

myTakeWhile :: (a -> Bool) -> [a] -> [a]
myTakeWhile f [] = []
myTakeWhile f (x:xs) = if f x then x : myTakeWhile f xs else []

myTakeRest :: (a -> Bool) -> [a] -> [a]
myTakeRest f [] = []
myTakeRest f (x:xs) = if f x then myTakeRest f xs else x : xs

zip' :: [a] -> [b] -> [(a, b)]
zip' [] [] = []
zip' [] ys = []
zip' xs [] = []
zip' (x:xs) (y:ys) = (x,y): zip' xs ys

nub' :: Eq a => [a] -> [a]
nub' [] = []
nub' (x:xs) = foldr (\x y -> if elem x y then y else x : y) [] (x:xs)

union' :: Eq a => [a] -> [a] -> [a]
union' [] ys = ys 
union' (x:xs) ys = if elem x ys then x : union' xs (delete' x ys) else x : union' xs ys

delete' :: Eq a => a -> [a] -> [a]
delete' item [] = []
delete' item xs = filter (/= item) xs 

intersect' :: Eq a => [a] -> [a] -> [a]
intersect' [] ys = []
intersect' (x:xs) ys = if elem x ys then x : intersect' xs ys else intersect' xs ys

--uncurry will take in a list of pairs and apply the given function to that list of pairs
dot' :: Num a => [a] -> [a] -> a
dot' xs ys = sum $ map (uncurry (*)) (zip' xs ys)

foldr' :: (t1 -> t -> t) -> t -> [t1] -> t
foldr' f seed [] = seed
foldr' f seed (x:xs) = f x (foldr f seed xs)

map' :: (a -> b) -> [a] -> [b]
map' f xs = foldr (\x y -> f x : y) [] xs

filter' :: Foldable t => (a -> Bool) -> t a -> [a]
filter' f xs = foldr (\x y -> if f x then x : y else y) [] xs

-- inits "abc" = ["", "a", "ab", "abc"]
inits' :: [a] -> [[a]]
inits' xs = reverse $ initsHelper xs

initsHelper :: [a] -> [[a]]
initsHelper [] = [[]]
initsHelper xs = xs : initsHelper (init xs)

-- group "Mississippi" = ["M", "i", "ss", "i", "ss", "i", "pp", "i"]
group' :: Eq a => [a] -> [[a]]
group' [] = []
group' (x:xs) = fst stuff : group (snd stuff)
                where stuff = mySpan (== x) (x:xs)

mySpan :: (a -> Bool) -> [a] -> ([a], [a])
mySpan f xs = (myTakeWhile f xs, myTakeRest f xs)

-- Higher order functions -- 
-- Compose, Flip, Curry, Uncurry, Map, Filter, Fold
-- Filter & map => fold 

-- Testing flipTuple (x,y) = uncurry (flip) (x,y)
reverse' xs = foldr (\x y -> y ++ [x]) [] xs

reverse'' xs = foldl (flip (:)) [] xs

foldl'' _ acc [] = acc
foldl'' f acc (x:xs) = foldl'' f (f acc x) xs


--need to go over more
outerProduct f xs ys = map (\x -> (map (\y -> f x y) ys)) xs

factors n = [(x,y) | x <- [1..n], y <- [1..n], x*y == n]

primes n = [x | x <- [1..n], ((== 2) . length) (factors x)]

church' f n = foldr (\x y -> (f . y)) id [1..n]

--HW4 with higher orders/list comprehensions

compose' :: (b -> c) -> (a -> b) -> a -> c
compose' f g x = f (g (x))

stutter :: [a] -> [a]
stutter xs = foldr (\x y -> x : x : y) [] xs

compress :: Eq a => [a] -> [a]
compress xs = foldr (\x y -> if x `elem` y then y else x : y) [] xs

findIndices :: (a -> Bool) -> [a] -> [Int]
findIndices f xs = [ snd x | x <- zip xs [0..(length xs)-1], f (fst x)]

intersect'' :: Eq a => [a] -> [a] -> [a]
intersect'' xs ys = foldr (\x y -> if x `elem` ys then x : y else y) [] xs

isPrefixOf' :: Eq a => [a] -> [a] -> Bool
isPrefixOf' xs ys = isPrefixOfHelper xs (take x ys)
    where x = length xs

isPrefixOfHelper [] [] = True
isPrefixOfHelper (x:xs) (y:ys) = if x == y then isPrefixOfHelper xs ys else False

isSuffixOf' :: Eq a => [a] -> [a] -> Bool
isSuffixOf' xs ys = isSuffixOfHelper xs (drop n ys)
    where n = length xs

isSuffixOfHelper [] [] = True
isSuffixOfHelper (x:xs) (y:ys) = if x == y then isSuffixOfHelper xs ys else False

length' xs = foldl'' (\acc _ -> 1 + acc) 0 xs

partitions' f xs = (filter f xs, filter (not . f) xs)


quickSort [] = []
quickSort [x] = [x]
quickSort (x:xs) = quickSort lo ++ [x] ++ quickSort hi
    where (lo, hi) = partitions' (< x) xs

intersect''' [] ys = []
intersect''' (x:xs) ys = if elem x ys then x : intersect''' xs ys else intersect''' xs ys

factors' n = [(x,y) | x <- [1..n], y <- [1..n], x*y == n]

primes' n = [x | x <- [1..n], ((== 2) . length) (factors x)]

div' :: Int -> Int -> Int
div' x y = x `div` y

merge' [] ys = ys
merge' xs [] = xs
merge' us@(x:xs) vs@(y:ys) = if x < y then x : merge' xs vs else y : merge' us ys

mergeSort' [] = []
mergeSort' [x] = [x]
mergeSort' xs = mergeSort' (evens xs) `merge'` mergeSort' (odds xs)

odds [] = []
odds (x:xs) = evens xs

evens [] = []
evens (x:xs) = x : (odds xs)

wrap xs = [ [ x ] | x <- xs ]

size :: [a] -> Int
size xs = foldr (+) 0 $ [ 1 | _ <- xs ] 

flatten [] = []
flatten (x:xs) = x ++ flatten xs

combine f g = f . g

emplace item [ ] = item : [ ]
emplace item (x:xs) = if item <= x then item : x : xs else x : emplace item xs

mapConcat proc xs = flatten $ map proc xs

increment x = x + 1

contains item [ ] = False
contai  ns item (x:xs) = if x == item then True else contains item xs

iter times pred start = if times == 0 then start else iter (times - 1) pred (pred start)

add x y = iter y (+1) x

--everything for unzip3

uno (x, y, z) = x

dos (x, y, z) = y

tres (x, y, z) = z

groupFirst xs = map (\x -> uno x) xs

groupMiddle xs = map (\x -> dos x) xs

groupLast xs = map (\x -> tres x) xs


--groupFirst xs = uno (head xs) : uno (head (tail xs)) : uno (head (tail (tail xs))) : []

--groupMiddle xs = dos (head xs) : dos (head (tail xs)) : dos (head (tail (tail xs))) : []

--groupLast xs = tres (head xs) : tres (head (tail xs)) : tres (head (tail (tail xs))) : []

testStuff xs = (groupFirst xs, groupMiddle xs, groupLast xs)

unzip3' xs = foldr (\(a,b,c) (as, bs, cs) -> (a:as, b:bs, c:cs)) ([],[],[]) xs

compress'' [] = []
compress'' (x:[]) = [x]
compress'' (x:xs) = if x == head xs then compress'' xs else x : compress'' xs

--compress, unzip3, nub, 

concatMap' f = foldr ((++) . f) []