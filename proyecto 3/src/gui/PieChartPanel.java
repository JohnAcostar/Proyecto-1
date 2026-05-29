package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Arc2D;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JPanel;

public class PieChartPanel extends JPanel {
    private final Color[] colors = {
            new Color(37, 99, 235), new Color(16, 185, 129), new Color(245, 158, 11)
    };
    private String title;
    private Map<String, Double> values = new LinkedHashMap<>();

    public PieChartPanel(String title) {
        this.title = title;
        setOpaque(false);
        setPreferredSize(new Dimension(360, 260));
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setValues(Map<String, Double> values) {
        this.values = values == null ? new LinkedHashMap<>() : new LinkedHashMap<>(values);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(31, 41, 55));
        g2.setFont(getFont().deriveFont(Font.BOLD, 16f));
        g2.drawString(title, 16, 24);

        double total = values.values().stream().mapToDouble(Double::doubleValue).sum();
        if (total <= 0) {
            drawEmpty(g2);
            g2.dispose();
            return;
        }

        int size = Math.min(getWidth() - 190, getHeight() - 70);
        size = Math.max(size, 120);
        int x = 24;
        int y = 48;
        double start = 90;
        int i = 0;
        for (Map.Entry<String, Double> entry : values.entrySet()) {
            double angle = entry.getValue() / total * 360.0;
            g2.setColor(colors[i % colors.length]);
            g2.fill(new Arc2D.Double(x, y, size, size, start, -angle, Arc2D.PIE));
            start -= angle;
            i++;
        }

        i = 0;
        int legendX = x + size + 24;
        int legendY = y + 18;
        g2.setFont(getFont().deriveFont(13f));
        for (Map.Entry<String, Double> entry : values.entrySet()) {
            g2.setColor(colors[i % colors.length]);
            g2.fillRoundRect(legendX, legendY - 10, 14, 14, 4, 4);
            g2.setColor(new Color(55, 65, 81));
            String text = String.format("%s: %.0f (%.0f%%)", entry.getKey(), entry.getValue(),
                    entry.getValue() / total * 100.0);
            g2.drawString(text, legendX + 22, legendY + 2);
            legendY += 24;
            i++;
        }
        g2.dispose();
    }

    private void drawEmpty(Graphics2D g2) {
        g2.setColor(new Color(107, 114, 128));
        g2.setFont(getFont().deriveFont(14f));
        g2.drawString("No hay datos disponibles para graficar.", 24, 72);
    }
}
