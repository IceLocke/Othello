package com.othello.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.UBJsonReader;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.othello.game.core.LocalOthelloCore;
import com.othello.game.core.OthelloGame;
import com.othello.game.player.AIPlayer;
import com.othello.game.player.LocalPlayer;
import com.othello.game.player.OnlinePlayer;
import com.othello.game.player.Player;
import com.othello.game.processor.GameInputProcessor;
import com.othello.game.processor.HomeInputProcessor;
import com.othello.game.online.OnlineOthelloClient;
import com.othello.game.online.OnlineOthelloServer;
import com.othello.game.utils.*;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;

import static com.othello.game.utils.OthelloConstants.AIDifficulty.*;
import static com.othello.game.utils.OthelloConstants.DiscType.*;

public class Othello extends ApplicationAdapter {

	// 3D ???????????????
	public PerspectiveCamera cam;

	public Environment environment;

	public Model frameModel;

	public Model boardModel;
	public Model discModel;
	public Model tableModel;
	public Model pointerModel;
	public ModelBatch modelBatch;

	public ModelInstance frameInstance;
	public ModelInstance boardInstance;
	public ModelInstance tableInstance;
	public ArrayList<ModelInstance> discInstanceList;
	public ArrayList<ModelInstance> pointerInstanceList;
	public ArrayList<AnimationController> discAnimationControllerList;
	public DiscList discList;
	public ArrayList<ModelInstance> renderInstanceList;

	// ?????????????????????
	public FreeTypeFontGenerator generator;
	public FreeTypeFontGenerator generatorBold;
	public BitmapFont font;
	public BitmapFont boldFont;
	public BitmapFont titleFont;
	public BitmapFont buttonFont;
	public BitmapFont buttonFontBold;

	// ?????????????????????
	public static Sound chessSound1;
	public static Sound chessSound2;
	public static Sound bgm;
	public static long bgmId;
	public static boolean isMuted;

	// ?????????????????????
	public static int interfaceType = OthelloConstants.InterfaceType.HOME;
	public static int menuButtonType = OthelloConstants.MenuButtonType.NONE;
	public static int lastTurnColor = OthelloConstants.DiscType.BLANK;
	public static boolean menuButtonPressed = false;
	public static boolean boardClicked = false;
	public static boolean aiIsThinking = false;
	public static Position boardClickPosition;
	public static OthelloGame game;
	public static final float FPS = 1f / 60;
	public static boolean remotePlayerDisconnected = false;

	// ??????UI???????????????
	protected SpriteBatch batch;
	protected Texture homeLoading;
	protected Texture homeDefault;
	protected Texture defaultBlackPlayerProfilePhoto;
	protected Texture defaultWhitePlayerProfilePhoto;
	protected int[][] board;
	protected int[][] newBoard;

	protected Skin skin;
	protected Stage homeStage;
	protected Stage gameStage;
	protected Table homeTable;
	protected Table gameButtonTable;
	protected Table playerTable;
	protected Label gameRoundLabel;
	protected Label player1NameLabel;
	protected Label player2NameLabel;
	protected Label player1WinCountLabel;
	protected Label player2WinCountLabel;
	protected Label.LabelStyle labelStyle;
	protected Label.LabelStyle boldLabelStyle;
	protected Label.LabelStyle titleLabelStyle;
	protected TextButton.TextButtonStyle buttonStyle;
	protected TextField.TextFieldStyle textFieldStyle;
	protected SelectBox.SelectBoxStyle selectBoxStyle;

	// ????????????by kl
	protected OnlineOthelloServer server;
	protected OnlineOthelloClient client;
	protected Label serverIP;
	public String onlinePlayerName = null;
	public String onlineRemotePlayerName = null;

	public boolean animationIsOver() {
		boolean over = true;
		for (Disc disc : discList.getDiscList())
			over = over && disc.animationIsOver;
		return over;
	}

	public void backToHome() {
		if (game != null && server != null) {
			if (game.getMode() == OthelloConstants.GameMode.ONLINE_LOCAL_PLAYER)
				server.disconnect();
			if (game.getMode() == OthelloConstants.GameMode.ONLINE_REMOTE_PLAYER)
				client.disconnect();
		}
		isMuted = false;
		bgm.stop(bgmId);
		clearBoard();
		interfaceType = OthelloConstants.InterfaceType.HOME;
		Gdx.input.setInputProcessor(new HomeInputProcessor());
	}
	public void loadBoard() {
		for (int i = 0; i <= 9; i++)
			for (int j = 0; j <= 9; j++)
				newBoard[i][j] = game.getNowPlayBoard()[i][j];
	}

