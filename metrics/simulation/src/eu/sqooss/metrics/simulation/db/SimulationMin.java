/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2007-2008 by Kritikos Apostolos <akritiko@csd.auth.gr>
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

package eu.sqooss.metrics.simulation.db;

import eu.sqooss.impl.service.CoreActivator;
import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.DBService;
//import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.StoredProject;

public class SimulationMin extends DAObject{
	
	private StoredProject storedProject;
	
	private long time;
	private long runtime;
	private double loc;
	private double bDens;
	private double bRepDens;
	private double q;
	private double totS;
	private double totB;
	private double totT;
	private double totF;
	private double occProgrammers;
	private double newBlood;
	
	/**
	 * @param time
	 * @param runtime
	 * @param loc
	 * @param dens
	 * @param repDens
	 * @param q
	 * @param totS
	 * @param totB
	 * @param totT
	 * @param totF
	 * @param occProgrammers
	 * @param newBlood
	 */
	public SimulationMin(long time, long runtime, double loc,
			double dens, double repDens, double q, double totS, double totB,
			double totT, double totF, double occProgrammers, double newBlood) {
		this.time = time;
		this.runtime = runtime;
		this.loc = loc;
		bDens = dens;
		bRepDens = repDens;
		this.q = q;
		this.totS = totS;
		this.totB = totB;
		this.totT = totT;
		this.totF = totF;
		this.occProgrammers = occProgrammers;
		this.newBlood = newBlood;
	}
	
	/**
	 * @return the time
	 */
	public StoredProject getStoredProject() {
		return storedProject;
	}

	/**
	 * @param the
	 *            time to set
	 */
	public void setStoredProject(StoredProject storedProject) {
		this.storedProject = storedProject;
	}
	
	/**
	 * @return the time
	 */
	public long getTime() {
		return time;
	}
	/**
	 * @param timw the time to set
	 */
	public void setTime(long time) {
		this.time = time;
	}
	
	/**
	 * @return the runtime
	 */
	public long getRuntime() {
		return runtime;
	}
	/**
	 * @param runtime the runtime to set
	 */
	public void setRuntime(long runtime) {
		this.runtime = runtime;
	}
	/**
	 * @return the loc
	 */
	public double getLoc() {
		return loc;
	}
	/**
	 * @param loc the loc to set
	 */
	public void setLoc(double loc) {
		this.loc = loc;
	}
	/**
	 * @return the bDens
	 */
	public double getbDens() {
		return bDens;
	}
	/**
	 * @param dens the bDens to set
	 */
	public void setbDens(double dens) {
		bDens = dens;
	}
	/**
	 * @return the bRepDens
	 */
	public double getbRepDens() {
		return bRepDens;
	}
	/**
	 * @param repDens the bRepDens to set
	 */
	public void setbRepDens(double repDens) {
		bRepDens = repDens;
	}
	/**
	 * @return the q
	 */
	public double getQ() {
		return q;
	}
	/**
	 * @param q the q to set
	 */
	public void setQ(double q) {
		this.q = q;
	}
	/**
	 * @return the totS
	 */
	public double getTotS() {
		return totS;
	}
	/**
	 * @param totS the totS to set
	 */
	public void setTotS(double totS) {
		this.totS = totS;
	}
	/**
	 * @return the totB
	 */
	public double getTotB() {
		return totB;
	}
	/**
	 * @param totB the totB to set
	 */
	public void setTotB(double totB) {
		this.totB = totB;
	}
	/**
	 * @return the totT
	 */
	public double getTotT() {
		return totT;
	}
	/**
	 * @param totT the totT to set
	 */
	public void setTotT(double totT) {
		this.totT = totT;
	}
	/**
	 * @return the totF
	 */
	public double getTotF() {
		return totF;
	}
	/**
	 * @param totF the totF to set
	 */
	public void setTotF(double totF) {
		this.totF = totF;
	}
	/**
	 * @return the occProgrammers
	 */
	public double getOccProgrammers() {
		return occProgrammers;
	}
	/**
	 * @param occProgrammers the occProgrammers to set
	 */
	public void setOccProgrammers(double occProgrammers) {
		this.occProgrammers = occProgrammers;
	}
	/**
	 * @return the newBlood
	 */
	public double getNewBlood() {
		return newBlood;
	}
	/**
	 * @param newBlood the newBlood to set
	 */
	public void setNewBlood(double newBlood) {
		this.newBlood = newBlood;
	}
	
	public String toString() {
		String output = new String("\t" + time + "\t" + runtime + "\t" + loc + "\t" + bDens + "\t" + bRepDens + "\t" + q + "\t" + totS + "\t" + totB + "\t" + totT + "\t" + totF + "\t" + occProgrammers + "\t" + newBlood + "\t");
		return output;
	}
}
