/**
 * The void slowly covers the whole battlefield and annihilates everything 
 * 
 * Instead of using simple rectangles, game draws custom polygon therefore I'm using lists of vertices
 * This method was chosen bc initially the game was going to draw more complicated shapes like hexagons, but it I changed it back to square bc I liked it more
 * */

package fireflies.the.game;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.LinkedList;

import fireflies.the.game.particles.Explosion;
import fireflies.the.game.utils.Point;

public class Blackout {

	public double SPEED = 0.015f*MainClass.screenScaling;

	LinkedList<Point> edges = new LinkedList<Point>();

	public Blackout() {

		int divider = 2;

		edges.add(new Point(-MainClass.SCREEN_SIZE_Y / divider,
				-MainClass.SCREEN_SIZE_Y / divider));
		edges.add(new Point(MainClass.mapSizeX + MainClass.SCREEN_SIZE_Y
				/ divider, -MainClass.SCREEN_SIZE_Y / divider));
		edges.add(new Point(MainClass.mapSizeX + MainClass.SCREEN_SIZE_Y
				/ divider, MainClass.mapSizeY + MainClass.SCREEN_SIZE_Y / divider));
		edges.add(new Point(-MainClass.SCREEN_SIZE_Y / divider,
				MainClass.mapSizeY + MainClass.SCREEN_SIZE_Y / divider));
		
		
		System.out.println(MainClass.mapSizeX + " " + MainClass.mapSizeY);
		for(Point edge : edges)
		{
			System.out.println(edge.x+""+edge.y);
		}
	}

	public void moveEdges() {
		for (int i = 0; i < edges.size(); i++) {
			for (int e = 0; e < edges.size(); e++) {
				edges.get(e).move(
						edges.get(e).getAngleToAPoint(
								new Point(MainClass.mapSizeX / 2,
										MainClass.mapSizeY / 2)), SPEED);
			}
		}

		for (int i = 0; i < MainClass.planets.size(); i++) {
			if (!inBlackout(MainClass.planets.get(i).position.x,
					MainClass.planets.get(i).position.y)) {
				MainClass.planets.get(i).dead = true;

				/*
				 * for(int ai = 0; ai < MainClass.aiPlayer.size(); ai++) {
				 * for(int p = 0; p <
				 * MainClass.aiPlayer.get(ai).governors.size(); p++) {
				 * 
				 * MainClass.aiPlayer.get(ai).governors.get(p).attackPlanetIndex
				 * = null;
				 * MainClass.aiPlayer.get(ai).governors.get(p).expansionPlanetIndex
				 * = null; } }
				 */// System.out.println("PLANET " + i +
					// " IS OUTSIDE OF BATTLEFIELD");
			}
		}
		for (int i = 0; i < MainClass.units.size(); i++) {
			if (!inBlackout(MainClass.units.get(i).position.x,
					MainClass.units.get(i).position.y)) {
				MainClass.planets.get(MainClass.units.get(i).planetParentId).unitsSpawned--;
				MainClass.explosions.add(new Explosion(
						MainClass.units.get(i).position.x, MainClass.units
								.get(i).position.y, MainClass.units.get(i).player));
				MainClass.units.remove(i);
				i--;
				// System.out.println("PLANET " + i +
				// " IS OUTSIDE OF BATTLEFIELD");
			}
		}

	}

	public Boolean inBlackout(double x, double y) {
		if (edges.get(0).x < x && edges.get(2).x > x && edges.get(0).y < y
				&& edges.get(2).y > y) {
			return true;
		}
		return false;
	}

	public void draw(Graphics2D g) {
		int[] coordsX = new int[4];
		int[] coordsY = new int[4];

		for (int i = 0; i < 4; i++) {
			coordsX = addElement(coordsX, (int) edges.get(i).x);
			coordsY = addElement(coordsY, (int) edges.get(i).y);
		}
		coordsX = addElement(coordsX, (int) edges.get(0).x);
		coordsY = addElement(coordsY, (int) edges.get(0).y);
		int lastX = coordsX[coordsX.length - 1];
		int lastY = coordsY[coordsY.length - 1];

		int topLeftX = 0 - MainClass.mapSizeX;
		int topLeftY = 0 - MainClass.mapSizeY;
		int bottomRightX = MainClass.mapSizeX + MainClass.mapSizeX;
		int bottomRightY = MainClass.mapSizeY + MainClass.mapSizeY;

		coordsX = addElement(coordsX, lastX);
		coordsY = addElement(coordsY, topLeftY);

		coordsX = addElement(coordsX, topLeftX);
		coordsY = addElement(coordsY, topLeftY);

		coordsX = addElement(coordsX, topLeftX);
		coordsY = addElement(coordsY, bottomRightY);

		coordsX = addElement(coordsX, bottomRightX);
		coordsY = addElement(coordsY, bottomRightY);

		coordsX = addElement(coordsX, bottomRightX);
		coordsY = addElement(coordsY, topLeftY);

		coordsX = addElement(coordsX, lastX);
		coordsY = addElement(coordsY, topLeftY);

		coordsX = addElement(coordsX, lastX);
		coordsY = addElement(coordsY, lastY);

		g.setColor(new Color(0x990099));

		g.setComposite(AlphaComposite
				.getInstance(AlphaComposite.SRC_OVER, 0.4f));
		g.fillPolygon(coordsX, coordsY, 16);
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

		// System.out.println(coordsX.length);
	}

	public int[] addElement(int[] pointY, double element) {
		int[] newArray = new int[pointY.length + 1];

		for (int i = 0; i < pointY.length; i++) {
			newArray[i] = pointY[i];
		}

		newArray[newArray.length - 1] = (int) element;

		return newArray;
	}
}
