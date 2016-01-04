import java.util.ArrayList;
import java.util.List;

import ij.process.ImageProcessor;

public class LocalBinaryPatterns {

    private double radius;
    private int numPoints;
    private int cells;

    private double angle;
    private double[] offsets;
    private ImageProcessor ip;
    
    private List<Histogram> data = new ArrayList<>();

    public LocalBinaryPatterns(double radius, int numPoints, int cells) {
    	this.radius = radius;
    	this.numPoints = numPoints;
        this.angle = 2.0 * Math.PI / numPoints;
        this.cells = cells;
    }

    public List<Histogram> run(ImageProcessor ip) {
        final int width = ip.getWidth();
        final int height = ip.getHeight();
        this.ip = ip.convertToByte(true);

        offsets = new double[numPoints * 2];
        for (int i = 0; i < numPoints; i++) {
            double a = i * angle;
            offsets[i * 2] = radius * Math.cos(a);
            offsets[i * 2 + 1] = radius * Math.sin(a);
        }

        for (int i = 0; i < Math.sqrt(cells); i++) {
        	for (int j = 0; j < Math.sqrt(cells); j++)
        	{
        		Histogram hist = new Histogram(numPoints);

                for (int y = (height/cells)*j; y < (height/cells)*(j+1); y++) {
                    for (int x = (width/cells)*i; x < (width/cells)*(i+1); x++) {
                    	int value = getBinaryPattern(x, y);
                    	if (value >= 0)
                    	{
                    		hist.add(getBinaryPattern(x, y));
                    	}
                    }
                }
                this.data.add(hist);
        	}
        }
        
        return data;
    }

    private int getBinaryPattern(final int x, final int y) {
        final float centerPixel = ip.getf(x, y);
        int pattern = 0;
        for (int i = 0; i < numPoints; i++) {
            double xi = x + offsets[i * 2];
            double yi = y + offsets[i * 2 + 1];
            if (xi < 0 || xi >= ip.getWidth() || yi < 0 || yi >= ip.getHeight())
                return -1;

            double val = ip.getInterpolatedPixel(xi, yi);
            if (val > centerPixel) {
                pattern |= 1 << i;
            }
        }
        return pattern;
    }
}
