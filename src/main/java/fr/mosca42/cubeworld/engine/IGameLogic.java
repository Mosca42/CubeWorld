package fr.mosca42.cubeworld.engine;

import fr.mosca42.cubeworld.engine.camera.input.MouseInput;

public interface IGameLogic {

	void init(Window window) throws Exception;

	void input(Window window, MouseInput mouseInput);

	void update(float interval, MouseInput mouseInput);

	void render(Window window);

	void cleanup();
}