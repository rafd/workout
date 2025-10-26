(ns workout.exercises.custom)

(def exercises
  #{
    {:exercise/name "Glute bridge"
     :exercise/media-file "glute-bridge.webm"}
    
    {:exercise/name "Monster walk"
      :exercise/media-file "monster-walk.webm"}
    
    {:exercise/name "Squat"
     :exercise/self-paced? true}
    
    {:exercise/name "face pull"}

    {:exercise/name "Band pull aparts"
     :exercise/media-file "posture-band-pull.webm"
     :exercise/instructions "Thumbs out"}
    
    {:exercise/name "Bench or Overhead press"
     :exercise/self-paced? true}
    
    {:exercise/name "Deadlift"
     :exercise/self-paced? true}
        
    {:exercise/name "Paloff press"
     :exercise/media-file "paloff.png"}
    
    {:exercise/name "Door Twist"
     :exercise/two-sided? true
     :exercise/instructions "Elbow against door frame; twist chest open; hand back"
     :exercise/media-file "posture-door-chest-twist.webm"}
  })
