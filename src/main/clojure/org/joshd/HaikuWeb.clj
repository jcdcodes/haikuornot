(ns org.joshd.HaikuWeb
  (:use compojure)
  (:gen-class
   :extends javax.servlet.http.HttpServlet))

(defroutes haiku-web
  (GET "/foo/bar*" (html [:h1 "bar bar bar bar bar/bar bar bar bar bar bar bar/bar bar bar bar bar"]))
  (GET "/foo*" (html [:h1 "FOOOOOOOOOOOOO"]))
  (ANY "*" (html [:h1 "Visualize org.joshd.HaikuWeb in 5-7-5."]))
)

(defservice haiku-web)
