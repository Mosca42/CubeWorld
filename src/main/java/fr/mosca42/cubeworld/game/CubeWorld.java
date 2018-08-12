package fr.mosca42.cubeworld.game;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_M;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_N;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_X;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_Z;

import org.joml.Vector2f;
import org.joml.Vector3f;

import fr.mosca42.cubeworld.engine.GameItem;
import fr.mosca42.cubeworld.engine.IGameLogic;
import fr.mosca42.cubeworld.engine.Material;
import fr.mosca42.cubeworld.engine.Mesh;
import fr.mosca42.cubeworld.engine.Texture;
import fr.mosca42.cubeworld.engine.Window;
import fr.mosca42.cubeworld.engine.camera.Camera;
import fr.mosca42.cubeworld.engine.camera.input.MouseInput;
import fr.mosca42.cubeworld.engine.light.PointLight;
import fr.mosca42.cubeworld.engine.obj.OBJLoader;
import fr.mosca42.cubeworld.terrain.Scene;
import fr.mosca42.cubeworld.terrain.Terrain;

public class CubeWorld implements IGameLogic {

	private static final float MOUSE_SENSITIVITY = 0.2f;

	private final Vector3f cameraInc;

	private final Renderer renderer;

	private final Camera camera;

	private GameItem[] gameItems;

	private Vector3f ambientLight;

	private PointLight pointLight;

	private Scene scene;

	private static final float CAMERA_POS_STEP = 0.05f;

	public CubeWorld() {
		renderer = new Renderer();
		camera = new Camera();
		cameraInc = new Vector3f(0.0f, 0.0f, 0.0f);
	}

	@Override
	public void init(Window window) throws Exception {
		renderer.init(window);

		scene = new Scene();

		float terrainScale = 0.05f;
		int terrainSize = 10;
		float minY = -0.1f;
		float maxY = 0.1f;
		int textInc = 40;
		Terrain terrain = new Terrain(terrainSize, terrainScale, minY, maxY, "/textures/heightmap.png",
				"/textures/grassblock.png", textInc);
		scene.setGameItems(terrain.getGameItems());

		float reflectance = 1f;

		Mesh mesh = OBJLoader.loadMesh("/models/cube.obj");
		Texture texture = new Texture("/textures/grassblock.png");
		Material material = new Material(texture, reflectance);

		mesh.setMaterial(material);
		GameItem gameItem = new GameItem(mesh);
		gameItem.setScale(0.5f);
		gameItem.setPosition(0, 5, -2);
		gameItems = new GameItem[] { gameItem };

		ambientLight = new Vector3f(0.3f, 0.3f, 0.3f);
		Vector3f lightColour = new Vector3f(1, 1, 1);
		Vector3f lightPosition = new Vector3f(0, 20, 0);
		float lightIntensity = 300.0f;
		pointLight = new PointLight(lightColour, lightPosition, lightIntensity);
		PointLight.Attenuation att = new PointLight.Attenuation(0.0f, 0.0f, 1.0f);
		pointLight.setAttenuation(att);
	}

	@Override
	public void input(Window window, MouseInput mouseInput) {
		cameraInc.set(0, 0, 0);
		if (window.isKeyPressed(GLFW_KEY_W)) {
			cameraInc.z = -1;
		} else if (window.isKeyPressed(GLFW_KEY_S)) {
			cameraInc.z = 1;
		}
		if (window.isKeyPressed(GLFW_KEY_A)) {
			cameraInc.x = -1;
		} else if (window.isKeyPressed(GLFW_KEY_D)) {
			cameraInc.x = 1;
		}
		if (window.isKeyPressed(GLFW_KEY_Z)) {
			cameraInc.y = -1;
		} else if (window.isKeyPressed(GLFW_KEY_X)) {
			cameraInc.y = 1;
		}
		float lightPos = pointLight.getPosition().z;
		if (window.isKeyPressed(GLFW_KEY_N)) {
			this.pointLight.getPosition().z = lightPos + 0.1f;
		} else if (window.isKeyPressed(GLFW_KEY_M)) {
			this.pointLight.getPosition().z = lightPos - 0.1f;
		}
	}

	@Override
	public void update(float interval, MouseInput mouseInput) {
		// Update camera position
		camera.movePosition(cameraInc.x * CAMERA_POS_STEP, cameraInc.y * CAMERA_POS_STEP,
				cameraInc.z * CAMERA_POS_STEP);

		// Update camera based on mouse
		if (mouseInput.isRightButtonPressed()) {
			Vector2f rotVec = mouseInput.getDisplVec();
			camera.moveRotation(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);
		}
	}

	@Override
	public void render(Window window) {
		renderer.render(window, camera, scene, ambientLight, pointLight);
	}

	@Override
	public void cleanup() {
		renderer.cleanup();
		scene.cleanup();
		for (GameItem gameItem : gameItems) {
			gameItem.getMesh().cleanUp();
		}
	}

}
