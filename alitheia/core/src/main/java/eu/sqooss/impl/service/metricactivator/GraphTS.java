/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2010 - Organization for Free and Open Source Software,  
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

package eu.sqooss.impl.service.metricactivator;

import eu.sqooss.service.abstractmetric.AlitheiaPlugin;

/**
 * Topological sorting for Alitheia Core plugin invocations.
 * 
 * @author Georgios Gousios <gousiosg@aueb.gr> 
 * based on code distributed in the public domain by
 * http://www.algorithm-code.com 
 *
 */
public class GraphTS {
	private final int MAX_VERTS = 20;

	private Vertex vertexList[]; // list of vertices

	private int matrix[][]; // adjacency matrix

	private int numVerts; // current number of vertices

	private AlitheiaPlugin sortedArray[];

	public GraphTS() {
		vertexList = new Vertex[MAX_VERTS];
		matrix = new int[MAX_VERTS][MAX_VERTS];
		numVerts = 0;
		for (int i = 0; i < MAX_VERTS; i++)
			for (int k = 0; k < MAX_VERTS; k++)
				matrix[i][k] = 0;
		sortedArray = new AlitheiaPlugin[MAX_VERTS]; // sorted vert labels
	}

	public int addVertex(AlitheiaPlugin lab) {
		vertexList[numVerts++] = new Vertex(lab);
		return numVerts;
	}

	public void addEdge(int start, int end) {
		matrix[start][end] = 1;
	}

	public void displayVertex(int v) {
		System.out.print(vertexList[v].label);
	}

	public AlitheiaPlugin[] topo() { // toplogical sort 
		int orig_nVerts = numVerts;

		while (numVerts > 0) // while vertices remain,
		{
			// get a vertex with no successors, or -1
			int currentVertex = noSuccessors();
			if (currentVertex == -1) // must be a cycle
			{
				System.out.println("ERROR: Graph has cycles");
				return null;
			}
			// insert vertex label in sorted array (start at end)
			sortedArray[numVerts - 1] = vertexList[currentVertex].label;

			deleteVertex(currentVertex); // delete vertex
		}

		// vertices all gone; display sortedArray
		System.out.print("Topologically sorted order: ");
		for (int j = 0; j < orig_nVerts; j++)
			System.out.print(sortedArray[j]);
		System.out.println("");
		return sortedArray;
	}

	public int noSuccessors() // returns vert with no successors (or -1 if no
	// such verts)
	{
		boolean isEdge; // edge from row to column in adjMat

		for (int row = 0; row < numVerts; row++) {
			isEdge = false; // check edges
			for (int col = 0; col < numVerts; col++) {
				if (matrix[row][col] > 0) // if edge to another,
				{
					isEdge = true;
					break; // this vertex has a successor try another
				}
			}
			if (!isEdge) // if no edges, has no successors
				return row;
		}
		return -1; // no
	}

	public void deleteVertex(int delVert) {
		if (delVert != numVerts - 1) // if not last vertex, delete from
		// vertexList
		{
			for (int j = delVert; j < numVerts - 1; j++)
				vertexList[j] = vertexList[j + 1];

			for (int row = delVert; row < numVerts - 1; row++)
				moveRowUp(row, numVerts);

			for (int col = delVert; col < numVerts - 1; col++)
				moveColLeft(col, numVerts - 1);
		}
		numVerts--; // one less vertex
	}

	private void moveRowUp(int row, int length) {
		for (int col = 0; col < length; col++)
			matrix[row][col] = matrix[row + 1][col];
	}

	private void moveColLeft(int col, int length) {
		for (int row = 0; row < length; row++)
			matrix[row][col] = matrix[row][col + 1];
	}

}

class Vertex {
	public AlitheiaPlugin label;

	public Vertex(AlitheiaPlugin lab) {
		label = lab;
	}
}
