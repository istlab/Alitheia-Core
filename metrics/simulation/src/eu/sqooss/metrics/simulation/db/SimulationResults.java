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

public class SimulationResults extends DAObject {
	
	private StoredProject storedProject;

	private long time;
	private long projectID;
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
	private double loc2;
	private double bDens2;
	private double bRepDens2;
	private double q2;
	private double totS2;
	private double totB2;
	private double totT2;
	private double totF2;
	private double occProgrammers2;
	private double newBlood2;

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
	 * @param loc2
	 * @param dens2
	 * @param repDens2
	 * @param q2
	 * @param totS2
	 * @param totB2
	 * @param totT2
	 * @param totF2
	 * @param occProgrammers2
	 * @param newBlood2
	 */

	public SimulationResults(long time, long runtime, double loc, double dens,
			double repDens, double q, double totS, double totB, double totT,
			double totF, double occProgrammers, double newBlood, double loc2,
			double dens2, double repDens2, double q2, double totS2,
			double totB2, double totT2, double totF2, double occProgrammers2,
			double newBlood2) {
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
		this.loc2 = loc2;
		bDens2 = dens2;
		bRepDens2 = repDens2;
		this.q2 = q2;
		this.totS2 = totS2;
		this.totB2 = totB2;
		this.totT2 = totT2;
		this.totF2 = totF2;
		this.occProgrammers2 = occProgrammers2;
		this.newBlood2 = newBlood2;
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
	 * @param the
	 *            time to set
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
	 * @param runtime
	 *            the runtime to set
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
	 * @param loc
	 *            the loc to set
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
	 * @param dens
	 *            the bDens to set
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
	 * @param repDens
	 *            the bRepDens to set
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
	 * @param q
	 *            the q to set
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
	 * @param totS
	 *            the totS to set
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
	 * @param totB
	 *            the totB to set
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
	 * @param totT
	 *            the totT to set
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
	 * @param totF
	 *            the totF to set
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
	 * @param occProgrammers
	 *            the occProgrammers to set
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
	 * @param newBlood
	 *            the newBlood to set
	 */
	public void setNewBlood(double newBlood) {
		this.newBlood = newBlood;
	}

	/**
	 * @return the loc2
	 */
	public double getLoc2() {
		return loc2;
	}

	/**
	 * @param loc2
	 *            the loc2 to set
	 */
	public void setLoc2(double loc2) {
		this.loc2 = loc2;
	}

	/**
	 * @return the bDens2
	 */
	public double getbDens2() {
		return bDens2;
	}

	/**
	 * @param dens2
	 *            the bDens2 to set
	 */
	public void setbDens2(double dens2) {
		bDens2 = dens2;
	}

	/**
	 * @return the bRepDens2
	 */
	public double getbRepDens2() {
		return bRepDens2;
	}

	/**
	 * @param repDens2
	 *            the bRepDens2 to set
	 */
	public void setbRepDens2(double repDens2) {
		bRepDens2 = repDens2;
	}

	/**
	 * @return the q2
	 */
	public double getQ2() {
		return q2;
	}

	/**
	 * @param q2
	 *            the q2 to set
	 */
	public void setQ2(double q2) {
		this.q2 = q2;
	}

	/**
	 * @return the totS2
	 */
	public double getTotS2() {
		return totS2;
	}

	/**
	 * @param totS2
	 *            the totS2 to set
	 */
	public void setTotS2(double totS2) {
		this.totS2 = totS2;
	}

	/**
	 * @return the totB2
	 */
	public double getTotB2() {
		return totB2;
	}

	/**
	 * @param totB2
	 *            the totB2 to set
	 */
	public void setTotB2(double totB2) {
		this.totB2 = totB2;
	}

	/**
	 * @return the totT2
	 */
	public double getTotT2() {
		return totT2;
	}

	/**
	 * @param totT2
	 *            the totT2 to set
	 */
	public void setTotT2(double totT2) {
		this.totT2 = totT2;
	}

	/**
	 * @return the totF2
	 */
	public double getTotF2() {
		return totF2;
	}

	/**
	 * @param totF2
	 *            the totF2 to set
	 */
	public void setTotF2(double totF2) {
		this.totF2 = totF2;
	}

	/**
	 * @return the occProgrammers2
	 */
	public double getOccProgrammers2() {
		return occProgrammers2;
	}

	/**
	 * @param occProgrammers2
	 *            the occProgrammers2 to set
	 */
	public void setOccProgrammers2(double occProgrammers2) {
		this.occProgrammers2 = occProgrammers2;
	}

	/**
	 * @return the newBlood2
	 */
	public double getNewBlood2() {
		return newBlood2;
	}

	/**
	 * @param newBlood2
	 *            the newBlood2 to set
	 */
	public void setNewBlood2(double newBlood2) {
		this.newBlood2 = newBlood2;
	}

	public String toString() {
		String output = new String("\t" + time + "\t" + runtime + "\t" + loc + "\t" + bDens + "\t" + bRepDens + "\t" + q + "\t" + totS + "\t" + totB + "\t" + totT + "\t" + totF + "\t" + occProgrammers + "\t" + newBlood + "\t" + loc2 + "\t" +  bDens2 + "\t" + bRepDens2 + "\t" + q2 + "\t" + "\t" +  totS2 + "\t" + totB2 + "\t" + totT2 + "\t" + totF2 + "\t" + occProgrammers2 + "\t" + newBlood2);
		return output;
	}
}
