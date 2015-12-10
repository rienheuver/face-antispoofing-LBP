import java.awt.image.BufferedImage;
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
					File lbp_file = new File(lbp_dir+"\\"+file.getParentFile().getName()+"\\"+file.getName());
					
					// load the image
			        ColorProcessor image = new ColorProcessor(ImageIO.read(file));

			        // set parameters of algorithm
			        final int radius = 1;
			        final int numPoints = 16;
			        final int histogramBins = 2;
			        
			        // initiate algorithm
			        LocalBinaryPatterns descriptor = new LocalBinaryPatterns(radius,numPoints,histogramBins);
			        
			        // obtain the features
			        List<double[]> features = descriptor.run(image);
		
			        BufferedImage img = new BufferedImage(image.getWidth(),image.getHeight(),BufferedImage.TYPE_INT_RGB);
			        for (double[] feature : features)
			        {
			            int colour = feature[2] == 1 ? -1 : 0;
			            img.setRGB((int) feature[0], (int) feature[1], colour);
			        }
			        ImageIO.write(img,  "jpg", lbp_file);
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
		        final int histogramBins = 2;
		        
		        // initiate algorithm
		        LocalBinaryPatterns descriptor = new LocalBinaryPatterns(radius,numPoints,histogramBins);
		        
		        // obtain the features
		        List<double[]> features = descriptor.run(image);
	
		        BufferedImage img = new BufferedImage(image.getWidth(),image.getHeight(),BufferedImage.TYPE_INT_RGB);
		        for (double[] feature : features)
		        {
		            int colour = feature[2] == 1 ? -1 : 0;
		            img.setRGB((int) feature[0], (int) feature[1], colour);
		        }
		        ImageIO.write(img,  "jpg", writeFile);
			}
		}
    }
}
