/*
 * Copyright (C) 2014 Joe George
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.george.minesweeper.engine;

public class MineTile {

	private boolean hasMine = false;
	private int state = 0; // 0 - hidden, 1 - flagged, 2 - display
		
	private int minedMinedNeighbors = 0;
	
	public void armMine() {
		this.hasMine = true;
	}
	
	public boolean hasMine() {
		return hasMine;
	}

	public void increaseMinedNeighbors() {
		minedMinedNeighbors++;		
	}
	
	public int getMinedNeighbors() {
		return minedMinedNeighbors;
	}

	public void flag() {
		this.state = 1;
	}

	public void reveal() {
		this.state = 2;
	}

	public int getState() {
		return this.state;
	}
}
