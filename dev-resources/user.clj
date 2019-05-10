(ns user
  (:require
    [contact_book_backend.schema :as s]
    [com.walmartlabs.lacinia :as lacinia]
    [clojure.walk :as walk])
  (:import (clojure.lang IPersistentMap)))

(def schema (s/load-schema))

(defn simplify
  "Converts all ordered maps nested within the map into standard hash maps, and
   sequences into vectors, which makes for easier constants in the tests, and eliminates ordering problems."
  [m]
  (walk/postwalk
    (fn [node]
      (cond
        (instance? IPersistentMap node)
        (into {} node)

        (seq? node)
        (vec node)

        :else
        node))
    m))

(defn q
  [query-string]
  (-> (lacinia/execute schema query-string nil nil)
      simplify))

(comment (q "{ contact_by_id(id: \"foo\") { id name company_name }}")
         (q "{ contact_by_id(id: \"1\") { id name company_name phones { phone contact { id name } }}}"))