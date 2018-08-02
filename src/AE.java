import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class AE extends Canvas implements Runnable {
	private static final long serialVersionUID = 1L;
	
	public Thread thread;
	public static int WIDTH = 1280;
	public static int HEIGHT = 720;
	private boolean isRunning = false;
	private Screen screen = new Screen(WIDTH, HEIGHT);
	public static BufferedImage img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
	private int[] pixels = ((DataBufferInt)img.getRaster().getDataBuffer()).getData();
	
	public AE() {
	
	}

	public static void main(String[] args) {
		AE ae = new AE();
		ae.screen.add(ae);
		ae.start();
		
	}
	
	public void start() {
		isRunning = true;
		thread = new Thread(this);
		thread.start();
	}
	
	public void stop() {
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while(isRunning) {
			render();
		}
	}

	private void render() {
		for (int i = 0; i < WIDTH*HEIGHT; i++) {
			pixels[i] = screen.pixels[i];
		}
		BufferStrategy bs = getBufferStrategy();
		if (bs == null) {createBufferStrategy(3); return;}
		Graphics g = bs.getDrawGraphics();
		Graphics2D g2d = img.createGraphics();
		screen.render(g2d);
		g.drawImage(img, 0, 0, WIDTH + 10, HEIGHT + 10, null);
		g.dispose();
		bs.show();
	}
}

