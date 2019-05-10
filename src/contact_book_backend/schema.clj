(ns contact_book_backend.schema
  "Contains custom resolvers and a function to provide the full schema."
  (:require
    [clojure.java.io :as io]
    [com.walmartlabs.lacinia.util :as util]
    [com.walmartlabs.lacinia.schema :as schema]
    [clojure.edn :as edn]))

(defn resolve-contact-by-id
  [contacts-map context args value]
  (let [{:keys [id]} args]
    (get contacts-map id)))

(defn resolver-map
  []
  (let [cb-data (-> (io/resource "cb-data.edn")
                    slurp
                    edn/read-string)
        contacts-map (->> cb-data
                          :contacts
                          (reduce #(assoc %1 (:id %2) %2) {}))]
    {:query/contact_by_id (partial resolve-contact-by-id contacts-map)}))

(defn load-schema
  []
  (-> (io/resource "cb-schema.edn")
      slurp
      edn/read-string
      (util/attach-resolvers (resolver-map))
      schema/compile))
