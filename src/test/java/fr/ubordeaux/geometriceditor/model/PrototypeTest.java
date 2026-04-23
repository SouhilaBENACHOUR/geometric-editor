package fr.ubordeaux.geometriceditor.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PrototypeTest {

    @Test
    void testRectangleCloneIndependant() {
        Rectangle original = new Rectangle(10, 20, 100, 50, 5);
        original.setColor(0xFF0000);
        Rectangle clone = (Rectangle) original.clone();

        // Même valeurs
        assertEquals(original.x(),          clone.x());
        assertEquals(original.y(),          clone.y());
        assertEquals(original.getWidth(),   clone.getWidth());
        assertEquals(original.getHeight(),  clone.getHeight());
        assertEquals(original.getColor(),   clone.getColor());

        // Indépendants — modifier le clone ne change pas l'original
        clone.set(999, 999);
        clone.setWidth(999);
        assertNotEquals(999, original.x());
        assertNotEquals(999, original.getWidth());
    }

    @Test
    void testPolygonCloneIndependant() {
        RegularPolygon original = new RegularPolygon(50, 50, 6, 30);
        original.setColor(0x0000FF);
        RegularPolygon clone = (RegularPolygon) original.clone();

        assertEquals(original.getSides(),      clone.getSides());
        assertEquals(original.getSideLength(), clone.getSideLength());
        assertEquals(original.getColor(),      clone.getColor());

        clone.setSides(3);
        assertNotEquals(3, original.getSides());
    }

    @Test
    void testGroupeCloneProfond() {
        Rectangle r1 = new Rectangle(0, 0, 100, 50, 0);
        Rectangle r2 = new Rectangle(100, 100, 80, 40, 0);
        FormComposite group = new FormComposite();
        group.add(r1);
        group.add(r2);

        FormComposite clone = (FormComposite) group.clone();

        // Même nombre d'enfants
        assertEquals(group.size(), clone.size());

        // Les enfants sont des copies indépendantes
        clone.set(500, 500);
        assertNotEquals(500, r1.x());
    }
}