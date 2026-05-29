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

import javax.swing.JPanel;

public class LineChartPanel extends JPanel {
    private final String title;
    private List<String> labels = new ArrayList<>();
    private List<Double> values = new ArrayList<>();

    public LineChartPanel(String title) {
        this.title = title;
        setOpaque(false);
        setPreferredSize(new Dimension(820, 260));
    }

    public void setData(List<String> labels, List<Double> values) {
        this.labels = new ArrayList<>(labels);
        this.values = new ArrayList<>(values);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int left = 58;
        int top = 44;
        int right = 28;
        int bottom = 48;
        int width = getWidth() - left - right;
        int height = getHeight() - top - bottom;

        g2.setColor(new Color(31, 41, 55));
        g2.setFont(getFont().deriveFont(Font.BOLD, 16f));
        g2.drawString(title, 16, 24);

        double max = Math.max(1, values.stream().mapToDouble(Double::doubleValue).max().orElse(1));
        g2.setColor(new Color(229, 231, 235));
        for (int i = 0; i <= 4; i++) {
            int y = top + (height * i / 4);
            g2.drawLine(left, y, left + width, y);
        }
        g2.setColor(new Color(75, 85, 99));
        g2.drawLine(left, top, left, top + height);
        g2.drawLine(left, top + height, left + width, top + height);

        if (values.isEmpty()) {
            g2.drawString("No hay reservas para mostrar.", left + 20, top + 40);
            g2.dispose();
            return;
        }

        int previousX = -1;
        int previousY = -1;
        g2.setStroke(new BasicStroke(3f));
        g2.setColor(new Color(16, 185, 129));
        for (int i = 0; i < values.size(); i++) {
            int x = left + (values.size() == 1 ? width / 2 : i * width / (values.size() - 1));
            int y = top + height - (int) Math.round(values.get(i) / max * height);
            if (previousX >= 0) {
                g2.drawLine(previousX, previousY, x, y);
            }
            previousX = x;
            previousY = y;
        }

        g2.setStroke(new BasicStroke(1f));
        g2.setFont(getFont().deriveFont(12f));
        for (int i = 0; i < values.size(); i++) {
            int x = left + (values.size() == 1 ? width / 2 : i * width / (values.size() - 1));
            int y = top + height - (int) Math.round(values.get(i) / max * height);
            g2.setColor(Color.WHITE);
            g2.fillOval(x - 6, y - 6, 12, 12);
            g2.setColor(new Color(16, 185, 129));
            g2.fillOval(x - 4, y - 4, 8, 8);
            g2.setColor(new Color(55, 65, 81));
            g2.drawString(labels.get(i), x - 12, top + height + 22);
            g2.drawString(String.valueOf(values.get(i).intValue()), x - 4, y - 12);
        }
        g2.setColor(new Color(55, 65, 81));
        g2.drawString("Max " + (int) Math.ceil(max) + " reservas", left, top - 10);
        g2.dispose();
    }
}
