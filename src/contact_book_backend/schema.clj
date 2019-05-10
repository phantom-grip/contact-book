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

(defn entity-map
  [data k]
  (reduce #(assoc %1 (:id %2) %2)
          {}
          (get data k)))

(defn resolve-contact-phones
  [phones-map context args contact]
  (->> contact
       :phones
       (map phones-map)))

(defn resolve-phone-contact
  [contacts-map context args phone]
  (let [{:keys [id]} phone]
    (->> contacts-map
         vals
         (filter #(-> % :phones (contains? id)))
         first)))

(defn resolver-map
  []
  (let [cb-data (-> (io/resource "cb-data.edn")
                    slurp
                    edn/read-string)
        contacts-map (entity-map cb-data :contacts)
        phones-map (entity-map cb-data :phones)]
    {:query/contact_by_id (partial resolve-contact-by-id contacts-map)
     :Contact/phones (partial resolve-contact-phones phones-map)
     :Phone/contact (partial resolve-phone-contact contacts-map)}))



(defn load-schema
  []
  (-> (io/resource "cb-schema.edn")
      slurp
      edn/read-string
      (util/attach-resolvers (resolver-map))
      schema/compile))
