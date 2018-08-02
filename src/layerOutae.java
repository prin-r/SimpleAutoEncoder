
public class layerOutae {
	
	public int trainingSize = 0;
	public int inputSize = 0;
	public int layerSize = 0;
	
	public double learningRate = 0.0;
	public double momentumRate = 0.0;
	public double weightDecay = 0.0;
	
	public double[][] x;
	public double[][] w;
	public double[][] dw;
	public double[][] o;
	public double[][] z;
	public double[][] sigma;
	public double[][] errorBack;
	public double[][] v;
	
	public layerOutae(int ts, int ips, int ls, double lr, double mr, double wd) {
		inputSize = ips;
		layerSize = ls;
		learningRate = lr;
		momentumRate = mr;
		weightDecay = wd;
		x = new double[trainingSize][inputSize];
		errorBack = new double[trainingSize][inputSize];
		
		w = new double[layerSize][inputSize+1];
		dw = new double[layerSize][inputSize+1];
		v = new double[layerSize][inputSize+1];
		
		o = new double[trainingSize][layerSize];
		z = new double[trainingSize][layerSize];
		sigma = new double[trainingSize][layerSize];
		
		initialWeight();
	}
	
	public void initialWeight() {
		double weightRange = 4.0*Math.sqrt(6.0/(inputSize+layerSize+1.0));
		for (int i = 0; i < layerSize; i++)
			for (int j = 0; j <= inputSize; j++) {
				double r = Math.random()*weightRange;
				w[i][j] = (Math.random() < 0.5)? -r:r;
			}
	}
	
	//------------------------------------------------------------------------------------------
	
	public void feed(double[][] input) {
		
		for (int i = 0; i < trainingSize; i++)
			for (int j = 0; j < inputSize; j++)
				x[i][j] = input[i][j];
		
		for (int i = 0; i < trainingSize; i++) {
			for (int j = 0; j < layerSize; j++) {
				z[i][j] = 0.0;
				for (int k = 0; k < inputSize; k++) {
					z[i][j] += x[i][k]*w[j][k];
				}
				z[i][j] += w[j][inputSize];
			}
		}
		
		activateSigmoid();
	}
	
	public void activateSigmoid() {
		for (int i = 0; i < trainingSize; i++) {
			for (int j = 0; j < layerSize; j++) {
				o[i][j] = 1.0/(1.0+Math.exp(-z[i][j]));
			}
		}
	}
	
	//------------------------------------------------------------------------------------------
	
	public void bpp(double[][] t) {
		computeSigma(t);
		//updateWeight();
		computeErrorBack();
	}
	
	public void computeSigma(double[][] t) {
		for (int i = 0; i < trainingSize; i++) {
			for (int j = 0; j < layerSize; j++) {
				sigma[i][j] = -(t[i][j]-o[i][j])*o[i][j]*(1.0-o[i][j]);
			}
		}
	}

	public void updateWeight() {
		for (int i = 0; i < layerSize; i++) {
			for (int j = 0; j < inputSize; j++) {
				dw[i][j] = 0.0;
			}
			dw[i][inputSize] = 0.0;
		}
		
		for (int i = 0; i < trainingSize; i++) {
			for (int j = 0; j < layerSize; j++) {
				for (int k = 0; k < inputSize; k++) {
					dw[j][k] += x[i][k]*sigma[i][j];
				}
				dw[j][inputSize] += sigma[i][j];
			}
		}
		
		for (int i = 0; i < layerSize; i++) {
			for (int j = 0; j < inputSize; j++) {
				double deltaW =  - learningRate*(dw[i][j]/(trainingSize+0.0) + weightDecay*w[i][j]) + momentumRate*v[i][j];
				w[i][j] = w[i][j] + deltaW;
				v[i][j] = deltaW;
			}
			double deltaB =  - learningRate*(dw[i][inputSize]/(trainingSize+0.0)) + momentumRate*v[i][inputSize];
			w[i][inputSize] = w[i][inputSize] + deltaB;
			v[i][inputSize] = deltaB;
		}
	}
	
	public void computeErrorBack() {
		for (int i = 0; i < trainingSize; i++)
			for (int j = 0; j < inputSize; j++)
				errorBack[i][j] = 0.0;
		
		for (int i = 0; i < trainingSize; i++)
			for (int j = 0; j < layerSize; j++)
				for (int k = 0; k < inputSize; k++)
					errorBack[i][k] += sigma[i][j]*w[j][k];
	}
}
