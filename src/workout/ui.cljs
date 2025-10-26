(ns workout.ui
  (:require
   [reagent.core :as r]
   [clojure.string :as string]
   [bloom.commons.ui.emoji-favicon :refer [emoji-favicon]]
   [workout.ui.border :as border]
   [workout.routine :as routine]
   [workout.exercises.bodyweight :as bodyweight]
   [workout.routines.ankle :as ankle]
   [workout.routines.elbow :as elbow]
   [workout.routines.knee :as knee]
   [workout.routines.posture :as posture]
   [workout.routines.stretch :as stretch]
   [workout.routines.strength :as strength]
   [workout.state :as state]))

(defn button [opts content]
  [:button (merge {:tw ["px-3 py-2 bg-gray-200 rounded m-2 hover:bg-gray-300 border"
                        "dark:bg-gray-700 dark:hover:bg-gray-600"]}
                  opts)
   content])

(defn input [opts]
  [:input (merge {:tw ["dark:bg-gray-700 dark:text-white border rounded px-2 py-1"]}
                 opts)])

(defn routine-view []
  [:div {:tw "fixed bottom-0 left-0 p-3"
         :style {:background "#1f2937b0"}}
   (doall
    (for [exercise @state/current-routine]
      ^{:key (:exercise/name exercise)}
      [:div {:on-click #(state/jump-to-exercise! exercise)
             :tw ["whitespace-nowrap"
                  (when (= exercise @state/display-subject)
                    "font-bold text-yellow-500")]}
       (:exercise/name exercise)]))])

(defn controls-view []
  [:div
   (if @state/paused?
     [button {:on-click #(state/resume!)} "resume"]
     [button {:on-click #(state/pause!)} "pause"])
   [button {:on-click #(state/skip!)} "skip"]
   [button {:on-click #(state/force-stop!)} "stop"]])

(defn app-view []
  [:div {:tw "dark:bg-gray-800 dark:text-white min-h-100vh py-4"}
   [border/animated-border-view]
   (case @state/display-subject
     :start
     [:div {:tw "flex flex-col items-center gap-4"}
      [emoji-favicon "🤸"]
      [:form
       [:div {:tw "space-y-2"}
        [:label {:tw "flex justify-between items-center gap-2"}
         "Exercise duration (seconds):"
         [input {:type "number"
                 :min 1
                 :style {:width "5em"}
                 :value (/ @state/exercise-duration 1000)
                 :on-change #(reset! state/exercise-duration (* 1000 (js/parseInt (.. % -target -value))))}]]
        [:label {:tw "flex justify-between items-center gap-2"}
         "Rest duration (seconds):"
         [input {:type "number"
                 :min 1
                 :style {:width "5em"}
                 :value (/ @state/rest-duration 1000)
                 :on-change #(reset! state/rest-duration (* 1000 (js/parseInt (.. % -target -value))))}]]]]
      [:div
       (for [[label data] [["bodyweight" (routine/make-routine bodyweight/exercises state/exercise-count)]
                           ["ankle" ankle/routine]
                           ["elbow" elbow/routine]
                           ["knee" knee/routine]
                           ["posture" posture/routine]
                           ["stretch" stretch/routine]
                           ["strength" strength/routine]]]
         ^{:key label}
         [button {:on-click #(state/start! data)} label])]]

     :starting
     [:div]

     :rest
     [:div {:tw "flex flex-col items-center gap-4"}
      [emoji-favicon "🧘"]
      [controls-view]
      [:div {:tw "text-4xl font-bold"} "Resting"]
      (when @state/paused?
        [:div {:tw "text-2xl font-bold h-40 flex items-center"} "PAUSED"])]

     :done
     [:div {:tw "flex flex-col items-center gap-4"}
      [emoji-favicon "🛀"]
      [button {:on-click #(state/restart!)} "start over"]
      [:div {:tw "text-2xl font-bold h-40 flex items-center"} "🎉 GOOD JOB! 🎉"]]

     ; default
     (let [exercise @state/display-subject
           filepath (or (:exercise/media-url exercise)
                        (str "/exercises/" (:exercise/media-file exercise)))]
       [:div {:tw "flex flex-col items-center gap-4"}
        [emoji-favicon "🏋"]
        [controls-view]
        [:div {:tw "text-4xl font-bold"} (:exercise/name exercise)]
        [:div (:exercise/instructions exercise)]
        (if @state/paused?
          [:div {:tw "text-2xl font-bold h-40 flex items-center"} "PAUSED"]
          (case (last (string/split filepath #"\."))
            ("png" "gif")
            [:img {:src filepath
                   :style {:width "100vw"
                           :max-height "100vh"
                           :object-fit "contain"}}]
            "webm"
            [:video
             {:src filepath
              :style {:width "100vw"
                      :max-height "100vh"
                      :object-fit "contain"}
              :auto-play true
              :muted true
              :loop true}]
            nil))]))

   (when @state/current-routine
     [routine-view])])
