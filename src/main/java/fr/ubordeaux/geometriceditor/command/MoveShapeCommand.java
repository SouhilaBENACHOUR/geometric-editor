package fr.ubordeaux.geometriceditor.command;

import fr.ubordeaux.geometriceditor.model.Form;

public class MoveShapeCommand implements Command {

    private final Form form;
    private final int dx, dy;

    public MoveShapeCommand(Form form, int dx, int dy) {
        this.form = form;
        this.dx   = dx;
        this.dy   = dy;
    }

    @Override
    public void execute() { form.set(form.x() + dx, form.y() + dy); }

    @Override
    public void undo()    { form.set(form.x() - dx, form.y() - dy); }
}