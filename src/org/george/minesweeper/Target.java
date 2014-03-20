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

package org.george.minesweeper;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Target {

	private Bitmap bitmap; // the actual bitmap
	private int x;   // the X coordinate
	private int y;   // the Y coordinate
	private Paint mPaint;

	public Target(int x, int y) {
		this.x = x;
		this.y = y;
		mPaint = new Paint();
		mPaint.setColor(Color.YELLOW);
	}

	public Bitmap getBitmap() {
		return bitmap;
	}
	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}

	public void draw(Canvas canvas) {
		canvas.drawCircle(x, y, 5, mPaint);	
	}

}