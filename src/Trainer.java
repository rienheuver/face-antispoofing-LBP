import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.StringTokenizer;

import edu.berkeley.compbio.jlibsvm.ImmutableSvmParameter;
import edu.berkeley.compbio.jlibsvm.ImmutableSvmParameterGrid;
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
	}

	public BinaryModel run() throws IOException
	{
		C_SVC svm = new C_SVC();

		ImmutableSvmParameterGrid.Builder builder = ImmutableSvmParameterGrid.builder();

		HashSet<Float> cSet;
		HashSet<LinearKernel> kernelSet;
		
		cSet = new HashSet<Float>();
		cSet.add(1.0f);
		
		kernelSet = new HashSet<LinearKernel>();
		kernelSet.add(new LinearKernel());
		
		//configuring parameters
		builder.eps = 0.001f;
		builder.Cset = cSet;
		builder.kernelSet = kernelSet;

		this.params = builder.build();

		read_problem();

		model = svm.train(problem, params);

		return model;
	}

	private void read_problem() throws IOException
	{
		BufferedReader fp = new BufferedReader(new FileReader(input_file_name));

		Map<Integer, SparseVector> data = new HashMap<Integer, SparseVector>();
		int lines = 0;

		while (true)
		{
			String line = fp.readLine();
			if (line == null)
			{
				break;
			}
			StringTokenizer st = new StringTokenizer(line, " \t\n\r\f:");

			int value_amount = (st.countTokens() - 1) / 2;

			SparseVector sv = new SparseVector(value_amount);
			int label = Integer.parseInt(st.nextToken());

			int[] indices = new int[value_amount];
			float[] values = new float[value_amount];
			for (int i = 0; i < value_amount; i++)
			{
				indices[i] = Integer.parseInt(st.nextToken());
				values[i] = Float.parseFloat(st.nextToken());
			}
			sv.indexes = indices;
			sv.values = values;
			data.put(label, sv);
			lines++;
		}

		problem = new MutableBinaryClassificationProblemImpl(String.class, lines);

		for (int i : data.keySet())
		{
			problem.addExample(data.get(i), i);
		}

		fp.close();
	}
}
