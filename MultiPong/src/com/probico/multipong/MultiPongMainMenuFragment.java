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

import net.probico.pong.PongMainMenuFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Fragment with the main menu for the game. The main menu allows the player to
 * choose a gameplay mode (Easy or Hard), and click the buttons to show view
 * achievements/leaderboards.
 * 
 * @author Bruno Oliveira (Google)
 * 
 */
public class MultiPongMainMenuFragment extends PongMainMenuFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public int getLayoutResourceId() {
		return R.layout.fragment_mainmenu;
	}

	@Override
	public int getSignInBtnResourceId() {
		return R.id.sign_in_button;
	}

	@Override
	public int getSignOutBtnResourceId() {
		return R.id.sign_out_button;
	}

	@Override
	public int getSinglePlayerBtnResourceId() {
		return R.id.single_player_button;
	}

	@Override
	public int getTwoPlayersBtnResourceId() {
		return R.id.two_players_button;
	}

	@Override
	public int getTwoPlayersOnlineBtnResourceId() {
		return R.id.two_players_online_button;
	}

	@Override
	public int getInvitationsBtnResourceId() {
		return R.id.invitations_button;
	}

	@Override
	public int getGreetingResourceId() {
		return R.id.hello;
	}

	@Override
	public int getSignInBarResourceId() {
		return R.id.sign_in_bar;
	}

	@Override
	public int getSignOutBarResourceId() {
		return R.id.sign_out_bar;
	}

}
