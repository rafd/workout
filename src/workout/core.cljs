(ns workout.core
  (:require
    [reagent.core :as r]
    [workout.routine :as routine]
    [workout.timer :as timer]
    [workout.speech :refer [speak!]]
    [clojure.string :as str]))

(def duration 2000)

(defn halfway-alert! []
  (speak! "halfway"))

(defn next-exercise! []
  (when (not (routine/has-ended))
    (routine/next-exercise!)
    (timer/start-timer next-exercise! halfway-alert! duration)))

(defn start! []
  (routine/create-routine!)
  (timer/start-timer next-exercise! halfway-alert! duration))

(defn app-view []
  (cond
    (routine/has-ended)
    [:div
     [:div "GOOD JOB"]
     [:button {:on-click #(start!)} "restart"]]

    (routine/has-started)
    (let [exercise (routine/current-exercise)
          name (exercise :name)
          filepath (str "/exercises/" (exercise :filename))]
      (speak! name)
      [:div
       [:div name]
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
