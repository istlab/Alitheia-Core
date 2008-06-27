/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2007-2008 by Stefanos Skalistis <sskalistis@gmail.com>
 * 											 <sskalist@gmail.com>
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

package eu.sqooss.webui.quality.bean;

import java.util.Comparator;

/**
 * This enumeration type defines the profiles/categories that the SQO-OSS is
 * currently supporting.<br>
 * It also provides comparing methods in order to compare a profile against
 * another.
 * 
 * @author <a href="mailto:sskalist@gmail.com">sskalist &lt sskalist@gmail.com
 *         &gt</a>
 * 
 */
public enum SQOOSSProfiles implements Comparable<SQOOSSProfiles>,
		Comparator<SQOOSSProfiles> {
	/**
	 * The four profiles as their are defined according to deliverable (D7) of
	 * the SQO-OSS Documentation. <br>
	 * They are order from worst to best and they should always remain in that
	 * order for the comparing methods to work.
	 */
	Poor, Fair, Good, Excellent;

	/**
	 * The number of profiles currently offered.
	 */
	private static int numberOfProfiles;

	static {
		numberOfProfiles = SQOOSSProfiles.values().length;
	}

	/**
	 * Gets the number of profiles.
	 * 
	 * @return The number of profiles.
	 */
	public static int getNumberOfProfiles() {
		return numberOfProfiles;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	/**
	 * Compares two profiles. Returns a negative, zero or positive integer if
	 * <code>first</code> is less, equal or greater than the
	 * <code>second</code> respectively.
	 * 
	 * @return The ordinal difference of the <code>first</code> from the
	 *         <code>second</code>.
	 */
	// @Override
	public int compare(SQOOSSProfiles first, SQOOSSProfiles second) {
		return first.compareTo(second);
	}

}
