(ns multiset.core)

(declare empty-multiset)

(defprotocol Multiplicity
  (multiplicity [this x]))

(deftype MultiSet [^clojure.lang.IPersistentMap t
                   ^int size]

  clojure.lang.IPersistentSet ;----------
  (get [this x]
    (let [v (get t x)]
      (if v x nil)))
  (contains [this x]
    (if (get t x) true false))
  (disjoin [this x]
    (let [oldcount (get t x)]
      (if (not oldcount)
        this
        (MultiSet. 
          (if (== 1 oldcount)
            (dissoc t x)
            (assoc t x (dec oldcount)))
          (dec size)))))
 
  clojure.lang.IPersistentCollection ;----------
  (cons [this x]
    (MultiSet.
      (assoc t x (inc (get t x 0)))
      (inc size)))
  (empty [this] empty-multiset)
  (equiv [this x] (.equals t (.t x)))
 
  clojure.lang.Seqable ;----------
  (seq [this]
    (let [k (first (keys t))]
      (if k
        (lazy-seq (cons k (.seq (.disjoin this k)))))))
 
  clojure.lang.Counted ;----------
  (count [this] size)

  Object ;----------
  (equals [this x]
    (.equals t (.t x)))
  (hashCode [this]
    (hash-combine (hash t) MultiSet))

  clojure.lang.IFn ;----------
  (invoke [this x]
    (.get this x))
  (invoke [this x default]
    (let [r (.get this x)]
      (if r
        r
        default)))

  Multiplicity ;----------
  (multiplicity [this x]
    (get t x 0)))

(def ^:private empty-multiset (MultiSet. {} 0))

(defn multiset [& xs]
  (into empty-multiset xs))

(defn multiplicity [ms x]
  (.multiplicity ms x))

