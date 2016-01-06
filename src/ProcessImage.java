import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import javax.imageio.ImageIO;

import ij.process.ColorProcessor;

public class ProcessImage
{
	private int radius;
	private int numPoints;
	private int cells;
	
	public ProcessImage(int[] params)
	{
		// set parameters of LBP
        this.radius = params[0];
        this.numPoints = params[1];
        this.cells = params[2];
	}
	
	public File run() throws IOException, URISyntaxException
	{
		String pict_dir = "normalized";
		String lbp_dir = "lbps";
		File dir = new File(pict_dir);
		FeatureFile trainData = new FeatureFile();
		File data = new File("faceSpoofing.data");
		data.createNewFile();
		
		for (File subdir : dir.listFiles()) // iterate through main directory
		{
			for (File file : subdir.listFiles()) // iterate through picture directories
			{
				File lbp_file = new File(lbp_dir+"/"+file.getParentFile().getName()+"/"+file.getName());
				
				// load the image
		        ColorProcessor image = new ColorProcessor(ImageIO.read(file));
		        
		        // initiate LBP
		        LocalBinaryPatterns descriptor = new LocalBinaryPatterns(this.radius,this.numPoints,this.cells);
		        
		        // obtain the features
		        List<Histogram> features = descriptor.run(image);
		        VisualHistogram vh = new VisualHistogram(features,(int) Math.pow(2, numPoints));
		        vh.saveImage(lbp_file);
		        int label = 0;
		        if (subdir.getName().equals("original"))
		        {
		        	label = 1;
		        }
		        trainData.addImage(features, label);
			}
			trainData.save(data);
		}
		return data;
    }
}
