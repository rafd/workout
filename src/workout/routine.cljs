(ns workout.routine
  (:require
    [clojure.set :as set]
    [reagent.core :as r]
    [workout.exercises :refer [exercises]]))

(defn tags []
  (->> exercises
       (mapcat (fn [exercise] (exercise :tags)))
       (set)))

(defn find-exercise [{:keys [tag not-tags exercises]}]
  (->> exercises
    (filter (fn [exercise] (and (contains? (exercise :tags) tag)
                             (empty (set/intersection (exercise :tags) not-tags)))))
    (shuffle)
    (first)))

(defn make-routine [{:keys [exercise-count]}]
  (->> (tags)
       (shuffle)
       (cycle)
       (take exercise-count)
       (reduce (fn [memo tag]
                 (let [previous-exercise (last (memo :routine))
                       next-exercise (or
                                       (find-exercise {:tag tag
                                                       :not-tags (if previous-exercise
                                                                   (previous-exercise :tags)
                                                                   #{})
                                                       :exercises (memo :available-exercises)})
                                       (find-exercise {:tag tag
                                                       :not-tags #{}
                                                       :exercises (memo :available-exercises)})
                                       (rand-nth (vec (memo :available-exercises))))]
                   (-> memo
                       (update :available-exercises disj next-exercise)
                       (update :routine conj next-exercise))))
         {:available-exercises exercises :routine []})
       :routine))

(defn current-exercise [routine]
  (first routine))

(defn has-started? [routine]
  (boolean (seq routine)))

(defn has-ended? [routine]
  (= routine []))
