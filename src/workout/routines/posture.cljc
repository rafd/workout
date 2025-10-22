(ns workout.routines.posture)

(def door-twist {:name "Door Twist. Elbow against door frame; twist chest open; hand back"
                 :two-sided? true
                 :filename "posture-door-chest-twist.webm"})

(def routine
  [door-twist

   {:name "Open Chest Roll"
    :filename "posture-open-chest-roll.webm"}

   door-twist

   {:name "Band Raise. Some tension, thumbs up"
    :filename "posture-band-up.webm"}

   door-twist

   {:name "Band Pull Aparts. Thumbs out"
    :filename "posture-band-pull.webm"}

   {:name "Door Shoulder Stretch. Shoulder against door frame; pinch shoulder blades; feet offset; raise and lower arm"
    :two-sided? true
    :filename "posture-door-arm-up.webm"}])
  