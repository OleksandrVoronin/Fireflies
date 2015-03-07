/**
 * General particle class
 * Something that appears on the screen and disappears after several ticks followed by some animation
 * */

package fireflies.the.game.particles;

import java.awt.Color;
import java.awt.Graphics2D;

import fireflies.the.game.MainClass;
import fireflies.the.game.utils.Point;

public class Particle {

	Point position;

	int lifeLength = 20;
	int lifeCounter = 0;

	public Particle(double x, double y, int frames) {
		this.position = new Point((int) x, (int) y);
		lifeLength = frames;
	}

	public void draw(Graphics2D g) {
		g.setColor(new Color(0xEEEEEE));
		MainClass.drawCalls++;
		g.drawOval((int) position.x, (int) position.y, 20, 20);

		lifeCounter++;
		if (lifeCounter >= lifeLength) {
			MainClass.explosions.removeFirst();
		}
	}
}
