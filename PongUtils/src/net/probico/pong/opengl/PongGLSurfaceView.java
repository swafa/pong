package net.probico.pong.opengl;

import net.probico.pong.PongMainActivity;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

/**
 * A view container where OpenGL ES graphics can be drawn on screen. This view
 * can also be used to capture touch events, such as a user interacting with
 * drawn objects.
 * 
 * @author samir
 */
public abstract class PongGLSurfaceView extends GLSurfaceView {

	private final PongGLRenderer renderer;

	float mPreviousXBottom;
	float mPreviousXTop;

	PongMainActivity activity;

	public PongGLRenderer getRenderer() {
		return renderer;
	}

	public PongGLSurfaceView(Context context) {
		super(context);

		this.setId(10);
		if (context instanceof PongMainActivity) {
			activity = (PongMainActivity) context;
		}

		// Create an OpenGL ES 2.0 context.
		setEGLContextClientVersion(2);

		// Set the Renderer for drawing on the GLSurfaceView
		renderer = getPongGLRenderer(context);
		setRenderer(renderer);

	}

	public abstract PongGLRenderer getPongGLRenderer(Context context);


	@Override
	public boolean onTouchEvent(MotionEvent e) {
		// MotionEvent reports input details from the touch screen
		// and other input controls. In this case, you are only
		// interested in events where the touch position changed.

		float x;
		float y;

		int pointerId;

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
							PongMainActivity.GameMode.SINGLE_PLAYER)
							|| this.activity.getGameMode().equals(
									PongMainActivity.GameMode.TWO_PLAYERS)
							|| (this.activity
									.getGameMode()
									.equals(PongMainActivity.GameMode.TWO_PLAYERS_ONLINE) && !renderer
									.getActivity()
									.isCurrentParticipantInvitee())) {
						getRenderer().getBottomPaddle().setxTranslateValue(
								-1f + (x / getRenderer().getScreenWidth())
										* (2f));


						if (this.activity.getGameMode().equals(
								PongMainActivity.GameMode.TWO_PLAYERS_ONLINE)
								&& !renderer.getActivity()
										.isCurrentParticipantInvitee()) {
							activity.sendPaddleX(getRenderer()
									.getBottomPaddle().getxTranslateValue());
						}

					}

				}

				// Move Top paddle
				else {
					if (this.activity.getGameMode().equals(
							PongMainActivity.GameMode.TWO_PLAYERS)
							|| (this.activity
									.getGameMode()
									.equals(PongMainActivity.GameMode.TWO_PLAYERS_ONLINE) && renderer
									.getActivity()
									.isCurrentParticipantInvitee())) {

						getRenderer()
								.getTopPaddle()
								.setxTranslateValue(
										-1f
												+ (x / getRenderer()
														.getScreenWidth()) * 2f);

						if (this.activity.getGameMode().equals(
								PongMainActivity.GameMode.TWO_PLAYERS_ONLINE)
								&& renderer.getActivity()
										.isCurrentParticipantInvitee()) {
							activity.sendPaddleX(getRenderer().getTopPaddle()
									.getxTranslateValue());
						}
					}

				}

			}

			break;

		default:
			break;
		}

		return true;
	}

}
