package net.probico.multigoal.opengl;

import net.probico.multigoal.R;
import net.probico.pong.opengl.PongGLRenderer;
import android.content.Context;

/**
 * Provides drawing instructions for a GLSurfaceView object.
 * 
 * @author samir
 */
public class GLRenderer extends PongGLRenderer {

	public GLRenderer(Context context) {
		super(context);
	}

	@Override
	public int getBackgroundTextureResourceId() {
		return R.drawable.gamescreen_background;
	}

	@Override
	public int getPaddleTextureResourceId() {
		return R.drawable.paddle;
	}

	@Override
	public float[] getBallColor() {
		return new float[]{1, 1, 1, 1};
	}

	@Override
	public float[] getSeperatorColor() {
		return new float[]{1, 1, 1, 1};
	}

}