	public void clearBoard() {
		for (int i = 1; i <= 8; i++) {
			for (int j = 1; j <= 8; j++)
				board[i][j] = 0;
		}
		renderInstanceList.clear();
		renderInstanceList.add(tableInstance);
		renderInstanceList.add(frameInstance);
		renderInstanceList.add(boardInstance);
		discList = new DiscList();
		lastTurnColor = OthelloConstants.DiscType.BLANK;

		for (int i = 0; i < discInstanceList.size(); i++) {
			discInstanceList.set(i, new ModelInstance(discModel));
			discAnimationControllerList.set(i, new AnimationController(discInstanceList.get(i)));
		}
	}

	// ???????????????
	public void renderHome() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		if (interfaceType == OthelloConstants.InterfaceType.HOME) {
			// ??????????????????????????????
			batch.draw(homeDefault, 0, 0);
			titleFont.draw(batch, "Othello!", 480f, 540f);
			// ????????????
			BitmapFont spFont, mpFont, ogFont, exitFont;
			spFont = mpFont = ogFont = exitFont = buttonFont;
			switch (menuButtonType) {
				case OthelloConstants.MenuButtonType.NONE:
					break;
				case OthelloConstants.MenuButtonType.LOCAL_SINGLE_PLAYER:
					spFont = buttonFontBold; break;
				case OthelloConstants.MenuButtonType.LOCAL_MULTIPLE_PLAYER:
					mpFont = buttonFontBold; break;
				case OthelloConstants.MenuButtonType.ONLINE_MULTIPLE_PLAYER:
					ogFont = buttonFontBold; break;
				case OthelloConstants.MenuButtonType.EXIT:
					exitFont = buttonFontBold; break;
			}
			spFont.draw(batch, "Single Player", 100f, 290f);
			mpFont.draw(batch, "Multiple Player", 100f, 230f);
			ogFont.draw(batch, "Online Game", 100f, 170f);
			exitFont.draw(batch, "Exit", 100f, 110f);
		}
		homeLogic();
		if (interfaceType != OthelloConstants.InterfaceType.HOME) {
			homeStage.act(Gdx.graphics.getDeltaTime());
			homeStage.draw();
		}
		batch.end();
	}

	private boolean dialog0Existed = false;
	private boolean dialog1Existed = false;
	private boolean dialog2Existed = false;
	private boolean dialog0Removed = false;
	private boolean roundUpdated = false;
	private Dialog dialog0, dialog1, dialog2;

	// ??????????????????
	public void renderGame(){
		// ???????????? ID
		if (onlineRemotePlayerName == null) {
			boolean nameUpdated = false;
			if (game.getMode() == OthelloConstants.GameMode.ONLINE_LOCAL_PLAYER) {
				onlineRemotePlayerName = server.getRemoteName();
				server.sendPlayerName(onlinePlayerName);
				server.receive();
				game.getPlayer2().setPlayerName(onlineRemotePlayerName);
				nameUpdated = true;
			}
			if (game.getMode() == OthelloConstants.GameMode.ONLINE_REMOTE_PLAYER) {
				onlineRemotePlayerName = client.getRemoteName();
				client.sendPlayerName(onlinePlayerName);
				game.getPlayer1().setPlayerName(onlineRemotePlayerName);
				nameUpdated = true;
			}
			if (nameUpdated)
				initHUD();
		}

		// ??????????????????
		if (game.getMode() == OthelloConstants.GameMode.ONLINE_REMOTE_PLAYER && !roundUpdated) {
			if (client.getMaximumRound() != 0) {
				roundUpdated = true;
				game.setMaximumPlay(client.getMaximumRound());
				initHUD();
			}
		}

		// ??????????????????
		if (remotePlayerDisconnected && !dialog2Existed) {
			dialog2Existed = true;
			dialog2 = new Dialog("\nDisconnected", skin);
			dialog2.setMovable(false);
			dialog2.setSize(200, 140);
			dialog2.text(new Label("Remote Disconnected", skin)).pad(10, 10, 10, 10);
			dialog2.button("Back").pad(10, 10, 10, 10);
			dialog2.getButtonTable().addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					clearBoard();
					backToHome();
					dialog2Existed = false;
					dialog2.remove();
				}
			});
			dialog2.setPosition(540, 360);
			gameStage.addActor(dialog2);
		}

		// ??????????????????
		if (game.getNowPlay().isOver()) {
			// round over
			if (!dialog0Existed) {
				dialog0Existed = true;
				dialog0Removed = false;
				dialog0 = new Dialog("\nRound Over", skin);
				dialog0.setMovable(false);
				dialog0.setSize(200, 140);
				dialog0.button("OK").pad(10, 10, 10, 10);
				dialog0.getButtonTable().addListener(new ChangeListener() {
					@Override
					public void changed(ChangeEvent event, Actor actor) {
						if (!game.isOver())
							dialog0Existed = false;
						dialog0Removed = true;
						game.switchToNewGame();
						clearBoard();
						dialog0.remove();
					}
				});
				dialog0.setPosition(540, 360);
				if (game.getNowPlay().getWinner() == game.getPlayer1().getColor())
					dialog0.text(new Label(String.format("%s wins!", game.getPlayer1().getPlayerName()), skin)).pad(10, 10, 10, 10);
				else if (game.getNowPlay().getWinner() == game.getPlayer2().getColor()){
					dialog0.text(new Label(String.format("%s wins!", game.getPlayer2().getPlayerName()), skin)).pad(10, 10, 10, 10);
				}
				else {
					dialog0.text(new Label("Draw!", skin)).pad(10, 10, 10, 10);
				}
				gameStage.addActor(dialog0);
			}
			// game over
			// local
			if (game.isOver() && dialog0Existed && dialog0Removed) {
				if (!dialog1Existed) {
					dialog1Existed = true;
					dialog1 = new Dialog("\nGame Over", skin);
					dialog1.setMovable(false);
					dialog1.setSize(200, 140);
					dialog1.button("OK").pad(10, 10, 10, 10);
					dialog1.getButtonTable().addListener(new ChangeListener() {
						@Override
						public void changed(ChangeEvent event, Actor actor) {
						dialog1Existed = false;
						gameStage.dispose();
						clearBoard();
						dialog1.remove();
						backToHome();
						}
					});
					dialog1.setPosition(540, 360);
					if (game.getWinner() != null)
						dialog1.text(new Label(String.format("%s wins!", game.getWinner().getPlayerName()), skin)).pad(10, 10, 10, 10);
					else
						dialog1.text(new Label("Draw!", skin)).pad(10, 10, 10, 10);
					gameStage.addActor(dialog1);
				}
			}
		}

		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		// ?????? 3D ??????
		loadBoard();
		for (int i = 1; i <= 8; i++) {
			for (int j = 1; j <= 8; j++) {
				if (board[i][j] != newBoard[i][j]) {
					if (board[i][j] == BLANK) {
						// ???????????????????????????????????????????????????
						ModelInstance newDiscInstance = discInstanceList.get(discList.getDiscListSize());
						AnimationController newController = discAnimationControllerList.get(discList.getDiscListSize());
						discList.addDisc(new Disc(i, j, newBoard[i][j], newDiscInstance, newController));
						renderInstanceList.add(newDiscInstance);
					} else if (board[i][j] != BLANK && newBoard[i][j] != BLANK) {
						// ????????????
						Disc disc = discList.getDiscAtPosition(i, j);
						disc.rotate();
					} else
						clearBoard();
				}
				board[i][j] = newBoard[i][j];
			}
		}

		ArrayList<Disc> discArrayList = discList.getDiscList();
		for (Disc disc : discArrayList) {
			disc.animationController.update(FPS);
		}

		pointerInstanceList = new ArrayList<>();
		ArrayList<Position> validPositions = game.getNowPlay().getValidPosition();
		for (Position p : validPositions) {
			Pointer pointer = new Pointer(p.getX(), p.getY(), new ModelInstance(pointerModel));
			pointerInstanceList.add(pointer.getModelInstance());
		}

		// ??????????????????
		if (game.getNowPlayer().getColor() != lastTurnColor && animationIsOver() && !game.isOver()) {
			if (game.getNowPlayer() == game.getPlayer1()) {
				player1NameLabel.setStyle(boldLabelStyle);
				player2NameLabel.setStyle(labelStyle);
			}
			else {
				player1NameLabel.setStyle(labelStyle);
				player2NameLabel.setStyle(boldLabelStyle);
			}
			lastTurnColor = game.getNowPlayer().getColor();
		}

		ArrayList<ModelInstance> renderList = new ArrayList<>();
		if (pointerInstanceList != null && animationIsOver() && game.getNowPlayer().getClass() != AIPlayer.class && !game.isCheat())
			renderList.addAll(pointerInstanceList);
		renderList.addAll(renderInstanceList);
		modelBatch.begin(cam);
		modelBatch.render(renderList, environment);
		modelBatch.end();

		gameRoundLabel.setText(String.format("Round %d/%d", game.getRoundCount(), game.getMaximumPlay()));
		player1WinCountLabel.setText(String.format("Wins: %d", game.getPlayer1Score()));
		player2WinCountLabel.setText(String.format("Wins: %d", game.getPlayer2Score()));
		gameStage.draw();

		// ??????????????????
		gameLogic();
	}

	// ???????????????
	public void homeLogic() {

		final TextField inputServerIP = new TextField("", skin);
		final TextButton connectToServerButton = new TextButton("Connect", skin);

		if (menuButtonPressed) {
			if (menuButtonType != OthelloConstants.MenuButtonType.EXIT) {
				homeStage = new Stage(new ScreenViewport());
				Gdx.input.setInputProcessor(homeStage);
				homeTable = new Table();
				homeTable.setSize(1280, 720);
				homeStage.addActor(homeTable);
			}

			Label titleLabel = null;
			final Label blankLabel = new Label("", labelStyle);
			final Label player1Label = new Label("Player 1", labelStyle);
			Label player2Label = new Label("Player 2", labelStyle);
			Label difficultyLabel = new Label("Difficulty", labelStyle);
			final Label gameRoundLabel = new Label("Rounds", labelStyle);
			TextButton startButton = new TextButton("Start", skin);
			final TextButton backButton = new TextButton("Back", skin);
			TextButton loadButton = new TextButton("Load", skin);
			final TextField player1TextField = new TextField("Player1", skin);
			final TextField player2TextField = new TextField("Player2", skin);
			final SelectBox<String> difficultySelectBox;
			difficultySelectBox = new SelectBox(skin);
			final SelectBox<String> gameRoundSelectBox;
			gameRoundSelectBox = new SelectBox(skin);
			final SelectBox<String> bgmSelectBox;
			bgmSelectBox = new SelectBox(skin);

			difficultySelectBox.setItems("Easy", "Normal", "Hard");
			gameRoundSelectBox.setItems("1", "3", "5");
			bgmSelectBox.setItems();

			// ?????????????????? by kl
			final Label serverIPLabel = new Label("IP", labelStyle);
			TextButton createServerButton = new TextButton("Create", skin);
			TextButton joinServerButton = new TextButton("Join", skin);

			switch (menuButtonType) {
				case OthelloConstants.MenuButtonType.EXIT:
					Gdx.app.exit();
					break;
				case OthelloConstants.MenuButtonType.LOCAL_SINGLE_PLAYER:
					interfaceType = OthelloConstants.InterfaceType.LOCAL_SINGLE_PLAYER_MENU;
					titleLabel = new Label("Single Player", titleLabelStyle);
					player2TextField.setText("AI");
					player2TextField.setDisabled(true);
					break;
				case OthelloConstants.MenuButtonType.LOCAL_MULTIPLE_PLAYER:
					interfaceType = OthelloConstants.InterfaceType.LOCAL_MULTIPLE_PLAYER_MENU;
					titleLabel = new Label("Multiple Player", titleLabelStyle);
					difficultySelectBox.setDisabled(true);
					difficultySelectBox.setItems("N/A");
					break;
				case OthelloConstants.MenuButtonType.ONLINE_MULTIPLE_PLAYER:
					interfaceType = OthelloConstants.InterfaceType.ONLINE_MULTIPLE_PLAYER_MENU;
					titleLabel = new Label("Online Game", titleLabelStyle);
					player1Label.setText("Name");
					break;
				default: break;
			}

			menuButtonPressed = false;

			backButton.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					backToHome();
				}
			});
			loadButton.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
				game = OthelloGame.loadGame(menuButtonType);
				if(game == null) {
					final Dialog dialog = new Dialog("Loading failed", skin);
					dialog.setMovable(false);
					dialog.setSize(200, 140);
					dialog.text(new Label("No corresponding save data or data was broken.", skin)).pad(10, 10, 10, 10);
					dialog.button("OK").pad(10, 10, 10, 10);
					dialog.getButtonTable().addListener(new ChangeListener() {
						@Override
						public void changed(ChangeEvent event, Actor actor) {
							dialog.remove();
						}
					});
					dialog.setPosition(540, 360);
					homeStage.addActor(dialog);
				} else {
					interfaceType = OthelloConstants.InterfaceType.GAME;
					bgmId = bgm.loop(0.01f);
					initHUD();
				}
				}
			});

			switch (interfaceType) {
				case OthelloConstants.InterfaceType.LOCAL_SINGLE_PLAYER_MENU:
					startButton.addListener(new ChangeListener() {
						@Override
						public void changed(ChangeEvent event, Actor actor) {
								Player p1 = new LocalPlayer(1, player1TextField.getText(),
										"data/skin/profile_photo.jpg", BLACK);
								int difficultyNum;
								switch (difficultySelectBox.getSelected()) {
									case "Easy":
										difficultyNum = EASY;
										break;
									case "Normal":
										difficultyNum = NORMAL;
										break;
									case "Hard":
										difficultyNum = HARD;
										break;
									default:
										difficultyNum = 0;
										break;
								}
								Player p2 = new AIPlayer(difficultyNum, WHITE);
								game = new OthelloGame(p1, p2, new LocalOthelloCore());
								game.setMode(OthelloConstants.GameMode.LOCAL_SINGLE_PLAYER);
								game.setMaximumPlay(Integer.parseInt(gameRoundSelectBox.getSelected()));
								bgmId = bgm.loop(0.01f);
								interfaceType = OthelloConstants.InterfaceType.GAME;
								initHUD();
						}
					});
					break;
				case OthelloConstants.InterfaceType.LOCAL_MULTIPLE_PLAYER_MENU:
					startButton.addListener(new ChangeListener() {
						@Override
						public void changed(ChangeEvent event, Actor actor) {
							Player p1 = new LocalPlayer(1, player1TextField.getText(),
									"data/skin/profile_photo.jpg", BLACK);
							Player p2 = new LocalPlayer(2, player2TextField.getText(),
									"data/skin/profile_photo.jpg", WHITE);
							game = new OthelloGame(p1, p2, new LocalOthelloCore());
							game.setMode(OthelloConstants.GameMode.LOCAL_MULTIPLE_PLAYER);
							game.setMaximumPlay(Integer.parseInt(gameRoundSelectBox.getSelected()));
							interfaceType = OthelloConstants.InterfaceType.GAME;
							bgmId = bgm.loop(0.01f);
							initHUD();
						}
					});
					break;
				case OthelloConstants.InterfaceType.ONLINE_MULTIPLE_PLAYER_MENU:
					createServerButton.addListener(new ChangeListener() {
						@Override
						public void changed(ChangeEvent event, Actor actor) {
							server = new OnlineOthelloServer();
							Player p1 = new LocalPlayer(10, player1TextField.getText(),
									"data/skin/profile_photo.jpg", BLACK);
							Player p2 = new OnlinePlayer(20, "Remote",
									"data/skin/profile_photo.jpg", WHITE);
							game = new OthelloGame(p1, p2, new LocalOthelloCore());
							game.setMode(OthelloConstants.GameMode.ONLINE_LOCAL_PLAYER);
							game.setMaximumPlay(Integer.parseInt(gameRoundSelectBox.getSelected()));
							serverIP = new Label("Server IP: " + server.getIP() + ":" + server.getPort() +
									"(Automatically in your clipboard.)", skin);
							Toolkit.getDefaultToolkit().getSystemClipboard().
									setContents(
											new StringSelection(server.getIP() + ":" + server.getPort()), null
									);
							homeTable.row();
							homeTable.add(blankLabel).width(100).height(80);
							homeTable.add(new Label("Waiting...", skin)).width(100);
							homeTable.add(blankLabel).width(100);
							homeTable.row();
							homeTable.add(blankLabel).width(100);
							homeTable.add(new Label("Your IP: " + server.getIP() + ":" + server.getPort(), skin)).width(100);
							homeTable.add(blankLabel).width(100);
							onlinePlayerName = player1TextField.getText();
							interfaceType = OthelloConstants.InterfaceType.ONLINE_LOCAL_PLAYER_WAITING;
						}
					});
					final Label finalTitleLabel = titleLabel;
					joinServerButton.addListener(new ChangeListener() {
						@Override
						public void changed(ChangeEvent event, Actor actor) {
							Player p1 = new OnlinePlayer(40, "Remote(s)",
									"data/skin/profile_photo.jpg", BLACK);
							Player p2 = new LocalPlayer(30, player1TextField.getText(),
									"data/skin/profile_photo.jpg", WHITE);
							game = new OthelloGame(p1, p2, new LocalOthelloCore());
							game.setMode(OthelloConstants.GameMode.ONLINE_REMOTE_PLAYER);
							game.setMaximumPlay(1);
							interfaceType = OthelloConstants.InterfaceType.ONLINE_REMOTE_PLAYER_BEFORE_CONNECTING;

							homeStage = new Stage(new ScreenViewport());
							Gdx.input.setInputProcessor(homeStage);
							homeTable = new Table();
							homeTable.setSize(1280, 720);
							homeStage.addActor(homeTable);
							homeTable.setBackground(skin.newDrawable("white", new Color(0x54BCB5ff)));
							assert finalTitleLabel != null;
							finalTitleLabel.setText("Join");
							homeTable.add(blankLabel).width(100).height(80);
							homeTable.add(finalTitleLabel);
							homeTable.add(blankLabel).width(100).height(80);
							homeTable.row();
							player1Label.setText("Name");
							homeTable.add(player1Label).height(80).width(100).align(Align.left);
							homeTable.add(blankLabel).width(100);
							homeTable.add(player1TextField);
							homeTable.row();
							homeTable.add(serverIPLabel).width(100).height(80);
							homeTable.add(blankLabel).width(120).height(80);
							homeTable.add(inputServerIP).width(170).align(Align.left);
							homeTable.row();
							homeTable.add(connectToServerButton).width(100);
							homeTable.add(blankLabel).width(100).height(80);
							homeTable.add(backButton).width(100);
						}
					});
					connectToServerButton.addListener(new ChangeListener() {
						@Override
						public void changed(ChangeEvent event, Actor actor) {
							String[] splitIP = inputServerIP.getText().split(":");
							String IP = splitIP[0];
							int port = Integer.parseInt(splitIP[1]);
							client = new OnlineOthelloClient(IP, port);
							interfaceType = OthelloConstants.InterfaceType.ONLINE_REMOTE_PLAYER_WAITING;
							onlinePlayerName = player1TextField.getText();
							game.getPlayer2().setPlayerName(onlinePlayerName);
						}
					});
					break;
				default:
					break;
			}

			// ???????????????????????????
			homeTable.setBackground(skin.newDrawable("white", new Color(0x54BCB5ff)));
			if (menuButtonType != OthelloConstants.MenuButtonType.ONLINE_MULTIPLE_PLAYER) {
				homeTable.add(blankLabel).width(120);
				homeTable.add(titleLabel).height(100);
				homeTable.add(blankLabel).width(100);
				homeTable.row();
				homeTable.add(player1Label).height(80).width(100);
				homeTable.add(blankLabel).width(100);
				homeTable.add(player1TextField);
				homeTable.row();
				homeTable.add(player2Label).height(80).width(100);
				homeTable.add(blankLabel).width(100);
				homeTable.add(player2TextField);
				homeTable.row();
				homeTable.add(gameRoundLabel).width(100).height(80);
				homeTable.add(blankLabel).width(100);
				homeTable.add(gameRoundSelectBox);
				homeTable.row();
				if (menuButtonType == OthelloConstants.GameMode.LOCAL_SINGLE_PLAYER) {
					homeTable.add(difficultyLabel).width(100).height(80);
					homeTable.add(blankLabel).width(100);
					homeTable.add(difficultySelectBox);
					homeTable.row();
				}
				homeTable.add(startButton).width(100);
				homeTable.add(loadButton).width(100);
				homeTable.add(backButton).width(100);
			}
			// ???????????????????????????
			else if(interfaceType == OthelloConstants.InterfaceType.ONLINE_MULTIPLE_PLAYER_MENU) {
				player1TextField.setText("Player");
				homeTable.add(blankLabel).width(120);
				homeTable.add(titleLabel).height(100);
				homeTable.add(blankLabel).width(100);
				homeTable.row();
				homeTable.add(player1Label).height(80).width(100);
				homeTable.add(blankLabel).width(100);
				homeTable.add(player1TextField);
				homeTable.row();
				homeTable.add(gameRoundLabel).width(100).height(80);
				homeTable.add(blankLabel).width(100);
				homeTable.add(gameRoundSelectBox);
				homeTable.row();
				homeTable.add(createServerButton).width(100).height(30);
				homeTable.add(joinServerButton).width(100).height(30);
				homeTable.add(backButton).width(100).height(30);
			}
		}
		if(interfaceType == OthelloConstants.InterfaceType.ONLINE_LOCAL_PLAYER_WAITING) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					server.connectWithClient();
				}
			}).start();
			if(server.isConnected()) {
				remotePlayerDisconnected = false;
				interfaceType = OthelloConstants.InterfaceType.GAME;
				bgmId = bgm.loop(0.01f);
				initHUD();
				server.sendPlayerName(onlinePlayerName);
				server.sendMaximumRound(game.getMaximumPlay());
			}
		}
		if(interfaceType == OthelloConstants.InterfaceType.ONLINE_REMOTE_PLAYER_WAITING) {
			if(client.isConnected()) {
				remotePlayerDisconnected = false;
				client.sendPlayerName(onlinePlayerName);
				interfaceType = OthelloConstants.InterfaceType.GAME;
				bgmId = bgm.loop(0.01f);
				initHUD();
			} else {
				new Thread(new Runnable() {
					@Override
					public void run() {
						client.connectWithServer();
					}
				}).start();
			}
		}
	}

	public void initHUD() {
		if (gameStage != null)
			gameStage.dispose();
		dialog0Removed = false;

		gameStage = new Stage();
		gameButtonTable = new Table();
		playerTable = new Table();
		TextButton homeButton = new TextButton("Home", skin);
		TextButton saveButton = new TextButton("Save", skin);
		final TextButton muteButton = new TextButton("Mute", skin);
		final TextButton backButton = new TextButton("Back", skin);
		final CheckBox cheatCheckBox = new CheckBox("Cheat", skin);
		Image p1ProfilePhoto = new Image(defaultBlackPlayerProfilePhoto);
		Image p2ProfilePhoto = new Image(defaultWhitePlayerProfilePhoto);

		gameButtonTable.align(Align.left);
		gameButtonTable.add(homeButton);
		if (game.getMode() < OthelloConstants.GameMode.ONLINE_LOCAL_PLAYER) {
			gameButtonTable.add(saveButton).padLeft(10);
			gameButtonTable.add(backButton).padLeft(10);
		}
		gameButtonTable.add(muteButton).padLeft(10);
		if (game.getMode() < OthelloConstants.GameMode.ONLINE_LOCAL_PLAYER)
			gameButtonTable.add(cheatCheckBox).padLeft(10);
		gameButtonTable.setPosition(20, 50);

		homeButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				backToHome();
				clearBoard();
			}
		});

		saveButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				final Dialog dialog = new Dialog("\nSaved", skin);
				dialog.setMovable(false);
				dialog.setSize(200, 140);
				dialog.text(new Label("Game has been saved", skin)).pad(10, 10, 10, 10);
				dialog.button("OK").pad(10, 10, 10, 10);
				dialog.getButtonTable().addListener(new ChangeListener() {
					@Override
					public void changed(ChangeEvent event, Actor actor) {
						dialog.remove();
					}
				});
				dialog.setPosition(540, 360);
				if(!game.save(menuButtonType)) {
					dialog.text(new Label("Error. Failed to save.", skin)).pad(10, 10, 10, 10);
				}
				gameStage.addActor(dialog);
			}
		});

		muteButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (isMuted) {
					bgmId = bgm.loop(0.01f);
					isMuted = false;
					muteButton.setText("Mute");
				}
				else {
					bgm.stop(bgmId);
					isMuted = true;
					muteButton.setText("Unmute");
				}
			}
		});

		backButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if(!game.back()) {
					final Dialog dialog = new Dialog("\nBack Failed", skin);
					dialog.setMovable(false);
					dialog.setSize(200, 140);
					dialog.text(new Label("You have backed once.", skin)).pad(10, 10, 10, 10);
					dialog.button("OK").pad(10, 10, 10, 10);
					dialog.getButtonTable().addListener(new ChangeListener() {
						@Override
						public void changed(ChangeEvent event, Actor actor) {
							dialog.remove();
						}
					});
					dialog.setPosition(540, 360);
					gameStage.addActor(dialog);
				}
			}
		});

		cheatCheckBox.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (game.isCheat())
					game.setCheat(false);
				else game.setCheat(true);
			}
		});

		player1NameLabel = new Label(game.getPlayer1().getPlayerName(), labelStyle);
		player2NameLabel = new Label(game.getPlayer2().getPlayerName(), labelStyle);
		player1WinCountLabel = new Label("", labelStyle);
		player2WinCountLabel = new Label("", labelStyle);

		playerTable.add(p1ProfilePhoto).size(80, 80);
		playerTable.add(player1NameLabel).padLeft(10).center();
		playerTable.row();
		playerTable.add(player1WinCountLabel);
		playerTable.row();
		playerTable.add(p2ProfilePhoto).size(80, 80).padTop(50);
		playerTable.add(player2NameLabel).padLeft(10).padTop(50).center();
		playerTable.row();
		playerTable.add(player2WinCountLabel);

		playerTable.setPosition(20, 530);
		playerTable.align(Align.left);

		gameRoundLabel = new Label(String.format("Round %d/%d", 1, game.getMaximumPlay()), titleLabelStyle);
		gameRoundLabel.setPosition(1100, 640);

		gameStage.addActor(gameRoundLabel);
		gameStage.addActor(playerTable);
		gameStage.addActor(gameButtonTable);
		Gdx.input.setInputProcessor(new InputMultiplexer(gameStage, new GameInputProcessor()));
	}

	// ????????????
	public void gameLogic() {
		if (game.isOver()) return;

		// ????????????????????????
		if (!animationIsOver()) {
			boardClicked = false;
			return;
		}

		// ???????????????
		if(game.getNowPlayer().getID() == -1) { // AI
			if (aiIsThinking)
				return;
			aiIsThinking = true;
			new Thread(new Runnable() {
				@Override
				public void run() {
					game.getNowPlayer().addStep();
					if (!isMuted)
						chessSound1.play(0.1f);
					aiIsThinking = false;
				}
			}).start();
		} else if(game.getNowPlayer().getID() == 20) { // Online Client
			Position position = server.receive();
			if(position != null) game.getNowPlay().addStep(new Step(position, WHITE));
		} else if(game.getNowPlayer().getID() == 40) { // Online Server
			Position position = client.receive();
			if(position != null) game.getNowPlay().addStep(new Step(position, BLACK));
		} else if(boardClicked) {
			boardClicked = false;
			Step thisStep = new Step(boardClickPosition, game.getNowPlay().getTurnColor());
			if (!isMuted && game.getNowPlay().isValidPosition(boardClickPosition, game.getNowPlay().getTurnColor()))
				chessSound1.play(0.1f);
			if(game.getNowPlayer().getID() == 10)
				server.update(thisStep);
			if(game.getNowPlayer().getID() == 30)
				client.update(thisStep);
			game.getNowPlayer().addStep(thisStep);
		}
	}

	@Override
	public void create () {
		interfaceType = OthelloConstants.InterfaceType.HOME;
		batch = new SpriteBatch();

		homeLoading = new Texture(Gdx.files.internal("menu/home_blank.png"));
		batch.begin();
		batch.draw(homeLoading, 0, 0);
		batch.end();

		/* --- 3D ????????????????????? --- */

		// ???????????????
		cam = new PerspectiveCamera(60, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.position.set(3f, 8f, -3.5f);
		cam.lookAt(3f, 0f, -4f);
		cam.near = 1f;
		cam.far = 300f;
		cam.update();

		// ???????????????
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 0f));
		environment.add(new PointLight().set(0.8f, 0.8f, 0.8f, 3f, 10f, -5f, 1000));

		// ????????????????????????????????????
		UBJsonReader jsonReader = new UBJsonReader();
		G3dModelLoader loader = new G3dModelLoader(jsonReader);
		ObjLoader objLoader = new ObjLoader();
		frameModel = objLoader.loadModel(Gdx.files.internal("models/frame.obj"));
		boardModel = objLoader.loadModel(Gdx.files.internal("models/board.obj"));
		discModel = loader.loadModel(Gdx.files.internal("models/disc_animate.g3db"));
		tableModel = objLoader.loadModel(Gdx.files.internal("models/wooden_table.obj"));
		pointerModel = objLoader.loadModel(Gdx.files.internal("models/pointer.obj"));

		frameInstance = new ModelInstance(frameModel);
		frameInstance.transform = new Matrix4().setToTranslation(0, -0.1f, 0);
		frameInstance.calculateTransforms();
		boardInstance = new ModelInstance(boardModel);
		tableInstance = new ModelInstance(tableModel);
		tableInstance.transform = new Matrix4().setToTranslation(0, -0.2f, 0);
		tableInstance.calculateTransforms();
		discList = new DiscList();
		discInstanceList = new ArrayList<>();
		discAnimationControllerList = new ArrayList<>();
		for (int i = 1; i <= 64; i++) {
			ModelInstance discInstance = new ModelInstance(discModel);
			discInstanceList.add(discInstance);
			discAnimationControllerList.add(new AnimationController(discInstance));
		}
		modelBatch = new ModelBatch();

		// ????????????????????????????????????
		renderInstanceList = new ArrayList<>();
		renderInstanceList.add(tableInstance);
		renderInstanceList.add(frameInstance);
		renderInstanceList.add(boardInstance);

		// ?????????????????????
		board = new int[10][10];
		newBoard = new int[10][10];
		for (int i = 0; i <= 9; i++) {
			for (int j = 0; j <= 9; j++) {
				board[i][j] = OthelloConstants.DiscType.BLANK;
				newBoard[i][j] = OthelloConstants.DiscType.BLANK;
			}
		}

		/* --- 3D ????????????????????? --- */

		/* --- ????????? UI ??????????????? --- */
		homeDefault = new Texture(Gdx.files.internal("menu/home_blank.png"));
		generator = new FreeTypeFontGenerator(Gdx.files.internal("font/BRLNSR.TTF"));
		generatorBold = new FreeTypeFontGenerator(Gdx.files.internal("font/BRLNSDB.TTF"));
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();

		// ?????? BitmapFont
		parameter.size = 96;
		parameter.borderColor = Color.BLACK;
		parameter.borderWidth = 4;
		titleFont = generator.generateFont(parameter);

		parameter.size = 36;
		parameter.borderWidth = 0;
		buttonFont = generator.generateFont(parameter);
		parameter.borderWidth = 2;
		buttonFontBold = generatorBold.generateFont(parameter);

		parameter.size = 24;
		parameter.borderWidth = 0;
		font = generator.generateFont(parameter);
		parameter.borderWidth = 2;
		boldFont = generatorBold.generateFont(parameter);

		// ?????? UI ??????
		defaultBlackPlayerProfilePhoto = new Texture(Gdx.files.internal("profile_photo/black_default.png"));
		defaultWhitePlayerProfilePhoto = new Texture(Gdx.files.internal("profile_photo/white_default.png"));

		skin = new Skin(Gdx.files.internal("data/skin/skin-composer-ui.json"));
		labelStyle = new Label.LabelStyle();
		boldLabelStyle = new Label.LabelStyle();
		titleLabelStyle = new Label.LabelStyle();
		buttonStyle = new TextButton.TextButtonStyle();
		textFieldStyle = new TextField.TextFieldStyle();
		selectBoxStyle = new SelectBox.SelectBoxStyle();
		labelStyle.font = font;
		boldLabelStyle.font = boldFont;
		titleLabelStyle.font = buttonFont;
		buttonStyle.font = buttonFont;
		textFieldStyle.font = font;
		selectBoxStyle.font = font;

		Gdx.input.setInputProcessor(new HomeInputProcessor());
		/* --- ????????? UI ??????????????? --- */

		/* --- ?????????????????? --- */
		chessSound1 = Gdx.audio.newSound(Gdx.files.internal("sound/chess_sound1.mp3"));
		chessSound2 = Gdx.audio.newSound(Gdx.files.internal("sound/chess_sound2.mp3"));
		bgm = Gdx.audio.newSound(Gdx.files.internal("sound/bgm.mp3"));
	}

	@Override
	public void render () {
		if (interfaceType == OthelloConstants.InterfaceType.GAME) {
			renderGame();
		} else {
			renderHome();
		}
	}

	@Override
	public void dispose () {
		modelBatch.dispose();
		boardModel.dispose();
	}
}
