(ns workout.timer
  (:require
   [reagent.core :as r]))

(defonce timer (r/atom nil))

(defn percent-complete []
  (when-let [{:timer/keys [start duration]} @timer]
    (let [ms-since (- (.getTime (js/Date.)) (.getTime start))]
      (/ ms-since
         duration))))

(defn clear! []
  (reset! timer nil))

(defn raf-loop [_timestamp]
  (when-let [p (percent-complete)]
    (if (<= 1 p)
      (let [callback (:timer/callback @timer)]
        (clear!)
        (callback))
      (js/requestAnimationFrame raf-loop))))

(defn set! [callback ms]
  (clear!)
  (let [start (js/Date.)]
    (reset! timer {:timer/start start
                   :timer/duration ms
                   :timer/callback callback}))
  (js/requestAnimationFrame raf-loop))
