(ns workout.speech)

(def voice (atom nil))

(defonce _set_voice_
  (.addEventListener js/speechSynthesis
                     "voiceschanged"
                     (fn [_]
                       (reset! voice (.. js/window.speechSynthesis
                                         (getVoices)
                                         (find (fn [v]
                                                 (= (.-voiceURI v) "Google UK English Female"))))))))
(defn speak!
  ([text]
   (speak! text (fn [])))

  ([text callback]
   (if text
    (.. js/window.speechSynthesis (speak (doto (js/SpeechSynthesisUtterance. text)
                                               (aset "voice" @voice)
                                               (.addEventListener "end" callback))))
    (callback))))

