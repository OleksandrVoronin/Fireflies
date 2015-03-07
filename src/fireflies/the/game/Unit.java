/**
 * Unit class
 * has 4 states: circular movement around the planet, getting ready to move, 
 * moving somewhere and being idle
 * 
 * Has different attributes that define its speed, strength and ability to 
 * capture planets faster (aka pushing power).
 * Can be upgraded
 * Stores all the bunch of stuff about player who controls it and planet
 * that spawned it
 * */

package fireflies.the.game;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.LinkedList;
import java.util.Random;

import fireflies.the.game.particles.Explosion;
import fireflies.the.game.utils.Point;

public class Unit {

	static public final int MOVE = 0x000001;
	static public final int CIRCULAR = 0x000002;
	static public final int GOING_TO_MOVE = 0x000003;
	static public final int IDLE = 0x000004;

	static double initSpeed = MainClass.SCREEN_SIZE_Y / 720f;
	static int initStr = 2;
	static int initPushing = 1;

	int idleMovementSign = 1;

	double notMorphedAngle = 0;
	static double CIRCULAR_RADIUS = MainClass.SCREEN_SIZE_Y / 18;

	public int activity = CIRCULAR;

	Point position;
	Point destination;
	public int player;
	public int planetParentId;

	public static double speedUpStep = 0.7f * MainClass.screenScaling;
	double SPEED = initSpeed;
	int strength = initStr;
	public int pushingPower = initPushing;

	double angle;

	static int SIZE = MainClass.SCREEN_SIZE_Y / 45;

	double bitmapCounter = 0;

	// double wave = 0;
	public double WAVE_SIGN_ORIGINAL = 0.3;
	public double waveSign = WAVE_SIGN_ORIGINAL;
	final int WAVE_COUNTER_MAX = 20;
	public int waveCounter = 0;

	LinkedList<Point> lastPositions = new LinkedList<Point>();
	final int TAIL_MAX = 8;

	Boolean selected = false;

	public Unit(int x, int y, int player, int planetParentId,
			double speedUpgade, int strengthUpgrade, int pushingUpgrade) {
		// System.out.println(SPEED + " id:" + planetParentId);

		position = new Point(x, y);
		destination = new Point(x, (int) (y - CIRCULAR_RADIUS));
		this.player = player;
		this.planetParentId = planetParentId;

		SPEED += speedUpgade;
		strength += strengthUpgrade;
		pushingPower += pushingUpgrade;
	}

	public void setDestination(Point destination) {
		this.destination = destination;
	}

