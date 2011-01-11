haikuornot
==========

Counts syllables of your (English) triplet to determine whether is
haiku.  I mean haiku in the "5-7-5 syllables" sense that
English-speaking computer programmers mean, not in the proper sense
that literate Japanese mean.

If you run an internet haiku contest please consider vetting the entries against this webapp or something like it[1].

Two and a half approaches
-------------------------

The first way it counts syllables is by looking up the answer in a big
file from the [CMUSphinx][2] project.

The second-and-a-half way is by counting vowel runs and using a
[Porter stemmer][1].  The Porter stemmer is a thirty-year-old
algorithm for finding the common stem of similar English words; it can
tell you that _walk, walked,_ and _walking_ all have _walk_ as their
stem.  It's useful for text searching and indexing and stuff.  It
turns out also to work pretty well for improving naive syllable
counts.  For example, a naive count of vowel runs in the word _hoeing_
erroneously predicts that there's one syllable; identifying the _-ing_
suffix, noting that it's a syllable, and then counting the one
syllable in the stem _ho_ gives the correct count of two syllables.

[1]: http://tartarus.org/~martin/PorterStemmer/
[2]: http://cmusphinx.sourceforge.net/

The Porter stemmer is in raw Java.  The rest is Clojure.

Not dissimilar approaches might be found elsewhere on github.


One annoying problem
--------------------

So English contains a bunch of words that can, for the same spelling, have a different number of syllables depending on the context, the speaker, and sometimes the speaker's whim:

* _moped:_  "I _moped_ about the theft of my _moped._"
* _blessed:_  Can be one or two syllables depending, perhaps, on who did the blessing.
* _doing:_  "If he keeps _doing_ that to the rubber band it will snap with a loud _doing!_"
* _Porsche:_ Words brought to English from other languages are often pronounced differently by different folks. 
* In Texas, _elm_ might have two syllables, and _chimney_ might have three. In Boston, _settler_ might have two syllables; elsewhere three.
* and so forth

I have chosen so far to ignore this problem.


Usage
-----

Assuming you have Maven (2.x) all set up, you can download the dependencies and run the webapp by typing:

    mvn clojure:run

and visiting http://localhost:8080.  The first one you submit will be slow while the app parses the `cmudict.txt` file.

This code is running on [http://www.daghlian.com/haiku][3] so you can try it for yourself.

[3]: http://www.daghlian.com/haiku

License
-------

I used Dr. Porter's stemmer code, which is in the public domain, so all of this is, too.  Drop me a line if you do anything interesting with it.


[1] I actually wrote this because there was this one week during which Daring Fireball and boingboing both featured clever little haikus that weren't even 5-7-5 (and definitely weren't Japanese-style poetry).

