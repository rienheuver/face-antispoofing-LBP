public class Histogram {

    final int[] histogram;
    final double binWidth, maxValue;

    public Histogram(int bins, double maxValue) {
        this.histogram = new int[bins];
        this.binWidth = maxValue / bins;
        this.maxValue = maxValue;
    }

    public void add(double value) {
        int bin = (int) (value / binWidth);
        if (bin > histogram.length) {
            bin--;
        }
        histogram[bin]++;
    }
    
    public int[] getHistogram() {
        return histogram;
    }
}
