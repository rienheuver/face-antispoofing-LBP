import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import edu.berkeley.compbio.jlibsvm.ImmutableSvmParameter;
import edu.berkeley.compbio.jlibsvm.ImmutableSvmParameterGrid;
import edu.berkeley.compbio.jlibsvm.ImmutableSvmParameterPoint;
import edu.berkeley.compbio.jlibsvm.MutableSvmProblem;
import edu.berkeley.compbio.jlibsvm.SVM;
import edu.berkeley.compbio.jlibsvm.SolutionModel;
import edu.berkeley.compbio.jlibsvm.binary.BinaryClassificationProblem;
import edu.berkeley.compbio.jlibsvm.binary.BinaryClassificationSVM;
import edu.berkeley.compbio.jlibsvm.binary.C_SVC;
import edu.berkeley.compbio.jlibsvm.binary.MutableBinaryClassificationProblemImpl;
import edu.berkeley.compbio.jlibsvm.kernel.GammaKernel;
import edu.berkeley.compbio.jlibsvm.kernel.KernelFunction;
import edu.berkeley.compbio.jlibsvm.kernel.LinearKernel;
import edu.berkeley.compbio.jlibsvm.labelinverter.StringLabelInverter;
import edu.berkeley.compbio.jlibsvm.multi.MultiClassificationSVM;
import edu.berkeley.compbio.jlibsvm.multi.MutableMultiClassProblemImpl;
import edu.berkeley.compbio.jlibsvm.regression.MutableRegressionProblemImpl;
import edu.berkeley.compbio.jlibsvm.regression.RegressionSVM;
import edu.berkeley.compbio.jlibsvm.scaler.NoopScalingModel;
import edu.berkeley.compbio.jlibsvm.scaler.NoopScalingModelLearner;
import edu.berkeley.compbio.jlibsvm.scaler.ScalingModelLearner;
import edu.berkeley.compbio.jlibsvm.util.SparseVector;
import edu.berkeley.compbio.ml.CrossValidationResults;

public class Trainer
{
	SVM svm;

	ImmutableSvmParameter param;

	private MutableSvmProblem problem;		// set by read_problem
	private SolutionModel model;
	private String input_file_name;		// set by parse_command_line
	private String model_file_name;		// set by parse_command_line
	private boolean crossValidation;
	
	private static final Float UNSPECIFIED_GAMMA = -1F;
	
	public Trainer(File inputFile) throws IOException
	{
		this.input_file_name = inputFile.getName();
	}
	
	public void run() throws IOException
	{
		long startTime = System.currentTimeMillis();
		
		ImmutableSvmParameterGrid.Builder builder = ImmutableSvmParameterGrid.builder();
		
		builder.nu = 0.5f;
		builder.cache_size = 100;
		builder.eps = 1e-3f;
		builder.p = 0.1f;
		builder.shrinking = true;
		builder.probability = false;
		builder.redistributeUnbalancedC = true;
		
		ScalingModelLearner<SparseVector> scalingModelLearner = new NoopScalingModelLearner<SparseVector>();
		 
		int scalingExamples = 1000;
		boolean normalizeL2 = false;
		int svm_type = 0;
		int kernel_type = 2;
		int degree = 3;
		Set<Float> gammaSet = new HashSet<Float>();
		gammaSet.add(UNSPECIFIED_GAMMA);
		float coef0 = 0;
		
		int p = input_file_name.lastIndexOf('/');
		++p;
		model_file_name = input_file_name.substring(p) + ".model";
		
		builder.kernelSet = new HashSet<KernelFunction>();
		builder.kernelSet.add(new LinearKernel());
		builder.scalingModelLearner = scalingModelLearner;
		this.param = builder.build();
	
		svm = new C_SVC();
		
		if (svm instanceof BinaryClassificationSVM && problem.getLabels().size() > 2)
		{
			svm = new MultiClassificationSVM((BinaryClassificationSVM) svm);
		}
	
		model = svm.train(problem, param);
	
		model.save(model_file_name);
	
		// CV might have been done already for grid search or whatever
		CrossValidationResults cv = model.getCrossValidationResults();
		if (cv == null && crossValidation)
		{
			// but if not, force it
			cv = svm.performCrossValidation(problem, param);
		}
		if (cv != null)
		{
			System.out.println(cv.toString());
		}
	
	
		long endTime = System.currentTimeMillis();
	
		float time = (endTime - startTime) / 1000f;
	
		System.out.println("Finished in " + time + " secs");
	}

	private void read_problem() throws IOException
	{
		BufferedReader fp = new BufferedReader(new FileReader(input_file_name));
		Vector<Float> vy = new Vector<Float>();
		Vector<SparseVector> vx = new Vector<SparseVector>();
		int max_index = 0;
		
		while (true)
		{
			String line = fp.readLine();
			if (line == null)
			{
				break;
			}
		
			StringTokenizer st = new StringTokenizer(line, " \t\n\r\f:");
		
			vy.addElement(Float.parseFloat(st.nextToken()));
			int m = st.countTokens() / 2;
			SparseVector x = new SparseVector(m);
			for (int j = 0; j < m; j++)
			{
				x.indexes[j] = Integer.parseInt(st.nextToken());
				x.values[j] = Float.parseFloat(st.nextToken());
			}
			if (m > 0)
			{
				max_index = Math.max(max_index, x.indexes[m - 1]);
			}
			vx.addElement(x);
		}
		
		// build problem
		if (svm instanceof RegressionSVM)
		{
			problem = new MutableRegressionProblemImpl(vy.size());
		}
		else
		{
			Set<Float> uniqueClasses = new HashSet<Float>(vy);
			int numClasses = uniqueClasses.size();
			if (numClasses == 1)
			{
				problem = new MutableRegressionProblemImpl(vy.size());
			}
			else if (numClasses == 2)
			{
				problem = new MutableBinaryClassificationProblemImpl(String.class, vy.size());
			}
			else
			{
				problem = new MutableMultiClassProblemImpl<String, SparseVector>(String.class, new StringLabelInverter(), vy.size(), new NoopScalingModel<SparseVector>());
			}
		}
		
		for (int i = 0; i < vy.size(); i++)
		{
			problem.addExampleFloat(vx.elementAt(i), vy.elementAt(i));
		}
		if (problem instanceof BinaryClassificationProblem)
		{
			((BinaryClassificationProblem) problem).setupLabels();
		}
		
		
		if (param instanceof ImmutableSvmParameterGrid)
		{
			Collection<ImmutableSvmParameterPoint> gridParams = ((ImmutableSvmParameterGrid) param).getGridParams();
			for (ImmutableSvmParameterPoint subparam : gridParams)
			{
				updateKernelWithNumExamples(subparam, max_index);
			}
		}
		else
		{
			updateKernelWithNumExamples((ImmutableSvmParameterPoint) param, max_index);
		}
		
		fp.close();
	}
	
	private void updateKernelWithNumExamples(ImmutableSvmParameterPoint pointParam, int max_index)
	{
		KernelFunction kernel = pointParam.kernel;
	
		if (kernel instanceof GammaKernel && ((GammaKernel) kernel).getGamma() == UNSPECIFIED_GAMMA)
		{
			((GammaKernel) kernel).setGamma(1.0f / max_index);
		}
	}
}
