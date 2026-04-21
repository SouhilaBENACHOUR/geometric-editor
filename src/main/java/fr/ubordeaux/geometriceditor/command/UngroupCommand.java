package fr.ubordeaux.geometriceditor.command;

import fr.ubordeaux.geometriceditor.model.Form;
import fr.ubordeaux.geometriceditor.model.FormComposite;

import java.util.ArrayList;
import java.util.List;

public class UngroupCommand implements Command {

    private final FormComposite parent;
    private final FormComposite group;
    private final List<Form> children;

    public UngroupCommand(FormComposite parent, FormComposite group) {
        this.parent   = parent;
        this.group    = group;
        this.children = new ArrayList<>();
        for (Form f : group) this.children.add(f);
    }

    @Override
    public void execute() {
        parent.remove(group);
        for (Form f : children) parent.add(f);
    }

    @Override
    public void undo() {
        for (Form f : children) parent.remove(f);
        parent.add(group);
    }
}