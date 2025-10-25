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
     :exercise/self-paced? true
     :exercise/media-url "https://static.exercisedb.dev/media/ila4NZS.gif"}
        
    {:exercise/name "Paloff press"
     :exercise/media-file "paloff.png"}
  })
