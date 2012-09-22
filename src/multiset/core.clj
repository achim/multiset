(ns multiset.core
  (:require [clojure.algo.generic.functor :as fu]))

(declare empty-multiset)

(defprotocol Multiplicities
  (multiplicities [this]))

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
  (equiv [this x] (.equals this x))
 
  clojure.lang.Seqable ;----------
  (seq [this]
    (let [k (first (keys t))]
      (if k
        (lazy-seq (cons k (.seq (.disjoin this k)))))))
 
  clojure.lang.Counted ;----------
  (count [this] size)

  Object ;----------
  (equals [this x]
    (if (instance? (class this) x)
      (.equals t (.t x))
      false))
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

  Multiplicities ;----------
  (multiplicities [this] t))

(def ^:private empty-multiset (MultiSet. {} 0))

(defn multiset [& xs]
  (into empty-multiset xs))

(defn multiplicities->multiset [t]
  (let [size (reduce + (vals t))]
    (MultiSet. t size)))

(defn multiset? [x]
  (instance? MultiSet x))

(defn multiplicities [ms]
  (.multiplicities ms))

(defn multiplicity [ms x]
  (get (multiplicities ms) x 0))

(defn ^:private mults [coll]
  (if (multiset? coll)
    (multiplicities coll)
    (into {} (map #(vector % 1) coll))))

(defn ^:private msetop [keysfn multfn]
  (fn op
    ([a b]
      (let [a (mults a)
            b (mults b)
            ks (keysfn (keys a) (keys b))]
        (multiplicities->multiset
          (->> ks (map #(vector % (multfn a b %)))
                  (filter #(> (get % 1) 0))
                  (into {})))))))

(def intersect
  (msetop (fn [a b] a)
          #(min (get %1 %3 0) (get %2 %3 0))))

(def union
  (msetop #(-> #{} (into %1) (into %2))
          #(max (get %1 %3 0) (get %2 %3 0))))

(def cartprod
  (msetop (fn [a b] (mapcat #(map (fn [x] (vector %1 x)) b) a))
          (fn [a b [x y]] (* (get a x 0) (get b y 0)))))

(def sum
  (msetop #(-> #{} (into %1) (into %2))
          #(+ (get %1 %3 0) (get %2 %3 0))))

(def minus
  (msetop (fn [a b] a)
          #(max 0 (- (get %1 %3) (get %2 %3 0)))))

(defn scale [ms n]
  (multiplicities->multiset (fu/fmap #(* n %) (multiplicities ms))))

(defn subset [a b]
  (let [a (mults a)
        b (mults b)
        ks (-> #{} (into (keys a)) (into (keys b)))]
    (reduce #(and %1 (<= (get a %2 0) (get b %2 0))) true ks)))
