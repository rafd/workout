(ns workout.core
  (:require
    [reagent.core :as r]
    [workout.routine :as routine]
    [workout.speech :refer [speak!]]
    [clojure.string :as str]))

(def exercise-duration (* 4 1000))
(def rest-duration (* 2 1000))
(def exercise-count 4)

(defonce display-subject (r/atom :start))
(defonce schedule-timeout (atom nil))

(defn generate-schedule [{:keys [exercise-duration rest-duration exercises]}]
  (as-> exercises $
        (map (fn [exercise]
              [[:display exercise]
               [:say (exercise :name)]
               [:delay  (/ exercise-duration 2)]
               [:say  "halfway"]
               [:delay  (/ exercise-duration 2)]]) $)
        (interpose [[:display :rest]
                    [:say "rest"]
                    [:delay rest-duration]] $)
        (apply concat $)
        (concat $ [[:display :done]
                   [:say "Done! Great job!"]])))

(defmulti process-instruction!
  (fn [[instruction-type _]]
    instruction-type))

(defmethod process-instruction! :say
  [[_ text]]
  (speak! text))

(defmethod process-instruction! :display
  [[_ subject]]
  (reset! display-subject subject))

(defmethod process-instruction! :default [_])

(defn process-schedule! [schedule]
  (let [[instruction-type arg :as instruction]  (first schedule)]
    (process-instruction! instruction)
    (reset! schedule-timeout (js/setTimeout
                               #(process-schedule! (rest schedule))
                               (if (= :delay instruction-type)
                                 arg
                                 0)))))

(defn start! []
  (process-schedule! (generate-schedule {:exercise-duration exercise-duration
                                         :rest-duration rest-duration
                                         :exercises (routine/make-routine exercise-count)})))

(defn force-stop! []
  (js/clearTimeout @schedule-timeout)
  (reset! display-subject :start))

(defn app-view []
  (case @display-subject
    :start
    [:button {:on-click #(start!)} "start"]

    :rest
    [:div
     [:button {:on-click #(force-stop!)} "stop"]
     [:div "RESTING"]]

    :done
    [:div
     [:div "GOOD JOB"]
     [:button {:on-click #(start!)} "restart"]]

    ; default
    (let [exercise @display-subject
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
         [:img {:src filepath}])])))