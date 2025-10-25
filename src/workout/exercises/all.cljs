(ns workout.exercises.all
  (:require
    [workout.exercises.bodyweight]
    [workout.exercises.exercisedb]))

(def all
  (concat
    workout.exercises.bodyweight/exercises
    (workout.exercises.exercisedb/exercises)))

(def by-name
  (zipmap (map :exercise/name all) all))
