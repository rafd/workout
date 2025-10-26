(ns workout.state
  (:require
   [reagent.core :as r]
   [workout.speech :as speech :refer [speak!]]
   [workout.phrases :as phrases]
   [workout.exercises.all :as exercises]))

(def exercise-duration (r/atom (* 60 1000)))
(def rest-duration (r/atom (* 8 1000)))
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
                    [[:blocking-say (phrases/random :rest)]
                     [:display :rest]
                     [:blocking-say ((progress-phrase (count routine)) i)]])
                  $)
    (apply concat $)
    (concat [[:display :starting]
             [:blocking-say (phrases/random :introduction)]]
            $
            [[:display :done]
             [:eval (fn []
                      (release-wakelock!))]
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
                                         :routine routine})))
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