(ns app
  (:use swank.swank)
  (:gen-class))

(defn main [& args]
  (println "This is the main function for app."))


(defn start-swank
  []
  (swank.swank/start-server "/dev/null" :port 4005))
