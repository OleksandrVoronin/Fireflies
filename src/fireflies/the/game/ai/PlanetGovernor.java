/**
 * AI that controls the single planet and all its units.
 * */

package fireflies.the.game.ai;

import java.util.LinkedList;
import java.util.Random;

import fireflies.the.game.MainClass;
import fireflies.the.game.Planet;
import fireflies.the.game.Unit;

public class PlanetGovernor {

	public int AIid;

	public int player;
	public int index;

	public Integer expansionPlanetIndex = null;
	public Integer attackPlanetIndex = null;

	public PlanetGovernor(int player, int index, int AIid) {
		this.player = player;
		this.index = index;
		this.AIid = AIid;
	}

	/**
	 * Chooses the best planet to expand and sends all the units there
	 * */
	public void expansion() {
		if (expansionPlanetIndex == null
				|| MainClass.planets.get(expansionPlanetIndex)
						.getPlayerControllingPlanet() != 0) {
			expansionPlanetIndex = getPlanetForExpansion();
		}

		if (expansionPlanetIndex != null) {
			int pointsOfControlNeeded = 0;

			if (player == 1) {
				pointsOfControlNeeded = 10
						- MainClass.planets.get(expansionPlanetIndex).player1Control
						+ MainClass.planets.get(expansionPlanetIndex).player2Control;
			}
			if (player == 2) {
				pointsOfControlNeeded = 10
						- MainClass.planets.get(expansionPlanetIndex).player2Control
						+ MainClass.planets.get(expansionPlanetIndex).player1Control;
			}

			for (int i = 0; i < MainClass.units.size(); i++) {
				if (MainClass.units.get(i).player == player
						&& MainClass.units.get(i).planetParentId == index) {
					pointsOfControlNeeded -= MainClass.units.get(i).pushingPower;
				}
			}
			// System.out.println("PoCN " + pointsOfControlNeeded);
			if (pointsOfControlNeeded <= new Random().nextInt(4)) {
				for (int i = 0; i < MainClass.units.size(); i++) {
					if (MainClass.units.get(i).player == player
							&& MainClass.units.get(i).planetParentId == index) {
						if (MainClass.units.get(i).activity != Unit.MOVE) {
							MainClass.units
									.get(i)
									.setDestination(
											MainClass.planets
													.get(expansionPlanetIndex).position);

							if (!(MainClass.units.get(i).activity == Unit.MOVE || MainClass.units
									.get(i).activity == Unit.IDLE))
								MainClass.units.get(i).activity = Unit.GOING_TO_MOVE;
							else
								MainClass.units.get(i).activity = Unit.MOVE;

							MainClass.units.get(i).waveCounter = 0;
							MainClass.units.get(i).waveSign = MainClass.units
									.get(i).WAVE_SIGN_ORIGINAL;
						}
					}
				}
			}
		}
	}

	/**
	 * Chooses the new target to attack and sends all units there
	 * */
	public void offense() {
		if (attackPlanetIndex == null
				|| MainClass.planets.get(attackPlanetIndex).player1Control == 10
				|| MainClass.planets.get(attackPlanetIndex).player2Control == 10) {

			for (int i = 0; i < MainClass.aiPlayer.get(AIid).governors.size(); i++) {
				if (MainClass.aiPlayer.get(AIid).governors.get(i).attackPlanetIndex != null
						&& MainClass.planets
								.get(MainClass.aiPlayer.get(AIid).governors
										.get(i).attackPlanetIndex)
								.getPlayerControllingPlanet() != player) {
					attackPlanetIndex = MainClass.aiPlayer.get(AIid).governors
							.get(i).attackPlanetIndex;
					i = MainClass.aiPlayer.get(AIid).governors.size();
				}
			}

			if (attackPlanetIndex == null) {

				int newTarget = new Random().nextInt(MainClass.planets.size());
				int tryNumber = 0;
				do {
					newTarget = new Random().nextInt(MainClass.planets.size());
					tryNumber++;
				} while (tryNumber < 5
						&& ((MainClass.planets.get(newTarget)
								.getPlayerControllingPlanet() == player) || MainClass.planets
								.get(newTarget).dead == true));
				if (tryNumber < 5) {
					attackPlanetIndex = newTarget;
				} else {
					attackPlanetIndex = null;
				}
			}
		}

		if (attackPlanetIndex != null) {
			for (int i = 0; i < MainClass.units.size(); i++) {
				if (MainClass.units.get(i).player == player
						&& MainClass.units.get(i).planetParentId == index) {
					if (MainClass.units.get(i).activity == Unit.CIRCULAR) {
						MainClass.units
								.get(i)
								.setDestination(
										MainClass.planets
												.get(attackPlanetIndex).position);

						if (!(MainClass.units.get(i).activity == Unit.MOVE || MainClass.units
								.get(i).activity == Unit.IDLE))
							MainClass.units.get(i).activity = Unit.GOING_TO_MOVE;
						else
							MainClass.units.get(i).activity = Unit.MOVE;

						MainClass.units.get(i).waveCounter = 0;
						MainClass.units.get(i).waveSign = MainClass.units
								.get(i).WAVE_SIGN_ORIGINAL;
					}
				}
			}
		}
	}

