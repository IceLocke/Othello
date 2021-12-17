package com.othello.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.*;
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
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.UBJsonReader;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.othello.game.core.LocalOthelloCore;
import com.othello.game.core.OthelloGame;
import com.othello.game.player.AIPlayer;
import com.othello.game.player.LocalPlayer;
import com.othello.game.player.Player;
import com.othello.game.processor.GameInputProcessor;
import com.othello.game.processor.HomeInputProcessor;
import com.othello.game.utils.*;

import java.util.ArrayList;

import static com.othello.game.utils.OthelloConstants.AIDifficulty.*;
import static com.othello.game.utils.OthelloConstants.DiscType.*;

public class Othello extends ApplicationAdapter {

	// 3D 部分的变量
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

	// 字体部分的变量
	public FreeTypeFontGenerator generator;
	public FreeTypeFontGenerator generatorBold;
	public BitmapFont font;
	public BitmapFont boldFont;
	public BitmapFont titleFont;
	public BitmapFont buttonFont;
	public BitmapFont buttonFontBold;

	// 音效部分的变量
	public static Sound chessSound1;
	public static Sound chessSound2;
	public static Sound bgm;
	public static long bgmId;
	public static boolean isMuted;

	// 游戏状态的变量
	public static int interfaceType = OthelloConstants.InterfaceType.HOME;
	public static int menuButtonType = OthelloConstants.MenuButtonType.NONE;
	public static int lastTurnColor = OthelloConstants.DiscType.BLANK;
	public static boolean menuButtonPressed = false;
	public static boolean boardClicked = false;
	public static boolean aiIsThinking = false;
	public static Position boardClickPosition;
	public static OthelloGame game;
	public static final float FPS = 1f / 60;

	// BGM 列表
	protected String[] bgmList = {"Veibae_BGM", ""};

	// 游戏UI相关的变量
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

	public boolean animationIsOver() {
		boolean over = true;
		for (Disc disc : discList.getDiscList())
			over = over && disc.animationIsOver;
		return over;
	}

