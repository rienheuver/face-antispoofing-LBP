public class Histogram {

    private int[] histogram;

    public Histogram(int bins) {
        this.histogram = new int[(int) Math.pow(2, bins)];
    }

    public void add(int value) {
    	histogram[value]++;
    }
    
    public int[] getHistogram() {
        return histogram;
    }
    
    public int size()
    {
    	return histogram.length;
    }
}
