/**
 * 2D Point class stores 2 integer that represnt x and y values;
 * */

package fireflies.the.game.utils;

import fireflies.the.game.MainClass;

public class Point {

	public double x;
	public double y;

	/**
	 * Different constructors to declare objects with integers, doubles and
	 * strings (used for map generation from txt)
	 * */
	public Point(String s) // #x# format
	{
		x = (int) (Integer.parseInt(s.split("x")[0]) * MainClass.screenScaling);
		y = (int) (Integer.parseInt(s.split("x")[1]) * MainClass.screenScaling);
	}

	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Gets distance to some other point (modulus of the vector)
	 * */
	public int getDistanceTo(Point anotherPoint) {
		return (int) Math.sqrt(Math.pow(anotherPoint.x - this.x, 2)
				+ Math.pow(anotherPoint.y - this.y, 2));
	}

	/**
	 * Differences between X and Y values of two points
	 * */
	public int getDistanceX(Point anotherPoint) {
		return ((int) Math.abs(anotherPoint.x - this.x));
	}
	public int getDistanceY(Point anotherPoint) {
		return ((int) Math.abs(anotherPoint.y - this.y));
	}

	public Boolean hasSameLocation(Point anotherPoint) {
		if (anotherPoint.x == this.x && anotherPoint.y == this.y) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Translates point to some distance with direction defined by an angle in degrees
	 * */
	public void move(double angle, double speed) {
		x += speed * Math.cos(Math.toRadians(angle));
		y += speed * Math.sin(Math.toRadians(angle));
	}

	/**
	 * Returns an angle (direction) to some other point
	 * */
	public double getAngleToAPoint(Point anotherPoint) {
		double hy = Math.sqrt(Math.pow(anotherPoint.x - this.x, 2)
				+ Math.pow(anotherPoint.y - this.y, 2));
		double angle = -Math.toDegrees(Math
				.acos((anotherPoint.x - this.x) / hy));
		if (anotherPoint.y - this.y > 0) {
			angle = (180 - angle) + 180;
		}

		return angle;
	}

	/**
	 * Return Boolean representing whether this point lies in observed portion of the screen
	 * */
	public Boolean inSight(int sizeX, int sizeY) {
		double scaleFactor = 1 / (MainClass.scaleNotches * MainClass.SCALE_SPEED);
		if (x < (MainClass.SCREEN_SIZE_X - MainClass.SCREEN_SIZE_X
				* scaleFactor)
				/ 2 + MainClass.screenOffsetX - sizeX / 2) {
			return false;
		}
		if (x > (MainClass.SCREEN_SIZE_X - MainClass.SCREEN_SIZE_X
				* scaleFactor + sizeX / 2)
				/ 2
				+ MainClass.screenOffsetX
				+ MainClass.SCREEN_SIZE_X
				* scaleFactor) {
			return false;
		}
		if (y < (MainClass.SCREEN_SIZE_Y - MainClass.SCREEN_SIZE_Y
				* scaleFactor - sizeY / 2)
				/ 2 + MainClass.screenOffsetY) {
			return false;
		}
		if (y > (MainClass.SCREEN_SIZE_Y - MainClass.SCREEN_SIZE_Y
				* scaleFactor + sizeY / 2)
				/ 2
				+ MainClass.screenOffsetY
				+ MainClass.SCREEN_SIZE_Y
				* scaleFactor) {
			return false;
		}
		return true;
	}
}
