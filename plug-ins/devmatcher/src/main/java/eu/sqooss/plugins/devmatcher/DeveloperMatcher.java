/*
 * Copyright 2009 - 2010 - Organization for Free and Open Source Software,  
 *                 Athens, Greece.
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
package eu.sqooss.plugins.devmatcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.codec.language.DoubleMetaphone;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.Developer;
import eu.sqooss.service.db.DeveloperAlias;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.updater.MetadataUpdater;
import eu.sqooss.service.util.Pair;

/**
 * Heuristic based matcher for developer identities. Uses a combination of 
 * pattern and approximate string matching techniques and weights that are
 * evaluated at the end of the process. Will lock all developer
 * records per project to avoid concurrent access when running. 
 * 
 * @author Georgios Gousios <gousiosg@gmail.com>
 */
public class DeveloperMatcher implements MetadataUpdater {

    private StoredProject project;
    private DBService dbs;
    private Logger logger;
    private float progress;
    
    private Map<String, Developer> emailToDev = new TreeMap<String, Developer>();
    private Map<String, Developer> unameToDev = new TreeMap<String, Developer>();
    private Map<String, Developer> nameToDev = new TreeMap<String, Developer>();
    private Map<String, Developer> emailprefToDev = new TreeMap<String, Developer>();
    
    private Map<String, List<String>> mtphoneToUname = new TreeMap<String, List<String>>();
    
    private Map<Pair<Long, Long>, Integer> matches = new HashMap<Pair<Long, Long>, Integer>();
    
    public DeveloperMatcher() {
        dbs = AlitheiaCore.getInstance().getDBService();
    }
    
    @Override
    public int progress() {
        return (int) progress;
    }

    @Override
    public void setUpdateParams(StoredProject arg0, Logger arg1) {
        this.project = arg0;
        this.logger = arg1;
    }

