package net.probico.multigoal.opengl;

import net.probico.pong.opengl.PongGLRenderer;
import net.probico.pong.opengl.PongGLSurfaceView;
import android.content.Context;

/**
 * A view container where OpenGL ES graphics can be drawn on screen. This view
 * can also be used to capture touch events, such as a user interacting with
 * drawn objects.
 * 
 * @author samir
 */
public class GLSurfaceView extends PongGLSurfaceView {

	public GLSurfaceView(Context context) {
		super(context);
	}

	@Override
	public PongGLRenderer getPongGLRenderer(Context context) {
		return new GLRenderer(context);
	}

}
