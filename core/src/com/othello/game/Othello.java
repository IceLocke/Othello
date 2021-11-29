package com.othello.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
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
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.UBJsonReader;
import com.othello.game.core.OthelloGame;
import com.othello.game.processor.HomeInputProcessor;
import com.othello.game.utils.Disc;
import com.othello.game.utils.DiscList;
import com.othello.game.utils.OthelloConstants;

import java.util.ArrayList;

public class Othello extends ApplicationAdapter {
	public PerspectiveCamera cam;
	public CameraInputController camController;

	public Environment environment;

	public Model frameModel;

	public Model boardModel;
	public Model discModel;
	public Model tableModel;
	public ModelBatch modelBatch;

	public ModelInstance frameInstance;
	public ModelInstance boardInstance;
	public ModelInstance tableInstance;
	public ArrayList<ModelInstance> discInstanceList;
	public ArrayList<AnimationController> discAnimationControllerList;
	public DiscList discList;
	public ArrayList<ModelInstance> renderInstanceList;

	public FreeTypeFontGenerator generator;
	public FreeTypeFontGenerator generatorBold;
	public BitmapFont titleFont;
	public BitmapFont buttonFont;
	public BitmapFont buttonFontBold;

	public static int interfaceType = OthelloConstants.InterfaceType.HOME;
	public static int menuButtonType = OthelloConstants.MenuButtonType.NONE;
	public static boolean menuButtonPressed = false;

	protected SpriteBatch batch;
	protected Texture homeLoading;
	protected Texture homeDefault;
	protected OthelloGame game;
	protected int[][] board;
	protected int[][] newBoard;
	protected boolean newGame = true;

	protected Stage homeStage;
	protected Stage gameStage;
	protected Table homeTable;
	protected Table gameTable;

	public void loadBoard() {
		for (int i = 0; i <= 9; i++)
			for (int j = 0; j <= 9; j++)
				newBoard[i][j] = game.getNowPlayBoard()[i][j];
	}

	// 渲染主菜单
	public void renderHome() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.begin();

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

		homeLogic();
		batch.end();
	}

	// 渲染游戏界面
	public void renderGame(){
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		loadBoard();
		for (int i = 1; i <= 8; i++) {
			for (int j = 1; j <= 8; j++) {
				if (board[i][j] != newBoard[i][j]) {
					if (board[i][j] == OthelloConstants.DiscType.BLANK) {
						// 是新的棋子，将新的棋子加入渲染队列
						ModelInstance newDiscInstance = discInstanceList.get(discList.getDiscListSize());
						AnimationController newController = discAnimationControllerList.get(discList.getDiscListSize());
						discList.addDisc(new Disc(i, j, newBoard[i][j], newDiscInstance, newController));
						renderInstanceList.add(newDiscInstance);
					}
					else {
						// 翻转棋子
						Disc disc = discList.getDiscAtPosition(i, j);
						disc.rotate();
					}
				}
				board[i][j] = newBoard[i][j];
			}
		}

		ArrayList<Disc> discArrayList = discList.getDiscList();
		for (Disc disc : discArrayList) {
			disc.animationController.update(Gdx.graphics.getDeltaTime());
		}

		modelBatch.begin(cam);
		modelBatch.render(renderInstanceList, environment);
		modelBatch.end();
	}

	// 渲染本地双人对战选单
	public void renderLocalMultiplePlayerMenu() {

	}

	// 渲染AI对战选单
	public void renderLocalSinglePlayerMenu() {

	}

	public void renderOnlineMultiplePlayerMenu() {

	}

	// 主菜单逻辑
	public void homeLogic() {
		if (menuButtonPressed) {
			if (menuButtonType != OthelloConstants.MenuButtonType.EXIT) {
				homeStage = new Stage();
				Gdx.input.setInputProcessor(homeStage);
				homeTable = new Table();
				homeTable.setFillParent(true);
				homeStage.addActor(homeTable);
			}
			switch (menuButtonType) {
				case OthelloConstants.MenuButtonType.EXIT:
					Gdx.app.exit();
					break;
				case OthelloConstants.MenuButtonType.LOCAL_SINGLE_PLAYER:
					interfaceType = OthelloConstants.InterfaceType.LOCAL_SINGLE_PLAYER_MENU;
					break;
				case OthelloConstants.MenuButtonType.LOCAL_MULTIPLE_PLAYER:
					interfaceType = OthelloConstants.InterfaceType.LOCAL_MULTIPLE_PLAYER_MENU;
					break;
				case OthelloConstants.MenuButtonType.ONLINE_MULTIPLE_PLAYER:
					interfaceType = OthelloConstants.InterfaceType.ONLINE_MULTIPLE_PLAYER_MENU;
					break;
				default: break;
			}
			menuButtonPressed = false;
		}
		switch (interfaceType) {
			case OthelloConstants.InterfaceType.LOCAL_SINGLE_PLAYER_MENU:
				renderLocalSinglePlayerMenu(); break;
			case OthelloConstants.InterfaceType.LOCAL_MULTIPLE_PLAYER_MENU:
				renderLocalMultiplePlayerMenu(); break;
			case OthelloConstants.InterfaceType.ONLINE_MULTIPLE_PLAYER_MENU:
				renderOnlineMultiplePlayerMenu(); break;
			default: break;
		}
	}

	// 本地对战逻辑
	public void localGameLogic() {
	}

	// 在线对战逻辑
	public void onlineGameLogic() {

	}

	@Override
	public void create () {
		interfaceType = OthelloConstants.InterfaceType.HOME;
		batch = new SpriteBatch();

		homeLoading = new Texture(Gdx.files.internal("menu/home_loading.png"));
		batch.begin();
		batch.draw(homeLoading, 0, 0);
		batch.end();

		/* --- 3D 部分初始化开始 --- */

		// 初始化相机
		cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.position.set(3f, 7f, 1f);
		cam.lookAt(3f, 0f, -2.5f);
		cam.near = 1f;
		cam.far = 300f;
		cam.update();

		// 初始化场景
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 0f));
		environment.add(new PointLight().set(0.8f, 0.8f, 0.8f, 3f, 10f, -5f, 1000));

		// 加载棋盘、棋子、桌子模型
		UBJsonReader jsonReader = new UBJsonReader();
		ModelLoader loader = new G3dModelLoader(jsonReader);
		ModelLoader objLoader = new ObjLoader();
		frameModel = objLoader.loadModel(Gdx.files.internal("models/frame.obj"));
		boardModel = objLoader.loadModel(Gdx.files.internal("models/board.obj"));
		discModel = loader.loadModel(Gdx.files.internal("models/disc.g3db"));
		tableModel = objLoader.loadModel(Gdx.files.internal("models/wooden_table.obj"));

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
		generatorBold = new FreeTypeFontGenerator(Gdx.files.internal("font/BRLNSR.TTF"));
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

		Gdx.input.setInputProcessor(new HomeInputProcessor());
		/* --- 主菜单 UI 初始化结束 --- */
	}

	@Override
	public void render () {
		switch (interfaceType) {
			case OthelloConstants.InterfaceType.HOME:
				renderHome(); break;
			case OthelloConstants.InterfaceType.GAME:
				renderGame(); break;
			default: break;
		}
	}
	
	@Override
	public void dispose () {
		modelBatch.dispose();
		boardModel.dispose();
	}
}
