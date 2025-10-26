(ns workout.exercises.all
  (:require
   [clojure.string :as string]
   [workout.exercises.bodyweight :as bodyweight]
   [workout.exercises.custom :as custom]
   [workout.exercises.exercisedb :as exercisedb]))

(def all
  (concat
   bodyweight/exercises
   custom/exercises
   (exercisedb/exercises)))

(def indexed
  (zipmap (map (comp string/lower-case :exercise/name) all)
          all))

(defn by-name
  [name]
  (get indexed (string/lower-case name)))
