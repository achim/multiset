# multiset

A simple multiset/bag implementation.

## Usage

### Installation via Leiningen

Add this to your `:depependencies`:

```clojure
[org.clojars.achim/multiset "0.1.0-SNAPSHOT"]
```

### Example usage

#### Define some multisets
```clojure
user=> (def a (ms/multiset 1 2 4 5 4 2 7))
#'user/a
user=> (def b (ms/multiset 4 5 6 6 9))
#'user/b
user=> (def c (ms/multiset 1 2 4 7))
#'user/c
```
#### Basic functionality
```clojure
user=> a
#{7 5 4 4 2 2 1}
user=> (contains? a 3)
false
user=> (contains? a 2)
true
user=> (disj a 2)
#{7 5 4 4 2 1}
user=> (conj a 4)
#{7 5 4 4 4 2 2 1}
user=> (a 2)
```
#### Multiset-specific stuff
```clojure
user=> (ms/multiset? a)
true
user=> (ms/multiplicities a)
{7 1, 5 1, 4 2, 2 2, 1 1}
user=> (ms/multiplicity a 3)
0
user=> (ms/multiplicity a 2)
2
```
#### Multiset operators
```clojure
user=> (ms/intersect a (conj b 4))
#{5 4 4}
user=> (ms/union a b)
#{1 2 2 4 4 5 6 6 7 9}
user=> (ms/sum a b)
#{1 2 2 4 4 4 5 5 6 6 7 9}
user=> (ms/minus a b)
#{7 4 2 2 1}
user=> (ms/scale a 3)
#{7 7 7 5 5 5 4 4 4 4 4 4 2 2 2 2 2 2 1 1 1}
user=> (ms/cartprod a b)
#{[5 4] [7 6] [7 6] [4 4] [4 4] [5 5] [4 5] [4 5] [5 6] [5 6] [2 4] [2 4] [4 6] [4 6] [4 6] [4 6] [7 9] [2 5] [2 5] [1 4] [2 6] [2 6] [2 6] [2 6] [5 9] [1 5] [4 9] [4 9] [1 6] [1 6] [2 9] [2 9] [1 9] [7 4] [7 5]}
user=> (ms/subset a b)
false
user=> (ms/subset c a)
true
user=> (ms/subset (ms/multiset 2 2 2) a)
false
```

## License

Copyright (C) 2012 Achim Passen

Distributed under the Eclipse Public License. See COPYING.
