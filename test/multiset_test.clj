(ns multiset-test
    (:require [multiset.core :refer :all]
              [clojure.test :refer :all]))

(deftest ms-union-should-add-multiplicities
  (are [expected left right]
    (= expected (union left right)) 
    (multiset 1 1 )     (multiset 1)    (multiset 1)
    (multiset 1 2 )     (multiset 1)    (multiset 2)
    (multiset 2 1 )     (multiset 2)    (multiset 1)
    (multiset 1 1 2 )   (multiset 1 2)  (multiset 1)
    (multiset 1 1 2 )   (multiset 1)    (multiset 1 2)
    (multiset 1 1 1 2 2 2 )   (multiset 1 2 2) (multiset 1 1 2)
  ))