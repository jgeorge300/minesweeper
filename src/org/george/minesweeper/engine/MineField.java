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

import java.util.Date;
import java.util.Random;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

public class MineField {
	public enum GameState {WON, LOST, PLAYING};
	public static int LO = 0;

	MineTile[][] field;
	int columns = 0; 
	int rows = 0;
	int numOfMines = 0;
	int flaggedMines = 0;
	int revealedSpaces = 0;

	GameState gameState = GameState.PLAYING;

	Date start = null;
	Date end = null;

	public MineField(int columns, int rows, int numOfMines) {
		this.columns = columns;
		this.rows = rows;
		this.numOfMines = numOfMines;
		
		field = new MineTile[columns+2][];
		for (int i = 0; i < columns+2; i++) {
			field[i] = new MineTile[rows+2];
			for (int j = 0; j < rows+2; j++) {
				field[i][j] = new MineTile();
			}
		}
		Random rand = new Random();
		int row,col;
		for (int i = 0; i < numOfMines; i++) {
			row = rand.nextInt(rows)+1;
			col = rand.nextInt(columns)+1;
			field[col][row].armMine();
			field[col+1][row+1].increaseMinedNeighbors();
			field[col+1][row].increaseMinedNeighbors();
			field[col+1][row-1].increaseMinedNeighbors();
			field[col][row+1].increaseMinedNeighbors();
			field[col][row-1].increaseMinedNeighbors();
			field[col-1][row+1].increaseMinedNeighbors();
			field[col-1][row].increaseMinedNeighbors();
			field[col-1][row-1].increaseMinedNeighbors();
		}
	}

	public static void main(String[] args) {
		MineField mf = new MineField(9, 9, 10);
		mf.printBoard();
		mf.reveal(5, 6);
		mf.printBoard();

	}

	void printBoard() {
		for(int col = 1; col <= columns; col++) {
			for(int row = 1; row <= rows; row++) {
				if (field[col][row].getState() == 0) {
					System.out.print('X');
				} else if (field[col][row].getState() == 1) {
					System.out.print('F');
				} else {
					if (field[col][row].hasMine())
						System.out.print('*');
					else
						System.out.print(field[col][row].getMinedNeighbors());
				}
			}
			System.out.println();
		}
	}

	public void flag(int col, int row)
	{
		if (start == null)
			start = new Date();

		field[col][row].flag();
		if (field[col][row].getState() == 0)
			flaggedMines -= 1;
		else if (field[col][row].getState() == 1)
			flaggedMines += 1;
	}

	public void reveal(int col, int row)
	{
		if (col < 1 || col > columns) return;
		if (row < 1 || row > rows) return;
		if (field[col][row].getState() != 0) return;

		if (start == null)
			start = new Date();

		field[col][row].reveal();
		revealedSpaces++;

		if (field[col][row].hasMine()) {
			gameState = GameState.LOST;
			end = new Date();
			return;
		}

		if ((flaggedMines == numOfMines) && (revealedSpaces + flaggedMines == (columns*rows))) {
			gameState = GameState.WON;				
			end = new Date();
			return;
		}

		if (field[col][row].getMinedNeighbors() == 0) {
			reveal(col+1,row+1);
			reveal(col+1,row);
			reveal(col+1,row-1);
			reveal(col,row+1);
			reveal(col,row-1);
			reveal(col-1,row+1);
			reveal(col-1,row);
			reveal(col-1,row-1);
		}
	}

	public void draw(Canvas canvas, Paint mPaint) {
		int width = (int) (canvas.getWidth()*.75 / columns);
		int height = (int) (canvas.getHeight() / rows);

		String cell = null;
		for(int col = 1; col <= columns; col++) {
			for(int row = 1; row <= rows; row++) {
				if (field[col][row].getState() == 0) {
					cell = "X";
				} else if (field[col][row].getState() == 1) {
					mPaint.setColor(Color.MAGENTA);
					cell = "F";
				} else {
					if (field[col][row].hasMine()) {

						mPaint.setColor(Color.RED);
						cell = "*";
					} else {
						mPaint.setColor(Color.GREEN);
						cell = "" + field[col][row].getMinedNeighbors();
					}
				}
				int x = (int)((width/2)+ (col-1)*width);
				int y = (int)((height/2)+ (row-1)*height);
				canvas.drawText(cell, x, y+5, mPaint);
				mPaint.setColor(Color.WHITE);
			}
		}
	}

	public GameState gameState() {
		return this.gameState;
	}

	public long getTime() {
		if (start == null) {
			return 0;
		} else if (end != null) {
			return (end.getTime() - start.getTime()) / 1000; 
		} else {
			return (new Date().getTime() - start.getTime()) / 1000; 
		}
	}
	
	public int getFlagged() {
		return flaggedMines;
	}

}
