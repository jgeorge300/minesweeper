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

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder.AudioSource;
import android.os.Bundle;
import android.view.MotionEvent;

import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;


public class MineSweeperActivity extends Activity implements SensorEventListener {

	private static final int SAMPLING_RATE = 44100;

	private MineSweeperView mMineSweeperView;

	private SensorManager mSensorManager;
	private GestureDetector mGestureDetector;


	private RecordingThread mRecordingThread;
	private int mBufferSize;
	private short[] mAudioBuffer;

	private final float[] mRotationMatrix = new float[16];
	private final float[] mOrientation = new float[9];

	float previousHeading = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_minesweeper);

		mMineSweeperView = (MineSweeperView) findViewById(R.id.minesweeper_view);

		// Compute the minimum required audio buffer size and allocate the buffer.
		mBufferSize = AudioRecord.getMinBufferSize(SAMPLING_RATE, AudioFormat.CHANNEL_IN_MONO,
				AudioFormat.ENCODING_PCM_16BIT);
		mAudioBuffer = new short[mBufferSize / 2];

		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mGestureDetector = createGestureDetector(this);


	}

	@Override
	protected void onResume() {
		super.onResume();

		mRecordingThread = new RecordingThread();
		mRecordingThread.start();

		registerListeners();
	}

	@Override
	protected void onPause() {
		super.onPause();

		if (mRecordingThread != null) {
			mRecordingThread.stopRunning();
			mRecordingThread = null;
		}
		unregisterListeners();
	}

	@Override
	protected void onDestroy() {
		super.onPause();

		if (mRecordingThread != null) {
			mRecordingThread.stopRunning();
			mRecordingThread = null;
		}
		unregisterListeners();
	}
	
	private class RecordingThread extends Thread {

		private boolean mShouldContinue = true;

		@Override
		public void run() {
			android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_AUDIO);

			AudioRecord record = new AudioRecord(AudioSource.MIC, SAMPLING_RATE,
					AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, mBufferSize);
			record.startRecording();

			while (shouldContinue()) {
				record.read(mAudioBuffer, 0, mBufferSize / 2);
				mMineSweeperView.updateData();
				reveal();
			}

			record.stop();
			record.release();
		}

		private synchronized boolean shouldContinue() {
			return mShouldContinue;
		}

		public synchronized void stopRunning() {
			mShouldContinue = false;
		}

		private void reveal() {
			double sum = 0;

			for (short rawSample : mAudioBuffer) {
				double sample = rawSample / 32768.0;
				sum += sample * sample;
			}

			double rms = Math.sqrt(sum / mAudioBuffer.length);
			final double db = 20 * Math.log10(rms);

			if (db > -30) {
				mMineSweeperView.reveal();
			}
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		return;
	}
	
	private static float GetHeadingDiff(float start, float end)
    {
		float diff = end - start;
		float absDiff = Math.abs(diff);

        if (absDiff <= 180f)
        {
            return absDiff == 180f ? absDiff : diff;
        } else if (end > start) {
            return absDiff - 360f;
        } else {
            return 360f - absDiff;
        }
    }

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
			SensorManager.getRotationMatrixFromVector(mRotationMatrix, event.values);
			SensorManager.remapCoordinateSystem(mRotationMatrix, SensorManager.AXIS_X,
					SensorManager.AXIS_Z, mRotationMatrix);
			SensorManager.getOrientation(mRotationMatrix, mOrientation);

			float pitch = (float) Math.toDegrees(mOrientation[1]);

			pitch = (pitch > 20) ? 20 : pitch;
			pitch = (pitch < -20) ? -20 : pitch;
			int y = 180 + (int)(9 * pitch);

			float currentHeading = (float) Math.toDegrees(mOrientation[0]);

			int x;
			if (previousHeading == -1) {
				previousHeading = currentHeading;
				x = (int)((640 * .75) / 2);
			} else {
				x = mMineSweeperView.getTarget().getX();
			}
			x += (int) (20 * GetHeadingDiff(previousHeading, currentHeading));
			x = (x > 640) ? 640 : x;
			x = (x < 0) ? 0 : x;
			previousHeading = currentHeading;

			mMineSweeperView.getTarget().setX(x);
			mMineSweeperView.getTarget().setY(y);

		}
	}

	private void registerListeners() {
		mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR),
				SensorManager.SENSOR_DELAY_NORMAL);
	}

	private void unregisterListeners() {
		mSensorManager.unregisterListener(this);
	}
	
    private GestureDetector createGestureDetector(Context context) {
    GestureDetector gestureDetector = new GestureDetector(context);
        //Create a base listener for generic gestures
        gestureDetector.setBaseListener( new GestureDetector.BaseListener() {
            @Override
            public boolean onGesture(Gesture gesture) {
                if (gesture == Gesture.TAP) {
                    // do something on tap
                    return false;
                } else if (gesture == Gesture.TWO_TAP) {
                    // do something on two finger tap
                    return false;
                } else if (gesture == Gesture.SWIPE_RIGHT) {
                	mMineSweeperView.reveal();
                    return true;
                } else if (gesture == Gesture.SWIPE_LEFT) {
                    // do something on left (backwards) swipe
                	mMineSweeperView.flag();
                    return true;
                }
                return false;
            }
        });

        return gestureDetector;
    }

    /*
     * Send generic motion events to the gesture detector
     */
    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        if (mGestureDetector != null) {
            return mGestureDetector.onMotionEvent(event);
        }
        return false;
    }
}




