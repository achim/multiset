(ns multiset.core
  "A simple multiset/bag implementation for Clojure."
  (:require [clojure.algo.generic.functor :as fu])
  (:import (java.util Collection)
           (clojure.lang IPersistentMap)))

(declare empty-multiset)

(defprotocol Multiplicities
  (multiplicities [this]))

(deftype MultiSet [^IPersistentMap m
                   ^IPersistentMap t
                   ^int size]

  clojure.lang.IPersistentSet ;----------
  (get [this x]
    (if-let [e (find t x)]
      (key e)))
  (contains [this x]
    (boolean (find t x)))
  (disjoin [this x]
    (let [oldcount (get t x)]
      (if (not oldcount)
        this
        (MultiSet.
          m
          (if (== 1 oldcount)
            (dissoc t x)
            (assoc t x (dec oldcount)))
          (dec size)))))

  clojure.lang.IPersistentCollection ;----------
  (cons [this x]
    (MultiSet.
      m
      (assoc t x (inc (get t x 0)))
      (inc size)))
  (empty [this] (with-meta empty-multiset m))
  (equiv [this x] (.equals this x))

  clojure.lang.Seqable ;----------
  (seq [this]
    (if-let [entry (first (seq t))]
      (let [k (key entry)]
        (lazy-seq (cons k (.seq (.disjoin this k)))))))

  clojure.lang.Counted ;----------
  (count [this] size)

  clojure.lang.IMeta ;----------
  (meta [this] m)

  clojure.lang.IObj ;----------
  (withMeta [this m]
    (MultiSet. m t size))

  Object ;----------
  (equals [this x]
    (if (instance? MultiSet x)
      (.equals t (.t ^MultiSet x))
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

  Collection ;----------
  (isEmpty [this]
    (zero? size))
  (size [this] size)
  (toArray [this a]
    (.toArray ^Collection (or (seq this) ()) a))
  (toArray [this]
    (.toArray ^Collection (or (seq this) ())))
  (iterator [this]
    (.iterator ^Collection (or (seq this) ())))
  (containsAll [this coll]
    (.containsAll ^Collection (into #{} this) coll))

  Multiplicities ;----------
  (multiplicities [this] t))

(def ^:private empty-multiset (MultiSet. nil {} 0))

(defn multiset
  "Create a multiset with given elements."
  [& xs] (into empty-multiset xs))

(defn multiplicities->multiset
  "Create a multiset from a given multilicities map
  (see 'multiplicities')."
  [t] (let [mults (into {} (for [[k v] t :when (pos? v)] [k v]))
            size  (reduce + (vals mults))]
        (MultiSet. nil mults size)))

(defn multiset?
  "Return true if x is a multiset, false otherwise."
  [x] (instance? MultiSet x))

(defn multiplicity
  "Return the multiplicity of element x in m, 0 if x is not present."
  [m x] (get (multiplicities m) x 0))

(defn ^:private mults [coll]
  (if (multiset? coll)
    (multiplicities coll)
    (reduce #(assoc %1 %2 (inc (get %1 %2 0))) {} coll)))

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

(def ^{:arglists '([a b])} intersect
  "Return the intersection of a and b as a multiset."
  (msetop (fn [a b] a)
          #(min (get %1 %3 0) (get %2 %3 0))))

(def ^{:arglists '([a b])} union
  "Return the union of a and b as a multiset."
  (msetop #(-> #{} (into %1) (into %2))
          #(max (get %1 %3 0) (get %2 %3 0))))

(def ^{:arglists '([a b])} cartprod
  "Return the cartesian product of a and b as a multiset."
  (msetop (fn [a b] (mapcat #(map (fn [x] (vector %1 x)) b) a))
          (fn [a b [x y]] (* (get a x 0) (get b y 0)))))

(def ^{:arglists '([a b])} sum
  "Return the multiset sum of a and b as a multiset."
  (msetop #(-> #{} (into %1) (into %2))
          #(+ (get %1 %3 0) (get %2 %3 0))))

(def ^{:arglists '([a b])} minus
  "Return the difference a-b of a and b as a multiset."
  (msetop (fn [a b] a)
          #(max 0 (- (get %1 %3) (get %2 %3 0)))))

(defn scale
  "Return a multiset in which the multiplicity of each
  element in m is scaled by factor k."
  [m k]
  (when-not (neg? k)
    (multiplicities->multiset (fu/fmap #(* k %) (multiplicities m)))))

(defn subset?
  "Return true, if a is a subset of b."
  [a b]
  (let [a (mults a)
        b (mults b)]
    (reduce #(and %1 (<= (get a %2) (get b %2 0))) true (keys a))))
