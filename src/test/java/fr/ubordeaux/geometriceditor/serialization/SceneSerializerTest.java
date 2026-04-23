package fr.ubordeaux.geometriceditor.serialization;

import fr.ubordeaux.geometriceditor.model.FormComposite;
import fr.ubordeaux.geometriceditor.model.Rectangle;
import fr.ubordeaux.geometriceditor.model.RegularPolygon;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class SceneSerializerTest {

    @TempDir
    Path tempDir;

    @Test
    void testSaveLoadRectangle() throws IOException {
        FormComposite scene = new FormComposite();
        Rectangle rect = new Rectangle(10, 20, 100, 50, 5);
        rect.setColor(0xFF0000);
        scene.add(rect);

        String path = tempDir.resolve("scene.txt").toString();
        SceneSerializer.save(scene, path);

        FormComposite loaded = new FormComposite();
        SceneSerializer.load(loaded, path);

        assertEquals(1, loaded.size());
        Rectangle r = (Rectangle) loaded.iterator().next();
        assertEquals(10,      r.x());
        assertEquals(20,      r.y());
        assertEquals(100,     r.getWidth());
        assertEquals(50,      r.getHeight());
        assertEquals(5,       r.getArcRadius());
        assertEquals(0xFF0000, r.getColor());
    }

    @Test
    void testSaveLoadPolygon() throws IOException {
        FormComposite scene = new FormComposite();
        RegularPolygon poly = new RegularPolygon(50, 60, 6, 30);
        poly.setColor(0x0000FF);
        scene.add(poly);

        String path = tempDir.resolve("scene.txt").toString();
        SceneSerializer.save(scene, path);

        FormComposite loaded = new FormComposite();
        SceneSerializer.load(loaded, path);

        assertEquals(1, loaded.size());
        RegularPolygon p = (RegularPolygon) loaded.iterator().next();
        assertEquals(6,  p.getSides());
        assertEquals(30, p.getSideLength());
    }

    @Test
    void testSaveLoadSceneVide() throws IOException {
        FormComposite scene = new FormComposite();
        String path = tempDir.resolve("scene.txt").toString();
        SceneSerializer.save(scene, path);

        FormComposite loaded = new FormComposite();
        SceneSerializer.load(loaded, path);
        assertEquals(0, loaded.size());
    }

    @Test
    void testLoadRemplaceLaScene() throws IOException {
        FormComposite scene = new FormComposite();
        scene.add(new Rectangle(0, 0, 50, 50, 0));
        scene.add(new Rectangle(100, 100, 80, 40, 0));

        String path = tempDir.resolve("scene.txt").toString();
        SceneSerializer.save(scene, path);

        // Scène avec des formes différentes
        FormComposite loaded = new FormComposite();
        loaded.add(new RegularPolygon(0, 0, 3, 20));
        SceneSerializer.load(loaded, path);

        // Doit contenir exactement ce qui était sauvegardé
        assertEquals(2, loaded.size());
    }
}