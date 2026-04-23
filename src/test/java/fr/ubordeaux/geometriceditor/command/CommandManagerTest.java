package fr.ubordeaux.geometriceditor.command;

import fr.ubordeaux.geometriceditor.model.FormComposite;
import fr.ubordeaux.geometriceditor.model.Rectangle;
import fr.ubordeaux.geometriceditor.model.RegularPolygon;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CommandManagerTest {

    private CommandManager manager;
    private FormComposite  scene;
    private Rectangle      rect;

    @BeforeEach
    void setUp() {
        manager = new CommandManager();
        scene   = new FormComposite();
        rect    = new Rectangle(10, 10, 100, 50, 0);
    }

    @Test
    void testAddShape() {
        manager.executeCommand(new AddShapeCommand(scene, rect));
        assertEquals(1, scene.size());
    }

    @Test
    void testUndoAdd() {
        manager.executeCommand(new AddShapeCommand(scene, rect));
        manager.undo();
        assertEquals(0, scene.size());
    }

    @Test
    void testRedoAdd() {
        manager.executeCommand(new AddShapeCommand(scene, rect));
        manager.undo();
        manager.redo();
        assertEquals(1, scene.size());
    }

    @Test
    void testUndoSansAction() {
        manager.undo(); // ne doit pas planter
        assertEquals(0, scene.size());
    }

    @Test
    void testRedoSansUndo() {
        manager.redo(); // ne doit pas planter
        assertEquals(0, scene.size());
    }

    @Test
    void testNouvelleActionEffaceRedo() {
        manager.executeCommand(new AddShapeCommand(scene, rect));
        manager.undo();
        assertTrue(manager.canRedo());

        Rectangle rect2 = new Rectangle(50, 50, 80, 40, 0);
        manager.executeCommand(new AddShapeCommand(scene, rect2));
        assertFalse(manager.canRedo());
    }

    @Test
    void testMove() {
        manager.executeCommand(new AddShapeCommand(scene, rect));
        manager.executeCommand(new MoveShapeCommand(rect, 50, 30));
        assertEquals(60, rect.x());
        assertEquals(40, rect.y());

        manager.undo();
        assertEquals(10, rect.x());
        assertEquals(10, rect.y());

        manager.redo();
        assertEquals(60, rect.x());
        assertEquals(40, rect.y());
    }

    @Test
    void testGroup() {
        Rectangle rect2 = new Rectangle(200, 200, 60, 40, 0);
        manager.executeCommand(new AddShapeCommand(scene, rect));
        manager.executeCommand(new AddShapeCommand(scene, rect2));
        assertEquals(2, scene.size());

        List<fr.ubordeaux.geometriceditor.model.Form> toGroup = new ArrayList<>();
        for (fr.ubordeaux.geometriceditor.model.Form f : scene) toGroup.add(f);
        manager.executeCommand(new GroupCommand(scene, toGroup));
        assertEquals(1, scene.size());

        manager.undo();
        assertEquals(2, scene.size());

        manager.redo();
        assertEquals(1, scene.size());
    }

    @Test
    void testUngroup() {
        Rectangle rect2 = new Rectangle(200, 200, 60, 40, 0);
        manager.executeCommand(new AddShapeCommand(scene, rect));
        manager.executeCommand(new AddShapeCommand(scene, rect2));

        List<fr.ubordeaux.geometriceditor.model.Form> toGroup = new ArrayList<>();
        for (fr.ubordeaux.geometriceditor.model.Form f : scene) toGroup.add(f);
        GroupCommand groupCmd = new GroupCommand(scene, toGroup);
        manager.executeCommand(groupCmd);
        assertEquals(1, scene.size());

        manager.executeCommand(
            new UngroupCommand(scene, groupCmd.getNewGroup()));
        assertEquals(2, scene.size());

        manager.undo();
        assertEquals(1, scene.size());
    }

    @Test
    void testRemove() {
        manager.executeCommand(new AddShapeCommand(scene, rect));
        manager.executeCommand(new RemoveShapeCommand(scene, rect));
        assertEquals(0, scene.size());

        manager.undo();
        assertEquals(1, scene.size());
    }

    @Test
    void testClear() {
        manager.executeCommand(new AddShapeCommand(scene, rect));
        manager.clear();
        assertFalse(manager.canUndo());
        assertFalse(manager.canRedo());
    }
}