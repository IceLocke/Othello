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
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.utils.ScreenUtils;
import com.othello.game.core.OthelloGame;
import com.othello.game.utils.OthelloConstants;

import java.util.ArrayList;

public class Othello extends ApplicationAdapter {
	public PerspectiveCamera cam;
	public CameraInputController camController;
	public Environment environment;
	public Model boardModel;
	public Model disc;
	public ModelInstance boardInstance;
	public ArrayList<ModelInstance> discInstanceList;
	public ModelBatch modelBatch;

	int interfaceType;
	SpriteBatch batch;
	Texture img;
	OthelloGame game;

	// 渲染主菜单
	public void renderHome() {

	}

	// 渲染游戏界面
	public void renderGame() {

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

	//

	// 本地对战逻辑
	public void localGameLogic() {
	}

	// 在线对战逻辑
	public void onlineGameLogic() {

	}

	@Override
	public void create () {
		interfaceType = OthelloConstants.InterfaceType.HOME;

		cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.position.set(1f, 1f, 1f);
		cam.lookAt(0, 0, 0);
		cam.near = 1f;
		cam.far = 300f;
		cam.update();

		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -2f, -2f, -2f));

		ModelLoader loader = new ObjLoader();
		boardModel = loader.loadModel(Gdx.files.internal("models/board.obj"));
		boardInstance = new ModelInstance(boardModel);
		modelBatch = new ModelBatch();

		camController = new CameraInputController(cam);
		Gdx.input.setInputProcessor(camController);
	}

	@Override
	public void render () {
		camController.update();

		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		modelBatch.begin(cam);
		modelBatch.render(boardInstance, environment);
		modelBatch.end();
	}
	
	@Override
	public void dispose () {
		modelBatch.dispose();
		boardModel.dispose();
	}
}
