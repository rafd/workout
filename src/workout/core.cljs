(ns workout.core
  (:require
    [reagent.core :as r]
    [workout.routine :as routine]
    [workout.speech :refer [speak!]]
    [clojure.string :as str]))

(def duration (* 60 1000))
(def exercise-count 8)

(defonce routine (r/atom nil))

(defn alert-halfway! []
  (speak! "halfway"))

(declare do-exercise!)

(defn switch-to-next-exercise! []
  (when (seq @routine)
    (swap! routine rest)
    (do-exercise!)))

(defn do-exercise! []
  (when-let [exercise (routine/current-exercise @routine)]
    (speak! (:name exercise))
    (js/setTimeout alert-halfway! (/ duration 2))
    (js/setTimeout switch-to-next-exercise! duration)))

(defn start! []
  (reset! routine (routine/make-routine {:exercise-count exercise-count}))
  (do-exercise!))

(defn app-view []
  (cond
    (routine/has-ended? @routine)
    [:div
     [:div "GOOD JOB"]
     [:button {:on-click #(start!)} "restart"]]

    (routine/has-started? @routine)
    (let [exercise (routine/current-exercise @routine)
          filepath (str "/exercises/" (exercise :filename))]
      [:div
       [:div (exercise :name)]
       (cond
         (str/ends-with? filepath ".mp4")
         [:video
          {:src filepath
           :auto-play true
           :muted true
           :loop true}]
         (str/ends-with? filepath ".gif")
         [:img {:src filepath}])])

    :else [:button {:on-click #(start!)} "start"]))
