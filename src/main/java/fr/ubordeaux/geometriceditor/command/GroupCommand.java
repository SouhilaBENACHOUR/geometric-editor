package fr.ubordeaux.geometriceditor.command;

import fr.ubordeaux.geometriceditor.model.Form;
import fr.ubordeaux.geometriceditor.model.FormComposite;

import java.util.List;

public class GroupCommand implements Command {

    private final FormComposite parent;
    private final List<Form> forms;
    private final FormComposite newGroup;

    public GroupCommand(FormComposite parent, List<Form> forms) {
        this.parent   = parent;
        this.forms    = List.copyOf(forms);
        this.newGroup = new FormComposite();
    }

    @Override
    public void execute() {
        for (Form f : forms) {
            parent.remove(f);
            newGroup.add(f);
        }
        parent.add(newGroup);
    }

    @Override
    public void undo() {
        parent.remove(newGroup);
        for (Form f : forms) {
            parent.add(f);
        }
    }

    public FormComposite getNewGroup() { return newGroup; }
}