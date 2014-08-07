/*
 * Copyright (C) 2011 The Android Open Source Project
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
package com.probico.multipong;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import android.widget.Toast;

/**
 * A view container where OpenGL ES graphics can be drawn on screen. This view
 * can also be used to capture touch events, such as a user interacting with
 * drawn objects.
 */
public class MyGLSurfaceView extends GLSurfaceView {

	private final GLRenderer renderer;

	float mPreviousXBottom;
	float mPreviousXTop;

	MainActivity activity;

	public GLRenderer getRenderer() {
		return renderer;
	}

	public MyGLSurfaceView(Context context) {
		super(context);

		this.setId(10);
		if (context instanceof MainActivity) {
			activity = (MainActivity) context;
		}

		// Create an OpenGL ES 2.0 context.
		setEGLContextClientVersion(2);

		// Set the Renderer for drawing on the GLSurfaceView
		renderer = new GLRenderer(context);
		setRenderer(renderer);

		// Render the view only when there is a change in the drawing data
//		setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
	}

	private final float TOUCH_SCALE_FACTOR = 2f;

	// private float mPreviousX;
	// private float mPreviousY;

	@Override
	public boolean onTouchEvent(MotionEvent e) {
		// MotionEvent reports input details from the touch screen
		// and other input controls. In this case, you are only
		// interested in events where the touch position changed.

		float x;
		float y;
		float dxBottom;
		float dxTop;

		int pointerId;

		// float leftX =
		// getRenderer().getBottomPaddle().getRectangleCoords()[0];
		// float rightX =
		// getRenderer().getBottomPaddle().getRectangleCoords()[9];

		switch (e.getAction()) {

		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_MOVE:

			int pointerCount = e.getPointerCount();
			for (int i = 0; i < pointerCount; i++) {
				pointerId = e.getPointerId(i);
				try {

					x = e.getX(pointerId);
					y = e.getY(pointerId);
				} catch (Exception exception) {
					break;
				}

				// Move bottom paddle
				if (y > getHeight() / 2) {
					if (this.activity.getGameMode().equals(
							MainActivity.GameMode.SINGLE_PLAYER)
							|| this.activity.getGameMode().equals(
									MainActivity.GameMode.TWO_PLAYERS)
							|| (this.activity.getGameMode().equals(
									MainActivity.GameMode.TWO_PLAYERS_ONLINE) && !renderer
									.getActivity()
									.isCurrentParticipantInvitee())) {
//						dxBottom = x - mPreviousXBottom;
//						float xTranslateValue = getRenderer().getBottomPaddle()
//								.getxTranslateValue()
//								+ dxBottom /getRenderer().getScreenWidth()
//								* TOUCH_SCALE_FACTOR;
						getRenderer().getBottomPaddle().setxTranslateValue(
								-1f + (x / getRenderer().getScreenWidth()) * (2f));
						
						
//						mPreviousXBottom = x;

						if (this.activity.getGameMode().equals(
								MainActivity.GameMode.TWO_PLAYERS_ONLINE)
								&& !renderer.getActivity()
										.isCurrentParticipantInvitee()) {
							activity.sendPaddleX(getRenderer().getBottomPaddle().getxTranslateValue());
						}
						
					}

				}

				// Move Top paddle
				else {
					if (this.activity.getGameMode().equals(
									MainActivity.GameMode.TWO_PLAYERS)
							|| (this.activity.getGameMode().equals(
									MainActivity.GameMode.TWO_PLAYERS_ONLINE) && renderer
									.getActivity()
									.isCurrentParticipantInvitee())) {
//						dxTop = x - mPreviousXTop;
//						float xTranslateValue = getRenderer().getTopPaddle()
//								.getxTranslateValue()
//								+ dxTop * 1.5f /getRenderer().getScreenWidth()
//								* TOUCH_SCALE_FACTOR;
//						getRenderer().getTopPaddle().setxTranslateValue(
//								xTranslateValue);
//
//						mPreviousXTop = x;
						
						getRenderer().getTopPaddle().setxTranslateValue(
								-1f + (x / getRenderer().getScreenWidth()) * 2f);

						if (this.activity.getGameMode().equals(
								MainActivity.GameMode.TWO_PLAYERS_ONLINE)
								&& renderer.getActivity()
										.isCurrentParticipantInvitee()) {
							activity.sendPaddleX(getRenderer().getTopPaddle().getxTranslateValue());
						}
					}

				}

			}

			// if (dx > 0) {
			// // if(dx < 2){
			// // dx = 0;
			// // }
			//
			// System.out.println("right");
			// } else {
			// // if(dx > -2){
			// // dx = 0;
			// // }
			// System.out.println("left");
			// }

			break;

		// case MotionEvent.ACTION_POINTER_DOWN:
		// case MotionEvent.ACTION_POINTER_UP:
		// Toast.makeText(getContext(), "touched paddle",
		// Toast.LENGTH_SHORT).show();

		default:
			break;
		}

		// if(x / mGLView.getHeight() > leftX
		// && x / mGLView.getHeight() < rightX){
		// Toast.makeText(getApplicationContext(), "touched paddle",
		// Toast.LENGTH_SHORT).show();
		// }

		// switch (e.getAction()) {
		// case MotionEvent.ACTION_MOVE:
		//
		// float dx = x - mPreviousX;
		// float dy = y - mPreviousY;
		//
		// // reverse direction of rotation above the mid-line
		// if (y > getHeight() / 2) {
		// dx = dx * -1;
		// }
		//
		// // reverse direction of rotation to left of the mid-line
		// if (x < getWidth() / 2) {
		// dy = dy * -1;
		// }
		//
		// mRenderer.setAngle(mRenderer.getAngle()
		// + ((dx + dy) * TOUCH_SCALE_FACTOR)); // = 180.0f / 320

//		if(!this.activity.getGameFreezed()){
//			requestRender();
//		}
		// }
		//
		//

		// mPreviousY = y;

		return true;
	}

	public void showToast(String s) {
		Toast.makeText(getContext(), "touched paddle", Toast.LENGTH_SHORT)
				.show();
	}

}
