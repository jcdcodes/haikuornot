(ns org.joshd.css)

(defn style
  [] (apply str (map #(if (not (= (str %) "\n")) % " ") (.trim "
div {
  border: 1px solid yellow;
}

div.header
{
 text-align:center;
 font-size:75%;
 float:left;
}

div.logo
{
 float:right;
}

div.failure
{
 color:red;
 background-color:black;
 font-size:200%;
 font-weight:bold;
}

div.success
{
  color:green;
  font-size:200%;
  font-weight:bold;
}

p
{
  border: 1px dotted pink;
}

h1
{
  border: 1px dotted blue;
}

h3
{
  border: 1px dotted green;
}

ul
{
 border: 1px dotted yellow;
}

"))
))