	public void backToHome() {
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

	// 渲染主菜单
	public void renderHome() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		if (interfaceType == OthelloConstants.InterfaceType.HOME) {
			// 绘制主菜单背景和标题
			batch.draw(homeDefault, 0, 0);
			titleFont.draw(batch, "Othello!", 480f, 540f);
			// 绘制按钮
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
	private Dialog dialog0, dialog1;
	// 渲染游戏界面
	public void renderGame(){
		// 游戏结束动画
		if (game.getNowPlay().isOver()) {
			// round over
			if(!dialog0Existed && !game.isOver()) {
				if(!dialog0Existed) {
					dialog0Existed = true;
					dialog0 = new Dialog("\nRound Over", skin);
					dialog0.setMovable(false);
					dialog0.setSize(200, 140);
					dialog0.button("OK").pad(10, 10, 10, 10);
					dialog0.getButtonTable().addListener(new ChangeListener() {
						@Override
						public void changed(ChangeEvent event, Actor actor) {
							dialog0Existed = false;
							game.switchToNewGame();
							clearBoard();
							dialog0.remove();
						}
					});
					dialog0.setPosition(540, 360);
					if (game.getNowPlay().getWinner() == game.getPlayer1().getColor())
						dialog0.text(new Label(String.format("%s wins!", game.getPlayer1().getPlayerName()), skin)).pad(10, 10, 10, 10);
					else
						dialog0.text(new Label(String.format("%s wins!", game.getPlayer2().getPlayerName()), skin)).pad(10, 10, 10, 10);
					gameStage.addActor(dialog0);
				}
			}
			// game over
			// local
			else if (game.isOver()) {
				if (game.getMode() != OthelloConstants.GameMode.ONLINE_MULTIPLE_PLAYER) {
					if(!dialog1Existed) {
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
						gameStage.addActor(dialog1);
						if (game.getWinner() != null)
							dialog1.text(new Label(String.format("%s wins!", game.getWinner().getPlayerName()), skin)).pad(10, 10, 10, 10);
						else
							dialog1.text(new Label("Draw!", skin)).pad(10, 10, 10, 10);
					}
				}
			}
		}

		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		// 棋盘 3D 部分
		loadBoard();
		for (int i = 1; i <= 8; i++) {
			for (int j = 1; j <= 8; j++) {
				if (board[i][j] != newBoard[i][j]) {
					if (board[i][j] == BLANK) {
						// 是新的棋子，将新的棋子加入渲染队列
						ModelInstance newDiscInstance = discInstanceList.get(discList.getDiscListSize());
						AnimationController newController = discAnimationControllerList.get(discList.getDiscListSize());
						discList.addDisc(new Disc(i, j, newBoard[i][j], newDiscInstance, newController));
						renderInstanceList.add(newDiscInstance);
					} else if (board[i][j] != BLANK && newBoard[i][j] != BLANK) {
						// 翻转棋子
						Disc disc = discList.getDiscAtPosition(i, j);
						disc.rotate();
					} else {
						System.out.println("backed");
						clearBoard();
					}
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

		// 切换玩家动画
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
		if (pointerInstanceList != null && animationIsOver() && game.getNowPlayer().getClass() != AIPlayer.class)
			renderList.addAll(pointerInstanceList);
		renderList.addAll(renderInstanceList);
		modelBatch.begin(cam);
		modelBatch.render(renderList, environment);
		modelBatch.end();

		gameRoundLabel.setText(String.format("Round %d/%d", game.getPlayer1Score() + game.getPlayer2Score() + 1, game.getMaximumPlay()));
		player1WinCountLabel.setText(String.format("Wins: %d", game.getPlayer1Score()));
		player2WinCountLabel.setText(String.format("Wins: %d", game.getPlayer2Score()));
		gameStage.draw();

		// 最后处理操作
		localGameLogic();
	}

	// 主菜单逻辑
	public void homeLogic() {
		if (menuButtonPressed) {
			if (menuButtonType != OthelloConstants.MenuButtonType.EXIT) {
				homeStage = new Stage(new ScreenViewport());
				Gdx.input.setInputProcessor(homeStage);
				homeTable = new Table();
				homeTable.setSize(1280, 720);
				homeStage.addActor(homeTable);
			}

			Label titleLabel = null;
			Label blankLabel = new Label("", labelStyle);
			Label player1Label = new Label("Player 1", labelStyle);
			Label player2Label = new Label("Player 2", labelStyle);
			Label difficultyLabel = new Label("Difficulty", labelStyle);
			final Label gameRoundLabel = new Label("Rounds", labelStyle);
			final Label bgmLabel = new Label("BGM", labelStyle);
			Label serverAddressLabel = null;
			TextButton startButton = new TextButton("Start", skin);
			final TextButton backButton = new TextButton("Back", skin);
			TextButton loadButton = new TextButton("Load", skin);
			final TextField player1TextField = new TextField("player1", skin);
			final TextField player2TextField = new TextField("player2", skin);
			TextField serverAddressTextField = null;
			final SelectBox<String> difficultySelectBox = new SelectBox(skin);
			final SelectBox<String> gameRoundSelectBox = new SelectBox(skin);
			final SelectBox<String> bgmSelectBox = new SelectBox(skin);

			difficultySelectBox.setItems("Easy", "Normal", "Hard");
			gameRoundSelectBox.setItems("1", "3", "5");
			bgmSelectBox.setItems();

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
					titleLabel = new Label("Multiple Player", titleLabelStyle);
					serverAddressLabel = new Label("Server Address", labelStyle);
					serverAddressTextField = new TextField("0.0.0.0", textFieldStyle);
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
					} else {
						interfaceType = OthelloConstants.InterfaceType.GAME;
						bgmId = bgm.loop(0.01f);
						initHUD();
						Gdx.input.setInputProcessor(new InputMultiplexer(gameStage, new GameInputProcessor()));
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
									difficultyNum = 0; break;
							}
							Player p2 = new AIPlayer(difficultyNum, WHITE);
							game = new OthelloGame(p1, p2, new LocalOthelloCore());
							game.setMode(OthelloConstants.GameMode.LOCAL_SINGLE_PLAYER);
							game.setMaximumPlay(Integer.parseInt(gameRoundSelectBox.getSelected()));
							interfaceType = OthelloConstants.InterfaceType.GAME;
							bgmId = bgm.loop(0.01f);
							initHUD();
							Gdx.input.setInputProcessor(new InputMultiplexer(gameStage, new GameInputProcessor()));
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
							game.refresh();
							p1.setCore(game.getNowPlay());
							p2.setCore(game.getNowPlay());
							interfaceType = OthelloConstants.InterfaceType.GAME;
							bgmId = bgm.loop(0.01f);
							initHUD();
							Gdx.input.setInputProcessor(new InputMultiplexer(gameStage, new GameInputProcessor()));
						}
					});
					break;
				case OthelloConstants.InterfaceType.ONLINE_MULTIPLE_PLAYER_MENU:

					/*
					* @IceLocke
					*
					* 选择：创建服务器 or 链接进服务器
					*
					* if 创建服务器
					* 		创建服务器
					* 		本机玩家为本地玩家（黑棋）
					* 		等带对手链接
					* else
					* 		输入ip+端口号
					* 		尝试链接
					* */
					break;
				default:
					break;
			}

			// 本地游戏的绘制
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
				homeTable.add(difficultyLabel).width(100).height(80);
				homeTable.add(blankLabel).width(100);
				homeTable.add(difficultySelectBox);
				homeTable.row();
				homeTable.add(startButton).width(100);
				homeTable.add(loadButton).width(100);
				homeTable.add(backButton).width(100);
			}

			// 在线游戏的绘制
			else {

			}
		}
	}

	public void initHUD() {
		gameStage = new Stage();
		gameButtonTable = new Table();
		playerTable = new Table();
		TextButton homeButton = new TextButton("Home", skin);
		TextButton saveButton = new TextButton("Save", skin);
		final TextButton muteButton = new TextButton("Mute", skin);
		final TextButton backButton = new TextButton("Back", skin);
		Image p1ProfilePhoto = new Image(defaultBlackPlayerProfilePhoto);
		Image p2ProfilePhoto = new Image(defaultWhitePlayerProfilePhoto);

		gameButtonTable.add(homeButton);
		if (interfaceType != OthelloConstants.InterfaceType.ONLINE_MULTIPLE_PLAYER_MENU)
			gameButtonTable.add(saveButton).padLeft(10);
		gameButtonTable.add(backButton).padLeft(10);
		gameButtonTable.add(muteButton).padLeft(10);
		gameButtonTable.setPosition(120, 50);

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
					System.out.println("Failed to save...");
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
				if(game.back()) {

				} else {
					// dialog: failed to back
					// @IceLocke
				}
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

		playerTable.setPosition(110, 530);

		gameRoundLabel = new Label(String.format("Round %d/%d", 1, game.getMaximumPlay()), titleLabelStyle);
		gameRoundLabel.setPosition(1100, 640);

		gameStage.addActor(gameRoundLabel);
		gameStage.addActor(playerTable);
		gameStage.addActor(gameButtonTable);
	}

	// 本地对战逻辑
	public void localGameLogic() {
		if(game.isOver()) return;

		// 优先把动画处理完
		if (!animationIsOver()) {
			boardClicked = false;
			return;
		}

		// 下棋的逻辑
		if(game.getNowPlayer().getID() == -1) {
			if (aiIsThinking)
				return;
			aiIsThinking = true;
			Gdx.app.postRunnable(new Runnable() {
				@Override
				public void run() {
					game.getNowPlayer().addStep();
					if (!isMuted)
						chessSound1.play(0.1f);
					aiIsThinking = false;
				}
			});
			for (int i = 1; i <= 8; i++) {
				for (int j = 1; j <= 8; j++)
					System.out.printf("%d ", game.getNowPlayBoard()[i][j]);
				System.out.println();
			}
		} else if (boardClicked) {
			System.out.printf("Othello: detected click, at position: %d %d\n", boardClickPosition.getX(), boardClickPosition.getY());
			boardClicked = false;
			System.out.printf("Othello: addStep(%d, %d, %d)\n", boardClickPosition.getX(), boardClickPosition.getY(), game.getNowPlay().getTurnColor());
			game.getNowPlayer().addStep(
					new Step(boardClickPosition, game.getNowPlay().getTurnColor())
			);
			if (!isMuted)
				chessSound1.play(0.1f);
			for (int i = 1; i <= 8; i++) {
				for (int j = 1; j <= 8; j++)
					System.out.printf("%d ", game.getNowPlayBoard()[i][j]);
				System.out.println();
			}
		}
	}

	// 在线对战逻辑
	public void onlineGameLogic() {

	}

	@Override
	public void create () {
		interfaceType = OthelloConstants.InterfaceType.HOME;
		batch = new SpriteBatch();

		homeLoading = new Texture(Gdx.files.internal("menu/home_blank.png"));
		batch.begin();
		batch.draw(homeLoading, 0, 0);
		batch.end();

		System.out.println(Gdx.graphics.getDeltaTime());

		/* --- 3D 部分初始化开始 --- */

		// 初始化相机
		cam = new PerspectiveCamera(60, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.position.set(3f, 8f, -3.5f);
		cam.lookAt(3f, 0f, -4f);
		cam.near = 1f;
		cam.far = 300f;
		cam.update();

		// 初始化场景
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 0f));
		environment.add(new PointLight().set(0.8f, 0.8f, 0.8f, 3f, 10f, -5f, 1000));

		// 加载棋盘、棋子、桌子模型
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

		// 将桌子和棋盘加入渲染队列
		renderInstanceList = new ArrayList<>();
		renderInstanceList.add(tableInstance);
		renderInstanceList.add(frameInstance);
		renderInstanceList.add(boardInstance);

		// 初始化棋盘数据
		board = new int[10][10];
		newBoard = new int[10][10];
		for (int i = 0; i <= 9; i++) {
			for (int j = 0; j <= 9; j++) {
				board[i][j] = OthelloConstants.DiscType.BLANK;
				newBoard[i][j] = OthelloConstants.DiscType.BLANK;
			}
		}

		/* --- 3D 部分初始化结束 --- */

		/* --- 主菜单 UI 初始化开始 --- */
		homeDefault = new Texture(Gdx.files.internal("menu/home_blank.png"));
		generator = new FreeTypeFontGenerator(Gdx.files.internal("font/BRLNSR.TTF"));
		generatorBold = new FreeTypeFontGenerator(Gdx.files.internal("font/BRLNSDB.TTF"));
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();

		// 生成 BitmapFont
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

		// 载入 UI 外观
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
		/* --- 主菜单 UI 初始化结束 --- */

		/* --- 加载声音资源 --- */
		chessSound1 = Gdx.audio.newSound(Gdx.files.internal("sound/chess_sound1.mp3"));
		chessSound2 = Gdx.audio.newSound(Gdx.files.internal("sound/chess_sound2.mp3"));
		bgm = Gdx.audio.newSound(Gdx.files.internal("sound/bgm.mp3"));

		System.out.println(Gdx.graphics.getDeltaTime());
	}

	@Override
	public void render () {
		switch (interfaceType) {
			case OthelloConstants.InterfaceType.GAME:
				renderGame(); break;
			default:
				renderHome(); break;
		}
	}
	
	@Override
	public void dispose () {
		modelBatch.dispose();
		boardModel.dispose();
	}
}
