/**
 * Extends Particle
 * Explosion that signals unit's death
 * Goes transparent through time and disappears
 * */

package fireflies.the.game.particles;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;

import fireflies.the.game.MainClass;

public class Explosion extends Particle {

	int player;
	int SIZE = MainClass.SCREEN_SIZE_Y / 36;

	public Explosion(double x, double y, int player) {
		super(x, y, 20);
		this.player = player;
	}

	public void draw(Graphics2D g) {
			lifeCounter++;
			if (lifeCounter >= lifeLength) {
				MainClass.explosions.removeFirst();
			} else {

				if (player == 2)
					g.setColor(new Color(0xfb7a7f));
				else
					g.setColor(new Color(0x238cab));
				MainClass.drawCalls++;

				g.setComposite(AlphaComposite.getInstance(
						AlphaComposite.SRC_OVER, (1f - lifeCounter * 0.05f)));
				g.fillRect((int) position.x - SIZE, (int) position.y - SIZE,
						SIZE, SIZE);
				g.setComposite(AlphaComposite.getInstance(
						AlphaComposite.SRC_OVER, 1f));
			}

		
	}

}
