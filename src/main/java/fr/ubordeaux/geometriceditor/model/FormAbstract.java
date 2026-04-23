package fr.ubordeaux.geometriceditor.model;

import fr.ubordeaux.geometriceditor.view.FormImplementation;

public abstract class FormAbstract extends Form {

    private int    color;
    private int    x, y;
    private double rotation = 0;

    public FormAbstract(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void draw() {
        FormImplementation.getInstance().draw_impl(this);
    }

    @Override
    public FormAbstract set(int x, int y) {
        this.x = x;
        this.y = y;
        this.notifyObservers();
        return this;
    }

    @Override
    public FormAbstract setColor(int color) {
        this.color = color;
        this.notifyObservers();
        return this;
    }

    @Override
    public double getRotation() {
        return rotation;
    }

    @Override
    public void setRotation(double deg) {
        this.rotation = deg;
        this.notifyObservers();
    }

    @Override public int x()        { return x; }
    @Override public int y()        { return y; }
    @Override public int getColor() { return color; }

    @Override
    public FormEditor createEditor() {
        return new fr.ubordeaux.geometriceditor.editor.GenericFormEditor(this);
    }

    @Override
    public FormAbstract clone() {
        return (FormAbstract) super.clone();
    }
}