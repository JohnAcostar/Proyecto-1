package gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.JPanel;

public class BarChartPanel extends JPanel {
    private final String title;
    private List<String> labels = new ArrayList<>();
    private List<Double> cafe = new ArrayList<>();
    private List<Double> juegos = new ArrayList<>();

    public BarChartPanel(String title) {
        this.title = title;
        setOpaque(false);
        setPreferredSize(new Dimension(440, 260));
    }

    public void setData(List<String> labels, List<Double> cafe, List<Double> juegos) {
        this.labels = new ArrayList<>(labels);
        this.cafe = new ArrayList<>(cafe);
        this.juegos = new ArrayList<>(juegos);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int left = 62;
        int top = 44;
        int right = 26;
        int bottom = 54;
        int width = getWidth() - left - right;
        int height = getHeight() - top - bottom;

        g2.setColor(new Color(31, 41, 55));
        g2.setFont(getFont().deriveFont(Font.BOLD, 16f));
        g2.drawString(title, 16, 24);

        double max = 1;
        for (Double value : cafe) {
            max = Math.max(max, value);
        }
        for (Double value : juegos) {
            max = Math.max(max, value);
        }

        g2.setColor(new Color(229, 231, 235));
        g2.setStroke(new BasicStroke(1f));
        for (int i = 0; i <= 4; i++) {
            int y = top + (height * i / 4);
            g2.drawLine(left, y, left + width, y);
        }

        g2.setColor(new Color(75, 85, 99));
        g2.drawLine(left, top, left, top + height);
        g2.drawLine(left, top + height, left + width, top + height);

        int groups = Math.max(labels.size(), 1);
        int groupWidth = width / groups;
        int barWidth = Math.max(12, Math.min(28, groupWidth / 5));
        for (int i = 0; i < labels.size(); i++) {
            int center = left + groupWidth * i + groupWidth / 2;
            drawBar(g2, center - barWidth - 2, top, height, barWidth, cafe.get(i), max, new Color(14, 165, 233));
            drawBar(g2, center + 2, top, height, barWidth, juegos.get(i), max, new Color(245, 158, 11));
            g2.setColor(new Color(75, 85, 99));
            g2.setFont(getFont().deriveFont(12f));
            g2.drawString(labels.get(i), center - 18, top + height + 20);
        }

        g2.setFont(getFont().deriveFont(12f));
        g2.setColor(new Color(14, 165, 233));
        g2.fillRect(left, getHeight() - 24, 12, 12);
        g2.setColor(new Color(55, 65, 81));
        g2.drawString("Cafeteria", left + 18, getHeight() - 14);
        g2.setColor(new Color(245, 158, 11));
        g2.fillRect(left + 110, getHeight() - 24, 12, 12);
        g2.setColor(new Color(55, 65, 81));
        g2.drawString("Juegos", left + 128, getHeight() - 14);
        g2.drawString(String.format(Locale.US, "Max $%,.0f", max), left, top - 10);
        g2.dispose();
    }

    private void drawBar(Graphics2D g2, int x, int top, int height, int width, double value, double max, Color color) {
        int barHeight = (int) Math.round(value / max * height);
        g2.setColor(color);
        g2.fillRoundRect(x, top + height - barHeight, width, barHeight, 5, 5);
    }
}
