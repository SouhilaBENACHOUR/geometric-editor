package fr.ubordeaux.geometriceditor.editor;

import fr.ubordeaux.geometriceditor.model.FormAbstract;
import fr.ubordeaux.geometriceditor.model.FormEditor;

public class GenericFormEditor implements FormEditor {

    private final FormAbstract form;

    public GenericFormEditor(FormAbstract form) {
        this.form = form;
    }

    @Override
    public void runModal() {
        // Sera remplacé par une vraie boîte de dialogue AWT à l'étape vue
        System.out.println("Edit form at ("
            + form.x() + "," + form.y()
            + ") color=" + form.getColor());
    }
}