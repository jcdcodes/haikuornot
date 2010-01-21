(ns org.joshd.HaikuWeb
  (:use compojure)
  (:use org.joshd.syllables)
  (:use clojure.contrib.logging)
  (:gen-class
   :extends javax.servlet.http.HttpServlet))

(defn generate-report-for
  [params]
  (let [candidate (params :candidate)
	syllables (count-syllables candidate)
	haiku-p (= syllables '(5 7 5))
	lines (.split candidate "/")]
    (org.joshd.history/observe-submission candidate haiku-p)
    (info (str "HC: " (into [] syllables) ": " candidate))
    (html [:div {:align "center"}
	   [:p "You wrote:" [:blockquote [:pre candidate]] "and Haikubot says:"]
	   (if haiku-p
	     [:div {:style "color:green;font-size:200%;font-weight:bold"} [:h1 "It's a haiku."]]
	     [:div {:style "color:red;background-color:black;font-size:200%;font-weight:bold"} [:h1 "Not a haiku."]])
	   [:table {:cellspacing 1 :cellpadding 5 :border "1px green"}
	    (map (fn [x] [:tr [:td (first x)] [:td (second x)]])
		 (map (fn [a b] [a b]) syllables lines))]
	   [:p {:style "font-size:150%"} [:a {:href "/"} "Try another"]]])))


(defn recent-successes
  []
  (html
  [:h3 "Recent haiku"]
  (into [:ul] (map (fn [x] [:li [:pre x]]) @org.joshd.history/*recent-successes*))))

(defn recent-failures
  []
  (html
  [:h3 "Recent failure"]
  (into [:ul] (map (fn [x] [:li [:pre x]]) @org.joshd.history/*recent-failures*))))

(def about [:div [:h3 "What is this?"] 

  [:p "I got tired of reading online haiku contest entries that didn't
  even meet the usual 5-7-5 syllable count, so I built the Haikubot,
  which counts (English) syllables and verifies that a piece of text
  has the right 5-7-5 meter.  It led to me learning all about Porter
  stemmers and syllable counting, and about the " [:a
  {:href "http://cmusphinx.org"} "CMU Sphinx speech recognition"] "
  project at Carnegie Mellon."]

  [:p "Furthermore, I needed a nontrivial project on which to try my
  hand at integrating maven, jetty, clojure, and slime.  So far so
  good."]

  [:p "You are certain to find errors.  Let me know: "
   [:a {:href "mailto:daghlian@gmail.com"} "daghlian@gmail.com"]]

])

(defn haiku-entry-form
  []
  (html [:div {:align "center"}
	 [:h1 "Am I haiku or not?"]
	 [:form {:action "/count" :method "POST"}
	  [:input {:type "text" :name "candidate" :width 500 :size 100
		   :value "type in a haiku/if you get the meter right/i will tell you so"}]
	  [:br]
	  [:input {:type "submit" :value "Check haiku-ness"}]]]
	about
	(recent-successes)
	(recent-failures)
	))

;;note: i love my nerd man/even though he is a nerd/he is wicked cute

(defroutes haiku-web
  (GET "/foo*" (html [:h1 "FOOOOOOOOOOOOO"]))
  (GET "/about*" (about params))
  (GET "/" (haiku-entry-form))
  (POST "/count" (generate-report-for params))
  (ANY "*" (html [:h1 "Visualize org.joshd.HaikuWeb in 5-7-5."]))
)

(defservice haiku-web)

