(ns org.joshd.css)

;(defn style [] "")
(defn style
  [] (apply str (map #(if (not (= (str %) "\n")) % " ") (.trim "
body {
 font-family:\"Gill Sans\",\"Trebuchet MS\",helvetica,arial,sans-serif;
}
div.header
{
 text-align:center;
 font-size:75%;
 float:left;
}

h1 {
 font-size:250%;
}

div.logo
{
 float:right;
}

div.failure
{
 color:darkRed;
 font-size:200%;
 font-weight:bold;
}

div.success
{
  color:green;
  font-size:200%;
  font-weight:bold;
}
"))))
