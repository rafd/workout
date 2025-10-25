(ns workout.ui
  (:require
   [clojure.string :as string]
   [bloom.commons.ui.emoji-favicon :refer [emoji-favicon]]
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

(defn app-view []
  [:div {:tw "dark:bg-gray-800 dark:text-white min-h-100vh"}
   [:div
    (if @state/wakelock
      [:div "🔒 Screen wake lock active"]
      [:div "🔓 Screen wake lock inactive"])]
   (case @state/display-subject
     :start
     [:<>
      [emoji-favicon "🤸"]
      (for [[label data] [["bodyweight" (routine/make-routine bodyweight/exercises state/exercise-count)]
                          ["ankle" ankle/routine]
                          ["elbow" elbow/routine]
                          ["knee" knee/routine]
                          ["posture" posture/routine]
                          ["stretch" stretch/routine]
                          ["strength" strength/routine]]]
        ^{:key label}
        [button {:on-click #(state/start! data)} label])]

     :starting
     [:div]

     :rest
     [:div
      [emoji-favicon "🧘"]
      [button {:on-click #(state/force-stop!)} "stop"]
      [:div "RESTING"]]

     :done
     [:div
      [emoji-favicon "🛀"]
      [:div "GOOD JOB"]
      [button {:on-click #(reset! state/display-subject :start)} "start over"]]

     ; default
     (let [exercise @state/display-subject
           filepath (or (:exercise/media-url exercise)
                        (str "/exercises/" (:exercise/media-file exercise)))]
       [:div
        [emoji-favicon "🏋"]
        [button {:on-click #(state/force-stop!)} "stop"]
        [button {:on-click #(state/skip!)} "next"]
        [:div (exercise :exercise/name)]
        (case (last (string/split filepath #"\."))
          ("png" "gif")
          [:img {:src filepath
                 :style {:width "100vw"
                         :max-height "100vh"
                         :object-fit "contain"}}]
          "webm"
          [:video
           {:src filepath
            :style {:width "100vw"}
            :auto-play true
            :muted true
            :loop true}]
          nil)]))])
