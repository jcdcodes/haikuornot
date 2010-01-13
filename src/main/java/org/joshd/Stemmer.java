/*

   Porter stemmer in Java. The original paper is in

       Porter, 1980, An algorithm for suffix stripping, Program, Vol. 14,
       no. 3, pp 130-137,

   See also http://www.tartarus.org/~martin/PorterStemmer

   History:

   Release 1

   Bug 1 (reported by Gonzalo Parra 16/10/99) fixed as marked below.
   The words 'aed', 'eed', 'oed' leave k at 'a' for step 3, and b[k-1]
   is then out outside the bounds of b.

   Release 2

   Similarly,

   Bug 2 (reported by Steve Dyrdahl 22/2/00) fixed as marked below.
   'ion' by itself leaves j = -1 in the test for 'ion' in step 5, and
   b[j] is then outside the bounds of b.

   Release 3

   Considerably revised 4/9/00 in the light of many helpful suggestions
   from Brian Goetz of Quiotix Corporation (brian@quiotix.com).

   Release 4

*/
package org.joshd;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Stemmer, implementing the Porter Stemming Algorithm
 *
 * The Stemmer class transforms a word into its root form.  The input
 * word can be provided a character at time (by calling addChar()), or at once
 * by calling one of the various stem(something) methods.
 */
public class Stemmer
{  private char[] buf;
   private int i,     /* offset into buf */
               i_end, /* offset to end of stemmed word */
               j, k;
   private int droppedSyllableCount;

   public Stemmer(String word) {
      buf = word.toCharArray();
      i = word.length();
      i_end = 0;
      stem();
   }

   /**
    * After a word has been stemmed, it can be retrieved by toString(),
    * or a reference to the internal buffer can be retrieved by getResultBuffer
    * and getResultLength (which is generally more efficient.)
    */
   public String toString() { return new String(buf,0,i_end) + " " + countSyllables(); }

   public int getDroppedSyllableCount() {
      return droppedSyllableCount;
   }

   /** isConsonant(i) is true <=> buf[i] is a consonant. */
   private boolean isConsonant(int i)
   {  switch (buf[i])
      {  case 'a': case 'e': case 'i': case 'o': case 'u': return false;
         case 'y': return (i == 0) || !isConsonant(i - 1);
         default: return true;
      }
   }

   /**
    * countConsonantSequences() measures the number of consonant sequences between 0 and j. if c is
    * a consonant sequence and v a vowel sequence, and <..> indicates arbitrary
    * presence,
    *
    *    <c><v>       gives 0
    *    <c>vc<v>     gives 1
    *    <c>vcvc<v>   gives 2
    *    <c>vcvcvc<v> gives 3
    *    ....
    */
   public int countConsonantSequences() { return countConsonantSequences(false); }
   public int countSyllables() { return Math.max(1, countConsonantSequences(true)); }
   private int countConsonantSequences(boolean countSyllables)
   {  int n = 0;
      int i = 0;
      while(true)
      {  if (i > j) return n;
         if (! isConsonant(i)) break; i++;
      }
      i++;
      while(true)
      {  while(true)
         {  if (i > j) return n + (countSyllables && buf[i-1]!='e' ? 1 : 0);
               if (isConsonant(i)) break;
               i++;
         }
         i++;
         n++;
         while(true)
         {  if (i > j) return n;
            if (! isConsonant(i)) break;
            i++;
         }
         i++;
       }
   }

   /** isVowelInStem() is true <=> 0,...j contains a vowel */
   private boolean isVowelInStem()
   {  for (int i = 0; i <= j; i++) if (! isConsonant(i)) return true;
      return false;
   }

   /** isDoubleConsonant(j) is true <=> j,(j-1) contain a double consonant. */
   private boolean isDoubleConsonant(int j)
   {  if (j < 1) return false;
      if (buf[j] != buf[j-1]) return false;
      return isConsonant(j);
   }

   /**
    * isCVC(i) is true <=> i-2,i-1,i has the form consonant - vowel - consonant
    * and also if the second c is not w,x or y. this is used when trying to
    * restore an e at the end of a short word. e.g.
    *
    * cav(e), lov(e), hop(e), crim(e), but
    * snow, box, tray.
    *
    */
   private boolean isCVC(int i)
   {  if (i < 2 || !isConsonant(i) || isConsonant(i-1) || !isConsonant(i-2)) return false;
      {  int ch = buf[i];
         if (ch == 'w' || ch == 'x' || ch == 'y') return false;
      }
      return true;
   }

   private boolean ends(String s)
   {  int l = s.length();
      int o = k-l+1;
      if (o < 0) return false;
      for (int i = 0; i < l; i++) if (buf[o+i] != s.charAt(i)) return false;
      j = k-l;
      return true;
   }

