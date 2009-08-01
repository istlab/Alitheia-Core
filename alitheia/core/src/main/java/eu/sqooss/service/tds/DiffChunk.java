/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2008 - Organization for Free and Open Source Software,  
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

package eu.sqooss.service.tds;

/**
 * A chunk in a diff file. It is generic enough to represent chunks in
 * both unified and original formats.
 * 
 * @author Georgios Gousios <gousiosg@gmail.com>
 */
public interface DiffChunk {
	
	/**
	 * All possible diff operations. Only applies in cases where the
	 * diff format provides this information. In all other cases, 
	 * the DiffOp is set to UNDEF. 
	 */
	public enum DiffOp {
		ADD,
		DELETE,
		CHANGE,
		UNDEF
	}
	
	/**
	 * Get the actual diff text.
	 */
	String getChunk();
	
	/**
	 * Get the starting line on the source file
	 */
	int getSourceStartLine();
	
	/**
	 * Get the length of text in the source file
	 */
	int getSourceLenght();
	
	/**
	 * Get the starting line on the target file
	 */
	int getTargetStartLine();
	
	/**
	 * Get the length of text in the target file
	 */
	int getTargetLength();
	
	/**
	 *  Get the repository path this diff chunk applies to 
	 */
	String getPath();
	
	/**
	 * The operation this diff chunk represents. 
	 */
	DiffOp getDiffOp();
}
