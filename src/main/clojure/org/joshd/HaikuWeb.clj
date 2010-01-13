(ns org.joshd.HaikuWeb
  (:use compojure org.joshd.syllables)
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
   [:p "Ignoring " (count params) " parameters."]
   [:blockquote "All the redefinitions work fine. It's like magic, and it makes me want to cry when I consider how much of my professional life has been wasted awaiting minutes-long build cycles just to see the sorts of changes that I can now see as fast as I can alt-tab over to the browser.  " [:em "Damn."]]))

(defn generate-report-for
  [params]
  (let [candidate (params :candidate)
	syllables (count-syllables candidate)
	lines (.split candidate "/")]
    (html [:div {:align "center"}
	   [:p "You wrote:" [:blockquote [:pre candidate]] "and Haikubot says:"]
	   (if (= syllables '(5 7 5))
	     [:div {:style "color:green;font-size:200%;font-weight:bold"} [:h1 "It's a haiku!"]]
	     [:div {:style "color:red;background-color:black;font-size:200%;font-weight:bold"} [:h1 "Not a haiku"]])
	   [:table {:cellspacing 1 :cellpadding 5 :border "1px green"}
	    (map (fn [x] [:tr [:td (first x)] [:td (second x)]])
		 (map (fn [a b] [a b]) syllables lines))]
	   [:p {:style "font-size:150%"} [:a {:href "/count"} "Try another"]]])))

(def about [:div [:h2 "What's this all about?"] 

[:p "I got tired of reading online haiku contest entries that didn't
even meet the usual 5-7-5 syllable count.  It led to waaay to much
time being spent learning about Porter stemmers and syllable counting,
and about the " [:a {:href "http://cmusphinx.org"} "CMU Sphinx speech
recognition"] " project at Carnegie Mellon."]

[:p "Furthermore, I needed a nontrivial project on which to try my
hand at integrating maven, jetty, clojure, and slime.  It all worked
pretty well."]

[:p "You are certain to find errors.  Let me know: " [:a
{:mailto "daghlian@gmail.com"} "daghlian@gmail.com"]]

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
	 about))

(defroutes haiku-web
  (GET "/foo/bar*" (html [:h1 "bar bar bar bar bar/bar bar bar bar bar bar bar/bar bar bar bar bar"]))
  (GET "/foo/:name" (greet (params :name)))
  (GET "/foo*" (html [:h1 "FOOOOOOOOOOOOO"]))
  (GET "/about*" (about params))
  (GET "/count" (haiku-entry-form))
  (GET "/" (haiku-entry-form))
  (POST "/count" (generate-report-for params))
  (ANY "*" (html [:h1 "Visualize org.joshd.HaikuWeb in 5-7-5."]))
)

(defservice haiku-web)

