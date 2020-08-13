(ns workout.core
  (:require
    [reagent.core :as r]
    [workout.routine :as routine]
    [workout.speech :refer [speak!]]
    [clojure.string :as str]))

(def duration (* 4 1000))
(def rest-duration (* 2 1000))
(def exercise-count 2)

(defonce routine (r/atom nil))
(defonce timers (atom #{}))

(declare do-exercise!)

(defn alert-halfway! []
  (speak! "halfway"))

(defn alert-finished! []
  (swap! routine rest)
  (if (routine/has-ended? @routine)
    (speak! "finished workout")
    (do
      (speak! "rest")
      (swap! timers conj (js/setTimeout do-exercise! rest-duration)))))

(defn do-exercise! []
  (let [exercise (routine/current-exercise @routine)]
    (speak! (:name exercise))
    (swap! timers conj (js/setTimeout alert-halfway! (/ duration 2)))
    (swap! timers conj (js/setTimeout alert-finished! duration))))

(defn start! []
  (reset! routine (routine/make-routine {:exercise-count exercise-count}))
  (do-exercise!))

(defn force-stop! []
  (doseq [timer @timers] (js/clearTimeout timer))
  (reset! routine nil))

(defn app-view []
  (cond
    (routine/has-ended? @routine)
    [:div
     [:div "GOOD JOB"]
     [:button {:on-click #(start!)} "restart"]]

    (routine/has-exercise? @routine)
    (let [exercise (routine/current-exercise @routine)
          filepath (str "/exercises/" (exercise :filename))]
      [:div
       [:button {:on-click #(force-stop!)} "stop"]
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
