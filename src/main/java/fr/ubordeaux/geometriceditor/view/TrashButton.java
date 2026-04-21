package fr.ubordeaux.geometriceditor.view;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;

public class TrashButton extends Canvas {

    private final Image icon;
    private boolean     hovered = false;

    public TrashButton() {
        this.icon = IconLoader.load("trash.png");
        setPreferredSize(new Dimension(82, 56));
        setBackground(new Color(245, 245, 245));

        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                hovered = true; repaint();
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                hovered = false; repaint();
            }
        });
    }

    @Override
    public void paint(Graphics g) {
        g.setColor(hovered ? new Color(255, 220, 220) : new Color(245, 245, 245));
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(new Color(200, 180, 180));
        g.drawLine(0, 0, getWidth(), 0);

        if (icon != null) {
            g.drawImage(icon, (getWidth() - 24) / 2, 4, 24, 24, this);
        }

        g.setColor(hovered ? new Color(180, 60, 60) : new Color(150, 100, 100));
        g.setFont(new Font("Arial", Font.PLAIN, 11));
        String txt = "Suppr.";
        g.drawString(txt, (getWidth() - g.getFontMetrics().stringWidth(txt)) / 2, 44);
    }

    public boolean contains(int screenX, int screenY) {
        java.awt.Point loc = getLocationOnScreen();
        return screenX >= loc.x && screenX <= loc.x + getWidth()
            && screenY >= loc.y && screenY <= loc.y + getHeight();
    }
}