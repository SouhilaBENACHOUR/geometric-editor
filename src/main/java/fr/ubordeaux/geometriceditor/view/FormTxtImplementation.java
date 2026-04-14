package fr.ubordeaux.geometriceditor.view;

import fr.ubordeaux.geometriceditor.model.Form;
import fr.ubordeaux.geometriceditor.model.FormComposite;
import fr.ubordeaux.geometriceditor.model.Rectangle;
import fr.ubordeaux.geometriceditor.model.RegularPolygon;

public class FormTxtImplementation extends FormImplementation {

    @Override
    public void draw_impl(Form f) {
        if (f instanceof Rectangle) {
            Rectangle r = (Rectangle) f;
            System.out.println("Rectangle at (" + r.x() + "," + r.y()
                + ") w=" + r.getWidth() + " h=" + r.getHeight()
                + " arc=" + r.getArcRadius()
                + " color=" + r.getColor());

        } else if (f instanceof RegularPolygon) {
            RegularPolygon p = (RegularPolygon) f;
            System.out.println("Polygon at (" + p.x() + "," + p.y()
                + ") sides=" + p.getSides()
                + " sideLength=" + p.getSideLength()
                + " color=" + p.getColor());

        } else if (f instanceof FormComposite) {
            FormComposite c = (FormComposite) f;
            System.out.println("Group at (" + c.x() + "," + c.y()
                + ") size=" + c.size());
            for (Form child : c) draw_impl(child);

        } else {
            System.out.println("Form at (" + f.x() + "," + f.y()
                + ") color=" + f.getColor());
        }
    }
}