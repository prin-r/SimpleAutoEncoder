
public class sae {
	
	public layer2ae h1 = new layer2ae(200,100,30, 0.09, 0.9, 0.001);
	public layerOutae out = new layerOutae(200,30,100, 0.09, 0.9, 0.001);
	public double lossCost = Double.MAX_VALUE;
	public double[][] w = new double[30][100];
	public int count = 0;
	
	public sae() {
		
	}
	
	public void train(double[][] input) {
		h1.feed(input);
		out.feed(h1.o);
		
		computeLoss(input);
		
		out.bpp(input);
		h1.bpp(out.errorBack);
		
		out.updateWeight();
	}
	
	public void computeLoss(double[][] input) {
		double tmp = 0.0;
		for (int i = 0; i < input.length; i++) {
			for (int j = 0; j < input[0].length; j++) {
				tmp += (out.o[i][j] - input[i][j])*(out.o[i][j] - input[i][j]);
			}
		}
		System.out.println("	" + tmp);
		if (tmp < lossCost) {
			lossCost = tmp;
			for (int i = 0; i < w.length; i++)
				for (int j = 0; j < w[0].length; j++)
					w[i][j] = h1.w[i][j];
		}
	}
	
	public void drawWeightNet(int p, int[] spix, int sw, int px, int py) {
		h1.normW2();
		int wx = 10+px;
		int hy = 10+py;
		for (int i = px; i < wx; i++) {
			for (int j = py; j < hy; j++) {
				int index = i+j*sw;
				int c = (int)((255*(this.w[p][i-px+(j-py)*10])));
				if (index >= 0 & index < spix.length) spix[index] = (c<<16)+(c<<8)+c;
			}
		}
	}

	public void drawWeightH1(int p, int[] spix, int sw, int px, int py) {
		h1.normW2();
		int wx = 10+px;
		int hy = 10+py;
		for (int i = px; i < wx; i++) {
			for (int j = py; j < hy; j++) {
				int index = i+j*sw;
				int c = (int)((255*(h1.nw[p][i-px+(j-py)*10])));
				if (index >= 0 & index < spix.length) spix[index] = (c<<16)+(c<<8)+c;
			}
		}
	}

	public void drawOut(int p, int[] spix, int sw, int px, int py) {
		int wx = 10+px;
		int hy = 10+py;
		for (int i = px; i < wx; i++) {
			for (int j = py; j < hy; j++) {
				int index = i+j*sw;
				int c = (int)(255*out.o[p][i-px+(j-py)*10]);
				if (index >= 0 & index < spix.length) spix[index] = (c<<16)+(c<<8)+c;
			}
		}
	}

}
