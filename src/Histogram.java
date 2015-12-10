public class Histogram {

    final double[] histogram;
    final double binWidth, maxValue;

    public Histogram(int bins, double maxValue) {
        this.histogram = new double[bins];
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
    
    public double[] getHistogramm() {
        return histogram;
    }
}
