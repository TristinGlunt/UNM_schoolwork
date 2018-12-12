data Jtree a = JLeaf a | JFork (Jtree a) (Jtree a) deriving (Show, Eq)


instance Functor Jtree where
    fmap f (JLeaf a) = (JLeaf (f a))
    fmap f (JFork a b) = JFork (fmap f a) (fmap f b)

jMap f u@(JLeaf a) = fmap f u

makeJTree :: [a] -> Jtree a
makeJTree [x] = JLeaf x
makeJTree xs = JFork (makeJTree lo) (makeJTree hi)
    where hi = drop ((length xs) `div` 2) xs
          lo = take ((length xs) `div` 2) xs

foldJtree f g (JLeaf x) = f x
foldJtree f g (JFork xt yt) = g (foldJtree f g xt) (foldJtree f g yt)

foldr' f seed [] = seed
foldr' f seed (x:xs) = f x (foldr f seed xs)

sizeJtree xs = foldJtree (const 1) (+) xs

sumJtree xs = foldJtree (\x -> x) (+) xs

--group "Mississippi" => ["M", "i", "ss", "i", "ss", "i", "pp", "i"]
--group xs = map (\x -> filter (== x) xs) xs

-- myTakeWhile (/= ’ ’) "This is practice."
-- => "This"
myTakeWhile f xs = foldr (\x y -> if f x then x : y else []) [] xs

myTakeRest f xs = drop (length (myTakeWhile f xs)) xs

mySpan f xs = (myTakeWhile f xs, myTakeRest f xs)

group :: Eq a => [a] -> [[a]]
group [] = []
group (x:xs) = fst z : group (snd z)
    where z = mySpan (== x) (x:xs)

transpose' [] = []
transpose' xs = [[x | x <- (map head xs)]] ++ transpose' (filter (\x -> x /= []) (map tail xs))

uncons :: [a] -> Maybe (a, [a])
uncons [] = Nothing
uncons (x:xs) = Just (x, xs)

iterate' :: (a -> a) -> a -> [a]
iterate' f seed = seed : iterate f (f seed)

mult x y = iterate (+ x) 0 !! (y-1)

foldl' f acc [] = acc
foldl' f acc (x:xs) = foldl' f (f acc x) xs

partitions f xs = (filter f xs, filter (not . f) xs)