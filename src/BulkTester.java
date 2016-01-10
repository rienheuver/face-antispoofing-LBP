import java.io.File;
import java.io.IOException;

import edu.berkeley.compbio.jlibsvm.binary.BinaryModel;

public class BulkTester {
	
	private BinaryModel model;
	private int[] params;
	
	public BulkTester(BinaryModel model, int[] params)
	{
		this.model = model;
		this.params = params;
	}
	
	public void run()
	{
		String pict_dir = "normalized";
		File dir = new File(pict_dir);
		int ta = 0;
		int fa = 0;
		int tr = 0;
		int fr = 0;
		int counter = 0;
		
		long total_time = 0;
		
		for (File subdir : dir.listFiles()) // iterate through main directory
		{
			for (File file : subdir.listFiles()) // iterate through picture directories
			{
				try
				{
					long start = System.nanoTime();
					Predicter p = new Predicter(this.model, file, this.params);
					int label = p.predict();
					if (subdir.getName().equals("original"))
					{
						if (label == 1)
						{
							ta++;
						}
						else
						{
							System.out.println("Falsely rejected: "+subdir.getName()+"/"+file.getName());
							fr++;
						}
					}
					else
					{
						if (label != 1)
						{
							tr++;
						}
						else
						{
							System.out.println("Falsely accepted: "+subdir.getName()+"/"+file.getName());
							fa++;
						}
					}
					counter++;
					total_time += System.nanoTime()-start;
				}
				catch (IOException e)
				{
					System.out.println("IOException with files");
				}
			}
		}
		System.out.println();
		System.out.println("TAR: "+((float) ta/counter*100)+"% ("+ta+"/"+counter);
		System.out.println("FAR: "+((float) fa/counter*100)+"% "+fa+"/"+counter);
		System.out.println("TRR: "+((float) tr/counter*100)+"% "+tr+"/"+counter);
		System.out.println("FRR: "+((float) fr/counter*100)+"% "+fr+"/"+counter);
		System.out.println("Average time: "+(float) (total_time/counter)/1000000+" ms");
		System.out.println("Total tests: "+counter);
	}
}
