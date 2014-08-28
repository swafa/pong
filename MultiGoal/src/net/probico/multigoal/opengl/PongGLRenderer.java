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
package net.probico.multigoal.opengl;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import net.probico.multigoal.MainActivity;
import net.probico.multigoal.R;
import net.probico.multigoal.shape.Circle;
import net.probico.multigoal.shape.Rectangle;
import net.probico.pong.common.RawResourceReader;
import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

/**
 * Provides drawing instructions for a GLSurfaceView object. This class must
 * override the OpenGL ES drawing lifecycle methods:
 * <ul>
 * <li>{@link android.opengl.GLSurfaceView.Renderer#onSurfaceCreated}</li>
 * <li>{@link android.opengl.GLSurfaceView.Renderer#onDrawFrame}</li>
 * <li>{@link android.opengl.GLSurfaceView.Renderer#onSurfaceChanged}</li>
 * </ul>
 */
public class PongGLRenderer implements GLSurfaceView.Renderer {

	private float widthToHeightRatio;
	
	
	private boolean firstRender = true;
	public void setFirstRender(boolean firstRender) {
		this.firstRender = firstRender;
	}

	private boolean secondRender = false;
	private float screenWidth;
	private static final String TAG = "PongGLRenderer";
	// private Triangle mTriangle;
	private Rectangle bottomPaddle;
	private Context context;

	
	private Rectangle background;
	
	final static float PADDLE_WIDTH = 0.6f;
	final static float PADDLE_HEIGHT = 0.15f;
	
	// Calculated visible width of the screen
//	final static float SCREEN_WIDTH = 2.95f;
	// Distance from center to nearest edge of the paddle
	final static float CENTER_TO_PADDLE = 2.15f;
	
	final static float CENTER_TO_VERTICAL_EDGE = 2.4f;

	public PongGLRenderer(Context context) {
		this.context = context;
		if (context instanceof MainActivity) {
			activity = (MainActivity) context;
		}
	}

	public Rectangle getBottomPaddle() {
		return bottomPaddle;
	}

	public void setBottomPaddle(Rectangle bottomPaddle) {
		this.bottomPaddle = bottomPaddle;
	}

	public Rectangle getTopPaddle() {
		return topPaddle;
	}

	public void setTopPaddle(Rectangle topPaddle) {
		this.topPaddle = topPaddle;
	}

	private Rectangle topPaddle;

	private Circle circle;
//	private Ball ball;

	public Circle getBall() {
		return circle;
	}

	public void setBall(Circle ball) {
		this.circle = ball;
	}

	// mMVPMatrix is an abbreviation for "Model View Projection Matrix"
	private final float[] mMVPMatrix = new float[16];
	private final float[] mProjectionMatrix = new float[16];
	private final float[] mViewMatrix = new float[16];
	// private final float[] mRotationMatrix = new float[16];
	private final float[] mModelMatrix = new float[16];

	private float mAngle;
	private MainActivity activity = null;

	public MainActivity getActivity() {
		return activity;
	}

	public void setActivity(MainActivity activity) {
		this.activity = activity;
	}

	private float yTranslateValue;

	@Override
	public void onSurfaceCreated(GL10 unused, EGLConfig config) {

		// Set the background frame color
		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

		bottomPaddle = new Rectangle(this, new float[] { -PADDLE_WIDTH/2, -CENTER_TO_PADDLE, 0.0f, // top
																			// left
				-PADDLE_WIDTH/2, -CENTER_TO_PADDLE - PADDLE_HEIGHT, 0.0f, // bottom left
				PADDLE_WIDTH/2, -CENTER_TO_PADDLE - PADDLE_HEIGHT, 0.0f, // bottom right
				PADDLE_WIDTH/2, -CENTER_TO_PADDLE, 0.0f }, R.drawable.paddle, false); // top right)

		topPaddle = new Rectangle(this, new float[] { -PADDLE_WIDTH/2, CENTER_TO_PADDLE + PADDLE_HEIGHT, 0.0f, // top
																			// left
				-PADDLE_WIDTH/2, CENTER_TO_PADDLE, 0.0f, // bottom left
				PADDLE_WIDTH/2, CENTER_TO_PADDLE, 0.0f, // bottom right
				PADDLE_WIDTH/2, CENTER_TO_PADDLE + PADDLE_HEIGHT, 0.0f }, R.drawable.paddle, true); // top right)
		


		circle = new Circle(this);
		
		background = new Rectangle(this, new float[] { -1.45f, CENTER_TO_VERTICAL_EDGE, 0.0f, // top
																		// left
				-1.45f, -CENTER_TO_VERTICAL_EDGE, 0.0f, // bottom left
				1.45f, -CENTER_TO_VERTICAL_EDGE, 0.0f, // bottom right
				1.45f, CENTER_TO_VERTICAL_EDGE, 0.0f }, R.drawable.gamescreen_background, false); // top right)
		
//		ball = new Ball(this);
		// mTriangle = new Triangle();
		// mSquare = new Square();
	}

