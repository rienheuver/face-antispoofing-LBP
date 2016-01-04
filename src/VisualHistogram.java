import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.Map;

import javax.swing.JPanel;

class VisualHistogram extends JPanel {
	
	static final long serialVersionUID = 36;

    protected static final int MIN_BAR_WIDTH = 4;
    private Map<Integer, Integer> mapHistory;
    private int maxKey;

    public VisualHistogram(Map<Integer, Integer> mapHistory, int maxKey) {
        this.mapHistory = mapHistory;
        int width = (mapHistory.size() * MIN_BAR_WIDTH) + 11;
        Dimension minSize = new Dimension(width, 128);
        Dimension prefSize = new Dimension(width, 256);
        setMinimumSize(minSize);
        setPreferredSize(prefSize);
        this.maxKey = maxKey;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (mapHistory != null) {
            int xOffset = 5;
            int yOffset = 5;
            int width = getWidth() - 1 - (xOffset * 2);
            int height = getHeight() - 1 - (yOffset * 2);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setColor(Color.DARK_GRAY);
            g2d.drawRect(xOffset, yOffset, width, height);
            int barWidth = Math.max(MIN_BAR_WIDTH, (int) Math.floor((float) width / (float) mapHistory.size()));
            int maxValue = 0;
            for (Integer key : mapHistory.keySet()) {
                int value = mapHistory.get(key);
                maxValue = Math.max(maxValue, value);
            }
            int xPos = xOffset;
            for (Integer key : mapHistory.keySet()) {
                int value = mapHistory.get(key);
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
            g2d.dispose();
        }
    }
}