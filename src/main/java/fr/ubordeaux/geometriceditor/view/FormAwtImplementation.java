package fr.ubordeaux.geometriceditor.view;

import fr.ubordeaux.geometriceditor.model.Form;
import fr.ubordeaux.geometriceditor.model.FormComposite;
import fr.ubordeaux.geometriceditor.model.Rectangle;
import fr.ubordeaux.geometriceditor.model.RegularPolygon;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

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

        if (f instanceof Rectangle) {
            drawRectangle((Rectangle) f);
        } else if (f instanceof RegularPolygon) {
            drawPolygon((RegularPolygon) f);
        } else if (f instanceof FormComposite) {
            for (Form child : (FormComposite) f) draw_impl(child);
        }
    }

    private void drawRectangle(Rectangle r) {
        int arc = r.getArcRadius();
        if (arc > 0) {
            g2d.fillRoundRect(r.x(), r.y(), r.getWidth(), r.getHeight(), arc, arc);
        } else {
            g2d.fillRect(r.x(), r.y(), r.getWidth(), r.getHeight());
        }
    }

    private void drawPolygon(RegularPolygon p) {
        int n     = p.getSides();
        double r  = p.getCircumradius();
        int[] xs  = new int[n];
        int[] ys  = new int[n];
        for (int i = 0; i < n; i++) {
            double angle = 2 * Math.PI * i / n - Math.PI / 2;
            xs[i] = (int) (p.x() + r * Math.cos(angle));
            ys[i] = (int) (p.y() + r * Math.sin(angle));
        }
        g2d.fillPolygon(xs, ys, n);
    }
}