	public void AI(int index) {

		lastPositions.add(new Point(position.x, position.y));

		bitmapCounter += 0.1;
		if (bitmapCounter > 5) {
			bitmapCounter = 0;
		}

		if (activity == MOVE) {

			Boolean toPlanet = false;

			for (int i = 0; i < MainClass.planets.size(); i++) {
				if (destination
						.hasSameLocation(MainClass.planets.get(i).position)
						&& (MainClass.planets.get(i)
								.getPlayerControllingPlanet() == player)) {
					toPlanet = true;
					// System.out.println("TO THE PLANET");
				}
			}

			if (destination.getDistanceTo(position) <= CIRCULAR_RADIUS
					&& toPlanet == true) {
				activity = CIRCULAR;
				this.angle = notMorphedAngle + 90;
			} else if (destination.hasSameLocation(position)
					&& toPlanet == false) {
				activity = IDLE;
			} else {
				// System.out.println("angle: " + angle);
				angle = position.getAngleToAPoint(destination);
				moveToDestination();
			}
		} else if (activity == CIRCULAR) {
			double anglePerTick = Math.toDegrees(SPEED / CIRCULAR_RADIUS);
			angle += anglePerTick;
			circularMovement();
		} else if (activity == GOING_TO_MOVE) {

			double anglePerTick = Math.toDegrees(SPEED / CIRCULAR_RADIUS);

			Point centerOfRotation = new Point(
					(int) (position.x + CIRCULAR_RADIUS
							* Math.cos(Math.toRadians(angle - 90))),
					(int) (position.y + CIRCULAR_RADIUS
							* Math.sin(Math.toRadians(angle - 90))));

			double angleToDest = centerOfRotation.getAngleToAPoint(destination);

			while (angleToDest < 0)
				angleToDest += 360;
			while (angleToDest > 360)
				angleToDest -= 360;
			while (angle < 0)
				angle += 360;
			while (angle > 360)
				angle -= 360;
			// System.out.println("angle" + angle);
			// System.out.println("angleToDest" + angleToDest);

			if ((Math.abs(angle - angleToDest - 270) <= anglePerTick * 1.5 || Math
					.abs(angle - angleToDest + 90) <= anglePerTick * 1.5)
					&& position.getDistanceTo(destination) < centerOfRotation
							.getDistanceTo(destination)) {
				activity = MOVE;
			} else {

				angle += anglePerTick;

				position.x -= SPEED * Math.cos(Math.toRadians(angle));
				position.y -= SPEED * Math.sin(Math.toRadians(angle));
			}
		} else if (activity == IDLE) {

			if (position.getDistanceY(destination) > SPEED * 2
					|| position.getDistanceY(destination) < -SPEED * 2) {
				idleMovementSign *= -1;
			}

			// if (position.getDistanceTo(destination) < idleMovementDistance) {
			// position.x -= SPEED * Math.cos(Math.toRadians(angle));
			position.y -= idleMovementSign * 0.15 * SPEED;
			// }
		}

		// if (player == 1) {

		for (int i = 0; i < MainClass.units.size(); i++) {
			if (MainClass.units.get(i).player != player) {
				int distanceX = MainClass.units.get(i).position
						.getDistanceX(position);
				int distanceY = MainClass.units.get(i).position
						.getDistanceY(position);
				if (distanceX < SIZE && distanceY < SIZE) {
					try {
						int str1 = MainClass.units.get(i).strength;
						int str2 = MainClass.units.get(index).strength;

						if (new Random().nextInt(str1 + str2) < str2) {
							MainClass.planets
									.get(MainClass.units.get(i).planetParentId).unitsSpawned--;
							MainClass.explosions.add(new Explosion(
									MainClass.units.get(i).position.x,
									MainClass.units.get(i).position.y,
									MainClass.units.get(i).player));
							MainClass.units.remove(i);
							if (i < index) {
								index--;
							}
							i = MainClass.units.size();
						}

						if (new Random().nextInt(str1 + str2) < str1) {
							MainClass.planets
									.get(MainClass.units.get(index).planetParentId).unitsSpawned--;
							MainClass.explosions.add(new Explosion(
									MainClass.units.get(index).position.x,
									MainClass.units.get(index).position.y,
									MainClass.units.get(index).player));
							MainClass.units.remove(index);
						}
					} catch (java.lang.NullPointerException e) {
					} catch (java.lang.IndexOutOfBoundsException e) {
					}
				} else if (distanceX < CIRCULAR_RADIUS * 1.5
						&& distanceY < CIRCULAR_RADIUS * 1.5) {
					position.x -= ((1 - distanceX / (CIRCULAR_RADIUS * 1.5))
							+ (1 - distanceY / (CIRCULAR_RADIUS * 1.5)) + 1)
							* SPEED
							* Math.cos(Math.toRadians(MainClass.units.get(i).position
									.getAngleToAPoint(position)));
					position.y -= ((1 - distanceX / CIRCULAR_RADIUS * 1.5)
							+ (1 - distanceY / CIRCULAR_RADIUS * 1.5) + 1)
							* SPEED
							* Math.sin(Math.toRadians(MainClass.units.get(i).position
									.getAngleToAPoint(position)));
					i = MainClass.units.size();
				}
			}
		}
		// }
	}

	public void circularMovement() {
		position.x -= SPEED * Math.cos(Math.toRadians(angle));
		position.y -= SPEED * Math.sin(Math.toRadians(angle));

		if (position.getDistanceTo(destination) > CIRCULAR_RADIUS * 1.1) {
			activity = MOVE;
		}
	}

	public void moveToDestination() {

		if (waveCounter == 0) {
			if (waveSign > 0) {
				notMorphedAngle = angle;
			}
			waveSign *= -1;
			waveCounter = WAVE_COUNTER_MAX;
		}

		waveCounter--;

		// if (Math.abs(position.x - destination.x) < SPEED)
		// position.x = destination.x;
		// else
		position.x += SPEED * Math.cos(Math.toRadians(angle) + waveSign);

		// if (Math.abs(position.y - destination.y) < SPEED)
		// position.y = destination.y;
		// else
		position.y += SPEED * Math.sin(Math.toRadians(angle) + waveSign);

		if (Math.abs(position.x - destination.x) < SPEED
				&& Math.abs(position.y - destination.y) < SPEED) {
			position.x = destination.x;
			position.y = destination.y;
		}

		// if (destination.getDistanceTo(position) <= CIRCULAR_RADIUS) {
		// this.activity = CIRCULAR;
		// this.angle = notMorphedAngle + 90;
		// }

		// -1 System.out.println("pos: " + position.x + "+" + position.y);
	}

