
public class binaryTest
{
	public static void main(String[] args)
	{
		int pattern = 0;
		String stringPattern = "";
		for (int i=0;i<8;i++)
		{
			if (i%3==0)
			{
				pattern |= 1 << i;
				stringPattern = "1" + stringPattern;
			}
			else
			{
				stringPattern = "0" + stringPattern;
			}
		}
		System.out.println(Integer.toBinaryString(pattern));
		System.out.println(stringPattern);
		System.out.println(pattern);
		System.out.println(Integer.parseInt(stringPattern,2));
	}
}
