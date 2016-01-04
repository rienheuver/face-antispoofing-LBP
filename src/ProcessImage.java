import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import javax.imageio.ImageIO;

import ij.process.ColorProcessor;

public class ProcessImage
{
	
	public static void main(String[] args) throws IOException, URISyntaxException
	{
		if (args.length < 1)
		{
			String pict_dir = "normalized";
			String lbp_dir = "lbps";
			File dir = new File(pict_dir);
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
				}
			}
		}
		else
		{
			for (String arg : args)
			{
				String dir = "fun";
				File file = new File(dir+"\\pictures\\"+arg);
				File writeFile = new File(dir+"\\lbps\\"+arg);
				
				// load the image
		        ColorProcessor image = new ColorProcessor(ImageIO.read(file));
	
		        // set parameters of algorithm
		        final int radius = 1;
		        final int numPoints = 16;
		        final int cells = 4;
		        
		        // initiate algorithm
		        LocalBinaryPatterns descriptor = new LocalBinaryPatterns(radius,numPoints,cells);
		        
		        // obtain the features
		        List<Histogram> features = descriptor.run(image);
		        VisualHistogram vh = new VisualHistogram(features,(int) Math.pow(2, numPoints));
		        vh.saveImage(writeFile);
			}
		}
    }
}
