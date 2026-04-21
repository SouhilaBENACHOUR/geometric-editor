package fr.ubordeaux.geometriceditor.view;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class IconButton extends Canvas {

    private final Image  icon;
    private final String tooltip;
    private boolean      hovered = false;
    private Runnable     onClick;

    public IconButton(String iconName, String tooltip) {
        this.icon    = IconLoader.load(iconName);
        this.tooltip = tooltip;
        setPreferredSize(new Dimension(40, 40));
        setBackground(new Color(235, 235, 235));

        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
            @Override public void mouseExited (MouseEvent e) { hovered = false; repaint(); }
            @Override public void mouseClicked(MouseEvent e) {
                if (onClick != null) onClick.run();
            }
        });
    }

    public void setOnClick(Runnable r) { this.onClick = r; }

    @Override
    public void paint(Graphics g) {
        // Fond
        g.setColor(hovered ? new Color(210, 210, 210) : new Color(235, 235, 235));
        g.fillRect(0, 0, getWidth(), getHeight());

        // Bordure légère au survol
        if (hovered) {
            g.setColor(new Color(180, 180, 180));
            g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
        }

        // Icône centrée
        if (icon != null) {
            g.drawImage(icon, (getWidth() - 24) / 2, (getHeight() - 24) / 2,
                        24, 24, this);
        } else {
            // Fallback texte si icône manquante
            g.setColor(Color.DARK_GRAY);
            g.setFont(new Font("Arial", Font.PLAIN, 10));
            g.drawString(tooltip.substring(0, Math.min(4, tooltip.length())),
                         4, getHeight() / 2 + 4);
        }
    }
}