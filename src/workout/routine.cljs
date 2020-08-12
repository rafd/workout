(ns workout.routine
  (:require
    [reagent.core :as r]
    [workout.exercises :refer [exercises]]))

(defonce state
  (r/atom {}))

(defn create-routine! []
  (swap! state assoc :sequence (shuffle exercises)))

(defn current-exercise []
  (last (@state :sequence)))

(defn next-exercise! []
  (if (seq (@state :sequence))
    (swap! state update :sequence pop)
    nil))

(defn has-started []
  (not (= (@state :sequence) nil)))

(defn has-ended []
  (= (@state :sequence) []))