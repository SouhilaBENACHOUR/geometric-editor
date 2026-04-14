package fr.ubordeaux.geometriceditor.editor;

import fr.ubordeaux.geometriceditor.model.FormEditor;
import fr.ubordeaux.geometriceditor.model.Rectangle;

public class RectangleFormEditor implements FormEditor {

    private final Rectangle rect;

    public RectangleFormEditor(Rectangle rect) {
        this.rect = rect;
    }

    @Override
    public void runModal() {
        // Sera remplacé par une vraie boîte de dialogue AWT
        System.out.println("Edit Rectangle:"
            + " w=" + rect.getWidth()
            + " h=" + rect.getHeight()
            + " arc=" + rect.getArcRadius()
            + " color=" + rect.getColor());
    }
}