(ns workout.ui.border
  (:require
   [reagent.core :as r]
   [workout.timer :as timer]))

(defn animated-border [percent]
  (let [percent (mod percent 1)
        w (.-innerWidth js/window)
        h (.-innerHeight js/window)
        perimeter (* 2 (+ w h))
        lengths [(* 0.5 w) h w h (* 0.5 w)]
        total-drawn-length (* perimeter percent)
        thickness "4px"
        color "rgb(234,179,8)"
        percentages (->> lengths
                         (reduce (fn [memo line]
                                   (cond
                                     (< line (:remaining memo))
                                     (-> memo
                                         (update :percentages conj 1.0)
                                         (update :remaining - line))
                                     (<= (:remaining memo) line)
                                     ;; returning just the percentages
                                     (reduced
                                      (conj (:percentages memo)
                                            (/ (:remaining memo) line)))))
                                 {:percentages []
                                  :remaining total-drawn-length}))]
    [:div
     ;; top second half
     [:div {:style {:position "fixed"
                    :z-index 1000
                    :background color
                    :top 0
                    :left "50%"
                    :width (str (* 100 0.5 (get percentages 0 0)) "%")
                    :height thickness}}]
     ;; right
     [:div {:style {:position "fixed"
                    :z-index 1000
                    :background color
                    :top 0
                    :right 0
                    :height (str (* 100 (get percentages 1 0)) "%")
                    :width thickness}}]
     ;; bottom
     [:div {:style {:position "fixed"
                    :z-index 1000
                    :background color
                    :right 0
                    :bottom 0
                    :width (str (* 100 (get percentages 2 0)) "%")
                    :height thickness}}]
     ;; left
     [:div {:style {:position "fixed"
                    :z-index 1000
                    :background color
                    :bottom 0
                    :left 0
                    :height (str (* 100 (get percentages 3 0)) "%")
                    :width thickness}}]
     ;; top first half
     [:div {:style {:position "fixed"
                    :z-index 1000
                    :background color
                    :top 0
                    :left 0
                    :width (str  (* 100 0.5 (get percentages 4 0)) "%")
                    :height thickness}}]]))

(defonce percent (r/atom 0))

(defonce _interval
  (js/setInterval (fn []
                    (reset! percent (timer/percent-complete)))
                  5))

(defn animated-border-view []
  [animated-border @percent])
