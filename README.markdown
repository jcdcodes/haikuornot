foo
===

This is a clojure project that's the result of running:

    `mvn archetype:create -DgroupId=org.daghlian
                          -DartifactId=foo
			  -DarchetypeGroupId=com.stuartsierra
			  -DarchetypeArtifactId=clojure-archetype
			  -DarchetypeVersion=1.0-SNAPSHOT`

on a command line; then tweaking a number of the files within.

So far I've verified that `mvn clean install` downloads stuff and successfully (pyhrrically) runs tests
(Running `mvn clojure:test` also works) and `mvn clojure:run` runs app.clj and then exits.  Good times.

It's a template project
-----------------------

This is a hello world clojure project.  I'll test it out by trying to do some kind of nontrivial web app...
