/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2008 - Organization for Free and Open Source Software,  *                Athens, Greece.
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

package eu.sqooss.impl.metrics.contrib;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Typesafe descriptions of actions supported by the contribution plugin
 *
 */
public class ContributionActions {

    /** Maps categories to actions*/
    public static HashMap<ActionCategory, ArrayList<ActionType>> types = 
        new java.util.HashMap<ActionCategory, ArrayList<ActionType>>();
    
    static {
        ArrayList<ActionType> c = new ArrayList<ActionType>();
        c.add(ActionType.CNS);
        c.add(ActionType.CND);
        c.add(ActionType.CDF);
        c.add(ActionType.CTF);
        c.add(ActionType.CBF);
        c.add(ActionType.CEC);
        c.add(ActionType.CMF);
        c.add(ActionType.CBN);
        c.add(ActionType.CPH);
        c.add(ActionType.CADD);
        c.add(ActionType.CREM);
        c.add(ActionType.CCNG);
        types.put(ActionCategory.C, c);
        
        c= new ArrayList<ActionType>();
        c.add(ActionType.MCT);
        c.add(ActionType.MST);
        c.add(ActionType.MSE);
        c.add(ActionType.MFR);
        types.put(ActionCategory.M, c);
        
        c = new ArrayList<ActionType>();
        c.add(ActionType.BCC);
        c.add(ActionType.BCL);
        c.add(ActionType.BDUP);
        c.add(ActionType.BRP);
        types.put(ActionCategory.B, c);
    }
    
    /**
     * A basic categorization of the all the possible actions on
     * various project assets
     */
    public enum ActionType {
        /** Commit new source file +*/
        CNS,
        /** Commit new directory +*/
        CND,
        /** Commit documentation files +*/
        CDF,
        /** Commit translation files +*/
        CTF,
        /** Commit binary files -*/
        CBF,
        /** Commit with empty commit message -*/
        CEC,
        /** Commit more than X files in a single commit -*/
        CMF,
        /** Total lines modified until current version +*/
        CCNG,
        /** Total lines added until current version +*/
        CADD,
        /** Total lines removed until current version +*/
        CREM,
        /** Commit comment that includes a bug report number +*/
        CBN,
        /** Commit comment that awards a pointy hat +*/
        CPH,
        /**Email that closes a thread +*/
        MCT,
        /**Email that starts a new thread +*/
        MST,
        /**First reply to a thread +*/
        MFR,
        /**Send an email to a list +*/
        MSE,
        /**Report a bug*/
        BRP,
        /**Report a bug that is closed/duplicate*/
        BDUP,
        /**Close a bug*/
        BCL,
        /**Create a comment on a bug+*/
        BCC
        ;
        
        public static ActionType fromString(String s) {
            if ("CNS".equalsIgnoreCase(s))
                return ActionType.CNS;
            else if ("CND".equalsIgnoreCase(s))
                return ActionType.CND;
            else if ("CDF".equalsIgnoreCase(s))
                return ActionType.CDF;
            else if ("CTF".equalsIgnoreCase(s))
                return ActionType.CTF;
            else if ("CBF".equalsIgnoreCase(s))
                return ActionType.CBF;
            else if ("CEC".equalsIgnoreCase(s))
                return ActionType.CEC;
            else if ("CMF".equalsIgnoreCase(s))
                return ActionType.CMF;
            else if ("CADD".equalsIgnoreCase(s))
                return ActionType.CADD;
            else if ("CREM".equalsIgnoreCase(s))
                return ActionType.CREM;
            else if ("CCNG".equalsIgnoreCase(s))
                return ActionType.CCNG;
            else if ("CBN".equalsIgnoreCase(s))
                return ActionType.CBN;
            else if ("CPH".equalsIgnoreCase(s))
                return ActionType.CPH;
            else if ("MCT".equalsIgnoreCase(s))
                return ActionType.MCT;
            else if ("MST".equalsIgnoreCase(s))
                return ActionType.MST;
            else if ("MFR".equalsIgnoreCase(s))
                return ActionType.MFR;
            else if ("MSE".equalsIgnoreCase(s))
                return ActionType.MSE;
            else if ("BRP".equalsIgnoreCase(s))
                return ActionType.BRP;
            else if ("BDUP".equalsIgnoreCase(s))
                return ActionType.BDUP;
            else if ("BCL".equalsIgnoreCase(s))
                return ActionType.BCL;
            else if ("BCC".equalsIgnoreCase(s))
                return ActionType.BCC;
            else
                return null;
        }
        
        public static ArrayList<ActionType> getActionTypes(ActionCategory a){
            return types.get(a);
        }
    }
    
    /**
     * An action can fall into in one of those categories
     */
    public enum ActionCategory{
        /** Code and documentation repository */
        C,
        /** Mailing lists - forums */
        M,
        /** Bug database*/
        B;  
        
        public static ActionCategory fromString(String s){
            if ("C".equalsIgnoreCase(s))
                return ActionCategory.C;
            else if ("M".equalsIgnoreCase(s))
                return ActionCategory.M;
            else if ("B".equalsIgnoreCase(s))
                return ActionCategory.B;
            else
                return null;
        }
        
        public static ActionCategory getActionCategory(ActionType a){
            for(ActionCategory ac : types.keySet()) {
                for(ActionType at : types.get(ac)) {
                    if (at.equals(a)) {
                        return ac;
                    }
                }
            }
            return null;
        }
    }
}

//vi: ai nosi sw=4 ts=4 expandtab
