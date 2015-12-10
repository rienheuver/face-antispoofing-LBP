import java.util.ArrayList;
import java.util.List;

import ij.process.ImageProcessor;

public class LocalBinaryPatterns {

    private double radius;
    private int numPoints;
    private int histogramSize;

    private double angle;
    private double[] offsets;
    private ImageProcessor ip;
    
    private List<double[]> data = new ArrayList<>(1);

    public LocalBinaryPatterns(double radius, int numPoints, int histogramBins) {
    	this.radius = radius;
    	this.numPoints = numPoints;
        this.angle = 2.0 * Math.PI / numPoints;
        this.histogramSize = histogramBins;
    }

    public List<double[]> run(ImageProcessor ip) {
        final int width = ip.getWidth();
        final int height = ip.getHeight();
        this.ip = ip.convertToByte(true);

        offsets = new double[numPoints * 2];
        for (int i = 0; i < numPoints; i++) {
            double a = i * angle;
            offsets[i * 2] = radius * Math.cos(a);
            offsets[i * 2 + 1] = radius * Math.sin(a);
        }

        for (int y = 0; y < height ; y++) {
            for (int x = 0; x < width; x++) {
            	this.data.add(processPixel(x, y));
            }
        }
        return data;
    }

    private double[] processPixel(final int x, final int y) {
        int xStart = Math.max(x, 0);
        int xEnd = Math.min(x + 1, ip.getWidth());
        int yStart = Math.max(y, 0);
        int yEnd = Math.min(y + 1, ip.getHeight());

        Histogram hist = new Histogram(histogramSize, (int) Math.pow(2, numPoints));

        for (int yi = yStart; yi < yEnd; yi++) {
            for (int xi = xStart; xi < xEnd; xi++) {
                hist.add(getBinaryPattern(xi, yi));
            }
        }

        double[] histarr = hist.getHistogramm();
        double[] data = new double[histarr.length + 2];
        data[0] = x;
        data[1] = y;
        System.arraycopy(histarr, 0, data, 2, histarr.length);

        return data;
    }

    private int getBinaryPattern(final int x, final int y) {
        final float centerPixel = ip.getf(x, y);
        int pattern = 0;
        for (int i = 0; i < numPoints; i++) {
            double xi = x + offsets[i * 2];
            double yi = y + offsets[i * 2 + 1];
            if (xi < 0 || xi >= ip.getWidth() || yi < 0 || yi >= ip.getHeight())
                return 0;

            double val = ip.getInterpolatedPixel(xi, yi);
            if (val > centerPixel) {
                pattern |= 1 << i;
            }
        }
        return pattern;
    }
}
