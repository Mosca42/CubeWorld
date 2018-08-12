package fr.mosca42.cubeworld.terrain;

import fr.mosca42.cubeworld.engine.GameItem;

public class Terrain {

	private final GameItem[] gameItems;

	public Terrain(int blocksPerRow, float scale, float minY, float maxY, String heightMap, String textureFile,
			int textInc) throws Exception {
		gameItems = new GameItem[blocksPerRow * blocksPerRow];
		PerlinNoise heightMapMesh = new PerlinNoise(minY, maxY, heightMap, textureFile, textInc);
		for (int row = 0; row < blocksPerRow; row++) {
			for (int col = 0; col < blocksPerRow; col++) {
				float xDisplacement = (col - ((float) blocksPerRow - 1) / 2) * scale * PerlinNoise.getXLength();
				float zDisplacement = (row - ((float) blocksPerRow - 1) / 2) * scale * PerlinNoise.getZLength();

				GameItem terrainBlock = new GameItem(heightMapMesh.getMesh());
				terrainBlock.setScale(scale);
				terrainBlock.setPosition(xDisplacement, 0, zDisplacement);
				gameItems[row * blocksPerRow + col] = terrainBlock;
			}
		}
	}

	public GameItem[] getGameItems() {
		return gameItems;
	}
}