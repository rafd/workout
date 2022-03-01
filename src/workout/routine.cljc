(ns workout.routine
  (:require
    [clojure.set :as set]
    [reagent.core :as r]
    [workout.exercises :refer [exercises]]))

(def max-stress 3.5)

;; stress is a map of tag -> intensity
;; ex. {:arms 3 :abs 2}

(defn ->stress [exercise]
  (zipmap (:tags exercise)
          (repeat (:intensity exercise))))

(defn +stress [stress1 stress2]
 (merge-with + stress1 stress2))

(defn dec-stress [stress]
 (->> stress
      (map (fn [[k v]] [k (Math/max 0.0 (- v 0.5))]))
      (into {})))

(defn under-threshold? [stress max-stress]
  (every? (fn [s] (<= s max-stress)) (vals stress)))

(defn take-fraction [n coll]
  (take (Math/max 0 (quot (count coll) n))
        coll))

(defn make-routine
  [exercise-count]
  (->> (range exercise-count)
       ;; as we choose each exercise, keep track of the stress on the various body parts
       ;; add stress based on intensity of each exercise
       ;; decrement stress by 1 after each exercise
       ;; choose next exercise so that every stress is below a max
       (reduce (fn [memo _]
                 (if (empty? (memo :available-exercises))
                   (reduced memo)
                   (let [next-exercise (or
                                           (->> (memo :available-exercises)
                                                (filter (fn [exercise]
                                                          (under-threshold?
                                                            (+stress (:stress memo)
                                                                     (->stress exercise))
                                                            max-stress)))
                                                (sort-by :intensity)
                                                reverse
                                                (take-fraction 4)
                                                shuffle
                                                first)
                                         (do (println "had to resort to random")
                                             (rand-nth (vec (memo :available-exercises)))))]
                     (-> memo
                         (update :available-exercises disj next-exercise)
                         (update :routine conj next-exercise)
                         (update :stress dec-stress)
                         (update :stress +stress (->stress next-exercise))))))
         {:available-exercises exercises
          :routine []
          :stress {}})
       :routine))

#_(make-routine 8)
