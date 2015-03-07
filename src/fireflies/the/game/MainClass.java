/**
 * Game applet
 * Does pretty much everything :s
 *
 * */

package fireflies.the.game;

import java.applet.Applet;
import java.awt.AlphaComposite;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

import fireflies.the.game.ai.AI;
import fireflies.the.game.particles.MousePointerAnimation;
import fireflies.the.game.particles.Particle;
import fireflies.the.game.utils.MapGen;
import fireflies.the.game.utils.Point;

public class MainClass extends Applet implements MouseListener,
		MouseMotionListener, Runnable, KeyListener, MouseWheelListener {

	private static final long serialVersionUID = 1L;

	private boolean[] key = new boolean[32767]; // stores booleans that
												// represent
	// whether key with this id
	// pressed or not

	static Font titleFont;
	static Font hugeFont;
	static Font smallFont;

	static public int[] unitsCount = new int[2];

	public static long gameStartTime = 0;

	public Boolean isPainting = false; // if repaint() is running
	public static int SCREEN_SIZE_X = 720;
	public static int SCREEN_SIZE_Y = 720;
	public static float screenScaling = MainClass.SCREEN_SIZE_Y/720.0f;
	public static float screenScalingX = MainClass.SCREEN_SIZE_X/720.0f;
	public Boolean initialized = false;

	public Point selectStart = null;
	public Point selectEnd = null;

	public static final double SCALE_SPEED = 0.05;
	public static int scaleNotches = 20;

	static Point mouseCurrentPos = new Point(SCREEN_SIZE_X / 2, SCREEN_SIZE_Y / 2);

	final double SCROLL_SPEED = 4.2f;

	public static int mapSizeX = 720;
	public static int mapSizeY = 720;

	public static double screenOffsetX = mapSizeX / 2 - SCREEN_SIZE_X / 2;
	public static double screenOffsetY = mapSizeY / 2 - SCREEN_SIZE_Y / 2;

	public int fps = 0;
	public int frames = 0;
	public long framesStart = 0;

	public Boolean gameIsOn = false;
	public Integer winner = null;

	public static int drawCalls = 0;

	static public LinkedList<AI> aiPlayer = new LinkedList<AI>();

	public int logicThreadUpdateTime = 0;

	public static BufferedImage[] upgrades = new BufferedImage[3];

	public static BufferedImage unitsBitmap;

	public static BufferedImage[] unitRebornBitmap = new BufferedImage[2];

	public static BufferedImage planetBitmapPlayer1;
	public static BufferedImage planetBitmapPlayer2;
	public static BufferedImage planetBitmapNeutral;
	public static BufferedImage planetBitmapNoEnergy;

	public static BufferedImage cursor;

	BufferedImage background;
	BufferedImage backgroundOverlay;

	// Unit p = new Unit(200, 200);
	static public LinkedList<Unit> units = new LinkedList<Unit>();
	static public LinkedList<Planet> planets = new LinkedList<Planet>();
	static public LinkedList<Particle> explosions = new LinkedList<Particle>();
	static public LinkedList<Particle> mousePointers = new LinkedList<Particle>();

	public static Blackout blackout;
	
	public Boolean cannotControlPlayer2 = true;

	public Menu menu = new Menu();

	public int level = 0;
	public String[] levels = { "res/map1.txt", "res/map2.txt", "res/map3.txt" };

	public Font loadFont(float size) {
		// Returned font is of pt size 1
		Font font = null;
		try {
			font = Font.createFont(Font.TRUETYPE_FONT, MainClass.class
					.getClassLoader().getResourceAsStream("res/pixelated.ttf"));
		} catch (FontFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return font.deriveFont(size);

	}

	public MainClass() {
		start();
	}

	public void start() {
		// setup
		if (initialized == false) {
			initialized = true;

			titleFont = loadFont(90f*screenScaling);
			hugeFont = loadFont(60f*screenScaling);
			smallFont = loadFont(30f*screenScaling);

			
			addMouseWheelListener(this);
			addKeyListener(this);
			addMouseListener(this);
			addMouseMotionListener(this);

			loadImagesIntoMemory();

			setSize(SCREEN_SIZE_X, SCREEN_SIZE_Y);

		}
	}

	public void startGame() {
		planets.clear();
		units.clear();

		MapGen mg = new MapGen();
		mg.generateMap(levels[level]);

		aiPlayer.clear();
		 aiPlayer.add(new AI(2, 0));
		 //aiPlayer.add(new AI(1, 1));

		blackout = new Blackout();

		gameStartTime = System.currentTimeMillis();
		for (int i = 0; i < planets.size(); i++) {
			if (planets.get(i).getPlayerControllingPlanet() == 2) {
				planets.get(i).strengthUpgrade = 99;
			}
		}

		new Thread(this).start();
	}

	// New frame canvas
	private Image offScreenImage;
	private Dimension offScreenSize;
	private Graphics offScreenGraphics;

	@SuppressWarnings("deprecation")
	public final synchronized void update(Graphics g2d) {
		Dimension d = size();
		if ((offScreenImage == null) || (d.width != offScreenSize.width)
				|| (d.height != offScreenSize.height)) {
			offScreenImage = createImage(d.width, d.height);
			offScreenSize = d;
			offScreenGraphics = offScreenImage.getGraphics();
		}
		// painting a canvas
		offScreenGraphics.clearRect(0, 0, d.width, d.height);
		paint(offScreenGraphics);
		g2d.drawImage(offScreenImage, 0, 0, null);
	}

	@Override
	public void paint(Graphics g) {

		drawCalls = 0;

		Graphics2D g2d = (Graphics2D) g;

		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		g2d.setClip(new Rectangle(-Planet.SIZE, -Planet.SIZE, SCREEN_SIZE_X + Planet.SIZE, SCREEN_SIZE_Y + Planet.SIZE));
		
		g2d.setColor(new Color(0, 0, 0));
		g2d.fillRect(0, 0, SCREEN_SIZE_X, SCREEN_SIZE_Y);

		drawCalls++;
		g2d.drawImage(background, 0, 0, SCREEN_SIZE_X, SCREEN_SIZE_X, 0, 0,
				SCREEN_SIZE_X, SCREEN_SIZE_X, null);

		int scrOffXinit = (int) -(((MainClass.mapSizeX / 2 - MainClass.SCREEN_SIZE_X / 2) - screenOffsetX) / 20);
		int scrOffYinit = (int) -(((MainClass.mapSizeY / 2 - MainClass.SCREEN_SIZE_X / 2) - screenOffsetY) / 20);
		g2d.drawImage(backgroundOverlay, -scrOffXinit, -scrOffYinit,
				SCREEN_SIZE_X - scrOffXinit, SCREEN_SIZE_X - scrOffYinit, 0, 0,
				SCREEN_SIZE_X, SCREEN_SIZE_X, null);

		double transX = screenOffsetX;
		double transY = screenOffsetY;

		// SCROLL FRAME
		double scaleFactor = scaleNotches * SCALE_SPEED;
		g2d.translate(mapSizeX * (1 - scaleFactor) / 2, mapSizeY
				* (1 - scaleFactor) / 2);
		g2d.translate(-transX, -transY);
		g2d.scale(scaleFactor, scaleFactor);

		for (int i = 0; i < units.size(); i++) {
			try {
				units.get(i).draw(g2d);
			} catch (java.lang.NullPointerException e) {
			}
		}
		

		for (int i = 0; i < planets.size(); i++) {
			planets.get(i).draw(g2d);
		}

		for (int i = 0; i < explosions.size(); i++) {
			explosions.get(i).draw(g2d);
		}
		if (blackout != null)
			blackout.draw(g2d);

		for (int i = 0; i < mousePointers.size(); i++) {
			mousePointers.get(i).draw(g2d);
		}

		// SCROLL FRAME BACK
		g2d.scale(1.0f / scaleFactor, 1.0f / scaleFactor);
		g2d.translate(-mapSizeX * (1 - scaleFactor) / 2, -mapSizeY
				* (1 - scaleFactor) / 2);
		g2d.translate(transX, transY);

		if (selectStart != null & selectEnd != null) {

			Point selectStartInst = new Point(selectStart.x, selectStart.y);
			Point selectEndInst = new Point(selectEnd.x, selectEnd.y);

			double x = selectStartInst.x;
			if (selectEndInst.x < x) {
				x = selectEndInst.x;
			}

			double y = selectStartInst.y;
			if (selectEndInst.y < y) {
				y = (int) selectEndInst.y;
			}

			g2d.setColor(new Color(0x47fb00));
			drawCalls++;
			g2d.drawRect((int) x, (int) y,
					(int) Math.abs(selectStartInst.x - selectEndInst.x),
					(int) Math.abs(selectStartInst.y - selectEndInst.y));

			// SCROLLING SELECT FIX
			{
				x += screenOffsetX;
				y += screenOffsetY;
				x -= mapSizeX * (1 - scaleFactor) / 2;
				y -= mapSizeY * (1 - scaleFactor) / 2;

				y /= (scaleFactor);
				x /= (scaleFactor);
			}

			for (int i = 0; i < units.size(); i++) {
				if (units.get(i).position.x > x
						&& units.get(i).position.x < x
								+ (int) Math.abs(selectStartInst.x
										- selectEndInst.x) / scaleFactor
						&& units.get(i).position.y > y
						&& units.get(i).position.y < y
								+ (int) Math.abs(selectStartInst.y
										- selectEndInst.y) / scaleFactor
						&& (units.get(i).player == 1 && cannotControlPlayer2)) {
					units.get(i).selected = true;
				} else {
					units.get(i).selected = false;
				}
			}
		}

		frames++;
		if (System.currentTimeMillis() - framesStart >= 1000) {
			fps = frames;
			frames = 0;
			framesStart = System.currentTimeMillis();
		}

		g2d.setFont(hugeFont);
		g2d.drawString("loading font", -2000, -2000);

		if (winner != null) {
			g2d.setColor(Color.WHITE);

			if (winner == 0) {
				drawStringCentre(g2d, "DRAW.", 0);
				drawStringCentre(g2d, "Press any key to retry.", 1);
			}
			if (winner == 1) {
				drawStringCentre(g2d, "YOU WIN.", 0);
				if (level == 2)
					drawStringCentre(g2d, "No more levels here. Wtf?", 1);
				else
					drawStringCentre(g2d, "Press any key to continue.", 1);
			}
			if (winner == 2) {
				drawStringCentre(g2d, "YOU LOSE.", 0);
				drawStringCentre(g2d, "Press any key to retry.", 1);
			}
		}

		if (menu.show)
			menu.draw(g2d);

		if (gameIsOn) {
			double barWidth = SCREEN_SIZE_X / 4 * 3;
			int totalUnits = unitsCount[0] + unitsCount[1];
			int red = unitsCount[1];
			int blue = totalUnits - red;

			int y_location = (int) (32*screenScaling);
			int height = (int) (28*screenScaling);

			g2d.setColor(new Color(0xEEEEEE));
			g2d.setComposite(AlphaComposite.getInstance(
					AlphaComposite.SRC_OVER, 0.5f));
			g2d.fillRect((int) (SCREEN_SIZE_X / 2 - barWidth / 2), y_location,
					(int) barWidth, height);

			g2d.setColor(new Color(0xfb7a7f));
			g2d.fillRect((int) (SCREEN_SIZE_X / 2 - barWidth / 2 + barWidth
					/ totalUnits * blue), y_location, (int) (barWidth
					/ totalUnits * red), height);

			g2d.setColor(new Color(0x238cab));
			g2d.fillRect((int) (SCREEN_SIZE_X / 2 - barWidth / 2), y_location,
					(int) (barWidth / totalUnits * blue), height);

			g2d.setComposite(AlphaComposite.getInstance(
					AlphaComposite.SRC_OVER, 1f));

			g2d.setFont(hugeFont);
			g2d.setColor(Color.white);
			g2d.drawString("Blue:", 7*screenScalingX, y_location+height);
			g2d.drawString(": Red", 635*screenScalingX, y_location+height);

			// System.out.println(unitsCount[0] + " " + unitsCount[1]);

		}
		g2d.setFont(smallFont); g2d.setColor(Color.WHITE);
		 //g2d.drawString("FPS: " + fps, 10, 20); 
		/* g2d.drawString("Objects: " +
		 * units.size(), 10, 40); g2d.drawString("Logic thread update time: " +
		 * logicThreadUpdateTime + "ms", 10, 60);
		 * g2d.drawString("Scale factor: " + (scaleNotches * SCALE_SPEED), 10,
		 * 80); g2d.drawString("Translation: " + transX + ":" + transY, 10,
		 * 100); g2d.drawString("Draw calls: " + drawCalls, 10, 120);
		 */
		isPainting = false;
	}

	@Override
	public void run() {
		while (gameIsOn) {
 
			long startTime = System.currentTimeMillis();

			blackout.moveEdges();

			for (int i = 0; i < units.size(); i++) {
				units.get(i).AI(i);
				/*
				 * if(i == 0) { for(int p = 0 ; p <
				 * units.get(i).lastPositions.size(); p++) {
				 * System.out.println("LAST POS: " +
				 * units.get(i).lastPositions.get(p).x + ":" +
				 * units.get(i).lastPositions.get(p).y); } System.out.println(
				 * "_____________________________________________________________________________________________"
				 * ); }
				 */
			}

			for (int i = 0; i < planets.size(); i++) {
				planets.get(i).AI(i);
			}

			for (int i = 0; i < aiPlayer.size(); i++) {
				aiPlayer.get(i).AICycle();
			}

			if (!isPainting) {
				isPainting = true;
				repaint();
			}

			if (key[KeyEvent.VK_S]) {
				if (screenOffsetY + SCROLL_SPEED < mapSizeY / 2 - SCREEN_SIZE_Y
						/ 4)
					screenOffsetY += SCROLL_SPEED;
			}
			if (key[KeyEvent.VK_W]) {
				if (screenOffsetY - SCROLL_SPEED > 0 - SCREEN_SIZE_Y / 4)
					screenOffsetY -= SCROLL_SPEED;
			}
			if (key[KeyEvent.VK_D]) {
				if (screenOffsetX + SCROLL_SPEED < mapSizeX - SCREEN_SIZE_X / 4
						* 3)
					screenOffsetX += SCROLL_SPEED;
			}
			if (key[KeyEvent.VK_A]) {
				if (screenOffsetX - SCROLL_SPEED > 0 - SCREEN_SIZE_X / 4)
					screenOffsetX -= SCROLL_SPEED;
			}

			updateUnitsCounts();

			int redPlanets = 0;
			int bluePlanets = 0;

			for (int i = 0; i < planets.size(); i++) {
				if (planets.get(i).dead == false) {
					if (planets.get(i).getPlayerControllingPlanet() == 1) {
						bluePlanets++;
					}
					if (planets.get(i).getPlayerControllingPlanet() == 2) {
						redPlanets++;
					}
				}
			}

			if (redPlanets == 0 && bluePlanets == 0) {
				System.out.println("DRAW");
				winner = 0;
				gameIsOn = false;
			} else if (redPlanets == 0) {
				System.out.println("BLUE WINS.");
				winner = 1;
				gameIsOn = false;
			} else if (bluePlanets == 0) {
				System.out.println("RED WINS");
				winner = 2;
				gameIsOn = false;
			}

			// loop execution end time
			long now = System.currentTimeMillis();

			if (now - gameStartTime > 40000) {
				for (int i = 0; i < planets.size(); i++) {
					if (planets.get(i).getPlayerControllingPlanet() == 2
							&& planets.get(i).strengthUpgrade == 99) {
						System.out.println("DEBUFFED");
						planets.get(i).strengthUpgrade = 0;
					}
				}

				for (int i = 0; i < units.size(); i++) {
					if (units.get(i).strength > 10) {
						units.get(i).strength = 2;
					}
				}
			}

			// DEBUG INFO
			// System.out.println("Time: " + (now - startTime));
			// System.out.println("Enemies: " + enemies.size());
			// System.out.println("Bullets: " + bullets.size());
			// System.out.println("Angle: " + angle);
			// System.out.println("===========================");

			// if loop was executed in less than 1000/60ms - sleep
			logicThreadUpdateTime = (int) (now - startTime);

			if (logicThreadUpdateTime <= 1000 / 60) {
				try {
					Thread.sleep(1000 / 60 - (logicThreadUpdateTime));
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
	}

	public void updateUnitsCounts() {
		int[] unitsCount = new int[2];

		for (int i = 0; i < units.size(); i++) {
			unitsCount[units.get(i).player - 1]++;
		}

		MainClass.unitsCount = unitsCount;
	}

	public static BufferedImage resize(BufferedImage img, int newW, int newH) {
		int w = img.getWidth();
		int h = img.getHeight();
		BufferedImage dimg = new BufferedImage(newW, newH, img.getType());
		Graphics2D g2d = dimg.createGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		g2d.drawImage(img, 0, 0, newW, newH, 0, 0, w, h, null);
		g2d.dispose();
		return dimg;
	}

	public static BufferedImage toBufferedImage(Image img) {
		if (img instanceof BufferedImage) {
			return (BufferedImage) img;
		}

		// Create a buffered image with transparency
		BufferedImage bimage = new BufferedImage(img.getWidth(null),
				img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

		// Draw the image on to the buffered image
		Graphics2D bGr = bimage.createGraphics();
		bGr.drawImage(img, 0, 0, null);
		bGr.dispose();

		// Return the buffered image
		return bimage;
	}

	public void loadImagesIntoMemory() {

		unitRebornBitmap[0] = resize(toBufferedImage(new ImageIcon(getClass()
				.getResource("/res/firefly1playerReborn.png")).getImage()),
				Unit.SIZE, Unit.SIZE);
		unitRebornBitmap[1] = resize(toBufferedImage(new ImageIcon(getClass()
				.getResource("/res/firefly2playerReborn.png")).getImage()),
				Unit.SIZE, Unit.SIZE);

		unitsBitmap = resize(toBufferedImage(new ImageIcon(getClass()
				.getResource("/res/unitsAtlas.png")).getImage()),
				Unit.SIZE * 6, Unit.SIZE * 2);

		upgrades[0] = resize(toBufferedImage(new ImageIcon(getClass()
				.getResource("/res/speedUp.png")).getImage()), UpgradePopup.buttonSize, UpgradePopup.buttonSize);
		upgrades[1] = resize(toBufferedImage(new ImageIcon(getClass()
				.getResource("/res/strUp.png")).getImage()), UpgradePopup.buttonSize, UpgradePopup.buttonSize);
		upgrades[2] = resize(toBufferedImage(new ImageIcon(getClass()
				.getResource("/res/pushUp.png")).getImage()), UpgradePopup.buttonSize, UpgradePopup.buttonSize);

		background = resize(toBufferedImage(new ImageIcon(getClass()
				.getResource("/res/background.png")).getImage()), SCREEN_SIZE_X,
				SCREEN_SIZE_X);
		backgroundOverlay = resize(toBufferedImage(new ImageIcon(getClass()
				.getResource("/res/backgroundOverlay.png")).getImage()),
				SCREEN_SIZE_X, SCREEN_SIZE_X);

		planetBitmapPlayer1 = resize(toBufferedImage(new ImageIcon(getClass()
				.getResource("/res/planetPlayer1.png")).getImage()),
				Planet.SIZE, Planet.SIZE);
		planetBitmapPlayer2 = resize(toBufferedImage(new ImageIcon(getClass()
				.getResource("/res/planetPlayer2.png")).getImage()),
				Planet.SIZE, Planet.SIZE);
		planetBitmapNeutral = resize(toBufferedImage(new ImageIcon(getClass()
				.getResource("/res/planetNeutral.png")).getImage()),
				Planet.SIZE, Planet.SIZE);
		planetBitmapNoEnergy = resize(toBufferedImage(new ImageIcon(getClass()
				.getResource("/res/noEnergy.png")).getImage()), Planet.SIZE,
				Planet.SIZE);

		cursor = resize(
				toBufferedImage(new ImageIcon(getClass().getResource(
						"/res/cursor.png")).getImage()), 32, 32);
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		if (SwingUtilities.isLeftMouseButton(arg0)) {

			Boolean planetClicked = false;

			double scaleFactor = scaleNotches * SCALE_SPEED;
			int clickX = (int) ((arg0.getX() + screenOffsetX - mapSizeX
					* (1 - scaleFactor) / 2) / (scaleNotches * SCALE_SPEED));
			int clickY = (int) ((arg0.getY() + screenOffsetY - mapSizeY
					* (1 - scaleFactor) / 2) / (scaleNotches * SCALE_SPEED));

			for (int p = 0; p < planets.size(); p++) {

				if (!(planets.get(p).popup.visible == true && planets.get(p).popup
						.actionPerformed(clickX, clickY))) {

					planets.get(p).popup.visible = false;

					if (planets.get(p).position.x - Planet.SIZE / 2.8 < clickX
							&& planets.get(p).position.x + Planet.SIZE / 2.8 > clickX
							&& planets.get(p).position.y - Planet.SIZE / 2.8 < clickY
							&& planets.get(p).position.y + Planet.SIZE / 2.8 > clickY) {
						planets.get(p);
						if (planets.get(p).spawnTick <= Planet.SPAWN_TIME
								&& !(planets.get(p)
										.getPlayerControllingPlanet() == 2 && cannotControlPlayer2)
								&& !(planets.get(p)
										.getPlayerControllingPlanet() == 0))
							planets.get(p).popup.visible = true;
						planetClicked = true;

					}
				}
			}

			if (!planetClicked) {
				for (int i = 0; i < units.size(); i++) {
					units.get(i).selected = false;
				}
				selectStart = new Point(arg0.getX(), arg0.getY());
			}

		}
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// System.out.println("MOUSE");
		// TODO Auto-generated method stub
		if (SwingUtilities.isRightMouseButton(arg0)) {

			double scaleFactor = scaleNotches * SCALE_SPEED;

			int clickX = (int) ((arg0.getX() + screenOffsetX - mapSizeX
					* (1 - scaleFactor) / 2) / (scaleNotches * SCALE_SPEED));
			int clickY = (int) ((arg0.getY() + screenOffsetY - mapSizeY
					* (1 - scaleFactor) / 2) / (scaleNotches * SCALE_SPEED));

			mousePointers.add(new MousePointerAnimation(clickX, clickY));

			Point destination = null;

			for (int p = 0; p < planets.size(); p++) {
				if (planets.get(p).position.x - Planet.SIZE / 2 < clickX
						&& planets.get(p).position.x + Planet.SIZE / 2 > clickX
						&& planets.get(p).position.y - Planet.SIZE / 2 < clickY
						&& planets.get(p).position.y + Planet.SIZE / 2 > clickY) {
					destination = new Point((int) (planets.get(p).position.x),
							(int) (planets.get(p).position.y));

					
				}
			}

			for (int i = 0; i < units.size(); i++) {
				if (units.get(i).selected) {

					// units.get(i).CIRCULAR_RADIUS = radius;

					if (destination == null) {
						// units.get(i).CIRCULAR_RADIUS = radius;

						int maxDistance = (int) Unit.CIRCULAR_RADIUS / 2;

						int mutationX = (int) (new Random()
								.nextInt((int) (maxDistance * 2)) - maxDistance);

						int maxY = (int) Math.sqrt(Math.pow(maxDistance, 2)
								- Math.pow(mutationX, 2));

						int mutationY = maxY;

						if (maxY > 0)
							mutationY = new Random().nextInt(maxY * 2) - maxY;

						units.get(i).setDestination(
								new Point(clickX + mutationX, clickY
										+ mutationY));
					} else {
						units.get(i).setDestination(destination);
						///System.out.println("TO THE PLANET");
					}
					if (!(units.get(i).activity == Unit.MOVE || units.get(i).activity == Unit.IDLE))
						units.get(i).activity = Unit.GOING_TO_MOVE;
					else
						units.get(i).activity = Unit.MOVE;

					units.get(i).waveCounter = 0;
					units.get(i).waveSign = units.get(i).WAVE_SIGN_ORIGINAL;
				}
			}
		}
		if (SwingUtilities.isLeftMouseButton(arg0)) {
			selectEnd = null;
			selectStart = null;
		}

	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub
		if (SwingUtilities.isLeftMouseButton(arg0)) {
			selectEnd = new Point(arg0.getX(), arg0.getY());
		}

		// System.out.println(mouseCurrentPos.x + ":" + mouseCurrentPos.y);
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub

		// System.out.println(mouseCurrentPos.x + ":" + mouseCurrentPos.y);
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		if (menu.show == true) {
			if (arg0.getKeyCode() == KeyEvent.VK_W
					|| arg0.getKeyCode() == KeyEvent.VK_UP) {
				menu.cursor--;
				if (menu.cursor < 0) {
					menu.cursor = menu.points.length - 1;
				}
			}
			if (arg0.getKeyCode() == KeyEvent.VK_S
					|| arg0.getKeyCode() == KeyEvent.VK_DOWN) {
				menu.cursor++;
				if (menu.cursor >= menu.points.length) {
					menu.cursor = 0;
				}
			}
			if (arg0.getKeyCode() == KeyEvent.VK_ESCAPE) {
				if (menu.controlsScreen) {
					menu.controlsScreen = false;
				}
			}
			if (arg0.getKeyCode() == KeyEvent.VK_ENTER) {
				if (menu.cursor != 0) {
					level = menu.cursor - 1;
					menu.show = false;
					gameIsOn = true;
					startGame();

				} else {
					menu.controlsScreen = true;
				}

			}
			repaint();
		} else {
			if (arg0.getKeyCode() == KeyEvent.VK_ESCAPE) {
				menu.show = true;
				menu.controlsScreen = false;
				gameIsOn = false;
				planets.clear();
				units.clear();
				blackout = null;
				explosions.clear();
				mousePointers.clear();
				winner = null;
				repaint();
			} else {
				key[arg0.getKeyCode()] = true;

				if (gameIsOn == false) {
					if (winner == 1)
						level++;
					if (level >= levels.length) {
						System.exit(0);
					} else {
						gameIsOn = true;
						winner = null;
						startGame();
						scaleNotches = 20;
					}
				}
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		key[arg0.getKeyCode()] = false;
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent arg0) {
		// TODO Auto-generated method stub
		int notches = arg0.getWheelRotation();
		// System.out.println("NOTCHESSS" + notches);

		if (scaleNotches - notches < 28 && scaleNotches - notches > 10) {
			scaleNotches -= notches;

			if (scaleNotches == 10) {
				mouseCurrentPos.x = arg0.getX();
				mouseCurrentPos.y = arg0.getY();
			} else {
				mouseCurrentPos.x -= (mouseCurrentPos.x - arg0.getX())
						/ (scaleNotches * SCALE_SPEED);
				mouseCurrentPos.y -= (mouseCurrentPos.y - arg0.getY())
						/ (scaleNotches * SCALE_SPEED);
			}
		}
	}

	public void drawStringCentre(Graphics g, String s, int line) {
		// get metrics from the graphics
		FontMetrics metrics = g.getFontMetrics(g.getFont());
		// get the height of a line of text in this
		// font and render context
		int hgt = metrics.getHeight();
		// get the advance of my text in this font
		// and render context
		int adv = metrics.stringWidth(s);

		g.drawString(s, SCREEN_SIZE_X / 2 - adv / 2, (int) (SCREEN_SIZE_Y / 2 - hgt
				/ 2 + line * hgt * 1.5));
	}

}
