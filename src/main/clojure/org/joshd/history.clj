(ns org.joshd.history)

(def *recent-successes* (ref '()))
(def *recent-failures* (ref '()))

(defn observe-submission
  [submission haiku-p]
  (let
      [recent (if haiku-p *recent-successes* *recent-failures*)]
    (dosync
     (commute recent conj submission)
     (if (> (count @recent) 10)
       (commute recent drop-last))
     )
    @recent))