    @Override
    public void update() throws Exception {
        dbs.startDBSession();
        project = dbs.attachObjectToDBSession(project);
        DoubleMetaphone dm = new DoubleMetaphone();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("storedProject", project);
        List<Developer> devs = dbs.findObjectsByPropertiesForUpdate(Developer.class, params);
        long ts = System.currentTimeMillis();
        //Fill in indices
        for (Developer d : devs) {
            for (DeveloperAlias da : d.getAliases()) {
                emailToDev.put(da.getEmail(), d);
                String uname = da.getEmail().substring(0, da.getEmail().indexOf('@')).toLowerCase();
                emailprefToDev.put(uname, d);
                addMetaphone(dm.doubleMetaphone(uname), uname);            
            }
            
            if (d.getUsername() != null && !d.getUsername().equals("")) {
                String uname = d.getUsername().toLowerCase();
                unameToDev.put(uname, d);
                addMetaphone(dm.doubleMetaphone(uname), uname);
            }
            
            if (d.getName() != null && !d.getName().equals("")) {
                nameToDev.put(d.getName().toLowerCase(), d);
            }
        }
        progress = 30;
        for (String name : nameToDev.keySet()) {
            
            List<String> usernames = getPossibleUnames(name);
            for (String uname : usernames) {
                // Try strict matching first
                if (unameToDev.containsKey(uname)) {
                    addMatch(nameToDev.get(name).getId(), 
                            unameToDev.get(uname).getId(), 10);
                }
                
                /*String mph = dm.doubleMetaphone(uname);

                // Try metaphone matching
                if (mtphoneToUname.containsKey(mph)) {
                    List<String> mfMatches = mtphoneToUname.get(mph);
                    for (String mfMatch : mfMatches) {
                        //Match against user names list first
                        Developer d = unameToDev.get(mfMatch);
                        if (d != null) {
                            addMatch(nameToDev.get(name).getId(),
                                    d.getId(), 
                                    10 - levenshtein(mfMatch, uname));    
                        } else {
                            //Match against email prefixes
                            d = emailprefToDev.get(mfMatch);
                            addMatch(nameToDev.get(name).getId(),
                                    d.getId(),  
                                    10 - levenshtein(mfMatch, uname)); 
                        }
                        
                    }
                }
             
                // Try levenshtein distance matching
                for (String develuname : unameToDev.keySet()) {
                    int dist = Math.abs(levenshtein(uname, develuname));
                    int mlen = Math.abs(uname.length() - develuname.length()); 
                    //We arbitrarily consider a diff > 2 too big
                    if (mlen <= 2 && dist <= 2) {
                        addMatch(nameToDev.get(name).getId(),
                                unameToDev.get(develuname).getId(), 
                                2 - dist);
                    }
                }*/
            }
        }
        
        for (String username: unameToDev.keySet()) {
            if (emailprefToDev.containsKey(username)) {
                addMatch(emailprefToDev.get(username).getId(),
                        unameToDev.get(username).getId(), 10);
            }
        }
        
        progress = 60;
        List<String> updates = new ArrayList<String>(); 
        updates.add("update ProjectVersion set committer = :new where committer = :old");
        updates.add("update MailMessage set sender = :new where sender = :old");
        updates.add("update Bug set reporter = :new where reporter = :old");
        updates.add("update BugReportMessage set reporter = :new where reporter = :old");

        List<String> deletes = new ArrayList<String>(); 
        deletes.add("delete from DeveloperAlias d where d.developer.id = :oldid");
        deletes.add("delete from Developer d where d.id = :oldid");
        
        for (Pair<Long, Long> match : matches.keySet()) {
            
            //if (matches.get(match) < 30)
               // continue;
            
            Developer byEmail = Developer.loadDAObyId(match.first, Developer.class);
            Developer byUsrName = Developer.loadDAObyId(match.second, Developer.class);
            
            Map<String, Object> updParam = new HashMap<String, Object>();
            updParam.put("old", byEmail);
            updParam.put("new", byUsrName);
            
            Map<String, Object> delParam = new HashMap<String, Object>();
            delParam.put("oldid", byEmail.getId());
            
            //Copy emails
            for (DeveloperAlias da : byEmail.getAliases()) {
                debug("Adding alias " + da.getEmail() + " to dev " + byUsrName);
                byUsrName.addAlias(da.getEmail());
            }
            
            if (byUsrName.getName() == null || byUsrName.getName().trim().equals("")) {
                byUsrName.setName(byEmail.getName());
                debug("Setting " +  byUsrName.getUsername() + "'s name to " + byEmail.getName());
            }
            
            for (String upd : updates) {
                long lines = dbs.executeUpdate(upd, updParam);
                debug(upd + " old:" + byEmail + " new:" + 
                        byUsrName + " " + lines + " changed");
            }
            
            for (String del : deletes) {
                long lines = dbs.executeUpdate(del, delParam);
                debug(del + " old:" + byEmail.getId() + lines + " changed");
            }

            debug("Replaced dev " + match.first + "->" 
                    + match.second + ", score: " + matches.get(match));
        }
        
        info("Matched " + matches.size() + " developers in " 
                + (System.currentTimeMillis() - ts) + "ms");
        dbs.commitDBSession();
        progress = 100;
    }
    
    /*
     * Get a list of possible usernames that may originate from 
     * a given real name
     */
    private List<String> getPossibleUnames(String realName) {
        List<String> names = new ArrayList<String>();
        realName = cleanup(realName);
        
        String[] nameParts = realName.split(" ");
        String fname = nameParts[0];
        //names.add(fname);
        
        String surname = null;
        if (nameParts.length > 0) {
            surname = nameParts[nameParts.length - 1];
        }
        
        String mname = null;
        if (nameParts.length == 3) {
            mname = nameParts[1];
        }
        
        //Not a lot of usernames possible without a surname 
        if (surname == null) 
            return names;
        
        //fnamesurname
        names.add(fname + surname);
        //fname.surname
        names.add(fname + "." + surname);
        //surname.fname
        names.add(surname + "." + fname);
        //sfname
        names.add(surname.charAt(0) + fname);
        //fsurname
        names.add(fname.charAt(0) + surname);
        //surnamef
        names.add(surname + fname.charAt(0));
        //fnames
        names.add(fname + surname.charAt(0));
        //f.surname
        names.add(fname.charAt(0) + "." + surname);
        //fname.s
        names.add(fname + "." + surname.charAt(0));
        
        if (mname != null) {
            //Traditional Unix 
            names.add("" + fname.charAt(0) + mname.charAt(0) + surname.charAt(0));
        }
        
        return names;
    }
    
