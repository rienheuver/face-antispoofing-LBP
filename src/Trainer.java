import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import edu.berkeley.compbio.jlibsvm.ImmutableSvmParameter;
import edu.berkeley.compbio.jlibsvm.ImmutableSvmParameterGrid;
import edu.berkeley.compbio.jlibsvm.ImmutableSvmParameterPoint;
import edu.berkeley.compbio.jlibsvm.binary.BinaryClassificationProblem;
import edu.berkeley.compbio.jlibsvm.binary.BinaryModel;
import edu.berkeley.compbio.jlibsvm.binary.C_SVC;
import edu.berkeley.compbio.jlibsvm.binary.MutableBinaryClassificationProblemImpl;
import edu.berkeley.compbio.jlibsvm.kernel.LinearKernel;
import edu.berkeley.compbio.jlibsvm.util.SparseVector;

public class Trainer
{
	ImmutableSvmParameter params;

	private MutableBinaryClassificationProblemImpl problem; // set by
															// read_problem
	private BinaryModel model;
	private String input_file_name; // set by parse_command_line
	private String model_file_name; // set by parse_command_line
	private boolean crossValidation;

	private static final Float UNSPECIFIED_GAMMA = -1F;

	public Trainer(File inputFile) throws IOException
	{
		this.input_file_name = inputFile.getName();
		this.model_file_name = inputFile.getName()+".model";
	}

	public BinaryModel run() throws IOException
	{
		C_SVC svm = new C_SVC();

		ImmutableSvmParameterGrid.Builder builder = ImmutableSvmParameterGrid.builder();

		HashSet<Float> cSet;
		HashSet<LinearKernel> kernelSet;
		
		cSet = new HashSet<Float>();
		cSet.add(100.0f);
		
		kernelSet = new HashSet<LinearKernel>();
		kernelSet.add(new LinearKernel());
		
		//configuring parameters
		builder.eps = 0.001f;
		builder.Cset = cSet;
		builder.kernelSet = kernelSet;

		this.params = builder.build();

		read_problem();

		model = svm.train(problem, params);
		model.save(model_file_name);
		
		return model;
	}

	private void read_problem() throws IOException
	{
		BufferedReader fp = new BufferedReader(new FileReader(input_file_name));
		Vector<Float> vy = new Vector<Float>();
		Vector<SparseVector> vx = new Vector<SparseVector>();
		int max_index = 0;
		
		Map<Integer, SparseVector> data = new HashMap<Integer, SparseVector>();

		while (true)
		{
			String line = fp.readLine();
			if (line == null)
			{
				break;
			}
			StringTokenizer st = new StringTokenizer(line, " \t\n\r\f:");

			vy.addElement(Float.parseFloat(st.nextToken()));
			int value_amount = st.countTokens() / 2;

			SparseVector sv = new SparseVector(value_amount);

			for (int i = 0; i < value_amount; i++)
			{
				sv.indexes[i] = Integer.parseInt(st.nextToken());
				sv.values[i] = Float.parseFloat(st.nextToken());
			}
			if (value_amount > 0)
			{
				max_index = Math.max(max_index,  sv.indexes[value_amount-1]);
			}
			vx.addElement(sv);
		}
		
		problem = new MutableBinaryClassificationProblemImpl(String.class, vy.size());

		for (int i = 0; i < vy.size(); i++)
		{
			problem.addExampleFloat(vx.elementAt(i), vy.elementAt(i));
		}
		((BinaryClassificationProblem) problem).setupLabels();

		/*
		*** Niet nodig want geen gamma-kernel
		for (ImmutableSvmParameterPoint subparam : gridParams)
		{
			updateKernelWithNumExamples(subparam, max_index);
		}
		*/
		
		fp.close();
	}
}
