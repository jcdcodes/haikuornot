foo
===

Josh's project whereby he learns how to rig up maven, clojure, compojure, and some hand-rolled code.

This is a clojure project that's the result of running:

     mvn archetype:create -DgroupId=org.daghlian
                          -DartifactId=foo
                          -DarchetypeGroupId=com.stuartsierra
                          -DarchetypeArtifactId=clojure-archetype
                          -DarchetypeVersion=1.0-SNAPSHOT

on a command line; then tweaking a number of the files within.

So far I've verified that `mvn clean install` downloads stuff and
successfully (pyhrrically) runs tests (Running `mvn clojure:test` also
works) and `mvn clojure:run` runs app.clj and then exits.  Good times.

It's a template project
-----------------------

This is a hello world clojure project.  I'll test it out by trying to
do some kind of nontrivial web app, and will probably modify it in place accordingly, but know that it's a decent template project as of the second checkin.

It runs compojure on jetty
--------------------------

I followed the [hello-mvn-clj][] example and played around a bit from there.  I got to the point of configuring the `mvn-jetty-plugin` to my contentment, including setting the default context-path to `/` and setting up a decent HTTP log.

To start the webapp do this:

`mvn jetty:run`

At some point we'll do smoething



[hello-mvn-clj]: http://bitbucket.org/jimdowning/hello-mvn-clj
