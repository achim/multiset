(ns multiset.t_core
  (:require [multiset.core :as ms]
            [midje.sweet :refer :all])
  (:import [multiset.core MultiSet]))


(fact "get/invoke works like partial identity"
      (let [x1 (Object.)
            x2 (Object.)
            a (ms/multiset x1 x2 4 5 4 x2 7)]
        (get a x1) => x1
        (get a x2) => x2
        (get a 23) => nil
        (a x1) => x1
        (a x2) => x2
        (a 23) => nil))

(let [a (ms/multiset 1 2 4 5 4 2 7)]
  (fact "contains works correctly"
        (contains? a 0) => falsey
        (contains? a 2) => truthy
        (contains? a 7) => truthy)
  
  (fact "disj works correctly"
        (contains? (disj a 7) 7) => falsey
        (contains? (disj a 2) 2) => truthy
        (contains? (-> a (disj 2) (disj 2)) 2) => falsey))

(fact "conj works correctly"
      (let [e (ms/multiset)]
        (contains? (conj e 1) 1) => truthy
        (contains? (-> e (conj 1) (conj 1)) 1) => truthy
        (contains? (-> e (conj 1) (conj 1)) (disj 1)) => truthy
        (contains? e 1) => falsey))

(fact "count works correctly"
      (count (ms/multiset)) => 0
      (count (ms/multiset 1)) => 1
      (count (ms/multiset 1 2 1)) => 3)

(fact "value-based equality"
      (let [a (ms/multiset 1 2 2 4 7)
            b (ms/multiset 4 2 2 7 1)]
        (= a b) => truthy))

(fact "seq retains falsey values"
      (let [a (ms/multiset nil nil false false)]
        (apply ms/multiset (seq a)) => a))

(fact "empty multiset is a multiset"
      (instance? MultiSet (empty (ms/multiset))))

(fact "multiplicites are counted correctly"
      (let [a (ms/multiset 2 1 3 2 3 3)]
        (ms/multiplicities a) => {1 1 2 2 3 3}))

(fact "multiplicities are returned correctly"
      (let [a (ms/multiset 4 4 4 4 5)]
        (ms/multiplicity a 4) => 4
        (ms/multiplicity a 7) => 0))

(let [a (ms/multiset 1 2 4 5 4 2 7)
      b (ms/multiset 4 5 6 6 9)]

  (fact "intersections work"
        (ms/intersect a (conj b 4)) => (ms/multiset 5 4 4))

  (fact "unions work"
      (ms/union a b) => (ms/multiset 1 2 2 4 4 5 6 6 7 9))

  (fact "sums work"
      (ms/sum a b) => (ms/multiset 1 2 2 4 4 4 5 5 6 6 7 9))

  (fact "minuses work"
      (ms/minus a b) => (ms/multiset 7 4 2 2 1))

  (fact "scaling multiplicites"
      (ms/scale a 3) => (ms/multiset 7 7 7 5 5 5 4 4 4 4 4 4 2 2 2 2 2 2 1 1 1)
      (ms/scale a 0) => (ms/multiset)
      (ms/scale a -1) => nil)

  (fact "we can do cartesian products"
        (ms/cartprod a b) => (ms/multiset
                               [5 4] [7 6] [7 6] [4 4] [4 4] [5 5] [4 5] [4 5]
                               [5 6] [5 6] [2 4] [2 4] [4 6] [4 6] [4 6] [4 6]
                               [7 9] [2 5] [2 5] [1 4] [2 6] [2 6] [2 6] [2 6]
                               [5 9] [1 5] [4 9] [4 9] [1 6] [1 6] [2 9] [2 9]
                               [1 9] [7 4] [7 5])))

(fact "subset? is correct"
      (ms/subset? (ms/multiset 1 2 3) (ms/multiset 1 2 2 3 3 4)) => truthy
      (ms/subset? (ms/multiset 1 2 2 2) (ms/multiset 1 2 2 3 3 4)) => falsey
      (ms/subset? #{1 2 3} (ms/multiset 1 2 2 3 3 4)) => truthy
      (ms/subset? (ms/multiset 1 2 2) #{1 2 3}) => falsey
      (ms/subset? (ms/multiset 1 2) #{1 2}) => truthy)

(fact "i can haz meta"
      (let [m {:foo :bar}]
        (meta (with-meta (ms/multiset) m)) => m))

(fact "empty retains meta"
      (let [m {:foo :bar}]
        (meta (empty (with-meta (ms/multiset) m))) => m))

(fact "vec works"
      (vec (ms/multiset)) => []
      (vec (ms/multiset 9 9)) => [9 9])

(fact ".toArray works"
      (seq (.toArray (ms/multiset))) => nil
      (seq (.toArray (ms/multiset 42 42))) => [42 42])
