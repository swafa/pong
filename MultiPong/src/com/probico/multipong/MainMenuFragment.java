/* Copyright (C) 2013 Google Inc.
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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.common.SignInButton;

/**
 * Fragment with the main menu for the game. The main menu allows the player to
 * choose a gameplay mode (Easy or Hard), and click the buttons to show view
 * achievements/leaderboards.
 * 
 * @author Bruno Oliveira (Google)
 * 
 */
public class MainMenuFragment extends Fragment implements OnClickListener {
	String mGreeting = "";

	public interface Listener {

		public void onSignInButtonClicked();

		public void onSignOutButtonClicked();

		public void onTwoPlayersOnlineButtonClicked();
		
		public void onSinglePlayerButtonClicked();
		
		public void onTwoPlayersButtonClicked();
		
		public void onInvitationsButtonClicked();
		
	}

	Listener mListener = null;
	boolean mShowSignIn = true;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_mainmenu, container, false);
		
		SignInButton gSignInButton = (SignInButton) v.findViewById(R.id.sign_in_button);
		gSignInButton.setOnClickListener(this);
		gSignInButton.setEnabled(true);
		gSignInButton.setSize(SignInButton.SIZE_ICON_ONLY);// wide button style
		gSignInButton.setColorScheme(SignInButton.COLOR_LIGHT);
		
		final int[] CLICKABLES = new int[] { R.id.single_player_button,
				// R.id.easy_mode_button, R.id.hard_mode_button,
				// R.id.quick_start_button,
				R.id.two_players_button,
				R.id.two_players_online_button,
				R.id.invitations_button,
				// R.id.show_invitations_button,
				// R.id.show_achievements_button, R.id.show_leaderboards_button,
				R.id.sign_in_button, R.id.sign_out_button };
		for (int i : CLICKABLES) {
			v.findViewById(i).setOnClickListener(this);
		}
		
		
		return v;
	}

	public void setListener(Listener l) {
		mListener = l;
	}

	@Override
	public void onStart() {
		super.onStart();
		updateUi();
	}

	public void setGreeting(String greeting) {
		mGreeting = greeting;
		updateUi();
	}

	void updateUi() {
		if (getActivity() == null)
			return;
		TextView tv = (TextView) getActivity().findViewById(R.id.hello);
		if (tv != null)
			tv.setText(mGreeting);

		getActivity().findViewById(R.id.sign_in_bar).setVisibility(
				mShowSignIn ? View.VISIBLE : View.GONE);
		getActivity().findViewById(R.id.sign_out_bar).setVisibility(
				mShowSignIn ? View.GONE : View.VISIBLE);
	}

	@Override
	public void onClick(View view) {
		int id = view.getId();
		if (id == R.id.sign_in_button) {
			mListener.onSignInButtonClicked();
		} else if (id == R.id.sign_out_button) {
			mListener.onSignOutButtonClicked();
		} else if (id == R.id.single_player_button) {
			mListener.onSinglePlayerButtonClicked();
		} else if (id == R.id.two_players_button) {
			mListener.onTwoPlayersButtonClicked();
		} else if (id == R.id.two_players_online_button) {
			mListener.onTwoPlayersOnlineButtonClicked();
		}
	 else if (id == R.id.invitations_button) {
		mListener.onInvitationsButtonClicked();
	}
		
	}

	public void setShowSignInButton(boolean showSignIn) {
		mShowSignIn = showSignIn;
		updateUi();
	}
}
