/**
 * Planet class
 * 
 * Spawns units every tick (has max number of units spawned)
 * Can be controlled by some player, neutral or even dead (if covered with blackout)
 * 
 * Stores all that information and information about units that this 
 * planet is supposed to spawn 
 * */

package fireflies.the.game;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;

import fireflies.the.game.particles.Explosion;
import fireflies.the.game.utils.Point;

public class Planet {

	static public int SIZE = MainClass.SCREEN_SIZE_Y/9;
	public Point position;

	public Boolean dead = false;

	final int UPGRADE_TIME = 793;
	public final static int SPAWN_TIME = 193;
	public int spawnTick = 1;

	static public final int SPAWN_MAX = 13;
	public int unitsSpawned = 0;

	public int player1Control = 0;
	public int player2Control = 0;

	public int id;
	
	UpgradePopup popup;
	public double speedUpgrade = 0;
	public int strengthUpgrade = 0;
	public int pushingUpgrade = 0;

	public int getPlayerControllingPlanet() {
		if (player1Control == 10) {
			return 1;
		} else if (player2Control == 10) {
			return 2;
		} else {
			return 0;
		}
	}

	public void addPlayer1Control(int value) {
		if (player2Control > 0) {
			player2Control -= value;
		} else {
			if (player1Control < 10) // 10 -max
			{
				player1Control += value;
				if (player1Control > 10) {
					player1Control = 10;
				}
			}
		}
	}

	public void addPlayer2Control(int value) {
		if (player1Control > 0) {
			player1Control -= value;
		} else {
			if (player2Control < 10) // 10 -max
			{
				player2Control += value;
				if (player2Control > 10) {
					player2Control = 10;
				}
			}
		}
	}

	public Planet(Point p, int player, double speedUpgrade,
			int strengthUpgrade, int pushingUpgrade, int id) {
		if (player == 1) {
			this.addPlayer1Control(10);
		}
		if (player == 2) {
			this.addPlayer2Control(10);
		}

		this.setSpeedUpgrade(speedUpgrade);
		this.strengthUpgrade = strengthUpgrade;
		this.pushingUpgrade = pushingUpgrade;

		this.position = p;

		this.id = id;

		popup = new UpgradePopup(this);
	}

	public void upgrade() {
		spawnTick = UPGRADE_TIME;
	}

	public void AI(int index) {
		if (!dead) {
			int areaAroundPlanet = (int) (SIZE / 1.75);
			int effectRadius = (int) (SIZE / 2.35);

			Boolean player1unitsLocated = false;
			Boolean player2unitsLocated = false;

			for (int i = 0; i < MainClass.units.size(); i++) {
				if ((player1unitsLocated == false && MainClass.units.get(i).player == 1)
						|| (player2unitsLocated == false && MainClass.units
								.get(i).player == 2)) {
					if (MainClass.units.get(i).position.x > position.x
							- areaAroundPlanet
							&& MainClass.units.get(i).position.x < position.x
									+ areaAroundPlanet
							&& MainClass.units.get(i).position.y > position.y
									- areaAroundPlanet
							&& MainClass.units.get(i).position.y < position.y
									+ areaAroundPlanet) {
						if (MainClass.units.get(i).player == 1) {
							player1unitsLocated = true;
						} else if (MainClass.units.get(i).player == 2) {
							player2unitsLocated = true;
						}
					}
				}
			}

			if (!(player1unitsLocated && player2unitsLocated)) {
				for (int i = 0; i < MainClass.units.size(); i++) {
					if ((player1unitsLocated == true && MainClass.units.get(i).player == 1)
							|| (player2unitsLocated == true && MainClass.units
									.get(i).player == 2)) {
						if (MainClass.units.get(i).position.x > position.x
								- effectRadius
								&& MainClass.units.get(i).position.x < position.x
										+ effectRadius
								&& MainClass.units.get(i).position.y > position.y
										- effectRadius
								&& MainClass.units.get(i).position.y < position.y
										+ effectRadius) {
							if (player1unitsLocated && player1Control < 10) {
								addPlayer1Control(MainClass.units.get(i).pushingPower);
								MainClass.planets
										.get(MainClass.units.get(i).planetParentId).unitsSpawned--;
								MainClass.explosions.add(new Explosion(
										MainClass.units.get(i).position.x,
										MainClass.units.get(i).position.y, 1));

								MainClass.units.remove((int) i);
							}
							if (player2unitsLocated && player2Control < 10) {
								addPlayer2Control(MainClass.units.get(i).pushingPower);
								MainClass.planets
										.get(MainClass.units.get(i).planetParentId).unitsSpawned--;
								MainClass.explosions.add(new Explosion(
										MainClass.units.get(i).position.x,
										MainClass.units.get(i).position.y, 2));

								MainClass.units.remove((int) i);
							}
						}
					}
				}
			}

			if (unitsSpawned != SPAWN_MAX || spawnTick > SPAWN_TIME) {
				if (player1Control == 10 || player2Control == 10) {
					spawnTick--;
					double angle = position.getAngleToAPoint(new Point(
							MainClass.mapSizeX / 2, MainClass.mapSizeY / 2));

					if (position.hasSameLocation(new Point(
							MainClass.mapSizeX / 2, MainClass.mapSizeY / 2))) {
						angle = 270;
					}

					int aiBonus = 0;
					if(getPlayerControllingPlanet() == 2 //&& System.currentTimeMillis() - MainClass.gameStartTime > 80000
							)
					{
						aiBonus++;
					}
					
					if (spawnTick == 0) {
						MainClass.units.add(new Unit((int) (position.x - SIZE
								/ 2 * (Math.cos(Math.toRadians(angle)))),
								(int) (position.y - SIZE / 2
										* (Math.sin(Math.toRadians(angle)))),
								this.getPlayerControllingPlanet(), index,
								getSpeedUpgrade(), strengthUpgrade + aiBonus,
								pushingUpgrade));
						MainClass.units.getLast().angle = angle + 90;
						MainClass.units.getLast().destination = this.position;
						unitsSpawned++;
						spawnTick = SPAWN_TIME;
					}
				}
			}

			if (spawnTick == SPAWN_TIME + 1) {
				for (int i = 0; i < MainClass.units.size(); i++) {
					if (MainClass.units.get(i).planetParentId == index) {
						MainClass.units.get(i).strength = Unit.initStr
								+ strengthUpgrade;
						MainClass.units.get(i).SPEED = Unit.initSpeed
								+ getSpeedUpgrade();
						MainClass.units.get(i).pushingPower = Unit.initPushing
								+ pushingUpgrade;
					}
				}
			}

			if (spawnTick == UPGRADE_TIME - 1) {
				for (int i = 0; i < MainClass.units.size(); i++) {
					if (MainClass.units.get(i).planetParentId == index) {
						MainClass.units.get(i).SPEED = 0;
					}
				}
			}
		}
	}

