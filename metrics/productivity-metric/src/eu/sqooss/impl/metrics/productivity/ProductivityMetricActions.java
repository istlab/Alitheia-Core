/* This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 * 
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2007-2008 Georgios Gousios <gousiosg@gmail.com>
 * 
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

package eu.sqooss.impl.metrics.productivity;

public class ProductivityMetricActions {

    public enum ActionType {
        CNS, // Commit new source file
        CND, // Commit new directory
        CDF, // Commit documentation files
        CTF, // Commit translation files
        CBF, // Commit binary files
        CEC, // Commit with empty commit comment,
        CMF, // Commit more than X files in a single commit
        TCO, // Commit (for calculating the number of commits per developer)
        TCF, // Commit files (for calculating the number of committed files
                // per developer)
        CBN, // Commit comment that includes a bug report number
        CPH; // Commit comment that awards a pointy hat
        
        public static ActionType fromString(String s) {
            if ("CNS".equals(s))
                return ActionType.CNS;
            else if ("CND".equals(s))
                return ActionType.CND;
            else if ("CDF".equals(s))
                return ActionType.CDF;
            else if ("CTF".equals(s))
                return ActionType.CTF;
            else if ("CBF".equals(s))
                return ActionType.CBF;
            else if ("CEC".equals(s))
                return ActionType.CEC;
            else if ("CMF".equals(s))
                return ActionType.CMF;
            else if ("TCO".equals(s))
                return ActionType.TCO;
            else if ("TCF".equals(s))
                return ActionType.TCF;
            else if ("CBN".equals(s))
                return ActionType.CBN;
            else if ("CPH".equals(s))
                return ActionType.CPH;
            else
                return null;
        }
    }

}

//vi: ai nosi sw=4 ts=4 expandtab
