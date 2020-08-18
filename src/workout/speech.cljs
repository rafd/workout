(ns workout.speech)

(def voice
  (.. js/window.speechSynthesis
      (getVoices)
      (find (fn [v]
              (= (.-voiceURI v) "Google UK English Female")))))

(defn speak!
  ([text]
   (speak! text (fn [])))

  ([text callback]
   (.. js/window.speechSynthesis (speak (doto (js/SpeechSynthesisUtterance. text)
                                              (aset "voice" voice)
                                              (.addEventListener "end" callback))))))

