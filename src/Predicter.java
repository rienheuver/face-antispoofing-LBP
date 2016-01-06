import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import edu.berkeley.compbio.jlibsvm.binary.BinaryModel;
import edu.berkeley.compbio.jlibsvm.util.SparseVector;
import ij.process.ColorProcessor;

public class Predicter {
	private BinaryModel model;
	private File imageFile;
	private int radius;
	private int numPoints;
	private int cells;
	
	public Predicter(BinaryModel model, File imageFile, int[] params)
	{
		this.model = model;
		this.imageFile = imageFile;
		// set parameters of LBP
        this.radius = params[0];
        this.numPoints = params[1];
        this.cells = params[2];
	}
	
	public int predict() throws IOException
	{
		ColorProcessor image = new ColorProcessor(ImageIO.read(this.imageFile));
		
		// initiate LBP
        LocalBinaryPatterns descriptor = new LocalBinaryPatterns(this.radius,this.numPoints,this.cells);
        
        // obtain the features
        List<Histogram> features = descriptor.run(image);
        
        /* Save histogram image
        VisualHistogram vh = new VisualHistogram(features,(int) Math.pow(2, numPoints));
        vh.saveImage(new File("histograms/lbp_"+imageFile.getName()));
        */
        
        SparseVector sv = featurize(features);
        int predictedLabel = (int) model.predictLabel(sv);
		return predictedLabel;
	}
	
	private SparseVector featurize(List<Histogram> features)
	{
		int value_amount = 0;
		for (Histogram h : features)
		{
			value_amount += h.size();
		}
		SparseVector sv = new SparseVector(value_amount);
		
		List<Integer> ins = new ArrayList<Integer>();
		List<Integer> vals = new ArrayList<Integer>();
		for (Histogram h : features)
		{
			for (int i : h.getHistogram().keySet())
			{
				ins.add(i);
				vals.add(h.getHistogram().get(i));
			}
		}
		
		int[] indices = new int[value_amount];
		float[] values = new float[value_amount];
		
		for (int i = 0; i < value_amount; i++)
		{
			indices[i] = ins.get(i);
			values[i] = vals.get(i);
		}
		
		sv.indexes = indices;
		sv.values = values;
		
		return sv;
	}
}
