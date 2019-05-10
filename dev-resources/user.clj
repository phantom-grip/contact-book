(ns user
  (:require
    [contact_book_backend.schema :as s]
    [com.walmartlabs.lacinia :as lacinia]))

(def schema (s/load-schema))

(defn q
  [query-string]
  (lacinia/execute schema query-string nil nil))

(comment (q "{ contact_by_id(id: \"foo\") { id name company_name }}"))