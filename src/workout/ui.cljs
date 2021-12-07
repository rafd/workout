(ns workout.ui
  (:require
    [reagent.core :as r]
    [bloom.commons.ui.emoji-favicon :refer [emoji-favicon]]
    [workout.routine :as routine]
    [workout.speech :as speech :refer [speak!]]
    [workout.phrases :as phrases]))

(def exercise-duration (* 60 1000))
(def rest-duration (* 10 1000))
(def exercise-count 8)

(defonce display-subject (r/atom :start))
(defonce schedule-timeout (atom nil))

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
              [[:display exercise]
               [:say (str (phrases/random :transition)
                          (exercise :name))]
               [:delay (/ exercise-duration 2)]
               [:say (phrases/random :motivation)]
               [:delay (/ exercise-duration 2)]]) $)
        (interpose-fn (fn [i]
                        [[:display :rest]
                         [:say (phrases/random :rest)]
                         [:delay rest-duration]
                         [:blocking-say ((progress-phrase (count exercises)) i)]]) $)
        (apply concat $)
        (concat [[:display :starting]
                 [:blocking-say (phrases/random :introduction)]]
                $
                [[:display :done]
                 [:say (phrases/random :completion)]])))

(comment
  (require 'cljs.pprint)
  (cljs.pprint/pprint
   (generate-schedule {:exercise-duration exercise-duration
                       :rest-duration 2
                       :exercises (routine/make-routine 6)})))


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
  #_(js/navigator.wakeLock.request "screen")
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
    [:<>
     [emoji-favicon "🤸"]
     [:button {:on-click #(start!)} "start"]]

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
     [:button {:on-click #(start!)} "restart"]
     [:div "GOOD JOB"]]

    ; default
    (let [exercise @display-subject
          filepath (str "/exercises/" (exercise :filename))]
      [:div
       [emoji-favicon "🏋"]
       [:button {:on-click #(force-stop!)} "stop"]
       [:div (exercise :name)]
       [:video
        {:src filepath
         :auto-play true
         :muted true
         :loop true}]])))

