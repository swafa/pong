package net.probico.multigoal;

import net.probico.pong.PongMainMenuFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Fragment with the main menu for the game. The main menu allows the player to
 * choose a gameplay mode.
 * 
 * @author samir
 * 
 */
public class MainMenuFragment extends PongMainMenuFragment {

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
