/*
 * Copyright (C) 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.probico.multipong;

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
import com.probico.multipong.MainActivity.GameMode;

/**
 * Fragment for the gameplay portion of the game. It shows the keypad where the
 * user can request their score.
 * 
 * @author Bruno Oliveira (Google)
 * 
 */
public class GameplayFragment extends Fragment {
	// int mRequestedScore = 5000;
	//
	// static int[] MY_BUTTONS = {
	// R.id.digit_button_0, R.id.digit_button_1, R.id.digit_button_2,
	// R.id.digit_button_3, R.id.digit_button_4, R.id.digit_button_5,
	// R.id.digit_button_6, R.id.digit_button_7, R.id.digit_button_8,
	// R.id.digit_button_9, R.id.digit_button_clear, R.id.ok_score_button
	// };
	//
	// public interface Listener {
	// public void onEnteredScore(int score);
	// }
	//
	// Listener mListener = null;

	private static final String MAIN_ACTIVITY_KEY = "main_activity_key";
	private MainActivity activity;

	TextView countDownTxtView;

	RelativeLayout topLayout;
	RelativeLayout bottomLayout;

	TextView player1ScoreTxtView;
	TextView player2ScoreTxtView;

	TextView youWinTxtView;
	TextView youLoseTxtView;

	FrameLayout gameLayout;

	// public GameplayFragment(MainActivity activity){
	// this.activity = activity;
	// }

	public static GameplayFragment newInstance(MainActivity mainActivity) {
		GameplayFragment fragment = new GameplayFragment();
		Bundle bundle = new Bundle();
		bundle.putSerializable(MAIN_ACTIVITY_KEY, mainActivity);
		fragment.setArguments(bundle);

		return fragment;

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_gameplay, container, false);
		// for (int i : MY_BUTTONS) {
		// ((Button) v.findViewById(i)).setOnClickListener(this);
		// }

		gameLayout = (FrameLayout) v.findViewById(R.id.screen_gameplay);
		activity = (MainActivity) getArguments().getSerializable(
				MAIN_ACTIVITY_KEY);

		inflateGameLayout();
		return v;
	}

	public void inflateGameLayout() {

		String displayName;

		if (activity.getApiClientInstance().isConnected()
				&& Games.Players.getCurrentPlayer(activity
						.getApiClientInstance()) != null) {
			displayName = Games.Players.getCurrentPlayer(
					activity.getApiClientInstance()).getDisplayName();
		} else {
			displayName = "You";
		}

		activity.getGlSurfaceView().getRenderer().setFirstRender(true);

		if (activity.getGlSurfaceView().getParent() != null) {
			((FrameLayout) activity.getGlSurfaceView().getParent())
					.removeAllViews();
		}

		gameLayout.addView(activity.getGlSurfaceView());

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
			// vlp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			currentPlayerNameTxtView
					.setLayoutParams(currentPlayerNameLayoutParams);

			currentPlayerNameTxtView.setText(displayName);
			// tv.setBackgroundColor(0x4060ff70);

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
		// vlp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		player1ScoreTxtView.setLayoutParams(scoreBottomLayoutParams);
		player1ScoreTxtView.setText(activity.getPlayer1Score() + "");
		// tv.setBackgroundColor(0x4060ff70);
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

		// TextView countDownTxtView = new TextView(activity);
		// countDownTxtView.setText("1 sec");
		// countDownTxtView.setTextColor(Color.BLUE);
		// countDownTxtView.setTextAppearance(activity,
		// R.style.XLargeTextStyle);
		// resultMessageLayoutParameters.addRule(RelativeLayout.CENTER_IN_PARENT);
		// ll.addView(countDownTxtView);
		//
		// try {
		// Thread.sleep(3000);
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// countDownTxtView.setText("2 sec");

	}

	// public void setListener(Listener l) {
	// mListener = l;
	// }

	@Override
	public void onStart() {
		super.onStart();
		// updateUi();
	}

	void updateUi() {
		// if (getActivity() == null) return;
		// TextView scoreInput = ((TextView)
		// getActivity().findViewById(R.id.score_input));
		// if (scoreInput != null) scoreInput.setText(String.format("%04d",
		// mRequestedScore));
	}

	// @Override
	// public void onClick(View view) {
	// switch (view.getId()) {
	// case R.id.digit_button_clear:
	// mRequestedScore = 0;
	// updateUi();
	// break;
	// case R.id.digit_button_0:
	// case R.id.digit_button_1:
	// case R.id.digit_button_2:
	// case R.id.digit_button_3:
	// case R.id.digit_button_4:
	// case R.id.digit_button_5:
	// case R.id.digit_button_6:
	// case R.id.digit_button_7:
	// case R.id.digit_button_8:
	// case R.id.digit_button_9:
	// int x = Integer.parseInt(((Button)view).getText().toString().trim());
	// mRequestedScore = (mRequestedScore * 10 + x) % 10000;
	// updateUi();
	// break;
	// case R.id.ok_score_button:
	// mListener.onEnteredScore(mRequestedScore);
	// break;
	// }
	// }

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
		 // TODO Auto-generated catch block
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
