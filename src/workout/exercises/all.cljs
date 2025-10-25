(ns workout.exercises.all
  (:require
   [workout.exercises.bodyweight]
   [workout.exercises.custom]
   [workout.exercises.exercisedb]))

(def all
  (concat
   workout.exercises.bodyweight/exercises
   workout.exercises.custom/exercises
   (workout.exercises.exercisedb/exercises)))

(def by-name
  (zipmap (map :exercise/name all) all))
