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
    
	public static void main(String[] args)
	{
		if (args.length < 1)
		{
			System.out.println("Run the system with one of the following parameters:\n"
					+ "train\n"
					+ "test <modelFile> <imageFile>");
		}
		else
		{
			int[] params = {radius, numPoints, cells};
			switch (args[0])
			{
				case "train":
					System.out.println("Starting training");
					ProcessImage pi = new ProcessImage(params);
					try
					{
						File data = pi.run();
						Trainer t = new Trainer(data);
						t.run();
					}
					catch (IOException|URISyntaxException e)
					{
						System.out.println("Training failed");
					}
					
				break;
				
				case "test":
					BinaryModel model = (BinaryModel) SolutionModel.identifyTypeAndLoad(args[1]);
					File image = new File(args[2]);
					
					Predicter p = new Predicter(model, image, params);
				break;
			}
		}
	}
}
