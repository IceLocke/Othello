package com.othello.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
import com.badlogic.gdx.utils.UBJsonReader;
import com.othello.game.core.OthelloGame;
import com.othello.game.utils.Disc;
import com.othello.game.utils.DiscList;
import com.othello.game.utils.OthelloConstants;
import jdk.nashorn.internal.runtime.options.OptionTemplate;

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

	protected int interfaceType;
	protected SpriteBatch batch;
	protected Texture img;
	protected OthelloGame game;
	protected int[][] board;
	protected int[][] newBoard;
	protected boolean newGame = true;

	public void loadBoard() {
		for (int i = 0; i <= 9; i++) {
			for (int j = 0; j <= 9; j++)
				newBoard[i][j] = OthelloConstants.DiscType.BLANK;
		}
		newBoard[4][4] = newBoard[5][5] = OthelloConstants.DiscType.WHITE;
		newBoard[4][5] = newBoard[5][4] = OthelloConstants.DiscType.BLACK;
//		for (int i = 0; i <= 9; i++) {
//			for (int j = 0; j <= 9; j++) {
//				newBoard[i][j] = game.getNowPlayBoard()[i][j];
//			}
//		}
	}

	// 渲染主菜单
	public void renderHome() {

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

	// 主菜单逻辑
	public void homeLogic() {

	}

	// 本地对战逻辑
	public void localGameLogic() {
	}

	// 在线对战逻辑
	public void onlineGameLogic() {

	}

	@Override
	public void create () {
		interfaceType = OthelloConstants.InterfaceType.GAME;

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
		renderInstanceList = new ArrayList<ModelInstance>();
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

///
	}

	@Override
	public void render () {
		 // camController.update();

		if (interfaceType == OthelloConstants.InterfaceType.HOME) {
			renderHome();
		}

		if (interfaceType == OthelloConstants.InterfaceType.GAME) {
			renderGame();
		}

	}
	
	@Override
	public void dispose () {
		modelBatch.dispose();
		boardModel.dispose();
	}
}
