import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import edu.berkeley.compbio.jlibsvm.binary.BinaryModel;
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
        VisualHistogram vh = new VisualHistogram(features,(int) Math.pow(2, numPoints));
        vh.saveImage(new File("lbp_"+imageFile.getName()));
		return 0;
	}
}
