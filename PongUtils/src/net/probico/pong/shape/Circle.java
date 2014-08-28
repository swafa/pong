package net.probico.pong.shape;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import net.probico.pong.PongMainActivity;
import net.probico.pong.PongMainActivity.GameMode;
import net.probico.pong.PongMainActivity.Level;
import net.probico.pong.opengl.PongGLRenderer;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

/**
 * Circle shape. Used for pong ball.
 * 
 * @author samir
 * 
 */
public class Circle {

	final static float PI = 3.1415926535897932384626433832795f;
	private int mProgram, mPositionHandle, mColorHandle, mMVPMatrixHandle;
	private FloatBuffer mVertexBuffer;
	private float vertices[] = new float[360 * 3];
	float color[] = { 1f, 0f, 0f, 1.0f };

	float ySpeed = 0.01f;
	float xSpeed = 0.01f;

	public float getyTranslateValue() {
		return yTranslateValue;
	}

	public void setyTranslateValue(float yTranslateValue) {
		this.yTranslateValue = yTranslateValue;
	}

	public float getxTranslateValue() {
		return xTranslateValue;
	}

	public void setxTranslateValue(float xTranslateValue) {
		this.xTranslateValue = xTranslateValue;
	}

	private float yTranslateValue = 0;
	private float xTranslateValue = 0;

	private PongGLRenderer gameRenderer;

	private boolean ballMovingDirectionUp = false;

	public boolean isBallMovingDirectionUp() {
		return ballMovingDirectionUp;
	}

	public void setBallMovingDirectionUp(boolean ballMovingDirectionUp) {
		this.ballMovingDirectionUp = ballMovingDirectionUp;
	}

	public boolean isBallMovingDirectionRight() {
		return ballMovingDirectionRight;
	}

	public void setBallMovingDirectionRight(boolean ballMovingDirectionRight) {
		this.ballMovingDirectionRight = ballMovingDirectionRight;
	}

	private boolean ballMovingDirectionRight = false;

	private final String vertexShaderCode = "uniform mat4 uMVPMatrix;"
			+ "attribute vec4 vPosition;" + "void main() {"
			+ "  gl_Position = uMVPMatrix * vPosition;" + "}";

	private final String fragmentShaderCode = "precision mediump float;"
			+ "uniform vec4 vColor;" + "void main() {"
			+ "  gl_FragColor = vColor;" + "}";

	final static float radius = 0.05f;

	public Circle(PongGLRenderer gameRenderer) {

		this.gameRenderer = gameRenderer;
		for (int i = 0; i < 360; i++) {
			vertices[(i * 3) + 0] = (float) (radius * Math.cos((float) i));
			vertices[(i * 3) + 1] = (float) (radius * Math.sin((float) i));
			vertices[(i * 3) + 2] = 0;
		}

		Log.v("Thread", "" + vertices[0] + "," + vertices[1] + ","
				+ vertices[2]);
		ByteBuffer vertexByteBuffer = ByteBuffer
				.allocateDirect(vertices.length * 4);
		vertexByteBuffer.order(ByteOrder.nativeOrder());
		mVertexBuffer = vertexByteBuffer.asFloatBuffer();
		mVertexBuffer.put(vertices);
		mVertexBuffer.position(0);
		// prepare shaders and OpenGL program
		int vertexShader = PongGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER,
				vertexShaderCode);
		int fragmentShader = PongGLRenderer.loadShader(
				GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

		mProgram = GLES20.glCreateProgram(); // create empty OpenGL ES Program
		GLES20.glAttachShader(mProgram, vertexShader); // add the vertex shader
														// to program
		GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment
															// shader to program
		GLES20.glLinkProgram(mProgram);

	}

	public void draw(float[] mvpMatrix) {

		GLES20.glUseProgram(mProgram);

		// get handle to vertex shader's vPosition member
		mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

		// Enable a handle to the triangle vertices
		GLES20.glEnableVertexAttribArray(mPositionHandle);

		// Prepare the triangle coordinate data
		GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT,
				false, 12, mVertexBuffer);

		// get handle to fragment shader's vColor member
		mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

		// Set color for drawing the triangle
		GLES20.glUniform4fv(mColorHandle, 1, color, 0);

		mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

		doPhysics();

		// translate ball by xTranslateValue and yTranslateValue
		final float[] mModelMatrix = new float[16];
		Matrix.setIdentityM(mModelMatrix, 0); // initialize to identity

		float[] resultMatrix = new float[16];
		float[] scratch = new float[16];

		Matrix.translateM(resultMatrix, 0, mModelMatrix, 0, xTranslateValue,
				yTranslateValue, 0);

		Matrix.multiplyMM(scratch, 0, resultMatrix, 0, mvpMatrix, 0);

