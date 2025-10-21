(ns workout.exercises.bodyweight)

(def exercises
  #{;; cardio

    #_{:name "Jumping Jacks"
     :intensity 2
     :position :standing
     :filename "jumping-jacks.webm"
     :tags #{:cardio :legs}}

    #_{:name "Skater Hops"
     :intensity 2
     :position :standing
     :filename "skater-hops.webm"
     :tags #{:cardio :legs}}

    {:name "High Knees"
     :intensity 2
     :position :standing
     :filename "high-knees.webm"
     :tags #{:legs :cardio}}

    #_{:name "Pogo Hops"
     :intensity 1
     :position :standing
     :tags #{:legs :cardio}}

    {:name "Mage Ender"
     :intensity 1
     :position :standing
     :tags #{:legs :arms :cardio}}

    {:name "Side Punches"
     :intensity 1
     :position :standing
     :tags #{:arms :cardio}}

    ;; murder

    {:name "Squat Jacks"
     :intensity 3
     :position :standing
     :filename "squat-jacks.webm"
     :tags #{:legs :cardio
             :quadriceps :gluteus :hips :abs}}

    {:name "Burpees"
     :intensity 3
     :position :standing
     :filename "burpees.webm"
     :tags #{:cardio :legs
             :abs :erector-spinae}}

    {:name "Frogger"
     :intensity 3
     :position :floor
     :filename "frogger.webm"
     :tags #{:legs :arms :cardio}}

    ;; squats

    {:name "Jump Squats"
     :intensity 3
     :position :standing
     :filename "jump-squats.webm"
     :tags #{:legs :cardio}}

    {:name "Split Squats"
     :intensity 2
     :position :standing
     :tags #{:legs
             :quadriceps}}

    {:name "Squats"
     :intensity 1
     :position :standing
     :filename "squats.webm"
     :tags #{:legs
             :quadriceps :gluteus :hips :abs}}

    {:name "Lunges"
     :intensity 1
     :position :standing
     :filename "lunges.webm"
     :tags #{:legs
             :quadriceps :gluteus :hamstrings :hips}}

    {:name "Reverse Lunge"
     :intensity 1
     :position :standing
     :filename "reverse-lunge.webm"
     :tags #{:legs}}

    {:name "Side Lunge"
     :intensity 1
     :position :standing
     :two-sided? true
     :filename "side-lunge.webm"
     :tags #{:legs}}

    #_{:name "Crab Walk"
     :intensity 1
     :position :standing
     :filename "crab-walk.webm"
     :tags #{:legs}}

    ;; planks and arms

    {:name "Forearm Side Plank Twist"
     :filename "forearm-side-plank-twist.webm"
     :intensity 2
     :position :floor
     :two-sided? true
     :tags #{:arms
             :abs :gluteus :hips :obliques}}

    {:name "Plank T Rotation"
     :filename "plank-t-rotation.webm"
     :intensity 2
     :position :floor
     :tags #{:arms
             :abs :gluteus :hips :obliques}}

    {:name "Side Plank"
     :intensity 2
     :position :floor
     :two-sided? true
     :tags #{:arms}}

    {:name "Plank Ups"
     :intensity 3
     :position :floor
     :filename "plank-ups.webm"
     :tags #{:arms
             :erector-spinae :abs}}

    {:name "Plank Taps"
     :intensity 2
     :position :floor
     :filename "plank-taps.webm"
     :tags #{:arms
             :erector-spinae :abs}}

    {:name "Pushups"
     :intensity 2
     :position :floor
     :tags #{:arms
             :pectorals :triceps}}

    {:name "Mountain Climber"
     :intensity 2
     :position :floor
     :filename "mountain-climber.webm"
     :tags #{:abs :arms :legs}}

    {:name "Wall Pushups"
     :intensity 0
     :position :standing
     :filename "wall-pushups.webm"
     :tags #{:arms}}

    ;; abs

    {:name "Reach Crunch"
     :intensity 1
     :position :floor
     :tags #{:abs
             :rectus-abdominis :obliques}}

    {:name "Bicycle Crunch"
     :intensity 2
     :position :floor
     :filename "bicycle-crunch.webm"
     :tags #{:abs :legs}}

    ;; other

    {:name "Single Leg Toe Touch"
     :intensity 1
     :position :standing
     :tags #{:legs}}

    {:name "Single Leg Hip Raise"
     :intensity 1
     :position :floor
     :two-sided? true
     :tags #{:legs}}

    {:name "Leg Raise"
     :intensity 2
     :position :floor
     :tags #{:legs :abs}}

    {:name "Chair Dip"
     :intensity 2
     :position :standing
     :equipment #{:chair}
     :tags #{:arms}}

    {:name "Calf Raise"
     :intensity 0
     :position :standing
     :filename "calf-raise.webm"
     :tags #{:legs
             :calves}}

    {:name "Side Leg Raises"
     :intensity 0
     :position :standing
     :filename "side-leg-raises.webm"
     :tags #{:legs
             :hips}}

    {:name "Lateral Leg Raise"
     :intensity 0
     :position :floor
     :filename "lateral-leg-raise.webm"
     :tags #{:legs}}

    {:name "Knee to Chest"
     :intensity 1
     :position :floor
     :filename "knee-to-chest.webm"
     :tags #{:abs}}

    {:name "Knee to Elbow"
     :intensity 1
     :position :standing
     :filename "knee-to-elbow.webm"
     :tags #{:abs}}

    {:name "Wall Sit"
     :intensity 2
     :position :standing
     :filename "wall-sit.webm"
     :tags #{:legs}}

    {:name "Marching Glute Bridge"
     :intensity 1
     :position :floor
     :filename "marching-glute-bridge.webm"
     :tags #{:abs :legs}}

    {:name "Fire Hydrants"
     :intensity 1
     :position :floor
     :filename "fire-hydrants.webm"
     :tags #{:legs}}

    {:name "Bird Dog Crunch"
     :intensity 1
     :position :floor
     :two-sided? true
     :filename "bird-dog-crunch.webm"
     :tags #{:abs}}

    {:name "Dead Bug"
     :intensity 1
     :position :floor
     :filename "dead-bug.webm"
     :tags #{:arms :legs}}})
