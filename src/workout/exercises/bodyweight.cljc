(ns workout.exercises.bodyweight)

(def exercises
  #{;; cardio

    #_{:exercise/name "Jumping Jacks"
       :exercise/intensity 2
       :exercise/position :position/standing
       :exercise/media-file "jumping-jacks.webm"
       :exercise/target-muscles #{:cardio :legs}}

    #_{:exercise/name "Skater Hops"
       :exercise/intensity 2
       :exercise/position :position/standing
       :exercise/media-file "skater-hops.webm"
       :exercise/target-muscles #{:cardio :legs}}

    {:exercise/name "High Knees"
     :exercise/intensity 2
     :exercise/position :position/standing
     :exercise/media-file "high-knees.webm"
     :exercise/target-muscles #{:legs :cardio}}

    #_{:exercise/name "Pogo Hops"
       :exercise/intensity 1
       :exercise/position :position/standing
       :exercise/target-muscles #{:legs :cardio}}

    {:exercise/name "Mage Ender"
     :exercise/intensity 1
     :exercise/position :position/standing
     :exercise/target-muscles #{:legs :arms :cardio}}

    {:exercise/name "Side Punches"
     :exercise/intensity 1
     :exercise/position :position/standing
     :exercise/target-muscles #{:arms :cardio}}

    ;; murder

    {:exercise/name "Squat Jacks"
     :exercise/intensity 3
     :exercise/position :position/standing
     :exercise/media-file "squat-jacks.webm"
     :exercise/target-muscles #{:legs :cardio
                                :quadriceps :gluteus :hips :abs}}

    {:exercise/name "Burpees"
     :exercise/intensity 3
     :exercise/position :position/standing
     :exercise/media-file "burpees.webm"
     :exercise/target-muscles #{:cardio :legs
                                :abs :erector-spinae}}

    {:exercise/name "Frogger"
     :exercise/intensity 3
     :exercise/position :position/floor
     :exercise/media-file "frogger.webm"
     :exercise/target-muscles #{:legs :arms :cardio}}

    ;; squats

    {:exercise/name "Jump Squats"
     :exercise/intensity 3
     :exercise/position :position/standing
     :exercise/media-file "jump-squats.webm"
     :exercise/target-muscles #{:legs :cardio}}

    {:exercise/name "Split Squats"
     :exercise/intensity 2
     :exercise/position :position/standing
     :exercise/target-muscles #{:legs
                                :quadriceps}}

    {:exercise/name "Squats"
     :exercise/intensity 1
     :exercise/position :position/standing
     :exercise/media-file "squats.webm"
     :exercise/target-muscles #{:legs
                                :quadriceps :gluteus :hips :abs}}

    {:exercise/name "Lunges"
     :exercise/intensity 1
     :exercise/position :position/standing
     :exercise/media-file "lunges.webm"
     :exercise/target-muscles #{:legs
                                :quadriceps :gluteus :hamstrings :hips}}

    {:exercise/name "Reverse Lunge"
     :exercise/intensity 1
     :exercise/position :position/standing
     :exercise/media-file "reverse-lunge.webm"
     :exercise/target-muscles #{:legs}}

    {:exercise/name "Side Lunge"
     :exercise/intensity 1
     :exercise/position :position/standing
     :exercise/two-sided? true
     :exercise/media-file "side-lunge.webm"
     :exercise/target-muscles #{:legs}}

    #_{:exercise/name "Crab Walk"
       :exercise/intensity 1
       :exercise/position :position/standing
       :exercise/media-file "crab-walk.webm"
       :exercise/target-muscles #{:legs}}

    ;; planks and arms

    {:exercise/name "Forearm Side Plank Twist"
     :exercise/media-file "forearm-side-plank-twist.webm"
     :exercise/intensity 2
     :exercise/position :position/floor
     :exercise/two-sided? true
     :exercise/target-muscles #{:arms
                                :abs :gluteus :hips :obliques}}

    {:exercise/name "Plank T Rotation"
     :exercise/media-file "plank-t-rotation.webm"
     :exercise/intensity 2
     :exercise/position :position/floor
     :exercise/target-muscles #{:arms
                                :abs :gluteus :hips :obliques}}

    {:exercise/name "Side Plank"
     :exercise/intensity 2
     :exercise/position :position/floor
     :exercise/two-sided? true
     :exercise/target-muscles #{:arms}}

    {:exercise/name "Plank Ups"
     :exercise/intensity 3
     :exercise/position :position/floor
     :exercise/media-file "plank-ups.webm"
     :exercise/target-muscles #{:arms
                                :erector-spinae :abs}}

    {:exercise/name "Plank Taps"
     :exercise/intensity 2
     :exercise/position :position/floor
     :exercise/media-file "plank-taps.webm"
     :exercise/target-muscles #{:arms
                                :erector-spinae :abs}}

    {:exercise/name "Pushups"
     :exercise/intensity 2
     :exercise/position :position/floor
     :exercise/target-muscles #{:arms
                                :pectorals :triceps}}

    {:exercise/name "Mountain Climber"
     :exercise/intensity 2
     :exercise/position :position/floor
     :exercise/media-file "mountain-climber.webm"
     :exercise/target-muscles #{:abs :arms :legs}}

    {:exercise/name "Wall Pushups"
     :exercise/intensity 0
     :exercise/position :position/standing
     :exercise/media-file "wall-pushups.webm"
     :exercise/target-muscles #{:arms}}

    ;; abs

    {:exercise/name "Reach Crunch"
     :exercise/intensity 1
     :exercise/position :position/floor
     :exercise/target-muscles #{:abs
                                :rectus-abdominis :obliques}}

    {:exercise/name "Bicycle Crunch"
     :exercise/intensity 2
     :exercise/position :position/floor
     :exercise/media-file "bicycle-crunch.webm"
     :exercise/target-muscles #{:abs :legs}}

    ;; other

    {:exercise/name "Single Leg Toe Touch"
     :exercise/intensity 1
     :exercise/position :position/standing
     :exercise/target-muscles #{:legs}}

    {:exercise/name "Single Leg Hip Raise"
     :exercise/intensity 1
     :exercise/position :position/floor
     :exercise/two-sided? true
     :exercise/target-muscles #{:legs}}

    {:exercise/name "Leg Raise"
     :exercise/intensity 2
     :exercise/position :position/floor
     :exercise/target-muscles #{:legs :abs}}

    {:exercise/name "Chair Dip"
     :exercise/intensity 2
     :exercise/position :position/standing
     :equipment #{:chair}
     :exercise/target-muscles #{:arms}}

    {:exercise/name "Calf Raise"
     :exercise/intensity 0
     :exercise/position :position/standing
     :exercise/media-file "calf-raise.webm"
     :exercise/target-muscles #{:legs
                                :calves}}

    {:exercise/name "Side Leg Raises"
     :exercise/intensity 0
     :exercise/position :position/standing
     :exercise/media-file "side-leg-raises.webm"
     :exercise/target-muscles #{:legs
                                :hips}}

    {:exercise/name "Lateral Leg Raise"
     :exercise/intensity 0
     :exercise/position :position/floor
     :exercise/target-muscles #{:legs}}

    {:exercise/name "Knee to Chest"
     :exercise/intensity 1
     :exercise/position :position/floor
     :exercise/media-file "knee-to-chest.webm"
     :exercise/target-muscles #{:abs}}

    {:exercise/name "Knee to Elbow"
     :exercise/intensity 1
     :exercise/position :position/standing
     :exercise/media-file "knee-to-elbow.webm"
     :exercise/target-muscles #{:abs}}

    {:exercise/name "Wall Sit"
     :exercise/intensity 2
     :exercise/position :position/standing
     :exercise/media-file "wall-sit.webm"
     :exercise/target-muscles #{:legs}}

    {:exercise/name "Marching Glute Bridge"
     :exercise/intensity 1
     :exercise/position :position/floor
     :exercise/media-file "marching-glute-bridge.webm"
     :exercise/target-muscles #{:abs :legs}}

    {:exercise/name "Fire Hydrants"
     :exercise/intensity 1
     :exercise/position :position/floor
     :exercise/media-file "fire-hydrants.webm"
     :exercise/target-muscles #{:legs}}

    {:exercise/name "Bird Dog Crunch"
     :exercise/intensity 1
     :exercise/position :position/floor
     :exercise/two-sided? true
     :exercise/media-file "bird-dog-crunch.webm"
     :exercise/target-muscles #{:abs}}

    {:exercise/name "Dead Bug"
     :exercise/intensity 1
     :exercise/position :position/floor
     :exercise/media-file "dead-bug.webm"
     :exercise/target-muscles #{:arms :legs}}})
