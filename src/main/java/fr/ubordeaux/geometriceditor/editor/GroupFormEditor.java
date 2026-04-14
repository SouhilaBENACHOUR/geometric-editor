package fr.ubordeaux.geometriceditor.editor;

import fr.ubordeaux.geometriceditor.model.FormComposite;
import fr.ubordeaux.geometriceditor.model.FormEditor;

public class GroupFormEditor implements FormEditor {

    private final FormComposite group;

    public GroupFormEditor(FormComposite group) {
        this.group = group;
    }

    @Override
    public void runModal() {
        // Sera remplacé par une vraie boîte de dialogue AWT
        System.out.println("Edit Group at ("
            + group.x() + "," + group.y()
            + ") size=" + group.size());
    }
}