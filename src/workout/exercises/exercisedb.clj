(ns workout.exercises.exercisedb
  (:require
   [clojure.data.json :as json]
   [clojure.string :as str]
   [clojure.java.io :as io]))

(defn string->keyword [s]
  (-> s
      str/lower-case
      (str/replace #"\s+" "-")
      keyword))

(defn process-exercises []
  (->> (io/resource "exercises.json")
       slurp
       (json/read-str)
       (mapv (fn [{:strs [name gifUrl equipments
                          targetMuscles bodyParts secondaryMuscles]}]
               {:exercise/name name
                :exercise/equipment equipments
                :exercise/media-url gifUrl
                :exercise/target-muscles (->> (concat targetMuscles bodyParts secondaryMuscles)
                                              (map string->keyword)
                                              set)}))))

#_(process-exercises)

;; expose as macro, so that cljs can use the data at compile time
(defmacro exercises []
  (process-exercises))