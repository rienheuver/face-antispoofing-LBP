import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import edu.berkeley.compbio.jlibsvm.SolutionModel;
import edu.berkeley.compbio.jlibsvm.binary.BinaryModel;

public class detectSpoof
{
	// set parameters of algorithm
    private static int radius = 1;
    private static int numPoints = 8;
    private static int cells = 4;
    private static File data;
    
	public static void main(String[] args)
	{
		if (args.length < 1)
		{
			System.out.println("Run the system with one of the following parameters:\n"
					+ "lbp\n"
					+ "test <dataFile> <imageFile>"
					+ "stats <dataFile>");
		}
		else
		{
			int[] params = {radius, numPoints, cells};
			switch (args[0])
			{
				case "lbp":
					System.out.println("Starting lbp-algorithm");
					long startTime = System.nanoTime();
					ProcessImage pi = new ProcessImage(params);
					try
					{
						File data = pi.run();
					}
					catch (IOException|URISyntaxException e)
					{
						System.out.println("Training failed");
					}
					System.out.println("Finished in " + ((System.nanoTime() - startTime)/1000000) + " ms");
					
				break;
				
				case "test":
					System.out.println("Starting training");
					startTime = System.nanoTime();
					
					data = new File(args[1]);
					File image = new File(args[2]);
					
					try
					{
						Trainer t = new Trainer(data);
						BinaryModel model = t.run();
						System.out.println("Starting testing");
						Predicter p = new Predicter(model, image, params);
						int label = p.predict();
						System.out.println("Prediction: "+label);
					}
					catch (IOException e)
					{
						System.out.println("IOException with files");
					}
					
					System.out.println("Finished in " + ((System.nanoTime() - startTime)/1000000) + " ms");
				break;
				
				case "stats":
					System.out.println("Starting tests");
					startTime = System.nanoTime();
					
					data = new File(args[1]);
					
					try
					{
						Trainer t = new Trainer(data);
						BinaryModel model = t.run();
						BulkTester bt = new BulkTester(model, params);
						bt.run();
					}
					catch (IOException e)
					{
						System.out.println("IOException with files");
					}
					
					System.out.println("Finished in " + ((System.nanoTime() - startTime)/1000000) + " ms");
			}
		}
	}
}
