(ns workout.ui
  (:require
   [clojure.string :as string]
   [reagent.core :as r]
   [bloom.commons.ui.emoji-favicon :refer [emoji-favicon]]
   [workout.routine :as routine]
   [workout.speech :as speech :refer [speak!]]
   [workout.phrases :as phrases]
   [workout.exercises.all :as exercises]
   [workout.exercises.bodyweight :as bodyweight]
   [workout.routines.ankle :as ankle]
   [workout.routines.elbow :as elbow]
   [workout.routines.knee :as knee]
   [workout.routines.posture :as posture]
   [workout.routines.stretch :as stretch]
   [workout.routines.strength :as strength]))

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
               (concat [[:delay (* 0.25 rest-duration)]
                        [:display exercise]
                        [:say (str (phrases/random :transition)
                                   (exercise :exercise/name))]]
                       (if (:exercise/self-paced? exercise)
                         [[:say "Go at your own pace"]
                          [:delay 99999999999]]
                         [[:delay (* 0.75 rest-duration)]
                          [:blocking-say (phrases/random :start)]
                          [:delay (/ exercise-duration 2)]
                          [:say
                           (if (:exercise/two-sided? exercise)
                             (phrases/random :switch-sides)
                             (phrases/random :motivation))]
                          [:delay (/ exercise-duration 2)]]))) $)
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
                                       :exercises (->> routine
                                                       (map (fn [item]
                                                              (if (string? item)
                                                                (or (exercises/by-name item) 
                                                                    {:exercise/name item})
                                                                item))))}))
  (reset! current-schedule-index 0)
  (process-schedule!))

(defn force-stop! []
  (js/clearTimeout @schedule-timeout)
  (speak! "Quitting early? That's bollocks.")
  (release-wakelock!)
  (reset! display-subject :start))

(defn skip! []
  (js/clearTimeout @schedule-timeout)
  (speak! "Next exercise.")
  ;; need to have logic to skip to next exercise; not just next scuedule (which may be the halfway voice state)
  (swap! current-schedule-index inc)
  (process-schedule!))

(defn button [opts content]
  [:button (merge {:tw "px-3 py-2 bg-gray-200 rounded m-2 hover:bg-gray-300 border"}
                  opts)
   content])

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
                          ["stretch" stretch/routine]
                          ["strength" strength/routine]]]
        ^{:key label}
        [button {:on-click #(start! data)} label])]

     :starting
     [:div]

     :rest
     [:div
      [emoji-favicon "🧘"]
      [button {:on-click #(force-stop!)} "stop"]
      [:div "RESTING"]]

     :done
     [:div
      [emoji-favicon "🛀"]
      [:div "GOOD JOB"]
      [button {:on-click #(reset! display-subject :start)} "start over"]]

     ; default
     (let [exercise @display-subject
           filepath (or (:exercise/media-url exercise)
                        (str "/exercises/" (:exercise/media-file exercise)))]
       [:div
        [emoji-favicon "🏋"]
        [button {:on-click #(force-stop!)} "stop"]
        [button {:on-click #(skip!)} "next"]
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
