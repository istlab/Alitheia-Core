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

package eu.sqooss.metrics.java;

import java.util.Objects;

public final class ReducePair<L, R> {

	private final L left;
	private final R right;

    public ReducePair(L left, R right) {
        this.left = left;
        this.right = right;
    }
    
    public L getLeft() {
    	return this.left;
    }
    
    public R getRight() {
    	return this.right;
    }

    @Override
	public int hashCode() {
		return Objects.hash(this.left, this.right);
	}

    @Override
	@SuppressWarnings("unchecked")
	public boolean equals(Object other) {
		if (other instanceof ReducePair) {
			ReducePair<L, R> that = (ReducePair<L, R>) other;
			return Objects.equals(this.getLeft(), that.getLeft())
					&& Objects.equals(this.getRight(), that.getRight());
		}
		return false;
	}

	@Override
	public String toString() {
		return "<ReducePair[" + String.valueOf(this.getLeft()) + ", "
				+ String.valueOf(this.getRight()) + "]>";
	}
}


// vi: ai nosi sw=4 ts=4 expandtab

