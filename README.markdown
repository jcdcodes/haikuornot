haikuornot
==========

Josh's project whereby he learns how to rig up maven, clojure,
compojure, and some hand-rolled code.

It incidentally tells you whether it's been fed a haiku or not.

This is a clojure project that's the result of running:

     mvn archetype:create -DgroupId=org.daghlian
                          -DartifactId=foo
                          -DarchetypeGroupId=com.stuartsierra
                          -DarchetypeArtifactId=clojure-archetype
                          -DarchetypeVersion=1.0-SNAPSHOT

on a command line; then tweaking a number of the files within.

Running `mvn clean package` downloads stuff and successfully
(pyhrrically) runs tests (Running `mvn clojure:test` also works;
another empty victory) and `mvn clojure:run` runs the webapp.

Good times.

