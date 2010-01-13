(ns org.joshd.syllables
  (:import
   (java.io BufferedReader StringReader)
   (org.joshd Stemmer)
   )
  (:require [clojure.contrib.str-utils2 :as str-utils] [clojure.set :as set]))


;(def s (Stemmer. "infalliability"))
;(print s)
;(println (+ (.countSyllables s) (.getDroppedSyllableCount s)))

;; The Sphinx project Carnegie Mellon has (had?) a gigantic speech recognition
;; library, including a big old word-->phoneme lookup table that I've copied locally.
;; Slurp it, examine the number of syllabes for each, and construct an in-memory
;; map of word to syllable count.

(defn- count-syllables-in-line
  [line]
  (let [digits (re-pattern "[0-9]")]
    (.size (re-seq digits line))))

(defn- word-in-line
  [line]
  (let [word-at-line-start (re-pattern "^[A-Z]*[^ ]*")]
    (re-find word-at-line-start line)))

(defn- map-words-to-syllable-counts
  "Map words to number of syllables.  Now much faster due to proper use of into."
  [lines]
  (into {} (map #(let [k (word-in-line %) v (count-syllables-in-line %)] [k v]) lines)))

(def *syllables-for* (let
  [cmudict (slurp "src/main/resources/org/joshd/cmudict.0.7a.txt")
   lines (line-seq (BufferedReader. (StringReader. cmudict)))
   word-lines (filter #(re-find (re-pattern "^[A-Z].*") %) lines)
   the-map (map-words-to-syllable-counts word-lines)]
  the-map))

;; now some cleanup
(def letters (set (.split "A B C D E F G H I J K L M N O P Q R S T U V W X Y Z" " ")))

(def *syllables-for*
     (into (into (into *syllables-for* 
		       (map #(let [k % v 1] [k v]) letters))
		 [["W" 3]])
	   [[" " 0] ["" 0]]))

;;
;; TODO: improve syllable counting
;; 0) Strip out punctuation wisely. (hyphens->two words; acronyms->letters; apostrophes->nothing)
;; 0) Numbers --> words, which we then look up
;; 1) Look up in map of known values (Done.)
;; 2) Porter-stem the word down, look up the root, re-add stemmed-out syllables.
;; 3a) Count runs of vowels other than trailing silent E.
;; 3b) Guess that it's just letters --> acronym w/o periods --> letters
;;
(def vowels (set ["A" "E" "I" "O" "U"]))
(def consonants (set/difference letters vowels))
(defn to-cv
  [upword]
  (apply str (map #(if (vowels (str %)) "v" "c") upword)))
(defn count-vowel-runs
  [vcword]
  (count (re-seq #"(^v|cv)" vcword)))
(defn guess-syllables
  [upword]
  (count-vowel-runs (to-cv upword)))

(defn stemmer-guess-syllables
  [word]
  (print "**")
  (let [s (Stemmer. (.toLowerCase word))]
    (+ (.countSyllables s) (.getDroppedSyllableCount s))))

(defn to-syllables
  "This is the meat of the word->syllable mapping.  It makes no attempt to guess
     when it doesn't have an explicit match, but should."
  [word]
  (let [v (*syllables-for* (.toUpperCase word))]
    (if v v (stemmer-guess-syllables word))))


(doall (map #(println (str % ": " (to-syllables %)))
  (seq (.split "Who didn't put the bhomp in the miami montpelier. pedomp? monkey monkeymonkey" " "))))

;;
;; Methods for breaking text up into guessable components
;;
(defn syllable-seq
  [text]
  (map to-syllables (seq (. text split " "))))

(defn count-syllables-helper
  [text]
  (reduce #(+ %1 %2) 0 (syllable-seq (.trim text))))

(defn count-syllables
  [text]
  (map count-syllables-helper (.split text "/")))

(defn haiku?
  [text]
  (= (count-syllables text) '(5 7 5)))


;;;;;;;;;;;;;;;;;;
;; tests follow ;;
;;;;;;;;;;;;;;;;;;
(print (list
;; Should be all false:
(map haiku? '(
              "How much wood/could a woodchuck chuck/if a woodchuck could chuck wood"
              "Roses are red/Violets are blue/Sugar is sweet/and so are you"
              "Cheater cheater/pants on fire/Couldn't fit through the bathroom door"
              ))

;; Should be all true:
(map haiku? '(
              "The web site you seek/cannot be located but/countless more exist"
              "Three things are certain/death taxes and lost data/Guess which has occurred"
              "The tao that is seen/is not the true tao until/you bring fresh toner"
              "A crash reduces/your expensive computer/to a simple stone"
              "Windows N T crashed/I am the blue screen of death/No one hears your screams"
              "Having been erased/the document you're seeking/must now be re typed"
              "How much fucking wood/could a woodchuck chuck if a/woodchuck could chuck wood"
              ))
))