package fr.ubordeaux.geometriceditor.command;

import fr.ubordeaux.geometriceditor.model.Form;
import fr.ubordeaux.geometriceditor.model.FormComposite;

public class RemoveShapeCommand implements Command {

    private final FormComposite target;
    private final Form form;

    public RemoveShapeCommand(FormComposite target, Form form) {
        this.target = target;
        this.form   = form;
    }

    @Override public void execute() { target.remove(form); }
    @Override public void undo()    { target.add(form); }
}