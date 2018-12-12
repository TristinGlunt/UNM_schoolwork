-- Tristin Glunt
-- Homework 4
-- tglunt@unm.edu


--1
-- stutter "Hello World"
-- =>"HHeelllloo WWoorrlldd"
--stutter [1,2,3]
-- => [1,1,2,2,3,3]
stutter [] = []
stutter (x:xs) = x : x : stutter (xs)

--stutter' = foldr (\x -> x ++ [x,x]) []

--2
--compress "HHeelllloo WWoorrlldd"
-- =>"Helo World"
--compress [1,2,2,3,3,3]
-- => [1,2,3]
compress [] = []
compress [x] = [x]
compress (x:xs) = if x == head xs then compress xs else x : (compress xs)

--3
-- findIndices (< ’a’) "AbCdef"
-- => [0, 2]
-- *Main> findIndices (== 0) [1,2,0,3,0]
-- => [2, 4]
findIndices p [] = []
findIndices p (x:xs) = select' p (x:xs) (iota [(length (x:xs))])

select' _ [] [] = []
select' pred (x:xs) (y:ys) = if (pred x) then y:rest else rest where rest = select' pred xs ys

iota [] = []
iota [x] = [0..(x-1)]

--  intersect "abc" "cat"
-- => "ac"
-- intersect [1,2,3] [8]
-- => []
-- intersect [3,2,1] [1,2,3]
-- => [3,2,1]
intersect (x:xs) [] = (x:xs)
intersect [] (y:ys) = []
intersect (x:xs) (y:ys) = if (elem x (y:ys)) then x :(intersect xs (y:ys)) else (intersect xs (y:ys))

member item [] = False
member item (x:xs) = (item == x) || (member item xs)

--4
--"foo" ‘isPrefixOf‘ "foobar"
-- => True
-- isPrefixOf [1,2,3] [4,5,6]
-- => False
isPrefixOf (x:xs) [] = False
isPrefixOf (x:xs) (y:ys) = if (buildListToIndex (length (x:xs)) (y:ys)) == (x:xs) then True else False

buildListToIndex 0 (x:xs) = []
buildListToIndex n [] = []
buildListToIndex n x = if (length x) == n then x else (buildListToIndex n (init x))

--5
--"bar" ‘isSuffixOf‘ "foobar"
-- => True
-- isSuffixOf [1,2,3] [4,5,6]
-- => False
isSuffixOf (x:xs) [] = False
isSuffixOf (x:xs) (y:ys) = if (buildListToSuffix (length (x:xs)) (y:ys)) == (x:xs) then True else False

buildListToSuffix 0 (x:xs) = []
buildListToSuffix n [] = []
buildListToSuffix n x = if (length x) == n then x else (buildListToSuffix n (tail x))

--6
--dot [0,0,1] [0,1,0]
-- => 0
dot [] [] = 0
dot (x:xs) [] = 0
dot [] (y:ys) = 0
dot (x:xs) (y:ys) = (x * y) + (dot xs ys)

--dot' :: Num a => [a] -> [a] -> a
--dot' xs ys = sum $ zipWidth (*) xs ys

--7
increasing [] = False
increasing (x:[]) = True
increasing (x:xs) = if x < (head xs) then increasing xs else False

--8
--decimate [1..21]
-- => [1,2,3,4,5,6,7,8,9,11,12,13,14,15,16,17,18,19,21]
decimate [] = []
decimate (x:xs) = (take 9 (x:xs)) ++ (decimate (drop 10 (x:xs)))

--9
--encipher
--encipher [’A’..’Z’] [’a’..’z’] "THIS"
-- [(A,a), (B,b) .. (Y,y), (Z,z)]
-- => this


--10
--prefixSum [1..10]
-- => [1,3,6,10,15,21,28,36,45,55]
-- prefixSum [2, 5]
-- => [2, 7]
prefixSum [] = []
prefixSum xs = prefixSum2 xs 0

prefixSum2 [] acc = []
prefixSum2 (x:xs) acc = (x + acc) : (prefixSum2 xs (x + acc))


--11
--Main> :t select
-- => select :: (t -> Bool) -> [t] -> [a] -> [a]
--Main> select even [1..26] "abcdefghijklmnopqrstuvwxyz"
-- => "bdfhjlnprtvxz"
--Main> select (<= ’g’) "abcdefghijklmnopqrstuvwxyz" [1..26]
-- => [1,2,3,4,5,6,7]

select f xs ys = [ t | (x,t) <- zip xs ys, (f x)]

--12
-- numbers [1,2,3]
-- => 123
numbers [] = 0
numbers xs = numbers2 xs (length xs) 0

numbers2 [] n acc = acc
numbers2 (x:xs) n acc = let stuff = (x * (10^(n-1)) + acc) in
                        (numbers2 xs (n - 1) stuff)
