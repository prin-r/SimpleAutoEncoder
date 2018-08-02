
public class layer2ae {
	
	public int trainingSize = 0;
	public int inputSize = 0;
	public int layerSize = 0;
	
	public double learningRate = 0.0;
	public double momentumRate = 0.0;
	public double weightDecay = 0.0;
	
	public double constRoll = 0.05;
	public double beta = 0.5;
	
	public double lossCost = Double.MAX_VALUE;
	
	public double[][] x;
	public double[][] w;
	public double[][] dw;
	public double[][] nw;
	public double[][] o;
	public double[][] z;
	public double[] roll;
	public double[][] sigma;
	public double[][] errorBack;
	public double[][] v;
	public double[][] loss;
	public double[][] minW;
	
	public layer2ae(int ts, int ips, int ls, double lr, double mr, double wd) {
		trainingSize = ts;
		inputSize = ips;
		layerSize = ls;
		learningRate = lr;
		momentumRate = mr;
		weightDecay = wd;
		x = new double[trainingSize][inputSize];
		errorBack = new double[trainingSize][inputSize];
		
		w = new double[layerSize][inputSize+1];
		dw = new double[layerSize][inputSize+1];
		nw = new double[layerSize][inputSize+1];
		v = new double[layerSize][inputSize+1];
		minW = new double[layerSize][inputSize+1];
		
		o = new double[trainingSize][layerSize];
		z = new double[trainingSize][layerSize];
		sigma = new double[trainingSize][layerSize];
		loss = new double[trainingSize][layerSize];
		
		roll = new double[layerSize];
		
		initialWeight();
	}
	
	public void normW1() {
		for (int i = 0; i < layerSize; i++) {
			double min = Double.MAX_VALUE;
			double max = Double.MIN_VALUE;
			for (int j = 0; j < inputSize; j++) {
				min = (min < w[i][j])? min:w[i][j];
				max = (w[i][j] < max)? max:w[i][j];
			}
			for (int j = 0; j < inputSize; j++) nw[i][j] = (w[i][j]-min)/(max-min+0.0);
		}
	}
	
	public void normW2() {
		for (int i = 0; i < layerSize; i++) {
			double sumw2 = 0.0;
			for (int j = 0; j < inputSize; j++) {
				sumw2 += w[i][j]*w[i][j];
			}
			sumw2 = 1.0/Math.sqrt(sumw2);
			for (int j = 0; j < inputSize; j++) {
				nw[i][j] = w[i][j]/sumw2;
			}
		}
	}
	
	public void initialWeight() {
		double weightRange = 4.0*Math.sqrt(6.0/(inputSize+layerSize+1.0));
		for (int i = 0; i < layerSize; i++)
			for (int j = 0; j <= inputSize; j++) {
				double r = Math.random()*weightRange;
				w[i][j] = (Math.random() < 0.5)? -r:r;
			}
	}
	
	//----------------------------------------------------------------------------
	
	public void feed(double[][] input) {
		
		resetRoll();
		
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
		cumulativeRoll();
	}
	
	public void resetRoll() {
		for (int i = 0; i < layerSize; i++)
			roll[i] = 0.0;
	}
	
	public void activateSigmoid() {
		for (int i = 0; i < trainingSize; i++) {
			for (int j = 0; j < layerSize; j++) {
				o[i][j] = 1.0/(1.0+Math.exp(-z[i][j]));
			}
		}
	}
	
	public void cumulativeRoll() {
		for (int i = 0; i < trainingSize; i++)
			for (int j = 0; j < layerSize; j++)
				roll[j] += o[i][j];
		
		for (int i = 0; i < layerSize; i++)
			roll[i] = roll[i]/(trainingSize + 0.0);
	}
	
	//----------------------------------------------------------------------------
	
	public void bpp(double[][] prevError) {
		computeSigma(prevError);
		//computeLoss();
		updateWeight();
	}
	
	public void computeLoss() {
		double tmp1 = 0.0;
		for (int i = 0; i < trainingSize; i++) {
			for (int j = 0; j < layerSize; j++) {
				tmp1 += loss[i][j]*loss[i][j];
			}
		}
		tmp1 = 0.5*tmp1/(trainingSize+0.0);
		
		double tmp2 = 0.0;
		for (int i = 0; i < layerSize; i++) {
			for (int j = 0; j < inputSize; j++) {
				tmp2 += w[i][j]*w[i][j];
			}
		}
		tmp2 = tmp2*0.5*weightDecay;
		
		double tmp3 = 0.0;
		for (int i = 0; i < layerSize; i++)
			tmp3 += beta*constRoll*Math.log(constRoll/(roll[i]+0.0)) + (1-constRoll)*Math.log((1.0-constRoll)/(1.0-roll[i]));
		
		double tmp = tmp1 + tmp2 + tmp3;
		System.out.println(tmp);
		if (tmp < lossCost) {
			lossCost = tmp;
			for (int i = 0; i < layerSize; i++)
				for (int j = 0; j < inputSize; j++)
					minW[i][j] = w[i][j];
		}
		
	}
	
	public void computeSigma(double[][] prevError) {
		for (int i = 0; i < trainingSize; i++) {
			for (int j = 0; j < layerSize; j++) {
				//loss[i][j] = prevError[i][j];
				sigma[i][j] = o[i][j]*(1.0-o[i][j])*(prevError[i][j] + beta*( (-constRoll/roll[j] + (1.0-constRoll)/(1.0-roll[j]) )) );
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
