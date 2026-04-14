package fr.ubordeaux.geometriceditor.model;

public class RegularPolygon extends FormAbstract {

    private int sides;
    private int sideLength;

    public RegularPolygon(int x, int y, int sides, int sideLength) {
        super(x, y);
        if (sides < 3) throw new IllegalArgumentException("Au moins 3 côtés");
        this.sides      = sides;
        this.sideLength = sideLength;
    }

    public int getSides()                        { return sides; }
    public int getSideLength()                   { return sideLength; }
    public RegularPolygon setSides(int s)        { this.sides = s;      notifyObservers(); return this; }
    public RegularPolygon setSideLength(int l)   { this.sideLength = l; notifyObservers(); return this; }

    public double getCircumradius() {
        return sideLength / (2.0 * Math.sin(Math.PI / sides));
    }

    @Override
    public void accept(FormVisitor visitor) { visitor.visit(this); }

    @Override
    public RegularPolygon clone() { return (RegularPolygon) super.clone(); }
}