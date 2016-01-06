import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

public class FeatureFile
{
	private String fileContents = "";
	
	public void addImage(List<Histogram> features, int label)
	{
        //<label> <index1>:<value1> <index2>:<value2> ...
		String addition = label+" ";
		for (Histogram h : features)
		{
			Map<Integer,Integer> m = h.getHistogram();
			for (Integer i : m.keySet())
			{
				addition += i+":"+m.get(i)+" ";
			}
		}
		fileContents += addition.substring(0,(addition.length()-1))+"\n";
	}
	
	public void save(File file)
	{
		try
		{
			PrintWriter pw = new PrintWriter(file);
			pw.write(fileContents);
			pw.close();
		}
		catch (FileNotFoundException e)
		{
			System.out.println("File not found");
		}
	}
}
