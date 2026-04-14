package fr.ubordeaux.geometriceditor.model;

import fr.ubordeaux.geometriceditor.editor.RectangleFormEditor;

public class Rectangle extends FormAbstract {

    private int width;
    private int height;
    private int arcRadius;

    public Rectangle(int x, int y, int width, int height, int arcRadius) {
        super(x, y);
        this.width     = width;
        this.height    = height;
        this.arcRadius = arcRadius;
    }

    public int getWidth()                  { return width; }
    public int getHeight()                 { return height; }
    public int getArcRadius()              { return arcRadius; }

    public Rectangle setWidth(int w)       { this.width = w;      notifyObservers(); return this; }
    public Rectangle setHeight(int h)      { this.height = h;     notifyObservers(); return this; }
    public Rectangle setArcRadius(int arc) { this.arcRadius = arc; notifyObservers(); return this; }

    @Override
    public void accept(FormVisitor visitor) { visitor.visit(this); }

    @Override
    public FormEditor createEditor() { return new RectangleFormEditor(this); }

    @Override
    public Rectangle clone() { return (Rectangle) super.clone(); }
}