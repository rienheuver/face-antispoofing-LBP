import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import javax.imageio.ImageIO;

import ij.process.ColorProcessor;

public class ProcessImage
{
	
	public File run() throws IOException, URISyntaxException
	{
		String pict_dir = "normalized";
		String lbp_dir = "lbps";
		File dir = new File(pict_dir);
		FeatureFile trainData = new FeatureFile();
		File model = new File("faceSpoofing.data");
		model.createNewFile();
		
		for (File subdir : dir.listFiles()) // iterate through main directory
		{
			for (File file : subdir.listFiles()) // iterate through picture directories
			{
				File lbp_file = new File(lbp_dir+"/"+file.getParentFile().getName()+"/"+file.getName());
				
				// load the image
		        ColorProcessor image = new ColorProcessor(ImageIO.read(file));

		        // set parameters of algorithm
		        final int radius = 1;
		        final int numPoints = 8;
		        final int cells = 4;
		        
		        // initiate algorithm
		        LocalBinaryPatterns descriptor = new LocalBinaryPatterns(radius,numPoints,cells);
		        
		        // obtain the features
		        List<Histogram> features = descriptor.run(image);
		        VisualHistogram vh = new VisualHistogram(features,(int) Math.pow(2, numPoints));
		        vh.saveImage(lbp_file);
		        trainData.addImage(features, subdir.getName());
			}
			trainData.save(model);
		}
		return model;
    }
}
