(ns workout.core
  (:require
    [reagent.core :as r]
    [workout.exercises :refer [exercises]]))

(def duration 1000)

(defonce state
  (r/atom {}))

(defn next-exercise! []
  (if (seq (@state :sequence))
    (swap! state update :sequence pop)
    (js/clearInterval (@state :interval))))

(defn start! []
  (swap! state assoc :sequence (shuffle exercises))
  (swap! state assoc :interval (js/setInterval #(next-exercise!) duration)))

(defn speak! [text]
   (.. js/window.speechSynthesis (speak (js/SpeechSynthesisUtterance. text))))

(defn app-view []
  (case (@state :sequence)
    nil [:button {:on-click #(start!)} "start"]
    [] [:button {:on-click #(start!)} "restart"]
    (let [exercise (last (@state :sequence))]
      (speak! (exercise :name))
      [:div
       [:div (exercise :name)]
       [:video
        {:src (str "/exercises/" (exercise :filename))
         :auto-play true
         :muted true
         :loop true}]])))
