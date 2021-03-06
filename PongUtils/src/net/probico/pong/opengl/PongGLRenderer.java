package net.probico.pong.opengl;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import net.probico.pong.PongMainActivity;
import net.probico.pong.common.RawResourceReader;
import net.probico.pong.shape.Circle;
import net.probico.pong.shape.Line;
import net.probico.pong.shape.Rectangle;
import net.probico.pongutils.R;
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
 * 
 * @author samir
 */
public abstract class PongGLRenderer implements GLSurfaceView.Renderer {

	private float widthToHeightRatio;

	private boolean firstRender = true;

	public void setFirstRender(boolean firstRender) {
		this.firstRender = firstRender;
	}

	private boolean secondRender = false;
	private float screenWidth;
	private static final String TAG = "PongGLRenderer";
	private Rectangle bottomPaddle;
	private Context context;

	private Line separator;

	private Rectangle background;

	final static float PADDLE_WIDTH = 0.6f;
	final static float PADDLE_HEIGHT = 0.15f;

	final static float CENTER_TO_PADDLE = 2.15f;

	final static float CENTER_TO_VERTICAL_EDGE = 2.4f;

	public PongGLRenderer(Context context) {
		this.context = context;
		if (context instanceof PongMainActivity) {
			activity = (PongMainActivity) context;
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

	private float mAngle;
	private PongMainActivity activity = null;

	public PongMainActivity getActivity() {
		return activity;
	}

	public void setActivity(PongMainActivity activity) {
		this.activity = activity;
	}

	@Override
	public void onSurfaceCreated(GL10 unused, EGLConfig config) {

		// Set the background frame color
		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

		bottomPaddle = new Rectangle(this, new float[] { -PADDLE_WIDTH / 2,
				-CENTER_TO_PADDLE, 0.0f, // top
				// left
				-PADDLE_WIDTH / 2, -CENTER_TO_PADDLE - PADDLE_HEIGHT, 0.0f, // bottom
																			// left
				PADDLE_WIDTH / 2, -CENTER_TO_PADDLE - PADDLE_HEIGHT, 0.0f, // bottom
																			// right
				PADDLE_WIDTH / 2, -CENTER_TO_PADDLE, 0.0f },
				getPaddleTextureResourceId(), false); // top right)

		topPaddle = new Rectangle(this, new float[] { -PADDLE_WIDTH / 2,
				CENTER_TO_PADDLE + PADDLE_HEIGHT, 0.0f, // top
				// left
				-PADDLE_WIDTH / 2, CENTER_TO_PADDLE, 0.0f, // bottom left
				PADDLE_WIDTH / 2, CENTER_TO_PADDLE, 0.0f, // bottom right
				PADDLE_WIDTH / 2, CENTER_TO_PADDLE + PADDLE_HEIGHT, 0.0f },
				getPaddleTextureResourceId(), true); // top right)

		separator = new Line(this, new float[] { -1.45f, 0.0f, 0.0f, 1.45f,
				0.0f, 0.0f });

		circle = new Circle(this);

		background = new Rectangle(this, new float[] { -1.45f,
				CENTER_TO_VERTICAL_EDGE, 0.0f, // top
				// left
				-1.45f, -CENTER_TO_VERTICAL_EDGE, 0.0f, // bottom left
				1.45f, -CENTER_TO_VERTICAL_EDGE, 0.0f, // bottom right
				1.45f, CENTER_TO_VERTICAL_EDGE, 0.0f },
				getBackgroundTextureResourceId(), false); // top right)
	}

	public abstract int getBackgroundTextureResourceId();

	public abstract int getPaddleTextureResourceId();
	
	public abstract float[] getBallColor();
	public abstract float[] getSeperatorColor();

	@Override
	public void onDrawFrame(GL10 unused) {

		// Draw background color
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

		// Set the camera position (View matrix)
		Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -7, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

		// Calculate the projection and view transformation
		Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

		draw();
	}

	private void draw() {

		background.draw(mMVPMatrix);

		// bottomPaddle.setTextureId(R.drawable.paddle_green);
		bottomPaddle.draw(mMVPMatrix);

		// topPaddle.setTextureId(R.drawable.paddle_green);
		topPaddle.draw(mMVPMatrix);

		circle.setColor(getBallColor());
		circle.draw(mMVPMatrix);

		separator.setColor(getSeperatorColor());
		separator.draw(mMVPMatrix);

		if (secondRender) {
			activity.getmGameplayFragment().showCountDown();
			secondRender = false;
		}

		if (firstRender) {
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

	public String getVertexShader() {
		return RawResourceReader.readTextFileFromRawResource(context,
				R.raw.per_pixel_vertex_shader);
	}

	public String getFragmentShader() {
		return RawResourceReader.readTextFileFromRawResource(context,
				R.raw.per_pixel_fragment_shader);
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

}