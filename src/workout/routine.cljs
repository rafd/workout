(ns workout.routine
  (:require
    [clojure.set :as set]
    [reagent.core :as r]
    [workout.exercises :refer [exercises]]))

(defn make-routine [exercise-count]
  (->> (range exercise-count)
       (reduce (fn [memo _]
                 (if (empty? (memo :available-exercises))
                   (reduced memo)
                   (let [previous-exercise-tags (or (:tags (last (memo :routine))) #{})
                         next-exercise (or (->> (memo :available-exercises)
                                                (filter (fn [exercise]
                                                          (empty? (set/intersection
                                                                    (exercise :tags)
                                                                    previous-exercise-tags))))
                                                (shuffle)
                                                (first))
                                         (rand-nth (vec (memo :available-exercises))))]
                     (-> memo
                         (update :available-exercises disj next-exercise)
                         (update :routine conj next-exercise)))))
         {:available-exercises exercises :routine []})
       :routine))
