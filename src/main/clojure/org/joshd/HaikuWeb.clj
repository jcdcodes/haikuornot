(ns org.joshd.HaikuWeb
  (:use compojure)
  (:use org.joshd.syllables)
  (:use org.joshd.history)
  (:use org.joshd.css)
  (:use clojure.contrib.logging)
  (:gen-class
   :extends javax.servlet.http.HttpServlet))

(def *chr* {\< "&lt;" \> "&gt;" \& "&amp;"})
(defn sanitize-html
  [raw-text]
  (apply str (map #(let [c (*chr* %)]
		     (if (nil? c) % c))
		  raw-text)))


(defn header
  []
  (let [now (str (java.util.Date.))]
    [:div
     [:div {:class "header"}
      (str "[Logo here]" " " now " ")]
     [:div {:class "logo"} [:a {:href "/"} "Am I Haiku or Not?"]]
     ]))

(defn html-document
  [title & body]
  (html [:head [:title title]
	 [:style (org.joshd.css/style)]]
	[:body (header) body]))

(defn generate-report-page
  [params]
  (let [candidate (sanitize-html (params :candidate))
	syllables (count-syllables candidate)
	haiku-p (= syllables '(5 7 5))
	lines (.split candidate "/")]
    (org.joshd.history/observe-submission candidate haiku-p)
    (info (str "HC: " (into [] syllables) ": " candidate))
    (html-document "Haiku or Not?"
	  [:div {:align "center"}
	   [:p "You wrote:" [:blockquote [:pre candidate]] "and Haikubot says:"]
	   (if haiku-p
	     [:div {:class "success"} [:h1 "It's a haiku."]]
	     [:div {:class "failure"} [:h1 "Not a haiku."]])
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
  [:h3 "Recent non-haiku"]
  (into [:ul]
	(map (fn [x] [:li [:pre x]])
	     @org.joshd.history/*recent-failures*))))

(def about [:div [:h3 "What is this?"] 

  [:p "I got tired of reading online haiku contest entries that didn't
  even meet the usual 5-7-5 syllable count, so I built the Haikubot,
  which counts (English) syllables and verifies that a piece of text
  has the right 5-7-5 meter.  It led to me learning all about Porter
  stemmers and syllable counting, and about " [:a
  {:href "http://cmusphinx.org"} "speech recognition"] "."]

  [:p "Furthermore, I needed a nontrivial project on which to try my
  hand at integrating maven, jetty, clojure, and slime.  So far so
  good."]

  [:p "You are certain to find errors.  Let me know: "
   [:a {:href "mailto:daghlian@gmail.com"} "daghlian@gmail.com"]]

])

(defn haiku-entry-form
  []
  (html-document "Haiku or Not?"
        [:div {:align "center"}
	 [:h1 "Am I haiku or not?"]
	 [:form {:action "/count" :method "POST"}
	  [:input {:type "text" :name "candidate" :width "50em" :size 70
		   :value "type in a haiku/if you get the meter right/i will tell you so"}]
	  [:br]
	  [:input {:type "submit" :value "Check haiku-ness"}]]]
	about
	(recent-successes)
	(recent-failures)
	))

;;note: i love my nerd man/even though he is a nerd/he is wicked cute

(defroutes haiku-web
  (GET "/about*" (html-document "About the Haikubot" about))
  (POST "/count" (generate-report-page params))
  (GET "/" (haiku-entry-form))
  (ANY "*" (haiku-entry-form)) ;;;(html [:h1 "Visualize org.joshd.HaikuWeb in 5-7-5."]))
)

(defservice haiku-web)

