package fr.ubordeaux.geometriceditor.model;

public interface FormVisitor {
    void visit(FormComposite composite);
    void visit(Rectangle rectangle);
    void visit(RegularPolygon polygon);
}