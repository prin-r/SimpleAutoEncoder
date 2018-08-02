import java.awt.Graphics2D;
import java.io.File;
import javax.swing.JFrame;

public class Screen extends JFrame{
	
	private static final long serialVersionUID = 1L;
	
	private int width;
	private int height;
	public int[] pixels;
	public int x = 0, y = 0;
	public int[] pix;
	
	public image img = new image("C://Users/kanisorn/Dropbox/convolution/a.jpg");
	public String iDir = "C://Users/kanisorn/Dropbox/convolution/trainingSet/ptmp";
	public String tmpDir = "C://Users/kanisorn/Dropbox/convolution/trainingSet/tmp";
	public final File folder = new File(tmpDir);
	public image[] ilist;
	public String[] tmp = new String[1300];
	
	public int[][] t = new int[200][100];
	public double[][] normT = new double[200][100];
	
	public sae sae = new sae();
	
	public double[][] trainingSet = new double[1024][10];
	public double[][] target = new double[1024][10];
	
	public Screen (int width, int height) {
		pixels = new int[width*height];
		this.width =width;
		this.height = height;
		setResizable(false);
		setTitle("BezierTest");
		setSize(width, height);
		setLocation(50, 5);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
		makeTrainingSet();
	}
	
	public void render(Graphics2D g2d) {
		if (x==0) {
			listFilesForFolder(folder);
			x = 1;
		}
		
		if (x==1) {
			for (int i = 0; i < 200; i++) {
				for (int j = 0; j < 100; j++) {
					t[i][j] = ilist[i].pix1D[j];
					normT[i][j] = t[i][j]/255.0;
				}
			}
			x = 2;
			
			sae.h1.initialWeight();
			sae.out.initialWeight();
		}
		
		int a = 0;
		for (int j = 100; j < width && a < ilist.length; j+=12) {
			for (int k = 100; k < height/2 && a < ilist.length; k+=12) {
				ilist[a].draw1D(pixels, width, j, k);
				a++;
			}
		}
		
		int x1 = 250;
		int y1 = 200;
		
		for (int i = x1; i < x1+200; i++) {
			for (int j = y1; j < y1+100; j++) {
			int c = t[i-x1][j-y1];
				this.pixels[i+j*width] = (c<<16)+(c<<8)+c;
			}
		}
		
		y++;
		System.out.println(y);
		sae.train(normT);
		
		x1 = 600;
		y1 = 200;
		
		a = 0;
		for (int j = x1; j < width && a < 30; j+=12) {
			for (int k = y1; k < height*0.35 && a < 30; k+=12) {
				sae.drawWeightH1(a, pixels, width, j, k);
				a++;
			}
		}
		
		x1 = 600;
		y1 = 400;
		
		a = 0;
		for (int j = x1; j < width && a < 30; j+=12) {
			for (int k = y1; k < height*0.63 && a < 30; k+=12) {
				sae.drawWeightNet(a, pixels, width, j, k);
				a++;
			}
		}
		
		x1 = 800;
		y1 = 100;
		
		a = 0;
		for (int j = x1; j < width && a < 200; j+=12) {
			for (int k = y1; k < height/2 && a < 200; k+=12) {
				sae.drawOut(a, pixels, width, j, k);
				a++;
			}
		}
		/*mlp.bathTraining(trainingSet, target);
		
		int r = ((int)(1024*Math.random()))%1024;
		
		mlp.feed(trainingSet[r]);
		for (int i = 0; i < 10; i++) {
			System.out.print((int)target[r][i]);
		}
		System.out.print("			");
		for (int i = 0; i < 10; i++) {
			System.out.print(((int)(10000*mlp.out.out[i]))/10000.0);
			System.out.print(" | ");
		}
		System.out.println();
		//img.draw(pixels, width);*/
		/*x++;  
		
		int count = 0;
		double[] input = new double[10];
		double[] target = new double[10];
		for (int i = 0; i < 10; i++) count += input[i] = (Math.random()<0.5)? 0:1;
		target[count%10] = 1;
		
		mlp.feed(input);
		mlp.bpp(target);
		
		if (x>>10==1) {
			int val = 0;
			double tmp = -1;
			for (int i = 0; i < 10; i++) {
				if (mlp.out.out[i] > tmp) {
					tmp = mlp.out.out[i];
					val = i;
				}
			}
			System.out.println(val + "	" + (count%10));
			x = 0;
		}
		/*try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}*/
	}
	
	private void makeTrainingSet() {
		for (int i = 1; i < trainingSet.length; i++) {
			if (trainingSet[i-1][0] < 0.0000001) {
				trainingSet[i][0] = 1.0;
				for (int j = 1; j < trainingSet[i].length; j++) trainingSet[i][j] = trainingSet[i-1][j];
			} else {
				int j = 0;
				while (trainingSet[i-1][j] > 0.0000001 && j < 10) j++;
				trainingSet[i][j] = 1.0;
				j++;
				while (j < trainingSet[i-1].length) {
					trainingSet[i][j] = trainingSet[i-1][j];
					j++;
				}
			}
		}
		for (int i = 1; i < trainingSet.length; i++) {
			int count = 0;
			for (int j = 0; j < trainingSet[i].length; j++) if (trainingSet[i][j] > 0.0000001) count++;
			target[i][count-1] = 1;
		}
	}
	
	public void listFilesForFolder(final File folder) {
		int i = 0;
		int c = 0;
	    for (final File fileEntry : folder.listFiles()) {
	        if (fileEntry.isDirectory()) {
	            listFilesForFolder(fileEntry);
	        } else {
	        	String fName = fileEntry.getName();
	            System.out.println(tmpDir+"//"+fName);
	            tmp[i++] = tmpDir+"//"+fName;
	        }
	    }
	    for (int j = 0; j < i; j++) {
	    	c++;
	    	if (tmp[j] == null) break;
	    	else System.out.println(tmp[j]); 
	    }
	    this.ilist = new image[c];
	    for (int j = 0; j < c; j++) {
	    	this.ilist[j] = new image(tmp[j]);
	    }
	}
}
