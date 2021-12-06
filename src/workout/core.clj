(ns workout.core
  (:gen-class)
  (:require
    [bloom.omni.core :as omni]
    [bloom.commons.config :as config]))

(def config
  (config/read
    "config.edn"
    [:map
     [:http-port integer?]
     [:environment keyword?]]))

(def omni-config
  {:omni/http-port (:http-port config)
   :omni/environment (:environment config)
   :omni/title "Workout"
   :omni/cljs {:main "workout.core"}})

(defn start! []
  (omni/start! omni/system omni-config))

(defn stop! []
  (omni/stop!))

(defn -main []
  (start!))
