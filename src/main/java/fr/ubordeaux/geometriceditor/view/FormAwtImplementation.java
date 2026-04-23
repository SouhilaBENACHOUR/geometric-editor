package fr.ubordeaux.geometriceditor.view;

import fr.ubordeaux.geometriceditor.model.Form;
import fr.ubordeaux.geometriceditor.model.FormAbstract;
import fr.ubordeaux.geometriceditor.model.FormComposite;
import fr.ubordeaux.geometriceditor.model.Rectangle;
import fr.ubordeaux.geometriceditor.model.RegularPolygon;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;

public class FormAwtImplementation extends FormImplementation {

    private Graphics2D g2d;

    public void setGraphics(Graphics g) {
        this.g2d = (Graphics2D) g;
        g2d.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON
        );
    }

    @Override
    public void draw_impl(Form f) {
        if (g2d == null) return;

        g2d.setColor(new Color(f.getColor()));

        // Sauvegarde la transformation courante
        AffineTransform saved = g2d.getTransform();

        // Applique la rotation si la forme en a une
        double rotation = f.getRotation();
        if (rotation != 0) {
            // Centre de rotation = centre de la forme
            int cx = getCenterX(f);
            int cy = getCenterY(f);
            g2d.rotate(Math.toRadians(rotation), cx, cy);
        }

        if (f instanceof Rectangle) {
            drawRectangle((Rectangle) f);
        } else if (f instanceof RegularPolygon) {
            drawPolygon((RegularPolygon) f);
        } else if (f instanceof FormComposite) {
            for (Form child : (FormComposite) f) draw_impl(child);
        }

        // Restaure la transformation
        g2d.setTransform(saved);
    }

    private int getCenterX(Form f) {
        if (f instanceof Rectangle) {
            Rectangle r = (Rectangle) f;
            return r.x() + r.getWidth() / 2;
        }
        return f.x();
    }

    private int getCenterY(Form f) {
        if (f instanceof Rectangle) {
            Rectangle r = (Rectangle) f;
            return r.y() + r.getHeight() / 2;
        }
        return f.y();
    }

    private void drawRectangle(Rectangle r) {
        int arc = r.getArcRadius();
        if (arc > 0) {
            g2d.fillRoundRect(r.x(), r.y(), r.getWidth(), r.getHeight(), arc, arc);
            g2d.setColor(g2d.getColor().darker());
            g2d.drawRoundRect(r.x(), r.y(), r.getWidth(), r.getHeight(), arc, arc);
        } else {
            g2d.fillRect(r.x(), r.y(), r.getWidth(), r.getHeight());
            g2d.setColor(g2d.getColor().darker());
            g2d.drawRect(r.x(), r.y(), r.getWidth(), r.getHeight());
        }
    }

    private void drawPolygon(RegularPolygon p) {
        int    n   = p.getSides();
        double r   = p.getCircumradius();
        int[]  xs  = new int[n];
        int[]  ys  = new int[n];

        for (int i = 0; i < n; i++) {
            double angle = 2 * Math.PI * i / n - Math.PI / 2;
            xs[i] = (int) (p.x() + r * Math.cos(angle));
            ys[i] = (int) (p.y() + r * Math.sin(angle));
        }
        g2d.fillPolygon(xs, ys, n);
        g2d.setColor(g2d.getColor().darker());
        g2d.drawPolygon(xs, ys, n);
    }
}