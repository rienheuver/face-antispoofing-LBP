import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

class VisualHistogram extends JPanel {
	
	static final long serialVersionUID = 36;

    protected static final int MIN_BAR_WIDTH = 4;
    private List<Histogram> histograms;
    private int maxKey;

    public VisualHistogram(List<Histogram> histograms, int maxKey) {
        this.histograms = histograms;
        int width = 0;
        for (Histogram h : histograms)
        {
        	width += (h.getHistogram().size() * MIN_BAR_WIDTH) + 11;
        }
        Dimension minSize = new Dimension(width, 128);
        Dimension prefSize = new Dimension(width, 256);
        setMinimumSize(minSize);
        setPreferredSize(prefSize);
        setSize(prefSize);
        this.maxKey = maxKey;
    }
    
    public void saveImage(File file)
    {
    	BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
    	Graphics2D g = image.createGraphics();
    	printAll(g);
    	g.dispose();
    	try { 
    	    ImageIO.write(image, "jpg", file); 
    	} catch (IOException e) {
    	    e.printStackTrace();
    	}
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (histograms != null) {
            int xOffset = 5;
            int yOffset = 5;
            int width = getWidth() - 1 - (xOffset * 2);
            int height = getHeight() - 1 - (yOffset * 2);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setColor(Color.DARK_GRAY);
            g2d.drawRect(xOffset, yOffset, width, height);
            int barWidth = MIN_BAR_WIDTH;//Math.max(MIN_BAR_WIDTH, (int) Math.floor((float) width / (float) histograms.get(0).getHistogram().size()));
            int maxValue = 0;
            for (Histogram h : histograms)
            {
            	Map<Integer,Integer> map = h.getHistogram();
	            for (Integer key : map.keySet()) {
	                int value = map.get(key);
	                maxValue = Math.max(maxValue, value);
	            }
            }
            int xPos = xOffset;
            for (Histogram h : histograms)
            {
            	Map<Integer,Integer> map = h.getHistogram();
	            for (Integer key : map.keySet()) {
	                int value = map.get(key);
	                int barHeight = Math.round(((float) value / (float) maxValue) * height);
	            	int colourCode = Math.round((float) key/(float) maxKey*255);
	                g2d.setColor(new Color(colourCode,colourCode,colourCode));
	                int yPos = height + yOffset - barHeight;
	                //Rectangle bar = new Rectangle(xPos, yPos, barWidth, barHeight);
	                Rectangle2D bar = new Rectangle2D.Float(xPos, yPos, barWidth, barHeight);
	                g2d.fill(bar);
	                g2d.setColor(Color.DARK_GRAY);
	                g2d.draw(bar);
	                xPos += barWidth;
	            }
            }
            g2d.dispose();
        }
    }
}