(ns workout.ui
  (:require
   [clojure.string :as string]
   [reagent.core :as r]
   [bloom.commons.ui.emoji-favicon :refer [emoji-favicon]]
   [workout.routine :as routine]
   [workout.speech :as speech :refer [speak!]]
   [workout.phrases :as phrases]
   [workout.exercises.bodyweight :as bodyweight]
   [workout.routines.ankle :as ankle]
   [workout.routines.elbow :as elbow]
   [workout.routines.knee :as knee]
   [workout.routines.posture :as posture]
   [workout.routines.stretch :as stretch]))

(def exercise-duration (* 60 1000))
(def rest-duration (* 8 1000))
(def exercise-count 8)

(defonce display-subject (r/atom :start))
(defonce schedule-timeout (atom nil))
(defonce wakelock (atom nil))
(defonce schedule (atom nil))
(defonce current-schedule-index (atom nil))

(defn request-wakelock! []
  (when js/navigator.wakeLock
    (-> (js/navigator.wakeLock.request "screen")
        (.then (fn [lock]
                 (reset! wakelock lock)))
        (.catch (fn [err]
                  (js/console.warn "Wake lock request failed:" err))))))

(defn release-wakelock! []
  (when @wakelock
    (-> (.release @wakelock)
        (.then (fn []
                 (reset! wakelock nil)))
        (.catch (fn [err]
                  (js/console.warn "Wake lock release failed:" err))))))

#_(defn interpose-fn [sep-fn coll]
    (rest (interleave (repeatedly sep-fn) coll)))

(defn interpose-fn [sep-fn coll]
  (->> coll
       (map-indexed
        (fn [i item]
         [item (sep-fn i)]))
       (apply concat)
       butlast))

(defn progress-phrase [total]
  (fn [i]
    (let [i (inc i)]
      (cond
        (= i (dec total))
        "Last one!"
        (= i (- total 2))
        "Two more to go!"
        (= (int (/ total 2)) i) ;; TODO
        "Halfway done!"
        (= i (- total 3))
        "Almost there."))))

(defn generate-schedule [{:keys [exercise-duration rest-duration exercises]}]
  (as-> exercises $
        (map (fn [exercise]
              [[:delay (* 0.25 rest-duration)]
               [:display exercise]
               [:say (str (phrases/random :transition)
                          (exercise :exercise/name))]
               [:delay (* 0.75 rest-duration)]
               [:blocking-say (phrases/random :start)]
               [:delay (/ exercise-duration 2)]
               [:say
                (if (:exercise/two-sided? exercise)
                 (phrases/random :switch-sides)
                 (phrases/random :motivation))]
               [:delay (/ exercise-duration 2)]]) $)
        (interpose-fn (fn [i]
                        [[:blocking-say (phrases/random :rest)]
                         [:display :rest]
                         [:blocking-say ((progress-phrase (count exercises)) i)]])
                      $)
        (apply concat $)
        (concat [[:display :starting]
                 [:blocking-say (phrases/random :introduction)]]
                $
                [[:display :done]
                 [:eval (fn []
                         (release-wakelock!))]
                 [:say (phrases/random :completion)]])))

(comment
  (require 'cljs.pprint)
  (cljs.pprint/pprint
   (generate-schedule {:exercise-duration exercise-duration
                       :rest-duration 2
                       :exercises (routine/make-routine bodyweight/exercises 6)})))


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

(defmethod process-instruction! :eval
  [[_ f] done!]
  (f)
  (done!))

(defmethod process-instruction! :default
  [_ done!]
  (done!))

(defn process-schedule! []
  (when (< @current-schedule-index (count @schedule))
    (process-instruction! (nth @schedule @current-schedule-index)
                          (fn []
                            (swap! current-schedule-index inc)
                            (process-schedule!)))))

(defn start! [routine]
  (request-wakelock!)
  (reset! schedule (generate-schedule {:exercise-duration exercise-duration
                                         :rest-duration rest-duration
                                             :exercises routine}))
  (reset! current-schedule-index 0)
  (process-schedule!))

(defn force-stop! []
  (js/clearTimeout @schedule-timeout)
  (speak! "Quitting early? That's bollocks.")
  (release-wakelock!)
  (reset! display-subject :start))

(defn skip! []
  (js/clearTimeout @schedule-timeout)
  (speak! "Skipping ahead.")
  ;; need to have logic to skip to next exercise; not just next scuedule (which may be the halfway voice state)
  (swap! current-schedule-index inc)
  (process-schedule!))

(defn app-view []
  [:<>
   [:div
    (if @wakelock
      [:div "🔒 Screen wake lock active"]
      [:div "🔓 Screen wake lock inactive"])]
   (case @display-subject
     :start
     [:<>
      [emoji-favicon "🤸"]
      (for [[label data] [["bodyweight" (routine/make-routine bodyweight/exercises exercise-count)]
                          ["ankle" ankle/routine]
                          ["elbow" elbow/routine]
                          ["knee" knee/routine]
                          ["posture" posture/routine]
                          ["stretch" stretch/routine]]]
        ^{:key label}
        [:button {:on-click #(start! data)
                  :style {:margin "0.5em"
                          :padding "1em"}} label])]

     :starting
     [:div]

     :rest
     [:div
      [emoji-favicon "🧘"]
      [:button {:on-click #(force-stop!)} "stop"]
      [:div "RESTING"]]

     :done
     [:div
      [emoji-favicon "🛀"]
      [:div "GOOD JOB"]
      [:button {:on-click #(reset! display-subject :start)} "start over"]]

    ; default
     (let [exercise @display-subject
           filepath (str "/exercises/" (exercise :exercise/media-file))]
       [:div
        [emoji-favicon "🏋"]
        [:button {:on-click #(force-stop!)} "stop"]
        [:button {:on-click #(skip!)} "skip"]
        [:div (exercise :exercise/name)]
        (case (last (string/split filepath #"\."))
          "png"
          [:img {:src filepath
                 :style {:width "100vw"}}]
          "webm"
          [:video
           {:src filepath
            :style {:width "100vw"}
            :auto-play true
            :muted true
            :loop true}]
          nil)]))])
