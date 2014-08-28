package net.probico.pong;

import net.probico.pong.PongMainActivity.GameMode;
import net.probico.pongutils.R;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.google.android.gms.games.Games;

/**
 * Fragment for the gameplay portion of the game.
 * 
 * @author samir
 * 
 */
public abstract class PongGameplayFragment extends Fragment {

	private static final String MAIN_ACTIVITY_KEY = "main_activity_key";
	private PongMainActivity activity;

	TextView countDownTxtView;

	RelativeLayout topLayout;
	RelativeLayout bottomLayout;

	TextView player1ScoreTxtView;
	TextView player2ScoreTxtView;

	TextView youWinTxtView;
	TextView youLoseTxtView;

	FrameLayout gameLayout;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(getLayoutResourceId(), container, false);

		gameLayout = (FrameLayout) v.findViewById(getGameLayoutResourceId());
		activity = (PongMainActivity) getArguments().getSerializable(
				MAIN_ACTIVITY_KEY);

		inflateGameLayout();
		return v;
	}

	public abstract int getGameLayoutResourceId();

	protected abstract int getLayoutResourceId();

	/**
	 * 
	 * Inflates Android layout with opengl and Android components
	 * 
	 * @author samir
	 */
	public void inflateGameLayout() {

		String displayName;

		displayName = setDisplayName();

		// To display countdown only on first render
		activity.getGlSurfaceView().getRenderer().setFirstRender(true);

		clearAllViews();

		// Add GLSurfaceView
		gameLayout.addView(activity.getGlSurfaceView());

		addGameInfoUi(displayName);

	}

	private void addGameInfoUi(String displayName) {
		LinearLayout ll = new LinearLayout(activity);
		ll.setOrientation(LinearLayout.VERTICAL);

		topLayout = new RelativeLayout(activity);
		LinearLayout.LayoutParams relativeLayoutParameters = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT, 1);
		topLayout.setLayoutParams(relativeLayoutParameters);

		player2ScoreTxtView = new TextView(activity);
		player2ScoreTxtView.setTextColor(Color.WHITE);
		player2ScoreTxtView.setTextAppearance(activity, R.style.LargeTextStyle);
		RelativeLayout.LayoutParams scoreTopLayoutParamaters = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		scoreTopLayoutParamaters.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		player2ScoreTxtView.setLayoutParams(scoreTopLayoutParamaters);
		player2ScoreTxtView.setText(activity.getPlayer2Score() + "");
		topLayout.addView(player2ScoreTxtView);
		ll.addView(topLayout);

		bottomLayout = new RelativeLayout(activity);
		bottomLayout.setLayoutParams(relativeLayoutParameters);

		if (activity.getGameMode().equals(GameMode.SINGLE_PLAYER)
				|| activity.getGameMode().equals(GameMode.TWO_PLAYERS_ONLINE)) {

			RelativeLayout.LayoutParams currentPlayerNameLayoutParams = new RelativeLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

			TextView currentPlayerNameTxtView = new TextView(activity);
			currentPlayerNameTxtView.setTextColor(Color.WHITE);
			currentPlayerNameTxtView.setTextAppearance(activity,
					R.style.SmallTextStyle);
			currentPlayerNameLayoutParams
					.addRule(RelativeLayout.CENTER_VERTICAL);
			currentPlayerNameTxtView
					.setLayoutParams(currentPlayerNameLayoutParams);

			currentPlayerNameTxtView.setText(displayName);

			if ((activity.getGameMode().equals(GameMode.SINGLE_PLAYER) || (activity
					.getGameMode().equals(GameMode.TWO_PLAYERS_ONLINE) && !activity
					.isCurrentParticipantInvitee()))) {
				bottomLayout.addView(currentPlayerNameTxtView);
			} else if (activity.getGameMode().equals(
					GameMode.TWO_PLAYERS_ONLINE)
					&& activity.isCurrentParticipantInvitee()) {
				topLayout.addView(currentPlayerNameTxtView);
			}

		}

		RelativeLayout.LayoutParams scoreBottomLayoutParams = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		player1ScoreTxtView = new TextView(activity);
		player1ScoreTxtView.setTextColor(Color.WHITE);
		player1ScoreTxtView.setTextAppearance(activity, R.style.LargeTextStyle);
		scoreBottomLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		player1ScoreTxtView.setLayoutParams(scoreBottomLayoutParams);
		player1ScoreTxtView.setText(activity.getPlayer1Score() + "");
		bottomLayout.addView(player1ScoreTxtView);

		RelativeLayout.LayoutParams resultMessageLayoutParameters = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

		youWinTxtView = new TextView(activity);
		youWinTxtView.setText("YOU WIN!");
		youWinTxtView.setTextColor(Color.BLUE);
		youWinTxtView.setTextAppearance(activity, R.style.XLargeTextStyle);
		resultMessageLayoutParameters.addRule(RelativeLayout.CENTER_IN_PARENT);

		youWinTxtView.setLayoutParams(resultMessageLayoutParameters);

		youLoseTxtView = new TextView(activity);
		youLoseTxtView.setText("YOU LOSE!");
		youLoseTxtView.setTextColor(Color.BLUE);
		youLoseTxtView.setTextAppearance(activity, R.style.XLargeTextStyle);
		resultMessageLayoutParameters.addRule(RelativeLayout.CENTER_IN_PARENT);

		youLoseTxtView.setLayoutParams(resultMessageLayoutParameters);

		ll.addView(bottomLayout);

		gameLayout.addView(ll);
	}

	private void clearAllViews() {
		if (activity.getGlSurfaceView().getParent() != null) {
			((FrameLayout) activity.getGlSurfaceView().getParent())
					.removeAllViews();
		}
	}

	private String setDisplayName() {
		String displayName;
		if (activity.getApiClientInstance().isConnected()
				&& Games.Players.getCurrentPlayer(activity
						.getApiClientInstance()) != null) {
			displayName = Games.Players.getCurrentPlayer(
					activity.getApiClientInstance()).getDisplayName();
		} else {
			displayName = "You";
		}
		return displayName;
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	public void updateScoresUi() {
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {

				player1ScoreTxtView.setText(activity.getPlayer1Score() + "");
				player2ScoreTxtView.setText(activity.getPlayer2Score() + "");

			}

		});

	}

	public void updateUiPlayer1Win() {

		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (activity.getGameMode().equals(GameMode.SINGLE_PLAYER)) {
					bottomLayout.addView(youWinTxtView);
				} else if (activity.getGameMode().equals(GameMode.TWO_PLAYERS)) {
					bottomLayout.addView(youWinTxtView);
					topLayout.addView(youLoseTxtView);
				} else if (activity.getGameMode().equals(
						GameMode.TWO_PLAYERS_ONLINE)) {

					if (!activity.isCurrentParticipantInvitee()) {
						topLayout.removeView(youWinTxtView);
						bottomLayout.removeView(youWinTxtView);

						bottomLayout.addView(youWinTxtView);
					}

					else {
						bottomLayout.removeView(youLoseTxtView);
						topLayout.removeView(youLoseTxtView);

						topLayout.addView(youLoseTxtView);
					}
				}

			}

		});

	}

	public void updateUiPlayer2Win() {

		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {

				if (activity.getGameMode().equals(GameMode.SINGLE_PLAYER)) {
					bottomLayout.addView(youLoseTxtView);
				} else if (activity.getGameMode().equals(GameMode.TWO_PLAYERS)) {
					bottomLayout.addView(youLoseTxtView);
					topLayout.addView(youWinTxtView);
				} else if (activity.getGameMode().equals(
						GameMode.TWO_PLAYERS_ONLINE)) {

					if (!activity.isCurrentParticipantInvitee()) {
						topLayout.removeView(youLoseTxtView);
						bottomLayout.removeView(youLoseTxtView);
						bottomLayout.addView(youLoseTxtView);
					}

					else {
						bottomLayout.removeView(youWinTxtView);
						topLayout.removeView(youWinTxtView);
						topLayout.addView(youWinTxtView);
					}
				}

			}

		});

	}

	public void showCountDown() {
		final RelativeLayout.LayoutParams layoutParameters = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

		layoutParameters.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		layoutParameters.addRule(RelativeLayout.CENTER_HORIZONTAL);

		countDownTxtView = new TextView(activity);

		countDownTxtView.setText("3");

		countDownTxtView.setTextColor(getResources().getColor(R.color.red));
		countDownTxtView.setTextAppearance(activity, R.style.XLargeTextStyle);
		countDownTxtView.setLayoutParams(layoutParameters);

		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				topLayout.addView(countDownTxtView);
			}

		});

		try {
			for (int i = 3; i > 0; i--) {
				setTextFieldText(countDownTxtView, i + "");
				Thread.sleep(1000);
			}

		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				topLayout.removeView(countDownTxtView);
			}

		});
	}

	private void setTextFieldText(final TextView txtView, final String text) {
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				txtView.setText(text);
			}

		});

	}

}