	public void draw(Graphics2D g) {
		while (lastPositions.size() >= TAIL_MAX) {
			try {
				lastPositions.removeFirst();
			} catch (java.util.NoSuchElementException e) {
			}
		}
		LinkedList<Point> lastPositionsCopy = lastPositions;

		if (SPEED != 0) {
			if (player == 1) {
				for (int i = 0; i < lastPositionsCopy.size(); i++) { // 5 max
					g.setComposite(AlphaComposite
							.getInstance(AlphaComposite.SRC_OVER, 0.1f + 0.3f
									/ TAIL_MAX * i));
					try {
						MainClass.drawCalls++;
						g.drawImage(MainClass.unitsBitmap,
								(int) (lastPositionsCopy.get(i).x - SIZE / 2),
								(int) (lastPositionsCopy.get(i).y - SIZE / 2),
								(int) (lastPositionsCopy.get(i).x + SIZE / 2),
								(int) (lastPositionsCopy.get(i).y + SIZE / 2),
								(int) bitmapCounter * SIZE, 0,
								(int) bitmapCounter * SIZE + SIZE, SIZE, null);
					} catch (java.lang.NullPointerException e) {
						// System.out.println("ERROR: " +
						// lastPositionsCopy.get(i).x
						// + ":" + lastPositionsCopy.get(i).y);
					}
				}

				g.setComposite(AlphaComposite.getInstance(
						AlphaComposite.SRC_OVER, 1f));
				MainClass.drawCalls++;
				g.drawImage(MainClass.unitsBitmap,
						(int) (position.x - SIZE / 2),
						(int) (position.y - SIZE / 2),
						(int) (position.x + SIZE / 2),
						(int) (position.y + SIZE / 2), (int) bitmapCounter
								* SIZE, 0, (int) bitmapCounter * SIZE + SIZE,
						SIZE, null);

			} else if (player == 2) {

				for (int i = 0; i < lastPositionsCopy.size(); i++) { // 5 max
					g.setComposite(AlphaComposite
							.getInstance(AlphaComposite.SRC_OVER, 0.1f + 0.3f
									/ TAIL_MAX * i));
					try {
						MainClass.drawCalls++;
						g.drawImage(MainClass.unitsBitmap,
								(int) (lastPositionsCopy.get(i).x - SIZE / 2),
								(int) (lastPositionsCopy.get(i).y - SIZE / 2),
								(int) (lastPositionsCopy.get(i).x + SIZE / 2),
								(int) (lastPositionsCopy.get(i).y + SIZE / 2),
								(int) bitmapCounter * SIZE, SIZE,
								(int) bitmapCounter * SIZE + SIZE, 2 * SIZE,
								null);
					} catch (java.lang.NullPointerException e) {
						// System.out.println("ERROR: " +
						// lastPositionsCopy.get(i).x
						// + ":" + lastPositionsCopy.get(i).y);
					}
				}

				g.setComposite(AlphaComposite.getInstance(
						AlphaComposite.SRC_OVER, 1f));

				MainClass.drawCalls++;
				g.drawImage(MainClass.unitsBitmap,
						(int) (position.x - SIZE / 2),
						(int) (position.y - SIZE / 2),
						(int) (position.x + SIZE / 2),
						(int) (position.y + SIZE / 2), (int) bitmapCounter
								* SIZE, SIZE,
						(int) bitmapCounter * SIZE + SIZE, 2 * SIZE, null);
			}
		} else {
			g.drawImage(MainClass.unitRebornBitmap[player - 1],
					(int) (position.x - SIZE / 2),
					(int) (position.y - SIZE / 2),
					(int) (position.x + SIZE / 2),
					(int) (position.y + SIZE / 2), 0, 0, SIZE, SIZE, null);
		}

		if (selected == true) {
			g.setColor(new Color(0x47fb00));
			MainClass.drawCalls++;
			g.drawRect((int) position.x - SIZE / 2,
					(int) position.y - SIZE / 2, SIZE, SIZE);
		}
	}
}
