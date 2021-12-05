(ns workout.core
  (:require
    [reagent.core :as r]
    [workout.routine :as routine]
    [workout.speech :refer [speak!]]
    [workout.phrases :as phrases]
    [clojure.string :as str]))

(def exercise-duration (* 60 1000))
(def rest-duration (* 10 1000))
(def exercise-count 10)

(defonce display-subject (r/atom :start))
(defonce schedule-timeout (atom nil))

(defn interpose-fn [sep-fn coll]
  (rest (interleave (repeatedly sep-fn) coll)))

(defn generate-schedule [{:keys [exercise-duration rest-duration exercises]}]
  (as-> exercises $
        (map (fn [exercise]
              [[:display exercise]
               [:say (str (phrases/random :transition)
                          (exercise :name))]
               [:delay  (/ exercise-duration 2)]
               [:say (phrases/random :motivation)]
               [:delay  (/ exercise-duration 2)]]) $)
        (interpose-fn (fn []
                        [[:display :rest]
                         [:say (phrases/random :rest)]
                         [:delay rest-duration]]) $)
        (apply concat $)
        (concat [[:display :starting]
                 [:blocking-say (phrases/random :introduction)]]
                $
                [[:display :done]
                 [:say (phrases/random :completion)]])))

(defmulti process-instruction!
  (fn [[instruction-type _] _]
    instruction-type))

(defmethod process-instruction! :say
  [[_ text] done!]
  (speak! text)
  (done!))

(defmethod process-instruction! :blocking-say
  [[_ text] done!]
  (speak! text done!))

(defmethod process-instruction! :delay
  [[_ duration] done!]
  (reset! schedule-timeout (js/setTimeout done! duration)))

(defmethod process-instruction! :display
  [[_ subject] done!]
  (reset! display-subject subject)
  (done!))

(defmethod process-instruction! :default
  [_ done!]
  (done!))

(defn process-schedule! [schedule]
  (when (seq schedule)
    (process-instruction! (first schedule)
                          #(process-schedule! (rest schedule)))))

(defn start! []
  (process-schedule! (generate-schedule {:exercise-duration exercise-duration
                                         :rest-duration rest-duration
                                         :exercises (routine/make-routine exercise-count)})))

(defn force-stop! []
  (js/clearTimeout @schedule-timeout)
  (speak! "Quitting early? That's bollocks.")
  (reset! display-subject :start))

(defn app-view []
  (case @display-subject
    :start
    [:button {:on-click #(start!)} "start"]

    :starting
    [:div]

    :rest
    [:div
     [:button {:on-click #(force-stop!)} "stop"]
     [:div "RESTING"]]

    :done
    [:div
     [:button {:on-click #(start!)} "restart"]
     [:div "GOOD JOB"]]

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