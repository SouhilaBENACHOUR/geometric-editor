package fr.ubordeaux.geometriceditor.integration;

import fr.ubordeaux.geometriceditor.command.*;
import fr.ubordeaux.geometriceditor.model.*;
import fr.ubordeaux.geometriceditor.serialization.SceneSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class IntegrationTest {

    private CommandManager manager;
    private FormComposite  scene;

    @BeforeEach
    void setUp() {
        manager = new CommandManager();
        scene   = new FormComposite();
    }

    // --- Command + Scene ---

    @Test
    void testAjoutPuisUndoRedoSurScene() {
        Rectangle rect = new Rectangle(10, 10, 100, 50, 0);
        RegularPolygon poly = new RegularPolygon(200, 200, 6, 30);

        manager.executeCommand(new AddShapeCommand(scene, rect));
        manager.executeCommand(new AddShapeCommand(scene, poly));
        assertEquals(2, scene.size());

        manager.undo();
        assertEquals(1, scene.size());

        manager.undo();
        assertEquals(0, scene.size());

        manager.redo();
        manager.redo();
        assertEquals(2, scene.size());
    }

    @Test
    void testDeplacementAvecUndoRedo() {
        Rectangle rect = new Rectangle(10, 10, 100, 50, 0);
        manager.executeCommand(new AddShapeCommand(scene, rect));
        manager.executeCommand(new MoveShapeCommand(rect, 100, 50));

        assertEquals(110, rect.x());
        assertEquals(60,  rect.y());

        manager.undo(); // annule move
        assertEquals(10, rect.x());
        assertEquals(10, rect.y());

        manager.undo(); // annule add
        assertEquals(0, scene.size());
    }

    // --- Composite + Command ---

    @Test
    void testGrouperDegrouper() {
        Rectangle r1 = new Rectangle(0,   0,  80, 50, 0);
        Rectangle r2 = new Rectangle(100, 100, 60, 40, 0);
        manager.executeCommand(new AddShapeCommand(scene, r1));
        manager.executeCommand(new AddShapeCommand(scene, r2));

        // Grouper
        List<Form> toGroup = new ArrayList<>();
        for (Form f : scene) toGroup.add(f);
        GroupCommand groupCmd = new GroupCommand(scene, toGroup);
        manager.executeCommand(groupCmd);
        assertEquals(1, scene.size());
        assertTrue(scene.iterator().next() instanceof FormComposite);

        // Dégrouper
        manager.executeCommand(
            new UngroupCommand(scene, groupCmd.getNewGroup()));
        assertEquals(2, scene.size());

        // Undo dégrouper
        manager.undo();
        assertEquals(1, scene.size());

        // Undo grouper
        manager.undo();
        assertEquals(2, scene.size());
    }

    @Test
    void testGroupeImbrique() {
        Rectangle r1 = new Rectangle(0,   0,  50, 50, 0);
        Rectangle r2 = new Rectangle(100, 0,  50, 50, 0);
        Rectangle r3 = new Rectangle(200, 0,  50, 50, 0);

        manager.executeCommand(new AddShapeCommand(scene, r1));
        manager.executeCommand(new AddShapeCommand(scene, r2));
        manager.executeCommand(new AddShapeCommand(scene, r3));

        // Groupe r1 + r2
        List<Form> group1Forms = new ArrayList<>();
        group1Forms.add(r1);
        group1Forms.add(r2);
        GroupCommand g1 = new GroupCommand(scene, group1Forms);
        manager.executeCommand(g1);
        assertEquals(2, scene.size()); // groupe + r3

        // Groupe (groupe + r3)
        List<Form> group2Forms = new ArrayList<>();
        for (Form f : scene) group2Forms.add(f);
        GroupCommand g2 = new GroupCommand(scene, group2Forms);
        manager.executeCommand(g2);
        assertEquals(1, scene.size()); // un seul groupe racine

        // Undo tout
        manager.undo();
        assertEquals(2, scene.size());
        manager.undo();
        assertEquals(3, scene.size());
    }

    // --- Prototype + Scene ---

    @Test
    void testPrototypeIndependantDeLaScene() {
        Rectangle proto = new Rectangle(0, 0, 100, 50, 0);
        proto.setColor(0xFF0000);

        // Simule drag & drop depuis toolbar : on clone
        Rectangle clone1 = (Rectangle) proto.clone();
        Rectangle clone2 = (Rectangle) proto.clone();
        clone1.set(100, 100);
        clone2.set(200, 200);

        manager.executeCommand(new AddShapeCommand(scene, clone1));
        manager.executeCommand(new AddShapeCommand(scene, clone2));
        assertEquals(2, scene.size());

        // Modifier le prototype ne change pas les clones dans la scène
        proto.setColor(0x00FF00);
        proto.setWidth(999);
        assertEquals(0xFF0000, clone1.getColor());
        assertEquals(100,      clone1.getWidth());
        assertEquals(0xFF0000, clone2.getColor());
    }

    @Test
    void testCloneGroupeIndependant() {
        Rectangle r1 = new Rectangle(10, 10, 80, 50, 0);
        Rectangle r2 = new Rectangle(100, 100, 60, 40, 0);
        FormComposite group = new FormComposite();
        group.add(r1);
        group.add(r2);

        FormComposite clone = (FormComposite) group.clone();
        manager.executeCommand(new AddShapeCommand(scene, clone));

        // Déplacer le clone ne déplace pas le groupe original
        clone.set(500, 500);
        assertNotEquals(500, group.x());
    }

    // --- Serialisation + Scene ---

    @TempDir
    Path tempDir;

    @Test
    void testSaveLoadConserveScene() throws IOException {
        Rectangle r = new Rectangle(10, 20, 100, 50, 5);
        r.setColor(0xFF0000);
        RegularPolygon p = new RegularPolygon(200, 300, 6, 40);
        p.setColor(0x0000FF);

        manager.executeCommand(new AddShapeCommand(scene, r));
        manager.executeCommand(new AddShapeCommand(scene, p));

        String path = tempDir.resolve("scene.txt").toString();
        SceneSerializer.save(scene, path);

        FormComposite loaded = new FormComposite();
        SceneSerializer.load(loaded, path);

        assertEquals(2, loaded.size());
    }

    @Test
    void testSaveLoadApresUndoRedo() throws IOException {
        Rectangle r1 = new Rectangle(10, 10, 100, 50, 0);
        Rectangle r2 = new Rectangle(200, 200, 80, 40, 0);

        manager.executeCommand(new AddShapeCommand(scene, r1));
        manager.executeCommand(new AddShapeCommand(scene, r2));
        manager.undo(); // annule r2
        assertEquals(1, scene.size());

        String path = tempDir.resolve("scene.txt").toString();
        SceneSerializer.save(scene, path);

        FormComposite loaded = new FormComposite();
        SceneSerializer.load(loaded, path);

        // Seul r1 doit être dans le fichier
        assertEquals(1, loaded.size());
    }

    @Test
    void testSaveLoadGroupeImbrique() throws IOException {
        Rectangle r1 = new Rectangle(0,   0,  50, 50, 0);
        Rectangle r2 = new Rectangle(100, 0,  50, 50, 0);

        List<Form> toGroup = new ArrayList<>();
        toGroup.add(r1);
        toGroup.add(r2);
        GroupCommand groupCmd = new GroupCommand(scene, toGroup);
        manager.executeCommand(new AddShapeCommand(scene, r1));
        manager.executeCommand(new AddShapeCommand(scene, r2));
        manager.executeCommand(groupCmd);

        String path = tempDir.resolve("scene.txt").toString();
        SceneSerializer.save(scene, path);

        FormComposite loaded = new FormComposite();
        SceneSerializer.load(loaded, path);

        assertEquals(1, loaded.size());
        assertTrue(loaded.iterator().next() instanceof FormComposite);

        FormComposite loadedGroup = (FormComposite) loaded.iterator().next();
        assertEquals(2, loadedGroup.size());
    }

    // --- Observer ---

    @Test
    void testObserverNotifieAjout() {
        final int[] count = {0};
        scene.addObserver(form -> count[0]++);

        scene.add(new Rectangle(0, 0, 50, 50, 0));
        scene.add(new RegularPolygon(100, 100, 6, 30));

        assertEquals(2, count[0]);
    }

    @Test
    void testObserverNotifieSuppression() {
        Rectangle rect = new Rectangle(0, 0, 50, 50, 0);
        scene.add(rect);

        final int[] count = {0};
        scene.addObserver(form -> count[0]++);

        scene.remove(rect);
        assertEquals(1, count[0]);
    }

    @Test
    void testObserverNotifieModification() {
        Rectangle rect = new Rectangle(0, 0, 50, 50, 0);
        final int[] count = {0};
        rect.addObserver(form -> count[0]++);

        rect.set(100, 100);
        rect.setColor(0xFF0000);
        rect.setWidth(200);

        assertEquals(3, count[0]);
    }
}