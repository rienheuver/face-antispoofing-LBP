import java.util.Map;
import java.util.TreeMap;

public class Histogram
{

	private Map<Integer, Integer> histogram;

	public Histogram(int bins)
	{
		this.histogram = new TreeMap<Integer, Integer>();
	}

	public void add(int value)
	{
		int amount = 0;
		if (histogram.containsKey(value))
		{
			amount = histogram.get(value);
			amount++;
		}
		else
		{
			amount = 1;
		}
		histogram.put(value, amount);
	}

	public Map<Integer,Integer> getHistogram()
	{
		return histogram;
	}

	public int size()
	{
		return histogram.size();
	}
}
