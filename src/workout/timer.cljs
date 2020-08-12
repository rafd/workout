(ns workout.timer)

(defn start-timer [callback halfway-callback duration]
  (js/setTimeout halfway-callback (/ duration 2))
  (js/setTimeout callback duration))