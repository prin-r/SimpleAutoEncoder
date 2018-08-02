import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;


public class image {
	
	public int[][] pix;
	public int[] pix1D;
	public BufferedImage img = null;
	public int w;
	public int h;
	public int x;
	public int y;
	
	public image(String dir) {
		File in = new File(dir);
		try {
			img = ImageIO.read(in);
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.x = 0;
		this.y = 0;
		this.w = img.getWidth();
		this.h = img.getHeight();
		this.pix = new int[w][h];
		this.pix1D = new int[w*h];
		for (int i = 0; i < w; i++)
			for (int j = 0; j < h; j++) {
				int rgb = img.getRGB(i, j);
				int r = (rgb >> 16) & 0xFF;
		        int g = (rgb >> 8) & 0xFF;
		        int b = (rgb & 0xFF);
				pix[i][j] = (int)Math.abs((r+g+b)/3);
				pix1D[i + j*w] = pix[i][j];
			}
	}

	public void draw(int[] spix, int sw) {
		int wx = w+x;
		int hy = h+y;
		for (int i = x; i < wx; i++) {
			for (int j = y; j < hy; j++) {
				int index = i+j*sw;
				int c = pix[i-x][j-y];
				if (index >= 0 & index < spix.length) spix[index] = (c<<16)+(c<<8)+c;
			}
		}	
	}
	
	public void draw(int[] spix, int sw, int px, int py) {
		int wx = w+px;
		int hy = h+py;
		for (int i = px; i < wx; i++) {
			for (int j = py; j < hy; j++) {
				int index = i+j*sw;
				int c = pix[i-px][j-py];
				if (index >= 0 & index < spix.length) spix[index] = (c<<16)+(c<<8)+c;
			}
		}	
	}
	
	public void draw1D(int[] spix, int sw, int px, int py) {
		int wx = w+px;
		int hy = h+py;
		for (int i = px; i < wx; i++) {
			for (int j = py; j < hy; j++) {
				int index = i+j*sw;
				int c = pix1D[i-px+(j-py)*w];
				if (index >= 0 & index < spix.length) spix[index] = (c<<16)+(c<<8)+c;
			}
		}	
	}
}
