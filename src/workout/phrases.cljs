(ns workout.phrases)

(def phrases
  {:introduction
   #{"Welcome back!"
     "Let's get started!"
     "Are you ready for some pain?"
     "Well, look who got off the couch today."
     "Back for more?"
     "Let's go!"
     "Let's see how you do today."
     "Starting in 3... 2... 1..."}

   :switch-sides
   #{"Switch it up!"
     "Switch sides."
     "Change it up!"}

   :motivation
   #{"Keep it up!"
     "Almost there!"
     "Keep going!"
     "Pump it harder!"
     "Go! go! go!"
     "Believe in yourself!"
     "You can do it!"
     "Is that all you've got?"
     "No pain. No gain."
     "Pain is just weakness leaving the body."
     "What hurts today makes you stronger tomorrow."
     "Mind over body."
     "Sweat is fat crying."
     "Good things come to those who sweat."
     "The body achieves what the mind believes."
     "Hustle for that muscle."
     "Don't give up!"
     "How bad do you want it?"
     "Keep it up!"}

   :transition
   #{"Now we've got: "
     "Time for: "
     "Now do some: "}

   :rest
   #{"Take a breather."
     "Time for a rest."
     "You deserve a quick break."
     "Catch your breath."}

   :completion
   #{"That's it! Great job!"
     "That's a wrap. See you next time."
     "That's all for today. Good job!"
     "And... we're done! That was some good hustle!"
     "All right, that's it. Hit the showers!"}})

(defn random [type]
  (rand-nth (vec (phrases type))))