	@Override
	public void onDrawFrame(GL10 unused) {

		// Draw background color
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

		// Set the camera position (View matrix)
		Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -7, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

		// Calculate the projection and view transformation
		Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

		// Draw square
		// mSquare.draw(mMVPMatrix);

		// Create a rotation for the triangle

		// Use the following code to generate constant rotation.
		// Leave this code out when using TouchEvents.
		// long time = SystemClock.uptimeMillis() % 4000L;
		// float angle = 0.090f * ((int) time);

		// Matrix.setRotateM(mRotationMatrix, 0, mAngle, 0, 0, 1.0f);

		// Draw triangle
		// mTriangle.draw(scratch);

		draw();
	}

	private void draw() {

		// Matrix.translateM(mModelMatrix, 0, xTranslateValue, 0, 0); //
		// translation
		// to the

		// Matrix.translateM(resultMatrix, 0, mModelMatrix, 0, xTranslateValue,
		// 0, 0);
		// // left
		//
		// // Combine the rotation matrix with the projection and camera view
		// // Note that the mMVPMatrix factor *must be first* in order
		// // for the matrix multiplication product to be correct.
		// Matrix.multiplyMM(scratch, 0, resultMatrix, 0, mMVPMatrix, 0);
		
//		background.setTextureId(R.drawable.backgroundpattern_space);
		background.draw(mMVPMatrix);
		
//		bottomPaddle.setTextureId(R.drawable.paddle_green);
		bottomPaddle.draw(mMVPMatrix);
		
//		topPaddle.setTextureId(R.drawable.paddle_green);
		topPaddle.draw(mMVPMatrix);
		
		circle.draw(mMVPMatrix);
		
//		separator.setColor(new float[]{0.482352941f, 0f, 0.254901961f, 1.0f});
		
		if(secondRender){
			activity.getmGameplayFragment().showCountDown();
			secondRender = false;
		}
		
		if(firstRender){
			firstRender = false;
			secondRender = true;
		}

	}

	@Override
	public void onSurfaceChanged(GL10 unused, int width, int height) {
		// Adjust the viewport based on geometry changes,
		// such as screen rotation
		GLES20.glViewport(0, 0, width, height);

		screenWidth = width;
		widthToHeightRatio = (float) width / height;

		// this projection matrix is applied to object coordinates
		// in the onDrawFrame() method
		Matrix.frustumM(mProjectionMatrix, 0, -widthToHeightRatio,
				widthToHeightRatio, -1, 1, 3, 7);

	}

	public float getScreenWidth() {
		return screenWidth;
	}

	public void setScreenWidth(float screenWidth) {
		this.screenWidth = screenWidth;
	}

	
	public String getVertexShader()
	{
		return RawResourceReader.readTextFileFromRawResource(context, R.raw.per_pixel_vertex_shader);
	}
	
	public String getFragmentShader()
	{
		return RawResourceReader.readTextFileFromRawResource(context, R.raw.per_pixel_fragment_shader);
	}
	
	/**
	 * Utility method for compiling a OpenGL shader.
	 * 
	 * <p>
	 * <strong>Note:</strong> When developing shaders, use the checkGlError()
	 * method to debug shader coding errors.
	 * </p>
	 * 
	 * @param type
	 *            - Vertex or fragment shader type.
	 * @param shaderCode
	 *            - String containing the shader code.
	 * @return - Returns an id for the shader.
	 */
	public static int loadShader(int type, String shaderCode) {

		// create a vertex shader type (GLES20.GL_VERTEX_SHADER)
		// or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
		int shader = GLES20.glCreateShader(type);

		// add the source code to the shader and compile it
		GLES20.glShaderSource(shader, shaderCode);
		GLES20.glCompileShader(shader);

		return shader;
	}

	/**
	 * Utility method for debugging OpenGL calls. Provide the name of the call
	 * just after making it:
	 * 
	 * <pre>
	 * mColorHandle = GLES20.glGetUniformLocation(mProgram, &quot;vColor&quot;);
	 * PongGLRenderer.checkGlError(&quot;glGetUniformLocation&quot;);
	 * </pre>
	 * 
	 * If the operation is not successful, the check throws an error.
	 * 
	 * @param glOperation
	 *            - Name of the OpenGL call to check.
	 */
	public static void checkGlError(String glOperation) {
		int error;
		while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
			Log.e(TAG, glOperation + ": glError " + error);
			throw new RuntimeException(glOperation + ": glError " + error);
		}
	}

	/**
	 * Returns the rotation angle of the triangle shape (mTriangle).
	 * 
	 * @return - A float representing the rotation angle.
	 */
	public float getAngle() {
		return mAngle;
	}

	/**
	 * Sets the rotation angle of the triangle shape (mTriangle).
	 */
	public void setAngle(float angle) {
		mAngle = angle;
	}

	public void showMessage() {

		// ((Activity) context).runOnUiThread(new Runnable() {
		// public void run() {
		// Toast.makeText(context, "GOAL!!", Toast.LENGTH_SHORT).show();
		// }
		// });
		// Toast.makeText(context, "touched paddle",
		// Toast.LENGTH_SHORT).show();
	}

}