    /*
     * Names coming from email headers might contain various characters
     * which are not part of a real name. Try to filter those out. 
     */
    private String cleanup(String name) {
        String badCharsRE = "\\/|\\|\'|\"|!";

        name.replaceAll(badCharsRE, name);
        return name;
    }
    
    /*
     * Add a match to the matches table. If the match already exists,
     * just increase the match score.
     */
    private void addMatch(Long id1, Long id2, Integer score) {
        
        if (id1.longValue() == id2.longValue())
            return;
        
        Pair<Long, Long> match = new Pair<Long, Long>(id1, id2);
        Pair<Long, Long> revMatch = new Pair<Long, Long>(id2, id1);
        boolean foundRevMatch = false;
        
        if (matches.containsKey(match)) {
            matches.put(match, matches.get(match) + score);
        }
        else if (matches.containsKey(revMatch)) {
            matches.put(revMatch, matches.get(revMatch) + score);
            foundRevMatch = true;
        }
        else { 
            matches.put(match, score);
        }
    
        if (foundRevMatch)
            debug("Potential developer revmatch " + revMatch + ": " + matches.get(revMatch));
        else 
            debug("Potential developer match " + match + ": " + matches.get(match));
    }

    /*
     * Add a value to the metaphone cache, taking care duplicate keys
     */
    private void addMetaphone(String code, String username) {
        List<String> unames = mtphoneToUname.get(code);
        
        if (unames != null) {
            if (!unames.contains(username)) {
                unames.add(username);
            }
        } else {
            unames = new ArrayList<String>();
            unames.add(username);
            mtphoneToUname.put(code, unames);
        }
    }

    /*
     * Levenshtein distance algorithm, copied verbatim from
     * http://www.merriampark.com/ld.htm
     */
    public int levenshtein(String s, String t) {
        int d[][]; // matrix
        int n; // length of s
        int m; // length of t
        int i; // iterates through s
        int j; // iterates through t
        char s_i; // ith character of s
        char t_j; // jth character of t
        int cost; // cost

        // Step 1
        n = s.length();
        m = t.length();
        if (n == 0) {
            return m;
        }
        
        if (m == 0) {
            return n;
        }
        
        d = new int[n + 1][m + 1];

        // Step 2
        for (i = 0; i <= n; i++) {
            d[i][0] = i;
        }

        for (j = 0; j <= m; j++) {
            d[0][j] = j;
        }

        // Step 3
        for (i = 1; i <= n; i++) {
            s_i = s.charAt(i - 1);

            // Step 4
            for (j = 1; j <= m; j++) {

                t_j = t.charAt(j - 1);

                // Step 5
                if (s_i == t_j) {
                    cost = 0;
                } else {
                    cost = 1;
                }

                // Step 6
                d[i][j] = min(d[i - 1][j] + 1, d[i][j - 1] + 1,
                        d[i - 1][j - 1] + cost);
            }
        }

        // Step 7
        return d[n][m];
    }

    private int min(int a, int b, int c) {
        int mi;

        mi = a;
        if (b < mi) {
            mi = b;
        }
        if (c < mi) {
            mi = c;
        }
        return mi;

    }
    
    @Override
    public String toString() {
        return "Developer Updater - Project:" + project;
    }
    
    /** Convenience method to write info messages per project */
    protected void info(String message) {
        logger.info(project.getName() + ":" + message);
    }
    
    /** Convenience method to write debug messages per project */
    protected void debug(String message) {
        logger.debug(project.getName() + ":" + message);
    }
}
