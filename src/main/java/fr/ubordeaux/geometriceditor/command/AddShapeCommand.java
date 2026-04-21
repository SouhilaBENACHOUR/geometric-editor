package fr.ubordeaux.geometriceditor.command;

import fr.ubordeaux.geometriceditor.model.Form;
import fr.ubordeaux.geometriceditor.model.FormComposite;

public class AddShapeCommand implements Command {

    private final FormComposite target;
    private final Form form;

    public AddShapeCommand(FormComposite target, Form form) {
        this.target = target;
        this.form   = form;
    }

    @Override public void execute() { target.add(form); }
    @Override public void undo()    { target.remove(form); }
}