	/**
	 * Chooses a planet to expand to.
	 * It builds a list of all appropriate for expansion planets
	 * and rates them (less pts to control and closer is better)
	 * Returns one with the best rating
	 * */
	public Integer getPlanetForExpansion() {

		LinkedList<Planet> local = new LinkedList<Planet>();

		for (int i = 0; i < MainClass.planets.size(); i++) {
			if (MainClass.planets.get(i).getPlayerControllingPlanet() == 0
					&& MainClass.planets.get(i).dead == false) {
				local.add(MainClass.planets.get(i));
			}
		}

		if (local.size() == 0) {
			return null;
		} else {
			int indexLow = 0;
			int lowest = 99999;

			for (int i = 0; i < local.size(); i++) {

				int pointsOfControlNeeded = 0;

				if (player == 1) {
					pointsOfControlNeeded = 10 - local.get(i).player1Control
							+ local.get(i).player2Control;
				}
				if (player == 2) {
					pointsOfControlNeeded = 10 - local.get(i).player2Control
							+ local.get(i).player1Control;
				}

				int priorityScore = MainClass.planets.get(index).position
						.getDistanceTo(local.get(i).position)
						/ Planet.SIZE
						* 2
						+ pointsOfControlNeeded;

				// System.out.println("PS " + priorityScore);
				if (lowest > priorityScore
						|| (lowest == priorityScore && new Random().nextInt(2) == 0)) {
					indexLow = i;
					lowest = priorityScore;
				}

			}
			// System.out.println("\n iL" + lowest + "\n");

			return local.get(indexLow).id;
		}
	}

	public void upgradePushing() {
		MainClass.planets.get(index).pushingUpgrade += 1;
		MainClass.planets.get(index).upgrade();
	}

	public void upgradeStrength() {
		MainClass.planets.get(index).strengthUpgrade += 1;
		MainClass.planets.get(index).upgrade();
	}

	public void upgradeSpeed() {
		MainClass.planets.get(index).speedUpgrade += Unit.speedUpStep;
		MainClass.planets.get(index).upgrade();
	}

	/**
	 * Checks if none of the units are moving right now
	 * */
	static public Boolean noOneIsMoving(int index) {
		for (int i = 0; i < MainClass.units.size(); i++) {
			if (MainClass.units.get(i).planetParentId == index
					&& MainClass.units.get(i).activity == Unit.MOVE) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Governor decides what to do
	 * */
	public void doStuff() {
		/**
		 * Dont go to the planet that is dead
		 * */
		if (expansionPlanetIndex != null
				&& MainClass.planets.get(expansionPlanetIndex).dead) {
			expansionPlanetIndex = null;
		}
		if (attackPlanetIndex != null
				&& MainClass.planets.get(attackPlanetIndex).dead) {
			attackPlanetIndex = null;
		}

		// System.out.println("Governor index " + index + " does stuff");

		/**
		 * Checks if upgrading is appropriate (no upgrade is 
		 * currently running, units are idle (not going to attack 
		 * or expand) and player has advantage);
		 * 
		 * Then it will upgrade something with different chances
		 * */
		if (MainClass.unitsCount[player-1] >= MainClass.unitsCount[getOppositePlayer()-1] && noOneIsMoving(index)
				&& MainClass.planets.get(index).spawnTick <= Planet.SPAWN_TIME)
			if (AI.getNumberOfPlanetsControlled() <= MainClass.aiPlayer.get(0).governors
					.size() / 2
					&& MainClass.planets.get(index).pushingUpgrade == 0
					&& new Random().nextInt(512) == 0
					&& MainClass.planets.get(index)
							.getPlayerControllingPlanet() == player) {
				upgradePushing();
			} else if (AI.getNumberOfPlanetsControlled() >= MainClass.aiPlayer
					.get(0).governors.size() / 2
					&& MainClass.planets.get(index).strengthUpgrade < 2
					&& new Random().nextInt(2048) == 0
					&& MainClass.planets.get(index)
							.getPlayerControllingPlanet() == player) {
				upgradeStrength();
			} else if (AI.getNumberOfPlanetsControlled() > MainClass.aiPlayer
					.get(0).governors.size() / 2
					&& MainClass.planets.get(index).speedUpgrade < 2
					&& new Random().nextInt(4048) == 0
					&& MainClass.planets.get(index)
							.getPlayerControllingPlanet() == player) {
				upgradeSpeed();
			}

		/**
		 * If planet spawned max units - attack, otherwise try to expand
		 * */
		if (MainClass.planets.get(index).unitsSpawned == Planet.SPAWN_MAX
				- new Random().nextInt(2)) {
			offense();
			expansionPlanetIndex = null;
		} else {
			expansion();
			attackPlanetIndex = null;
		}
	}

	public int getOppositePlayer() {
		if (player == 1) {
			return 2;
		} else {
			return 1;
		}
	}
}
