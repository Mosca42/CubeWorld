package fr.mosca42.cubeworld.game;

import fr.mosca42.cubeworld.engine.GameEngine;
import fr.mosca42.cubeworld.engine.IGameLogic;

public class Start {

	public static void main(String[] args) {
		try {
			boolean vSync = true;
			IGameLogic gameLogic = new CubeWorld();
			GameEngine gameEng = new GameEngine("CubeWorld", 600, 480, vSync, gameLogic);
			gameEng.start();
		} catch (Exception excp) {
			excp.printStackTrace();
			System.exit(-1);
		}
	}
}