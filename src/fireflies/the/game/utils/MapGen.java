/**
 * Generates the map and all objects from the given text file
 * */

package fireflies.the.game.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;

import fireflies.the.game.MainClass;
import fireflies.the.game.Planet;

public class MapGen {

	LinkedList<String> dataStrings = new LinkedList<String>();

	BufferedReader in = null;

	public MapGen() {
		//
	}

	public void generateMap(String fileName) {
		try {
			dataStrings = readFile(fileName);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/**
		 * Map size is defined in the first line of map.txt
		 * */
		MainClass.mapSizeX = (int) (new Point(dataStrings.get(0)).x);
		MainClass.mapSizeY = (int) (new Point(dataStrings.get(0)).y);

		/**
		 * Cleans up all object lists and generates new planet object from every
		 * next line got from the text file
		 * */
		MainClass.explosions.clear();
		MainClass.units.clear();
		MainClass.planets.clear();
		for (int i = 1; i < dataStrings.size(); i++) {
			String[] data = dataStrings.get(i).split(" ");
			MainClass.planets.add(new Planet(new Point(data[0]), Integer
					.parseInt(data[1]), Double.parseDouble(data[2]), Integer
					.parseInt(data[3]), Integer.parseInt(data[4]), (i - 1)));

			/**
			 * If planet is player's planet - center the screen on it, check all
			 * the offsets for not being out of screen
			 * */
			if (Integer.parseInt(data[1]) == 1) {
				MainClass.screenOffsetX = new Point(data[0]).x
						- MainClass.SCREEN_SIZE_X / 2;
				MainClass.screenOffsetY = new Point(data[0]).y
						- MainClass.SCREEN_SIZE_Y / 2;

				if (MainClass.screenOffsetX < -MainClass.SCREEN_SIZE_X / 4) {
					MainClass.screenOffsetX = -MainClass.SCREEN_SIZE_Y / 4;
				}
				if (MainClass.screenOffsetY < -MainClass.SCREEN_SIZE_X / 4) {
					MainClass.screenOffsetY = -MainClass.SCREEN_SIZE_Y / 4;
				}
				if (MainClass.screenOffsetY > MainClass.mapSizeY / 2
						- MainClass.SCREEN_SIZE_X / 4) {
					MainClass.screenOffsetY = MainClass.mapSizeY / 2
							- MainClass.SCREEN_SIZE_Y / 4;
				}
				if (MainClass.screenOffsetX > MainClass.mapSizeX / 2
						- MainClass.SCREEN_SIZE_X / 4) {
					MainClass.screenOffsetX = MainClass.mapSizeX / 2
							- MainClass.SCREEN_SIZE_Y / 4;
				}
			}
		}

	}

	/**
	 * Simple line by line txt reader, returns list of strings
	 * */
	public LinkedList<String> readFile(String fileName)
			throws FileNotFoundException {
		InputStream f = MainClass.class.getClassLoader().getResourceAsStream(
				fileName);
		String line = null;
		LinkedList<String> data = new LinkedList<String>();

		in = new BufferedReader(new InputStreamReader(f));

		do {
			try {
				line = in.readLine();
			} catch (IOException e) {
				System.out.println("Problem reading data from file");
			}

			if (line != null) {
				data.add(line);
			}
		} while (line != null);

		try {
			in.close();
			System.out.println("Closing File");
		} catch (IOException e) {
			System.out.println("Problem Closing " + e);
		}

		return data;
	}
}
