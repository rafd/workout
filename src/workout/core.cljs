(ns ^:figwheel-hooks workout.core 
  (:require
    [bloom.omni.reagent :as omni]
    [workout.ui :as ui]))

(enable-console-print!)

(defn render
  []
  (omni/render [ui/app-view]))

(defn ^:export init
  []
  (render))

(defn ^:after-load reload
  []
  (render))
