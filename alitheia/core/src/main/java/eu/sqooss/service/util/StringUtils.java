/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007 - 2010 - Organization for Free and Open Source Software,  
 *                Athens, Greece.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *
 *     * Redistributions in binary form must reproduce the above
 *       copyright notice, this list of conditions and the following
 *       disclaimer in the documentation and/or other materials provided
 *       with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package eu.sqooss.service.util;

import java.util.List;

public class StringUtils {
    /**
    * Concatenate the strings in @p names, placing @p sep
    * between each (except at the end) and return the resulting
    * string.
    *
    * @see QStringList::join
    *
    * Test cases:
    *   - null separator, empty separator, one-character and longer string separator
    *   - null names, 0-length names, 1 name, n names
    */
    public static String join(String[] names, String sep) {
        if ( names == null ) {
            return null;
        }
        int l = names.length;

        if (l<1) {
            return "";
        }

        // This is just a (bad) guess at the capacity required
        StringBuilder b = new StringBuilder( l * sep.length() + l + 1 );
        for ( int i=0; i<l; i++ ) {
            b.append(names[i]);
            if ( (i < (l-1)) && (sep != null) ) {
                b.append(sep);
            }
        }
        return b.toString();
    }

    /**
     * Overload of join() for use with List.
     *
     * @param names List of strings to join together
     * @param sep   Separator between strings (may be null)
     * @return null if names is null; strings in names joined with
     *          sep in between otherwise.
     */
    public static String join(List<String> names, String sep) {
        if ( names == null ) {
            return null;
        }
        int l = names.size();
        if (l<1) {
            return "";
        }

        // This is just a (bad) guess at the capacity required
        StringBuilder b = new StringBuilder( l * sep.length() + l + 1);
        int i = 0;
        for ( String s : names ) {
            b.append(s);
            if ( (i < (l-1)) && (sep != null) ) {
                b.append(sep);
            }
        }
        return b.toString();
    }

    /**
    * Given a bitfield value @p value, and an array that names
    * each bit position, return a comma-separated string that
    * names each bit position that is set in @p value.
    *
    * Test cases:
    *   - value 0, a few random ones, -1 (0xffffffffffffffff), MAXINT.
    *   - null names, 0-length names, names contains nulls,
    *   - names contains empty strings, names too short for value,
    *   - names too long.
    */
    public static String bitfieldToString(String[] statenames, int value) {
        if ( (value == 0) || (statenames == null) || (statenames.length == 0) ) {
            return "";
        }
        StringBuilder b = new StringBuilder();
        for ( int statebit = 0; statebit < statenames.length; statebit++ ) {
            int statebitvalue = 1 << statebit ;
            if ( (value & statebitvalue) != 0 ) {
                // ASSERT: statebit < statenames.length
                // TODO: handle null strings
                b.append(statenames[statebit]);
                // TODO: make this bit-twiddling, may fail with negative value
                value -= statebitvalue;
                if ( value != 0 ) {
                    b.append(", ");
                }
            }
        }
        return b.toString();
    }

    /**
     * Given a String, this function returns an XHTML-safe version of the same
     */
    public static String makeXHTMLSafe(String line){
        return line.replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;");
    }

    /**
     * Find the given needle string in an array (haystack) of strings.
     * Returns the index of the needle in the haystack, or -1 if not found.
     *
     * @param haystack Array of strings to search.
     * @param needle String to search for.
     * @return -1 if the needle is not found or the needle or haystack
     *      is invalid. Otherwise the index (>=0) of the needle in the
     *      haystack.
     */
    public static int indexOf(String[] haystack, String needle) {
        if ( (haystack == null) || (needle == null) ) {
            return -1;
        }
        for (int i = 0; i<haystack.length; i++) {
            if (haystack[i].equals(needle)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * State whether the given needle string is to be found in the haystack.
     *
     * @param haystack Array of strings to search through.
     * @param needle String to search for.
     * @return true iff the needle occurs in the haystack.
     */
    public static boolean contains(String[] haystack, String needle) {
        return indexOf(haystack,needle) >= 0;
    }
}

