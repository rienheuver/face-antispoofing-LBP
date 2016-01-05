import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class detectSpoof
{
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
			switch (args[0])
			{
				case "train":
					System.out.println("Starting training");
					ProcessImage pi = new ProcessImage();
					try
					{
						File model = pi.run();
						Trainer t = new Trainer(model);
						t.run();
					}
					catch (IOException|URISyntaxException e)
					{
						System.out.println("Training failed");
					}
					
				break;
				
				case "test":
					File model = new File(args[1]);
					File image = new File(args[2]);
				break;
			}
		}
	}
}
