(ns workout.routine
  (:require
    [clojure.set :as set]
    [reagent.core :as r]
    [workout.exercises :refer [exercises]]))

(defn make-routine [exercise-count]
  (->> (range exercise-count)
       (reduce (fn [memo _]
                 (let [previous-exercise (last (memo :routine))
                       next-exercise (->> exercises
                                          (filter (fn [exercise]
                                                    (empty? (set/intersection (exercise :tags)
                                                              (or (:tags previous-exercise)
                                                                  #{})))))
                                          (shuffle)
                                          (first))]
                   (-> memo
                       (update :available-exercises disj next-exercise)
                       (update :routine conj next-exercise))))
         {:available-exercises exercises :routine []})
       :routine))
