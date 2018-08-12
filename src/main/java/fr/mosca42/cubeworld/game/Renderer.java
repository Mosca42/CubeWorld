package fr.mosca42.cubeworld.game;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glViewport;

import java.util.List;
import java.util.Map;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import fr.mosca42.cubeworld.engine.GameItem;
import fr.mosca42.cubeworld.engine.Mesh;
import fr.mosca42.cubeworld.engine.Shaders;
import fr.mosca42.cubeworld.engine.Transformation;
import fr.mosca42.cubeworld.engine.Utils;
import fr.mosca42.cubeworld.engine.Window;
import fr.mosca42.cubeworld.engine.camera.Camera;
import fr.mosca42.cubeworld.engine.light.PointLight;
import fr.mosca42.cubeworld.terrain.Scene;

public class Renderer {

	/**
	 * Field of View in Radians
	 */
	private static final float FOV = (float) Math.toRadians(60.0f);

	private static final float Z_NEAR = 0.01f;

	private static final float Z_FAR = 1000.f;

	private final Transformation transformation;

	private Shaders shaderProgram;

	private float specularPower;

	public Renderer() {
		transformation = new Transformation();
		specularPower = 10f;
	}

	public void init(Window window) throws Exception {
		// Create shader
		shaderProgram = new Shaders();
		shaderProgram.createVertexShader(Utils.loadResource("/shaders/vertex.vs"));
		shaderProgram.createFragmentShader(Utils.loadResource("/shaders/fragment.fs"));
		shaderProgram.link();

		// Create uniforms for modelView and projection matrices and texture
		shaderProgram.createUniform("projectionMatrix");
		shaderProgram.createUniform("modelViewMatrix");
		shaderProgram.createUniform("texture_sampler");
		// Create uniform for material
		shaderProgram.createMaterialUniform("material");
		// Create lighting related uniforms
		shaderProgram.createUniform("specularPower");
		shaderProgram.createUniform("ambientLight");
		shaderProgram.createPointLightUniform("pointLight");
	}

	public void clear() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	}

	public void render(Window window, Camera camera, Scene scene, Vector3f ambientLight, PointLight pointLight) {

		clear();

		if (window.isResized()) {
			glViewport(0, 0, window.getWidth(), window.getHeight());
			window.setResized(false);
		}

		shaderProgram.bind();

		// Update projection Matrix
		Matrix4f projectionMatrix = transformation.getProjectionMatrix(FOV, window.getWidth(), window.getHeight(),
				Z_NEAR, Z_FAR);
		shaderProgram.setUniform("projectionMatrix", projectionMatrix);

		// Update view Matrix
		Matrix4f viewMatrix = transformation.getViewMatrix(camera);

		// Update Light Uniforms
		shaderProgram.setUniform("ambientLight", ambientLight);
		shaderProgram.setUniform("specularPower", specularPower);
		// Get a copy of the light object and transform its position to view coordinates
		PointLight currPointLight = new PointLight(pointLight);
		Vector3f lightPos = currPointLight.getPosition();
		Vector4f aux = new Vector4f(lightPos, 1);
		aux.mul(viewMatrix);
		lightPos.x = aux.x;
		lightPos.y = aux.y;
		lightPos.z = aux.z;
		shaderProgram.setUniform("pointLight", currPointLight);

		shaderProgram.setUniform("texture_sampler", 0);
		// Render each gameItem
		renderScene(window, camera, scene);

		shaderProgram.unbind();
	}

	public void renderScene(Window window, Camera camera, Scene scene) {
		shaderProgram.bind();

		Matrix4f projectionMatrix = transformation.getProjectionMatrix();
		shaderProgram.setUniform("projectionMatrix", projectionMatrix);

		Matrix4f viewMatrix = transformation.getViewMatrix();

		shaderProgram.setUniform("texture_sampler", 0);
		// Render each mesh with the associated game Items
		Map<Mesh, List<GameItem>> mapMeshes = scene.getGameMeshes();
		for (Mesh mesh : mapMeshes.keySet()) {
			shaderProgram.setUniform("material", mesh.getMaterial());
			mesh.renderList(mapMeshes.get(mesh), (GameItem gameItem) -> {
				Matrix4f modelViewMatrix = transformation.buildModelViewMatrix(gameItem, viewMatrix);
				shaderProgram.setUniform("modelViewMatrix", modelViewMatrix);
			});
		}

		shaderProgram.unbind();
	}

	public void cleanup() {
		if (shaderProgram != null) {
			shaderProgram.cleanup();
		}
	}
}