package fr.ubordeaux.geometriceditor.view;

import fr.ubordeaux.geometriceditor.model.Form;
import fr.ubordeaux.geometriceditor.model.Rectangle;
import fr.ubordeaux.geometriceditor.model.RegularPolygon;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ToolbarButton extends Canvas {

    private final Form prototype;
    private final Image icon;
    private final String label;
    private boolean hovered  = false;
    private boolean selected = false;

    private Runnable onClick;

    public ToolbarButton(Form prototype, String iconName, String label) {
        this.prototype = prototype;
        this.icon      = IconLoader.load(iconName);
        this.label     = label;

        setPreferredSize(new Dimension(70, 64));
        setBackground(Color.WHITE);

        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
            @Override public void mouseExited (MouseEvent e) { hovered = false; repaint(); }
            @Override public void mouseClicked(MouseEvent e) {
                if (onClick != null) onClick.run();
                repaint();
            }
        });
    }

    public void setOnClick(Runnable r)  { this.onClick = r; }
    public void setSelected(boolean s)  { this.selected = s; repaint(); }
    public Form getPrototype()          { return prototype; }

    @Override
    public void paint(Graphics g) {
        // Fond
        if (selected) {
            g.setColor(new Color(200, 220, 255));
        } else if (hovered) {
            g.setColor(new Color(230, 230, 230));
        } else {
            g.setColor(Color.WHITE);
        }
        g.fillRect(0, 0, getWidth(), getHeight());

        // Bordure
        g.setColor(new Color(180, 180, 180));
        g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);

        // Icône PNG centrée
        if (icon != null) {
            g.drawImage(icon, (getWidth() - 32) / 2, 6, 32, 32, this);
        }

        // Label sous l'icône
        g.setColor(Color.DARK_GRAY);
        g.setFont(new Font("Arial", Font.PLAIN, 11));
        int labelX = (getWidth() - g.getFontMetrics().stringWidth(label)) / 2;
        g.drawString(label, labelX, 54);
    }
}