data RoseTree a = Node a [RoseTree a] deriving (Show,Eq)

foldRoseTree f (Node x []) = [x]
foldRoseTree f (Node x xs) = x : (f $ map (foldRoseTree f xs) xs)