(ns workout.routines.posture)

(def door-twist {:exercise/name "Door Twist. Elbow against door frame; twist chest open; hand back"
                 :exercise/two-sided? true
                 :exercise/media-file "posture-door-chest-twist.webm"})

(def routine
  [door-twist

   {:exercise/name "Open Chest Roll"
    :exercise/media-file "posture-open-chest-roll.webm"}

   door-twist

   {:exercise/name "Band Raise. Some tension, thumbs up"
    :exercise/media-file "posture-band-up.webm"}

   door-twist

   {:exercise/name "Band Pull Aparts. Thumbs out"
    :exercise/media-file "posture-band-pull.webm"}

   {:exercise/name "Door Shoulder Stretch. Shoulder against door frame; pinch shoulder blades; feet offset; raise and lower arm"
    :exercise/two-sided? true
    :exercise/media-file "posture-door-arm-up.webm"}])
  