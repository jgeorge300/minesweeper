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


import org.george.minesweeper.engine.MineField;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.util.AttributeSet;
import android.view.SurfaceView;

public class MineSweeperView extends SurfaceView {
	private final Paint mPaint;
	private MineField mf = null;
	private Target target = null;

	private int width;
	private int height;

	public MineSweeperView(Context context) {
		this(context, null, 0);
	}

	public MineSweeperView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public MineSweeperView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		mPaint = new Paint();
		mPaint.setColor(Color.WHITE);
		mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
		mPaint.setStrokeWidth(2);
		mPaint.setTextSize(30);

		mf = new MineField(9,9,10);
		target = new Target(0,0);
	}

	public synchronized void updateData() {
		// Update the display.
		Canvas canvas = getHolder().lockCanvas();
		if (canvas != null) {
			if (mf.isGameOver()) {
				mPaint.setColor(Color.RED);
				mPaint.setTextAlign(Align.CENTER);
				mPaint.setTextSize(100);
				canvas.drawText("BOOM!", (int)(width/2), (int)(height/2), mPaint);
				mPaint.setTextAlign(Align.LEFT);
				mPaint.setTextSize(30);

			} else {
				canvas.drawColor(Color.BLACK);
				width = canvas.getWidth();
				height = canvas.getHeight();

				mPaint.setColor(Color.WHITE);
				canvas.drawLine((int)(width*.75), 0, (int)(width*.75), height, mPaint);
				int w = (int)((width*.75)/9);
				int h = (int)(height/9);
				for (int i = 1; i < 9; i++) {
					canvas.drawLine((int)(w*i), 0, (int)(w*i), height, mPaint);
					canvas.drawLine(0, h*i, (int)(width*.75), h*i, mPaint);
				}

				target.draw(canvas);
				mf.draw(canvas, mPaint);
			}
			mPaint.setColor(Color.GREEN);

			canvas.drawText("" + mf.getTime(), (int)(width*.75 + 20), (int)(height/2), mPaint);
			getHolder().unlockCanvasAndPost(canvas);
		}
	}

	public Target getTarget() {
		return this.target;
	}

	public void reveal() {
		int col = (int)(target.getX() / ((width*.75) / 9)) + 1;
		int row = (int)(target.getY() / (height / 9) ) + 1;
		mf.reveal(col, row);		
	}

	public void flag() {
		int col = (int)(target.getX() / ((width*.75) / 9)) + 1;
		int row = (int)(target.getY() / (height / 9) ) + 1;
		mf.flag(col, row);		
	}

}
