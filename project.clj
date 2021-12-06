(defproject workout "0.0.1"
  :dependencies [[io.bloomventures/omni "0.27.7"]]

  :plugins [[io.bloomventures/omni "0.27.7"]]

  :main workout.core

  :omni-config workout.core/omni-config

  :profiles {:uberjar
             {:aot :all
              :prep-tasks [["omni" "compile"]
                           "compile"]}})