		// Apply the projection and view transformation
		GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, scratch, 0);

		PongGLRenderer.checkGlError("glUniformMatrix4fv");

		GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 360);

		// Disable vertex array
		GLES20.glDisableVertexAttribArray(mPositionHandle);

	}

	/**
	 * Static values are used by this method to check ball position information.
	 * For some reason using the expected values through constants doesn't work.
	 * Something might need to be adjusted in the opengl projection matrix.
	 * 
	 * @author samir
	 */
	private void doPhysics() {

		doPaddleCollisionPhysics();

		doWallCollisionPhysics();

		updateBallCoordinates();

		// If playing solo, update bot position
		if (this.gameRenderer.getActivity().getGameMode()
				.equals(GameMode.SINGLE_PLAYER)) {

			updateBotPosition();
		}

		doGoalPhysics();
	}

	private void doGoalPhysics() {
		// Goal
		if ((yTranslateValue < -0.95 && (gameRenderer.getActivity()
				.getGameMode().equals(GameMode.SINGLE_PLAYER)
				|| gameRenderer.getActivity().getGameMode()
						.equals(GameMode.TWO_PLAYERS) || (gameRenderer
				.getActivity().getGameMode()
				.equals(GameMode.TWO_PLAYERS_ONLINE) && !gameRenderer
				.getActivity().isCurrentParticipantInvitee())))
				|| (yTranslateValue > 0.95 && (gameRenderer.getActivity()
						.getGameMode().equals(GameMode.SINGLE_PLAYER)
						|| gameRenderer.getActivity().getGameMode()
								.equals(GameMode.TWO_PLAYERS) || (gameRenderer
						.getActivity().getGameMode()
						.equals(GameMode.TWO_PLAYERS_ONLINE) && gameRenderer
						.getActivity().isCurrentParticipantInvitee())))) {

			// Play sound
			playGoalSound();
			
			resetBallVelocity();
			
			incrementPlayerScore();
			
			resetBallPosition();

			// Reverse direction
			ballMovingDirectionUp = !ballMovingDirectionUp;
			
			// In online mode, a goal is only a goal if the current player sees that it's a goal.
			if (gameRenderer.getActivity().getGameMode()
					.equals(PongMainActivity.GameMode.TWO_PLAYERS_ONLINE)) {
				// Corrective action
				gameRenderer.getActivity().sendBallInformation(xTranslateValue,
						yTranslateValue, ballMovingDirectionRight,
						ballMovingDirectionUp, xSpeed, ySpeed);
				gameRenderer.getActivity().sendUpdateScoreMessage();

			}
		}
	}

	private void resetBallPosition() {
		// Reset ball coordinates
		xTranslateValue = 0;
		yTranslateValue = 0;
	}

	private void resetBallVelocity() {
		// Reset ball speed values
		ySpeed = 0.01f;
		xSpeed = 0.01f;
	}

	private void incrementPlayerScore() {
		// Player 1 scored
		if (yTranslateValue > 0.95) {
			this.gameRenderer.getActivity().incrementPlayer1Score();

		}
		// Player 2 scored
		else if (yTranslateValue < -0.95) {
			this.gameRenderer.getActivity().incrementPlayer2Score();
		}
	}

	private void playGoalSound() {
		gameRenderer
				.getActivity()
				.getSoundPool()
				.play(gameRenderer.getActivity().getSoundIds()[1], 1, 1, 1,
						0, 1.0f);
	}

	private void updateBotPosition() {
		float speedRatio = ySpeed != 0 ? xSpeed / ySpeed : 0;

		float robotFactor = 0f;

		if ((gameRenderer.getActivity().getLevel().equals(Level.EASY) && ySpeed > 0.014)
				|| (gameRenderer.getActivity().getLevel()
						.equals(Level.HARD) && ySpeed > 0.03)) {
			robotFactor = 0.20f;
		}

		if (ballMovingDirectionRight) {
			this.gameRenderer.getTopPaddle().setxTranslateValue(
					xTranslateValue - robotFactor * speedRatio);
		} else {
			this.gameRenderer.getTopPaddle().setxTranslateValue(
					xTranslateValue + robotFactor * speedRatio);
		}
	}

	private void updateBallCoordinates() {
		// If game is still ongoing and ball is still moving (game not paused)
		if (!(gameRenderer.getActivity().gameOver() || (xSpeed == 0 && ySpeed == 0))) {

			// Update x and y ball cooridinates 
			if (ballMovingDirectionUp)
				yTranslateValue += ySpeed;
			else
				yTranslateValue -= ySpeed;

			if (ballMovingDirectionRight) {
				xTranslateValue += xSpeed;

			} else {
				xTranslateValue -= xSpeed;
			}
		}
	}

	private void doWallCollisionPhysics() {
		// Ball hit right wall. Use of static values is temporary until opengl projection matrix is adjusted
		if (xTranslateValue > 0.95) {
			
			// Play sound
			playWallCollisionSound();
			
			// Reverse horizontal direction
			ballMovingDirectionRight = false;
		}

		// Ball hit right wall. Use of static values is temporary until opengl projection matrix is adjusted
		else if (xTranslateValue < -0.95) {
			// Play sound
			playWallCollisionSound();
			
			// Reverse horizontal direction
			ballMovingDirectionRight = true;
		}
	}

	private void playWallCollisionSound() {
		gameRenderer
				.getActivity()
				.getSoundPool()
				.play(gameRenderer.getActivity().getSoundIds()[0], 1, 1, 1,
						0, 1.0f);
	}

	private void doPaddleCollisionPhysics() {
		// Ball hit bottom paddle
		if (yTranslateValue <= -0.88
				&& gameRenderer.getBottomPaddle().getxTranslateValue() <= xTranslateValue + 0.22
				&& gameRenderer.getBottomPaddle().getxTranslateValue() >= xTranslateValue - 0.22) {

			playWallCollisionSound();

			// Reverse ball vertical speed
			ballMovingDirectionUp = true;

			// Accelerate ball if maximum speed has not been reached yet
			if ((gameRenderer.getActivity().getGameMode()
					.equals(GameMode.TWO_PLAYERS_ONLINE) && ySpeed <= 0.014)
					|| (gameRenderer.getActivity().getGameMode()
							.equals(GameMode.TWO_PLAYERS) || gameRenderer
							.getActivity().getGameMode()
							.equals(GameMode.SINGLE_PLAYER)) && ySpeed <= 0.03) {
				ySpeed += 0.002f;
			}

			// Return xSpeed to initial value
			xSpeed = 0.01f;

			// Ball hit edge. Reverse the horizontal direction and make the ball
			// move with a greater angle
			if (xTranslateValue > gameRenderer.getBottomPaddle()
					.getxTranslateValue() + 0.1) {
				ballMovingDirectionRight = true;
				xSpeed = ySpeed * 2;
			} else if (xTranslateValue < gameRenderer.getBottomPaddle()
					.getxTranslateValue() - 0.1) {
				ballMovingDirectionRight = false;
				xSpeed = ySpeed * 2;
			}

			// If playing online, send new ball position and velocity info to opponent
			if (gameRenderer.getActivity().getGameMode()
					.equals(PongMainActivity.GameMode.TWO_PLAYERS_ONLINE)
					&& !gameRenderer.getActivity()
							.isCurrentParticipantInvitee()) {
				gameRenderer.getActivity().sendBallInformation(xTranslateValue,
						yTranslateValue, ballMovingDirectionRight,
						ballMovingDirectionUp, xSpeed, ySpeed);
			}
		}
		// Ball hit top paddle
		else if (yTranslateValue >= 0.88
				&& gameRenderer.getTopPaddle().getxTranslateValue() <= xTranslateValue + 0.22
				&& gameRenderer.getTopPaddle().getxTranslateValue() >= xTranslateValue - 0.22) {

			playWallCollisionSound();
			
			// Reverse ball vertical moving direction
			ballMovingDirectionUp = false;

			// Accelerate ball if maximum speed has not been reached yet
			if ((gameRenderer.getActivity().getGameMode()
					.equals(GameMode.TWO_PLAYERS_ONLINE) && ySpeed <= 0.014)
					|| (gameRenderer.getActivity().getGameMode()
							.equals(GameMode.TWO_PLAYERS) || gameRenderer
							.getActivity().getGameMode()
							.equals(GameMode.SINGLE_PLAYER)) && ySpeed <= 0.03) {
				ySpeed += 0.002f;
			}

			// Return xSpeed to initial value
			xSpeed = 0.01f;
			
			// If ball meets the edge of the paddle, reverse the horizontal
			// direction and accelerate
			if (xTranslateValue > gameRenderer.getTopPaddle()
					.getxTranslateValue() + 0.1) {

				ballMovingDirectionRight = true;
				xSpeed = ySpeed * 2;
			} else if (xTranslateValue < gameRenderer.getTopPaddle()
					.getxTranslateValue() - 0.1) {

				ballMovingDirectionRight = false;
				xSpeed = ySpeed * 2;

			}

			// If playing online, send new ball position and velocity info to opponent
			if (gameRenderer.getActivity().getGameMode()
					.equals(PongMainActivity.GameMode.TWO_PLAYERS_ONLINE)
					&& gameRenderer.getActivity().isCurrentParticipantInvitee()) {
				gameRenderer.getActivity().sendBallInformation(xTranslateValue,
						yTranslateValue, ballMovingDirectionRight,
						ballMovingDirectionUp, xSpeed, ySpeed);
			}
		}
	}

	public float getySpeed() {
		return ySpeed;
	}

	public void setySpeed(float ySpeed) {
		this.ySpeed = ySpeed;
	}

	public float getxSpeed() {
		return xSpeed;
	}

	public void setxSpeed(float xSpeed) {
		this.xSpeed = xSpeed;
	}
	
	public void setColor(float[] color) {
		this.color = color;
	}

}
