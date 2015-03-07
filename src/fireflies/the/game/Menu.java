/**
 * Menu object to display level names on the screen and control cursor
 * */

package fireflies.the.game;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

public class Menu {

	String[] points = { "Information", "Level 1 (Easy)", "Level 2 (Medium)",
			"Level 3 (Impossible)" };
	String[] controls = { "Rules:", "Annihilate your enemy (red).",
			"Yea, that simple.", "", "Controls: ",
			"WASD to move the screen around", "Mouse wheel to zoom in/out",
			"Left click the planet to choose upgrades",
			"(speed, strength or pushing power)",
			"Left click and drag to select your units",
			"Right click to move your units" };
	public int cursor;
	public Boolean show = true;
	public Boolean controlsScreen = false;

	public Menu() {

	}

	public void draw(Graphics2D g) {
		g.setFont(MainClass.hugeFont);

		g.setComposite(AlphaComposite
				.getInstance(AlphaComposite.SRC_OVER, 0.4f));
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, MainClass.SCREEN_SIZE_X, MainClass.SCREEN_SIZE_Y);
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

		FontMetrics metrics = g.getFontMetrics(g.getFont());
		int height = metrics.getHeight();
		int total_height = (int) (metrics.getHeight() * points.length * 1.5);
		if (controlsScreen)
			total_height = (int) (metrics.getHeight() * controls.length * 1.5);

		g.setColor(Color.WHITE);

		if (controlsScreen) {
			g.drawString("ESC back", 10, 710 * MainClass.screenScaling);
			g.drawString("v1.0", 650 * MainClass.screenScalingX,
					710 * MainClass.screenScaling);

			for (int i = 0; i < controls.length; i++) {
				int weight = metrics.stringWidth(controls[i]);
				
				if(i == 0 || i == 4)
				{
					g.setColor(new Color(0x888888));
					drawPerspective(g, controls[i], MainClass.SCREEN_SIZE_X / 2 - weight
						/ 2, (int) (MainClass.SCREEN_SIZE_Y / 2 - total_height
						/ 2 + (height * 1.5) * i));
				}
					g.setColor(Color.WHITE);
				g.drawString(controls[i], MainClass.SCREEN_SIZE_X / 2 - weight
						/ 2, (int) (MainClass.SCREEN_SIZE_Y / 2 - total_height
						/ 2 + (height * 1.5) * i));

			}
		} else {
			g.drawString("Arrow keys - move cursor", 10,
					710 * MainClass.screenScaling);
			// g.drawString("ENTER - select", 10, 680);

			for (int i = 0; i < points.length; i++) {
				if (cursor == i) {
					int weight = metrics.stringWidth(points[i].toUpperCase());
					g.setColor(new Color(0x888800));
					drawPerspective(g, points[i].toUpperCase(), MainClass.SCREEN_SIZE_X / 2
							- weight / 2, (int) (MainClass.SCREEN_SIZE_Y / 2
							- total_height / 2 + (height * 1.5) * i));
					g.setColor(Color.yellow);
					g.drawString(points[i].toUpperCase(),
							MainClass.SCREEN_SIZE_X / 2 - weight / 2,
							(int) (MainClass.SCREEN_SIZE_Y / 2 - total_height
									/ 2 + (height * 1.5) * i));

				} else {
					int weight = metrics.stringWidth(points[i]);
					
					g.setColor(Color.WHITE);
					g.drawString(points[i], MainClass.SCREEN_SIZE_X / 2
							- weight / 2, (int) (MainClass.SCREEN_SIZE_Y / 2
							- total_height / 2 + (height * 1.5) * i));
				}
			}
			

			g.setFont(MainClass.titleFont);
			g.setColor(new Color(0x888888));
			drawPerspective(g, "THE FIREFLIES", (int) (160 * MainClass.screenScalingX),
					(int) (84 * MainClass.screenScaling));
			
			g.setColor(Color.white);
			g.drawString("THE FIREFLIES", 160 * MainClass.screenScalingX,
					84 * MainClass.screenScaling);
		}
	}

	public void drawPerspective(Graphics2D g, String text, int x, int y) {
		for (int offset = 0; offset < 5; offset++) {
			
			g.drawString(text, x - MainClass.SCREEN_SIZE_Y / 360 * offset, y
					- MainClass.SCREEN_SIZE_Y / 1280.0f * offset);
		}
	}

}
