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

package net.probico.pong;

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
public abstract class PongMainMenuFragment extends Fragment implements
		OnClickListener {
	String mGreeting = "";

	int signInBtnResourceId;
	int signOutBtnResourceId;

	int singlePlayerBtnResourceId;
	int twoPlayersBtnResourceId;
	int twoPlayersOnlineBtnResourceId;
	int invitationsBtnResourceId;
	int greetingResourceId;
	int signInBarResourceId;
	int signOutBarResourceId;

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
		View v = inflater.inflate(getLayoutResourceId(), container, false);
		initResourceIds();
		SignInButton gSignInButton = (SignInButton) v
				.findViewById(signInBtnResourceId);
		gSignInButton.setOnClickListener(this);
		gSignInButton.setEnabled(true);
		gSignInButton.setSize(SignInButton.SIZE_ICON_ONLY);// wide button style
		gSignInButton.setColorScheme(SignInButton.COLOR_LIGHT);

		final int[] CLICKABLES = new int[] {
				singlePlayerBtnResourceId,
				// R.id.easy_mode_button, R.id.hard_mode_button,
				// R.id.quick_start_button,
				twoPlayersBtnResourceId, twoPlayersOnlineBtnResourceId,
				invitationsBtnResourceId,
				// R.id.show_invitations_button,
				// R.id.show_achievements_button, R.id.show_leaderboards_button,
				signInBtnResourceId, signOutBtnResourceId };
		for (int i : CLICKABLES) {
			v.findViewById(i).setOnClickListener(this);
		}

		return v;
	}

	private void initResourceIds() {
		signInBtnResourceId = getSignInBtnResourceId();
		signOutBtnResourceId = getSignOutBtnResourceId();

		singlePlayerBtnResourceId = getSinglePlayerBtnResourceId();
		twoPlayersBtnResourceId = getTwoPlayersBtnResourceId();
		twoPlayersOnlineBtnResourceId = getTwoPlayersOnlineBtnResourceId();
		invitationsBtnResourceId = getInvitationsBtnResourceId();

		greetingResourceId = getGreetingResourceId();

		signInBarResourceId = getSignInBarResourceId();
		signOutBarResourceId = getSignOutBarResourceId();

	}

	public abstract int getSignInBtnResourceId();

	public abstract int getSignOutBtnResourceId();

	public abstract int getSinglePlayerBtnResourceId();

	public abstract int getTwoPlayersBtnResourceId();

	public abstract int getTwoPlayersOnlineBtnResourceId();

	public abstract int getInvitationsBtnResourceId();

	public abstract int getGreetingResourceId();

	public abstract int getSignInBarResourceId();

	public abstract int getSignOutBarResourceId();

	public abstract int getLayoutResourceId();

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
		TextView tv = (TextView) getActivity().findViewById(greetingResourceId);
		if (tv != null)
			tv.setText(mGreeting);

		getActivity().findViewById(signInBarResourceId).setVisibility(
				mShowSignIn ? View.VISIBLE : View.GONE);
		getActivity().findViewById(signOutBarResourceId).setVisibility(
				mShowSignIn ? View.GONE : View.VISIBLE);
	}

	@Override
	public void onClick(View view) {
		int id = view.getId();
		if (id == signInBtnResourceId) {
			mListener.onSignInButtonClicked();
		} else if (id == signOutBtnResourceId) {
			mListener.onSignOutButtonClicked();
		} else if (id == singlePlayerBtnResourceId) {
			mListener.onSinglePlayerButtonClicked();
		} else if (id == twoPlayersBtnResourceId) {
			mListener.onTwoPlayersButtonClicked();
		} else if (id == twoPlayersOnlineBtnResourceId) {
			mListener.onTwoPlayersOnlineButtonClicked();
		} else if (id == invitationsBtnResourceId) {
			mListener.onInvitationsButtonClicked();
		}

	}

	public void setShowSignInButton(boolean showSignIn) {
		mShowSignIn = showSignIn;
		updateUi();
	}
}