   /**
    * setto(s) sets (j+1),...k to the characters in the string s, readjusting
    * k.
    */
   private void setto(String s)
   {  int l = s.length();
      int o = j+1;
      for (int i = 0; i < l; i++) buf[o+i] = s.charAt(i);
      k = j+l;
   }

   /**
    * r(s) is used further down.
    */
   private void r(String s, int syllablesToDrop) {
      if (countConsonantSequences() > 0) {
         setto(s);
         droppedSyllableCount += syllablesToDrop;
      }
   }

   /**
    *  step1() gets rid of plurals and -ed or -ing. e.g.
    *
    * caresses  ->  caress
    *      ponies    ->  poni
    *      ties      ->  ti
    *      caress    ->  caress
    *      cats      ->  cat
    *
    *      feed      ->  feed
    *      agreed    ->  agree
    *      disabled  ->  disable
    *
    *      matting   ->  mat
    *      mating    ->  mate
    *      meeting   ->  meet
    *      milling   ->  mill
    *      messing   ->  mess
    *
    *      meetings  ->  meet
    */
   private void step1()
   {  if (buf[k] == 's')
      {  if (ends("sses")) { k -= 2; droppedSyllableCount += 1; } else
         if (ends("ies")) setto("i"); else
         if (buf[k-1] != 's') k--;
      }
      if (ends("eed")) { if (countConsonantSequences() > 0) { k--; droppedSyllableCount += 1; } } else

      if (isVowelInStem()) {
         if (ends("ed")) {
            k = j;
            if (ends("at")) { setto("ate"); droppedSyllableCount += 1; } else
            if (ends("bl")) { setto("ble"); } else
            if (ends("iz")) { setto("ize"); } else
            if (isDoubleConsonant(k))
            {  k--;
               {  int ch = buf[k];
                  if (ch == 'l' || ch == 's' || ch == 'z') k++; else droppedSyllableCount += 1;
               }
            }
            else if (countConsonantSequences() == 1 && isCVC(k)) { setto("e"); } else droppedSyllableCount += 1;
         } else if (ends("ing")) {
            k = j;
            if (ends("at")) { setto("ate"); } else
            if (ends("bl")) { setto("ble"); } else
            if (ends("iz")) { setto("ize"); } else
            if (isDoubleConsonant(k))
            {  k--;
               {  int ch = buf[k];
                  if (ch == 'l' || ch == 's' || ch == 'z') k++;
               }
            }
            else if (countConsonantSequences() == 1 && isCVC(k)) { setto("e"); }
            droppedSyllableCount++;
         }
      }
   }

   /**
    *  step2() turns terminal y to i when there is another vowel in the stem.
    */
   private void step2() { if (ends("y") && isVowelInStem()) buf[k] = 'i'; }

   /**
    * step3() maps double suffices to single ones. so -ization ( = -ize plus
    * -ation) maps to -ize etc. note that the string before the suffix must give
    * countConsonantSequences() > 0.
    */
   private void step3() { if (k == 0) return; /* For Bug 1 */ switch (buf[k-1])
   {
       case 'a': if (ends("ational")) { r("ate", 2); break; }
                 if (ends("tional")) { r("tion", 1); break; }
                 break;
       case 'c': if (ends("enci")) { r("ence", 1); break; }
                 if (ends("anci")) { r("ance", 1); break; }
                 break;
       case 'e': if (ends("izer")) { r("ize", 1); break; }
                 break;
       case 'l': if (ends("bli")) { r("ble", 1); break; }
                 if (ends("alli")) { r("al", 1); break; }
                 if (ends("entli")) { r("ent", 1); break; }
                 if (ends("eli")) { r("e", 1); break; }
                 if (ends("ousli")) { r("ous", 1); break; }
                 break;
       case 'o': if (ends("ization")) { r("ize", 2); break; }
                 if (ends("ation")) { r("ate", 1); break; }
                 if (ends("ator")) { r("ate", 1); break; }
                 break;
       case 's': if (ends("alism")) { r("al", 2); break; }
                 if (ends("iveness")) { r("ive", 1); break; }
                 if (ends("fulness")) { r("ful", 1); break; }
                 if (ends("ousness")) { r("ous", 1); break; }
                 break;
       case 't': if (ends("aliti")) { r("al", 2); break; }
                 if (ends("iviti")) { r("ive", 2); break; }
                 if (ends("biliti")) { r("ble", 2); break; }
                 break;
       case 'g': if (ends("logi")) { r("log", 1); break; }
   } }

