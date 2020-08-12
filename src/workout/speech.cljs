(ns workout.speech)

(defn speak! [text]
  (.. js/window.speechSynthesis (speak (js/SpeechSynthesisUtterance. text))))