/**
 * Extends Particle
 * Mouse animation when player right clicks
 * Goes transparent through time and disappears
 * */

package fireflies.the.game.particles;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;

import fireflies.the.game.MainClass;

public class MousePointerAnimation extends Particle {

	final static int LIFE_LENGTH = 25;

	public MousePointerAnimation(double x, double y) {
		super(x, y, LIFE_LENGTH);
	}

	public void draw(Graphics2D g) {
		lifeCounter++;
		if (lifeCounter >= lifeLength) {
			MainClass.mousePointers.removeFirst();
		} else {

			g.setColor(new Color(0xEEEEEE));
			MainClass.drawCalls++;

			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
					(LIFE_LENGTH - lifeCounter) * (1.0f / LIFE_LENGTH)));
			g.drawImage(MainClass.cursor,
					(int) position.x - MainClass.cursor.getWidth() / 2,
					(int) position.y - MainClass.cursor.getWidth() / 2,
					(int) position.x + MainClass.cursor.getWidth() / 2,
					(int) position.y + MainClass.cursor.getWidth() / 2, 0, 0,
					MainClass.cursor.getWidth(), MainClass.cursor.getHeight(),
					null);
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
					1f));
		}

	}

}
