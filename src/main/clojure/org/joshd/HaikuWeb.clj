(ns org.joshd.HaikuWeb
  (:use compojure)
  (:gen-class
   :extends javax.servlet.http.HttpServlet))

(defn greet
  [name]
  (html
    [:h1 "Hi again, " name]
    [:p "Isn't this a nicely metered poem, " name "?"]
    [:p "(Well, isn't it?)"]
    ))

(defn about
  [params]
  (html
   [:h1 "About Josh's crazy Haiku generator"]
   [:p "There's nothing all that fabulous about the Haiku generator yet.  There are pieces that run,
but mostly it's just a bunch of maven and clojure and compojure and slime and swank configuration that
(astonishingly!) seems to work together reasonably well.  This text is an experiment to see whether
we can modify " [:tt "defroutes "] "blocks as easily as we modify plain old methods to which the
routes delegate."]
   [:p "(Can you tell I'm pretty pleased?)"]
   [:p "Ignoring " (count params) " parameters."]))

(defroutes haiku-web
  (GET "/foo/bar*" (html [:h1 "bar bar bar bar bar/bar bar bar bar bar bar bar/bar bar bar bar bar"]))
  (GET "/foo/:name" (greet (params :name)))
  (GET "/foo*" (html [:h1 "FOOOOOOOOOOOOO"]))
  (GET "/about*" (about params))
  (ANY "*" (html [:h1 "Visualize org.joshd.HaikuWeb in 5-7-5."]))
)

(defservice haiku-web)

