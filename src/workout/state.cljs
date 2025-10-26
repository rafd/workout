(ns workout.state
  (:require
   [reagent.core :as r]
   [workout.speech :as speech :refer [speak!]]
   [workout.phrases :as phrases]
   [workout.exercises.all :as exercises]
   [workout.timer :as timer]))

(def exercise-duration (r/atom (* 60 1000)))
(def rest-duration (r/atom (* 8 1000)))
(def exercise-count 8)

(defonce display-subject (r/atom :start))
(defonce wakelock (atom nil))
(defonce schedule (atom nil))
(defonce current-schedule-index (atom nil))
(defonce current-routine (r/atom nil))
(defonce paused? (r/atom false))

(defn clear-timeout! []
  (timer/clear!))

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

(declare end!)

(defn generate-schedule [{:keys [exercise-duration rest-duration routine]}]
  (as-> routine $
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
                    [[:display :rest]
                     [:blocking-say (phrases/random :rest)]
                     [:blocking-say ((progress-phrase (count routine)) i)]])
                  $)
    (apply concat $)
    (concat [[:display :starting]
             [:blocking-say (phrases/random :introduction)]]
            $
            [[:display :done]
             [:say (phrases/random :completion)]
             [:eval (fn []
                      (end!))]])))

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
  (timer/set! done! duration))

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
  (reset! paused? false)
  (when (< @current-schedule-index (count @schedule))
    (process-instruction! (nth @schedule @current-schedule-index)
                          (fn []
                            (swap! current-schedule-index inc)
                            (process-schedule!)))))

(defn start! [routine]
  (request-wakelock!)
  (let [routine (->> routine
                     ;; routine may consist of exercise names or exercise maps
                     ;; normalize to maps
                     (map (fn [item]
                            (if (string? item)
                              (or (exercises/by-name item)
                                  {:exercise/name item})
                              item))))]
    (reset! schedule (generate-schedule {:exercise-duration @exercise-duration
                                         :rest-duration @rest-duration
                                         :routine routine}))
    (reset! current-schedule-index 0)
    (reset! current-routine routine))
  (process-schedule!))

(defn restart! []
  (reset! display-subject :start)
  (reset! schedule nil)
  (reset! current-schedule-index nil)
  (reset! current-routine nil))

(defn end! []
  (clear-timeout!)
  (release-wakelock!))
  
(defn force-stop! []
  (speak! "Quitting early? That's bollocks.")
  (reset! display-subject :done)
  (end!))

(defn jump-to-exercise! [exercise]
  (clear-timeout!)
  (let [index (->> @schedule
                   (map-indexed vector)
                   (some (fn [[index [instruction item]]]
                           (when (and (= :display instruction)
                                      (= exercise item))
                             index))))]
    (reset! current-schedule-index index)
    (process-schedule!)))

(defn skip! []
  (let [exercise (->> @schedule
                      (map-indexed vector)
                      (drop-while (fn [[index _]]
                                    (<= index @current-schedule-index)))
                      (some (fn [[_ [instruction item]]]
                              (when (and (= :display instruction)
                                         ;; is an exercise
                                         (map? item))
                                item))))]
    (if exercise
      (jump-to-exercise! exercise)
      (jump-to-exercise! :done))))

(defn pause! []
  (reset! paused? true)
  (clear-timeout!))

(defn resume! []
  (process-schedule!))