   /** step4() deals with -ic-, -full, -ness etc. similar strategy to step3. */
   private void step4() { switch (buf[k])
   {
       case 'e': if (ends("icate")) { r("ic", 1); break; }
                 if (ends("ative")) { r("", 2); break; }
                 if (ends("alize")) { r("al", 1); break; }
                 break;
       case 'i': if (ends("iciti")) { r("ic", 2); break; }
                 break;
       case 'l': if (ends("ical")) { r("ic", 1); break; }
                 if (ends("ful")) { r("", 1); break; }
                 break;
       case 's': if (ends("ness")) { r("", 1); break; }
                 break;
   } }

   /** step5() takes off -ant, -ence etc., in context <c>vcvc<v>. */
   private void step5()
   {
      int dsc = 0;
      if (k == 0) return; /* for Bug 1 */ switch (buf[k-1])
       {  case 'a': if (ends("al")) { dsc = 1; break; } return;
          case 'c': if (ends("ance")) { dsc = 1; break; }
                    if (ends("ence")) { dsc = 1; break; } return;
          case 'e': if (ends("er")) { dsc = 1; break; } return;
          case 'i': if (ends("ic")) { dsc = 1; break; } return;
          case 'l': if (ends("able")) { dsc = 2; break; }
                    if (ends("ible")) { dsc = 2; break; } return;
          case 'n': if (ends("ant")) { dsc += 1; break; }
                    if (ends("ement")) { dsc += 2; break; }
                    if (ends("ment")) { dsc += 1; break; }
                    /* element etc. not stripped before the countConsonantSequences */
                    if (ends("ent")) { dsc += 1; break; } return;
          case 'o': if (ends("ion") && j >= 0 && (buf[j] == 's' || buf[j] == 't')) { dsc += 1; break; }
                                    /* j >= 0 fixes Bug 2 */
                    if (ends("ou")) { dsc += 1; break; } return;
                    /* takes care of -ous */
          case 's': if (ends("ism")) { dsc += 2; break; } return;
          case 't': if (ends("ate")) { dsc += 1; break; }
                    if (ends("iti")) { dsc += 2; break; } return;
          case 'u': if (ends("ous")) { dsc += 1; break; } return;
          case 'v': if (ends("ive")) { dsc += 1; break; } return;
          case 'z': if (ends("ize")) { dsc += 1; break; } return;
          default: return;
       }
       if (countConsonantSequences() > 1) {
          k = j;
          droppedSyllableCount += dsc;
       }
   }

   /** step6() removes a final -e if countConsonantSequences() > 1. */
   private void step6()
   {  j = k;
      if (buf[k] == 'e')
      {  int a = countConsonantSequences();
         if (a > 1 || a == 1 && !isCVC(k-1)) k--;
      }
      if (buf[k] == 'l' && isDoubleConsonant(k) && countConsonantSequences() > 1) { k--; }
   }

   /**
    * Stem the word placed into the Stemmer buffer through calls to addChar().
    * Returns true if the stemming process resulted in a word different
    * from the input.  You can retrieve the result with
    * getResultLength()/getResultBuffer() or toString().
    */
   private void stem()
   {  k = i - 1;
      if (k > 1) { step1(); step2(); step3(); step4(); step5(); step6(); }
      i_end = k+1; i = 0;
   }

   /**
    * Test program for demonstrating the Stemmer.  It reads text from a
    * a list of files, stems each word, and writes the result to standard
    * output. Note that the word stemmed is expected to be in lower case:
    * forcing lower case must be done outside the Stemmer class.
    * Usage: Stemmer file-name file-name ...
    */
   public static void main(String[] args)
      throws Exception
   {
      if (args.length == 0) {
         throw new IllegalArgumentException("usage: main(nameOfWordFile)");
      }
      for (String filename : args) {
         try {
            InputStream in = new BufferedInputStream(new FileInputStream(filename));
            String line;
            while ((line = readLine(in)) != null) {
               Stemmer s = new Stemmer(line);
               System.out.println(s + "," + s.countSyllables() + ": " + line + "," + (s.getDroppedSyllableCount() + s.countSyllables()));
            }
         } catch (FileNotFoundException e) {
            System.out.println("file " + filename + " not found");
            break;
         }
      }
   }

   static String readLine(InputStream in) {
      StringBuffer sb = new StringBuffer();
      char c;
      try {
         while (Character.isLetter(c = (char)in.read())) {
            sb.append(c);
         }
         while (sb.length() > 0 && c != '\n') {
            c = (char)in.read();
         }
      } catch (IOException e) {
         return null;
      }
      return sb.length() == 0 ? null : sb.toString();
   }
}
