package net.probico.pong;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import net.probico.pong.opengl.PongGLSurfaceView;
import net.probico.pongutils.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesActivityResultCodes;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.Player;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.OnInvitationReceivedListener;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessage;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessageReceivedListener;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.realtime.RoomStatusUpdateListener;
import com.google.android.gms.games.multiplayer.realtime.RoomUpdateListener;
import com.google.example.games.basegameutils.BaseGameActivity;

/**
 * Our main activity for the game.
 * 
 * 
 * @author samir
 */
public abstract class PongMainActivity extends BaseGameActivity implements
		PongMainMenuFragment.Listener, PongLevelSelectionFragment.Listener,
		RoomUpdateListener, RealTimeMessageReceivedListener,
		RoomStatusUpdateListener, OnInvitationReceivedListener, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2325782740858547797L;

	private Level level;

	public Level getLevel() {
		return level;
	}

	public void setLevel(Level level) {
		this.level = level;
	}

	SoundPool soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);

	public SoundPool getSoundPool() {
		return soundPool;
	}

	public int[] getSoundIds() {
		return soundIds;
	}

	final int soundIds[] = new int[2];

	final static int MAX_SCORE = 5;
	int player1Score;

	public int getPlayer1Score() {
		return player1Score;
	}

	public void setPlayer1Score(int player1Score) {
		this.player1Score = player1Score;
	}

	int player2Score;

	public int getPlayer2Score() {
		return player2Score;
	}

	public void setPlayer2Score(int player2Score) {
		this.player2Score = player2Score;
	}

	private PongGLSurfaceView glSurfaceView;

	public enum GameMode {
		SINGLE_PLAYER, TWO_PLAYERS, TWO_PLAYERS_ONLINE
	}

	public enum Level {
		EASY, HARD
	}

	private GameMode gameMode;

	public GameMode getGameMode() {
		return gameMode;
	}

	public void setGameMode(GameMode gameMode) {
		this.gameMode = gameMode;
	}

	public PongGLSurfaceView getGlSurfaceView() {
		return glSurfaceView;
	}

	public void setGlSurfaceView(PongGLSurfaceView glSurfaceView) {
		this.glSurfaceView = glSurfaceView;
	}

	final static String MESSAGE_PADDLE_COORDINATES = "PaddleX";
	final static String MESSAGE_BALL_INFORMATION = "BallXY";
	final static String MESSAGE_SCORE = "score";

	private boolean currentParticipantInvitee = true;

	public boolean isCurrentParticipantInvitee() {
		return currentParticipantInvitee;
	}

	public void setCurrentParticipantInvitee(boolean invitee) {
		this.currentParticipantInvitee = invitee;
	}

	Room room;

	// request code for the "select players" UI
	// can be any number as long as it's unique
	final static int RC_SELECT_PLAYERS = 10000;

	// request code (can be any number, as long as it's unique)
	final static int RC_INVITATION_INBOX = 10001;

	// arbitrary request code for the waiting room UI.
	// This can be any integer that's unique in your Activity.
	final static int RC_WAITING_ROOM = 10002;

	// at least 2 players required for our game
	final static int MIN_PLAYERS = 2;

	// Fragments
	PongMainMenuFragment mMainMenuFragment;
	PongGameplayFragment mGameplayFragment;

	PongLevelSelectionFragment mLevelSelectionFragment;

	public PongGameplayFragment getmGameplayFragment() {
		return mGameplayFragment;
	}

	// request codes we use when invoking an external activity
	final int RC_RESOLVE = 5000, RC_UNUSED = 5001;

	// tag for debug logging
	final boolean ENABLE_DEBUG = true;
	final String TAG = "TanC";

	private String mIncomingInvitationId;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		// TODO: This is for ads and should be commented until decided to
		// activate cpi
		// AppsZoom.start(this);

		super.onCreate(savedInstanceState);

		// Create a GLSurfaceView instance and set it
		// as the ContentView for this Activity
		glSurfaceView = getPongGlSurfaceView();

		setContentView(R.layout.activity_main);

		// create fragments
		mMainMenuFragment = getPongMainMenuFragment();
		mLevelSelectionFragment = getPongLevelSelectionFragment();
		mGameplayFragment = getPongGamePlayFragment();

		// listen to fragment events
		mMainMenuFragment.setListener(this);
		mLevelSelectionFragment.setListener(this);

		// add initial fragment (welcome fragment)
		getSupportFragmentManager().beginTransaction()
				.add(R.id.fragment_container, mMainMenuFragment).commit();

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		// init sounds
		soundIds[0] = soundPool.load(this, R.raw.pong, 1);
		soundIds[1] = soundPool.load(this, R.raw.blop, 2);

		// TODO: This is for ads and should be commented until decided to
		// activate cpi
		// AppRater.app_launched(this);

	}

	public abstract PongLevelSelectionFragment getPongLevelSelectionFragment();

	public abstract PongMainMenuFragment getPongMainMenuFragment();

	public abstract PongGLSurfaceView getPongGlSurfaceView();

	protected abstract PongGameplayFragment getPongGamePlayFragment();

	// Switch UI to the given fragment
	void switchToFragment(Fragment newFrag) {
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.fragment_container, newFrag).commit();

	}

	@Override
	public void onSignInFailed() {
		// Sign-in failed, so show sign-in button on main menu
		mMainMenuFragment.setGreeting(getString(R.string.signed_out_greeting));
		mMainMenuFragment.setShowSignInButton(true);
	}

	@Override
	public void onSignInSucceeded() {

		Games.Invitations.registerInvitationListener(getApiClient(), this);

		// Show sign-out button on main menu
		mMainMenuFragment.setShowSignInButton(false);

		// Set the greeting appropriately on main menu
		Player p = Games.Players.getCurrentPlayer(getApiClient());
		String displayName;
		if (p == null) {
			Log.w(TAG, "mGamesClient.getCurrentPlayer() is NULL!");
			displayName = "???";
		} else {
			displayName = p.getDisplayName();
		}
		mMainMenuFragment.setGreeting("Hello, " + displayName);

		if (getInvitationId() != null) {
			RoomConfig.Builder roomConfigBuilder = makeBasicRoomConfigBuilder();
			roomConfigBuilder.setInvitationIdToAccept(getInvitationId());
			Games.RealTimeMultiplayer.join(getApiClient(),
					roomConfigBuilder.build());

			// prevent screen from sleeping during handshake
			getWindow()
					.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		}

	}

	@Override
	public void onSignInButtonClicked() {

		// start the sign-in flow
		beginUserInitiatedSignIn();

	}

	@Override
	public void onSignOutButtonClicked() {
		signOut();
		mMainMenuFragment.setGreeting(getString(R.string.signed_out_greeting));
		mMainMenuFragment.setShowSignInButton(true);
	}

	@Override
	public void onTwoPlayersOnlineButtonClicked() {

		if (!getApiClient().isConnected()) {
			Toast.makeText(this, R.string.please_sign_in_message,
					Toast.LENGTH_SHORT).show();
			return;
		}

		launchPlayerPickerUi();

	}

	private void launchPlayerPickerUi() {
		// launch the player selection screen
		// minimum: 1 other player; maximum: 3 other players
		Intent intent = Games.RealTimeMultiplayer.getSelectOpponentsIntent(
				getApiClient(), 1, 1);
		startActivityForResult(intent, RC_SELECT_PLAYERS);
		setCurrentParticipantInvitee(false);

	}

	@Override
	public void onActivityResult(int request, int response, Intent data) {
		super.onActivityResult(request, response, data);

		if (request == RC_SELECT_PLAYERS) {
			if (response != Activity.RESULT_OK) {
				// user canceled
				return;
			}

			// get the invitee list
			final ArrayList<String> invitees = data
					.getStringArrayListExtra(Games.EXTRA_PLAYER_IDS);

			// get auto-match criteria
			Bundle autoMatchCriteria = null;
			int minAutoMatchPlayers = data.getIntExtra(
					Multiplayer.EXTRA_MIN_AUTOMATCH_PLAYERS, 0);
			int maxAutoMatchPlayers = data.getIntExtra(
					Multiplayer.EXTRA_MAX_AUTOMATCH_PLAYERS, 0);

			if (minAutoMatchPlayers > 0) {
				autoMatchCriteria = RoomConfig.createAutoMatchCriteria(
						minAutoMatchPlayers, maxAutoMatchPlayers, 0);
			} else {
				autoMatchCriteria = null;
			}

			// create the room and specify a variant if appropriate
			RoomConfig.Builder roomConfigBuilder = makeBasicRoomConfigBuilder();
			roomConfigBuilder.addPlayersToInvite(invitees);
			if (autoMatchCriteria != null) {
				roomConfigBuilder.setAutoMatchCriteria(autoMatchCriteria);
			}
			RoomConfig roomConfig = roomConfigBuilder.build();
			Games.RealTimeMultiplayer.create(getApiClient(), roomConfig);

			// prevent screen from sleeping during handshake
			getWindow()
					.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		}

		if (request == RC_WAITING_ROOM) {
			if (response == Activity.RESULT_OK) {
				this.gameMode = GameMode.TWO_PLAYERS_ONLINE;
				switchToFragment(mGameplayFragment);

			} else if (response == Activity.RESULT_CANCELED) {
				// Waiting room was dismissed with the back button. The meaning
				// of this
				// action is up to the game. You may choose to leave the room
				// and cancel the
				// match, or do something else like minimize the waiting room
				// and
				// continue to connect in the background.

				// we take the simple approach and just leave
				// the room:
				if (room != null) {
					Games.RealTimeMultiplayer.leave(getApiClient(), this,
							room.getRoomId());
				}
				getWindow().clearFlags(
						WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			} else if (response == GamesActivityResultCodes.RESULT_LEFT_ROOM) {

				if (room != null) {
					// player wants to leave the room.
					Games.RealTimeMultiplayer.leave(getApiClient(), this,
							room.getRoomId());
				}
				getWindow().clearFlags(
						WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			}
		}

		if (request == RC_INVITATION_INBOX) {
			if (response != Activity.RESULT_OK) {
				// canceled
				return;
			}

			// get the selected invitation
			Bundle extras = data.getExtras();
			Invitation invitation = extras
					.getParcelable(Multiplayer.EXTRA_INVITATION);

			// accept it!
			RoomConfig roomConfig = makeBasicRoomConfigBuilder()
					.setInvitationIdToAccept(invitation.getInvitationId())
					.build();
			Games.RealTimeMultiplayer.join(getApiClient(), roomConfig);

			// prevent screen from sleeping during handshake
			getWindow()
					.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		}

	}

	private RoomConfig.Builder makeBasicRoomConfigBuilder() {
		return RoomConfig.builder(this).setMessageReceivedListener(this)
				.setRoomStatusUpdateListener(this);
	}

	@Override
	public void onRoomCreated(int statusCode, Room room) {
		if (statusCode != GamesStatusCodes.STATUS_OK) {
			// display error
			return;
		}

		// get waiting room intent
		Intent i = Games.RealTimeMultiplayer.getWaitingRoomIntent(
				getApiClient(), room, Integer.MAX_VALUE);
		startActivityForResult(i, RC_WAITING_ROOM);

		this.room = room;

	}

	@Override
	public void onJoinedRoom(int statusCode, Room room) {
		if (statusCode != GamesStatusCodes.STATUS_OK) {
			// display error
			return;
		}

		// get waiting room intent
		Intent i = Games.RealTimeMultiplayer.getWaitingRoomIntent(
				getApiClient(), room, Integer.MAX_VALUE);
		startActivityForResult(i, RC_WAITING_ROOM);

		this.room = room;

	}

	@Override
	public void onLeftRoom(int arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRoomConnected(int statusCode, Room room) {
		if (statusCode != GamesStatusCodes.STATUS_OK) {
			// let screen go to sleep
			getWindow().clearFlags(
					WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

			// show error message, return to main screen.
		}

		this.room = room;
	}

	@Override
	public void onRealTimeMessageReceived(RealTimeMessage rtm) {
		// get real-time message and decode it
		byte[] messageData = rtm.getMessageData();
		String message = new String(messageData, Charset.forName("UTF-8"));

		String[] messageComponents = message.split(" ");

		// Move opponent's paddle on screen
		if (messageComponents[0].equals(MESSAGE_PADDLE_COORDINATES)) {
			updateOpponentPaddlePosition(Float.parseFloat(messageComponents[1]));

		} else if (messageComponents[0].equals(MESSAGE_BALL_INFORMATION)) {
			float xTranslateValue = Float.parseFloat(messageComponents[1]);
			float yTranslateValue = Float.parseFloat(messageComponents[2]);

			boolean ballMovingDirectionRight = Boolean
					.parseBoolean(messageComponents[3]);
			boolean ballMovingDirectionUp = Boolean
					.parseBoolean(messageComponents[4]);

			float xSpeed = Float.parseFloat(messageComponents[5]);
			float ySpeed = Float.parseFloat(messageComponents[6]);

			correctBallPositionAndVelocity(xTranslateValue, yTranslateValue,
					ballMovingDirectionRight, ballMovingDirectionUp, xSpeed,
					ySpeed);

		}

		else if (messageComponents[0].equals(MESSAGE_SCORE)) {
			player1Score = Math.max(player1Score,
					Integer.parseInt(messageComponents[1]));
			player2Score = Math.max(player2Score,
					Integer.parseInt(messageComponents[2]));

			mGameplayFragment.updateScoresUi();
			checkGameOver();
		}

	}

	private void correctBallPositionAndVelocity(float xTranslateValue, float yTranslateValue,
			boolean ballMovingDirectionRight, boolean ballMovingDirectionUp,
			float xSpeed, float ySpeed) {
		float ballXSpeed = glSurfaceView.getRenderer().getBall()
				.getxSpeed();
		float ballYSpeed = glSurfaceView.getRenderer().getBall()
				.getySpeed();

		// Only correct ball position if it needs correction (happens when
		// the connection is slow or when one device renders the screen
		// relatively faster than the other device)
		if (Math.abs(glSurfaceView.getRenderer().getBall()
				.getyTranslateValue()
				- yTranslateValue) > (ballYSpeed * 0.15 / 0.01)
				|| Math.abs(glSurfaceView.getRenderer().getBall()
						.getxTranslateValue()
						- xTranslateValue) > (ballXSpeed * 0.15 / 0.01)) {

			glSurfaceView.getRenderer().getBall()
					.setxTranslateValue(xTranslateValue);
			glSurfaceView.getRenderer().getBall()
					.setyTranslateValue(yTranslateValue);
		}

		// Correct ball velocity information to ensure that game is
		// synchronized between both devices
		glSurfaceView.getRenderer().getBall()
				.setBallMovingDirectionRight(ballMovingDirectionRight);
		glSurfaceView.getRenderer().getBall()
				.setBallMovingDirectionUp(ballMovingDirectionUp);

		glSurfaceView.getRenderer().getBall().setxSpeed(xSpeed);
		glSurfaceView.getRenderer().getBall().setySpeed(ySpeed);
	}

	private void updateOpponentPaddlePosition(Float xPosition) {
		if (!isCurrentParticipantInvitee()) {
			glSurfaceView.getRenderer().getTopPaddle()
					.setxTranslateValue(xPosition);
		} else {
			glSurfaceView.getRenderer().getBottomPaddle()
					.setxTranslateValue(xPosition);
		}
	}

	@Override
	public void onConnectedToRoom(Room arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDisconnectedFromRoom(Room room) {
		// leave the room
		Games.RealTimeMultiplayer.leave(getApiClient(), this, room.getRoomId());

		// clear the flag that keeps the screen on
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		// show error message and return to main screen

	}

	@Override
	public void onP2PConnected(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onP2PDisconnected(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPeerDeclined(Room room, List<String> peers) {
		// leave the room
		Games.RealTimeMultiplayer.leave(getApiClient(), this, room.getRoomId());

		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	@Override
	public void onPeerInvitedToRoom(Room arg0, List<String> arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPeerJoined(Room arg0, List<String> arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPeerLeft(Room room, List<String> peers) {

		// leave the room
		Games.RealTimeMultiplayer.leave(getApiClient(), this, room.getRoomId());

		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		switchToFragment(mMainMenuFragment);

		Toast.makeText(this, R.string.game_cancelled_message,
				Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onPeersConnected(Room room, List<String> peers) {
		if (mGameplayFragment.isVisible()) {
			// add new player to an ongoing game
		} else if (shouldStartGame(room)) {
			// start game!
		}
	}

	@Override
	public void onPeersDisconnected(Room room, List<String> peers) {
		Games.RealTimeMultiplayer.leave(getApiClient(), this, room.getRoomId());
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		switchToFragment(mMainMenuFragment);
		Toast.makeText(this, R.string.game_cancelled_message,
				Toast.LENGTH_SHORT).show();

	}

	@Override
	public void onRoomAutoMatching(Room arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRoomConnecting(Room arg0) {
		// TODO Auto-generated method stub

	}

	// returns whether there are enough players to start the game
	boolean shouldStartGame(Room room) {
		int connectedPlayers = 0;
		for (Participant p : room.getParticipants()) {
			if (p.isConnectedToRoom())
				++connectedPlayers;
		}
		return connectedPlayers >= MIN_PLAYERS;
	}

	@Override
	protected void onStop() {
		super.onStop();

		if (room != null && getApiClient().isConnected()) {
			// leave the room
			Games.RealTimeMultiplayer.leave(getApiClient(), this,
					room.getRoomId());

			getApiClient().disconnect();

		}
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	@Override
	protected void onPause() {
		super.onPause();
		// The following call pauses the rendering thread.
		// If your OpenGL application is memory intensive,
		// you should consider de-allocating objects that
		// consume significant memory here.
		glSurfaceView.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();

		// The following call resumes a paused rendering thread.
		// If you de-allocated graphic objects for onPause()
		// this is a good place to re-allocate them.
		glSurfaceView.onResume();

		if (player1Score >= 5 || player2Score >= 5) {
			glSurfaceView.getRenderer().getBall().setxSpeed(0);
			glSurfaceView.getRenderer().getBall().setySpeed(0);
		}

	}

	public void sendPaddleX(float xTranslateValue) {
		String message = MESSAGE_PADDLE_COORDINATES + " " + xTranslateValue;
		byte[] messageData = message.getBytes(Charset.forName("UTF-8"));

		Games.RealTimeMultiplayer.sendUnreliableMessageToOthers(getApiClient(),
				messageData, room.getRoomId());

	}

	public void sendBallInformation(float xTranslateValue,
			float yTranslateValue, boolean ballMovingDirectionRight,
			boolean ballMovingDirectionUp, float xSpeed, float ySpeed) {
		String message = MESSAGE_BALL_INFORMATION + " " + xTranslateValue + " "
				+ yTranslateValue + " " + ballMovingDirectionRight + " "
				+ ballMovingDirectionUp + " " + xSpeed + " " + ySpeed;
		byte[] messageData = message.getBytes(Charset.forName("UTF-8"));

		Games.RealTimeMultiplayer.sendUnreliableMessageToOthers(getApiClient(),
				messageData, room.getRoomId());
	}

	@Override
	public void onSinglePlayerButtonClicked() {
		this.gameMode = GameMode.SINGLE_PLAYER;
		switchToFragment(mLevelSelectionFragment);

	}

	public void incrementPlayer1Score() {
		player1Score++;

		mGameplayFragment.updateScoresUi();

		checkGameOver();

	}

	private void checkGameOver() {

		if (gameOver()) {
			glSurfaceView.getRenderer().getBall().setxSpeed(0);
			glSurfaceView.getRenderer().getBall().setySpeed(0);

			if (player1Score >= MAX_SCORE) {
				mGameplayFragment.updateUiPlayer1Win();
			} else {
				mGameplayFragment.updateUiPlayer2Win();
			}

			if (gameMode.equals(GameMode.SINGLE_PLAYER)
					|| gameMode.equals(GameMode.TWO_PLAYERS)) {
				showRestartGameDialog();
			}

			// TODO: This is for ads and should be commented until decided to
			// activate cpi
			// AppsZoom.fetchAd(null, new AppsZoom.OnAdFetchedListener() {
			// @Override
			// public void onAdFetched() {
			// AppsZoom.showAd(PongMainActivity.this);
			// }
			// });

		}
	}

	public boolean gameOver() {
		return player1Score >= MAX_SCORE || player2Score >= MAX_SCORE;
	}

	private void showRestartGameDialog() {

		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// 1. Instantiate an AlertDialog.Builder with its constructor
				AlertDialog.Builder builder = new AlertDialog.Builder(
						PongMainActivity.this);

				// 2. Chain together various setter methods to set the dialog
				// characteristics
				builder.setMessage(R.string.new_match_prompt).setTitle(
						R.string.new_match);

				// Add the buttons
				builder.setPositiveButton(R.string.yes,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								resetScores();
								mGameplayFragment.updateScoresUi();

								mGameplayFragment.inflateGameLayout();

							}
						});
				builder.setNegativeButton(R.string.no,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								onBackPressed();
							}
						});

				// 3. Get the AlertDialog from create()
				AlertDialog dialog = builder.create();

				dialog.show();
			}

		});

	}

	public void incrementPlayer2Score() {

		player2Score++;

		mGameplayFragment.updateScoresUi();

		checkGameOver();
	}

	@Override
	public void onTwoPlayersButtonClicked() {
		this.gameMode = GameMode.TWO_PLAYERS;
		switchToFragment(mGameplayFragment);

	}

	public void sendUpdateScoreMessage() {
		String message = MESSAGE_SCORE + " " + player1Score + " "
				+ player2Score;
		byte[] messageData = message.getBytes(Charset.forName("UTF-8"));

		Games.RealTimeMultiplayer.sendUnreliableMessageToOthers(getApiClient(),
				messageData, room.getRoomId());

	}

	public GoogleApiClient getApiClientInstance() {
		return getApiClient();
	}

	@Override
	public void onBackPressed() {

		resetScores();

		if (mMainMenuFragment.isVisible()) {
			super.onBackPressed();
		} else {
			if (getApiClient().isConnected() && room != null) {
				Games.RealTimeMultiplayer.leave(getApiClient(), this,
						room.getRoomId());
				// clear the flag that keeps the screen on
				getWindow().clearFlags(
						WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			}
			switchToFragment(mMainMenuFragment);

		}

	}

	private void resetScores() {
		this.player1Score = 0;
		this.player2Score = 0;

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// No call for super(). Bug on API Level > 11.
	}

	@Override
	public void onInvitationsButtonClicked() {

		if (!getApiClient().isConnected()) {
			Toast.makeText(this, R.string.please_sign_in_message,
					Toast.LENGTH_SHORT).show();
			return;
		}

		// launch the intent to show the invitation inbox screen
		Intent intent = Games.Invitations
				.getInvitationInboxIntent(getApiClient());
		startActivityForResult(intent, RC_INVITATION_INBOX);

	}

	@Override
	public void onInvitationReceived(Invitation invitation) {
		// show in-game popup to let user know of pending invitation
		// 1. Instantiate an AlertDialog.Builder with its constructor
		AlertDialog.Builder builder = new AlertDialog.Builder(
				PongMainActivity.this);

		// 2. Chain together various setter methods to set the dialog
		// characteristics
		builder.setMessage(
				invitation.getInviter().getDisplayName()
						+ " invited you to play Pong. Would you like to accept the invitation?")
				.setTitle(R.string.incoming_invitation);

		// Add the buttons
		builder.setPositiveButton(R.string.yes,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {

						RoomConfig.Builder roomConfigBuilder = makeBasicRoomConfigBuilder();
						roomConfigBuilder
								.setInvitationIdToAccept(mIncomingInvitationId);
						Games.RealTimeMultiplayer.join(getApiClient(),
								roomConfigBuilder.build());

						// prevent screen from sleeping during handshake
						getWindow().addFlags(
								WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

						// PongMainActivity.this.gameMode =
						// GameMode.TWO_PLAYERS_ONLINE;
						// switchToFragment(mGameplayFragment);

					}
				});

		builder.setNegativeButton(R.string.not_now,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// Do nothing
					}
				});

		// 3. Get the AlertDialog from create()
		AlertDialog dialog = builder.create();

		dialog.show();

		// store invitation for use when player accepts this invitation
		mIncomingInvitationId = invitation.getInvitationId();

	}

	@Override
	public void onInvitationRemoved(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onEasyButtonClicked() {
		this.level = Level.EASY;
		switchToFragment(mGameplayFragment);
	}

	@Override
	public void onHardButtonClicked() {
		this.level = Level.HARD;
		switchToFragment(mGameplayFragment);

	}

}