	public void draw(Graphics2D g) {
		if (!dead) {
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
					(0.4f + (25 - unitsSpawned) * 0.024f)));

			if (player1Control == 10) {
				g.drawImage(MainClass.planetBitmapPlayer1,
						(int) (position.x - SIZE / 2),
						(int) (position.y - SIZE / 2),
						(int) (position.x + SIZE / 2),
						(int) (position.y + SIZE / 2), 0, 0, SIZE, SIZE, null);
			} else if (player2Control == 10) {
				g.drawImage(MainClass.planetBitmapPlayer2,
						(int) (position.x - SIZE / 2),
						(int) (position.y - SIZE / 2),
						(int) (position.x + SIZE / 2),
						(int) (position.y + SIZE / 2), 0, 0, SIZE, SIZE, null);
			} else {

				g.drawImage(MainClass.planetBitmapNeutral,
						(int) (position.x - SIZE / 2),
						(int) (position.y - SIZE / 2),
						(int) (position.x + SIZE / 2),
						(int) (position.y + SIZE / 2), 0, 0, SIZE, SIZE, null);

				if (player1Control > 0) {
					g.setColor(new Color(0x969696));
					g.fillRect((int) (position.x - SIZE / 3),
							(int) (position.y - SIZE / 2),
							(int) (2 * SIZE / 3 / 10 * (10)), 5);

					g.setColor(new Color(0x238cab));
					g.fillRect((int) (position.x - SIZE / 3),
							(int) (position.y - SIZE / 2),
							(int) (2 * SIZE / 3 / 10 * (player1Control)), 5);
				}

				if (player2Control > 0) {
					g.setColor(new Color(0x969696));
					g.fillRect((int) (position.x - SIZE / 3),
							(int) (position.y - SIZE / 2),
							(int) (2 * SIZE / 3 / 10 * (10)), 5);

					g.setColor(new Color(0xfb7a7f));
					g.fillRect((int) (position.x - SIZE / 3),
							(int) (position.y - SIZE / 2),
							(int) (2 * SIZE / 3 / 10 * (player2Control)), 5);
				}

			}
			MainClass.drawCalls++;
			if (unitsSpawned == SPAWN_MAX) {
				g.setComposite(AlphaComposite.getInstance(
						AlphaComposite.SRC_OVER, 0.4f));
				MainClass.drawCalls++;
				g.drawImage(MainClass.planetBitmapNoEnergy,
						(int) (position.x - SIZE / 2),
						(int) (position.y - SIZE / 2),
						(int) (position.x + SIZE / 2),
						(int) (position.y + SIZE / 2), 0, 0, SIZE, SIZE, null);
			}

			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
					1f));

			if (spawnTick > SPAWN_TIME) {

				g.setColor(new Color(0x969696));
				g.fillRect((int) (position.x - SIZE / 3),
						(int) (position.y - SIZE / 1.75),
						(int) (2 * SIZE / 3 / 10 * (10)), 5);
				g.setColor(new Color(0xFFFFFF));
				g.fillRect(
						(int) (position.x - SIZE / 3),
						(int) (position.y - SIZE / 1.75),
						(int) (2 * SIZE / 3.0 / (UPGRADE_TIME - SPAWN_TIME) * (spawnTick - SPAWN_TIME)),
						5);
			}

			popup.draw(g);
		}
	}

	public double getSpeedUpgrade() {
		return speedUpgrade;
	}

	public void setSpeedUpgrade(double speedUpgrade) {
		this.speedUpgrade = speedUpgrade;
	}
}
