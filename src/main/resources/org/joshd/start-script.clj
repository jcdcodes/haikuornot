;; haiku-web startup script
(ns app)

(print "Loading swank...")
(use 'swank.swank)
(println "Swank is loaded.")
(print "Starting swank...")
(swank.swank/start-server "/dev/null" :port 4005 :host "127.0.0.1")
(println "Swank is started.")

(println)

(println "Loading compojure...")
(use 'compojure)
(println "Compojure is loaded.")
(print "Loading HaikuWeb...")
(use 'org.joshd.HaikuWeb)
(println "HaikuWeb is loaded")
(print "Starting compojure (haiku-web)...")
(defserver server {:host "127.0.0.1" :port 8080} "/*" (servlet haiku-web))
(let [request-log-handler (org.mortbay.jetty.handler.RequestLogHandler.)
      log (org.mortbay.jetty.NCSARequestLog.)]
  (.setRequestLog request-log-handler log)
  (.setFilename log "logs/yyyy_mm_dd.request.log")
  (.setRetainDays log 365)
  (.setAppend log true)
  (.setExtended log true)
  (.setLogTimeZone log "GMT")
  (.addHandler server request-log-handler))
(start server)
(println "Compojure is started.")

(println)