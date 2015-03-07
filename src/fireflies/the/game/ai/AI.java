/**
 * Artificial Intelligence class
 * In my game, there's no central AI, each planet is (mostly) 
 * independent in all decisions and controls it's units separately. 
 * The planet AI is called planet governor and it decides whether to 
 * play defensive (expand/upgrade) or offensive (attack/contest planets).
 * This class only stores all the governors and makes them run
 * */

package fireflies.the.game.ai;

import java.util.LinkedList;

import fireflies.the.game.MainClass;

public class AI {

	public int player;
	public LinkedList<PlanetGovernor> governors = new LinkedList<PlanetGovernor>();

	public AI(int player, int AIid) {
		this.player = player;

		if (governors.size() == 0) {
			for (int i = 0; i < MainClass.planets.size(); i++) {
				governors.add(new PlanetGovernor(player, i, AIid));
			}
		}
	}

	static public int getNumberOfPlanetsControlled() {
		int controlled = 0;
		for (int i = 0; i < MainClass.planets.size(); i++) {
			if (MainClass.planets.get(i).getPlayerControllingPlanet() != 0) {
				controlled++;
			}
		}
		return controlled;
	}

	/**
	 * Run all the governors
	 * */
	public void AICycle() {
		for (int i = 0; i < governors.size(); i++) {
			governors.get(i).doStuff();
		}
	}

}
