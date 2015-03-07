/**
 * Popup that allows to choose an upgrade
 * */

package fireflies.the.game;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;

public class UpgradePopup {

	Planet parent;
	int sizeX = (int) (88 * MainClass.screenScaling);
	int sizeY = (int) (30* MainClass.screenScaling);

	static int buttonSize = (int) (28 * MainClass.screenScaling);

	int button1X, button2X, button3X, button1Y, button2Y, button3Y;

	Boolean visible = false;

	public Boolean actionPerformed(int x, int y) {
		if (x > button1X && x < button1X + buttonSize && y > button1Y
				&& y < button1Y + buttonSize) {
			if ((parent.getSpeedUpgrade() / Unit.speedUpStep) < 2) {// button1
				parent.setSpeedUpgrade(parent.getSpeedUpgrade() + Unit.speedUpStep);
				parent.upgrade();
				visible = false;
				return true;
			}
		}
		if (x > button2X && x < button2X + buttonSize && y > button2Y
				&& y < button2Y + buttonSize) {
			if (parent.strengthUpgrade < 2) {// button2
				parent.strengthUpgrade += 1;
				parent.upgrade();
				visible = false;
				return true;
			}
		}
		if (x > button3X && x < button3X + buttonSize && y > button3Y
				&& y < button3Y + buttonSize) {
			if (parent.pushingUpgrade < 2) { // button3
				parent.pushingUpgrade += 1;
				parent.upgrade();
				visible = false;
				return true;
			}
		}
		return false;
	}

	public UpgradePopup(Planet parent) {
		this.parent = parent;

		button1X = (int) parent.position.x - sizeX / 2 + 1;
		button1Y = (int) parent.position.y - sizeY;
		button2X = (int) parent.position.x - sizeX / 2 + 2 + buttonSize;
		button2Y = (int) parent.position.y - sizeY;
		button3X = (int) parent.position.x - sizeX / 2 + 3 + buttonSize * 2;
		button3Y = (int) parent.position.y - sizeY;
	}

	public void draw(Graphics2D g) {
		if (visible) {
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
					0.5f));
			MainClass.drawCalls += 7;

			g.setColor(new Color(0xDDDDDD));

			g.fillRect((int) parent.position.x - sizeX / 2,
					(int) parent.position.y - sizeY, (int) sizeX, (int) sizeY);

			g.setFont(MainClass.smallFont);

			if ((parent.getSpeedUpgrade() / Unit.speedUpStep) < 2) {
				g.drawImage(MainClass.upgrades[0], button1X, button1Y, button1X
						+ buttonSize, button1Y + buttonSize, 0, 0, buttonSize,
						buttonSize, null);
				g.fillRect(button1X, button1Y, (int) buttonSize,
						(int) buttonSize);
				g.drawString("+" + (int) (parent.getSpeedUpgrade() / 0.7f),
						button1X, button1Y);
			}

			if (parent.strengthUpgrade < 2) {
				g.drawImage(MainClass.upgrades[1], button2X, button2Y, button2X
						+ buttonSize, button2Y + buttonSize, 0, 0, buttonSize,
						buttonSize, null);
				g.fillRect(button2X, button2Y, (int) buttonSize,
						(int) buttonSize);
				g.drawString("+" + parent.strengthUpgrade, button2X, button2Y);
			}

			if (parent.pushingUpgrade < 2) {
				g.drawImage(MainClass.upgrades[2], button3X, button3Y, button3X
						+ buttonSize, button3Y + buttonSize, 0, 0, buttonSize,
						buttonSize, null);
				g.fillRect(button3X, button3Y, (int) buttonSize,
						(int) buttonSize);
				g.drawString("+" + parent.pushingUpgrade, button3X, button3Y);
			}

			g.setColor(new Color(0xFFFFFF));

			g.setFont(MainClass.smallFont);

			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
					1f));
		}
	}

}
