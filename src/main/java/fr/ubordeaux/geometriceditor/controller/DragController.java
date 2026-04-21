package fr.ubordeaux.geometriceditor.controller;

import fr.ubordeaux.geometriceditor.command.AddShapeCommand;
import fr.ubordeaux.geometriceditor.command.CommandManager;
import fr.ubordeaux.geometriceditor.command.RemoveShapeCommand;
import fr.ubordeaux.geometriceditor.model.Form;
import fr.ubordeaux.geometriceditor.model.FormComposite;
import fr.ubordeaux.geometriceditor.view.ToolbarButton;
import fr.ubordeaux.geometriceditor.view.ToolbarPanel;
import fr.ubordeaux.geometriceditor.view.Whiteboard;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class DragController {

    private final FormComposite  scene;
    private final CommandManager manager;
    private final Whiteboard     whiteboard;
    private final ToolbarPanel   toolbar;

    private Form    draggedPrototype    = null;
    private boolean draggingFromToolbar = false;

    private Form    draggedFromScene    = null;
    private boolean draggingFromScene   = false;

    public DragController(FormComposite scene, CommandManager manager,
                          Whiteboard whiteboard, ToolbarPanel toolbar) {
        this.scene      = scene;
        this.manager    = manager;
        this.whiteboard = whiteboard;
        this.toolbar    = toolbar;
        registerWhiteboardDrag();
    }

    public void registerButton(ToolbarButton btn) {
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                draggedPrototype    = btn.getPrototype();
                draggingFromToolbar = true;
                btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (!draggingFromToolbar) return;
                draggingFromToolbar = false;
                btn.setCursor(Cursor.getDefaultCursor());
                whiteboard.setGhost(null, -1, -1);

                Point abs = e.getLocationOnScreen();

                // Poubelle ?
                if (toolbar.getTrash().contains(abs.x, abs.y)) {
                    toolbar.removeButton(btn);
                    draggedPrototype = null;
                    return;
                }

                // Whiteboard ?
                Point wbLoc = whiteboard.getLocationOnScreen();
                int relX = abs.x - wbLoc.x;
                int relY = abs.y - wbLoc.y;
                if (relX >= 0 && relX <= whiteboard.getWidth()
                 && relY >= 0 && relY <= whiteboard.getHeight()) {
                    dropOnWhiteboard(relX, relY, draggedPrototype);
                }
                draggedPrototype = null;
            }
        });

        btn.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (!draggingFromToolbar || draggedPrototype == null) return;
                Point abs   = e.getLocationOnScreen();
                Point wbLoc = whiteboard.getLocationOnScreen();
                whiteboard.setGhost(draggedPrototype,
                    abs.x - wbLoc.x, abs.y - wbLoc.y);
            }
        });
    }

    private void registerWhiteboardDrag() {
        whiteboard.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1
                        && toolbar.getSelectedPrototype() == null) {
                    Form f = whiteboard.getFormAtPublic(e.getX(), e.getY());
                    if (f != null) {
                        draggedFromScene  = f;
                        draggingFromScene = true;
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (!draggingFromScene || draggedFromScene == null) {
                    draggingFromScene = false;
                    return;
                }
                draggingFromScene = false;
                whiteboard.setCursor(Cursor.getDefaultCursor());

                Point abs = e.getLocationOnScreen();

                // Poubelle → supprime de la scène
                if (toolbar.getTrash().contains(abs.x, abs.y)) {
                    manager.executeCommand(
                        new RemoveShapeCommand(scene, draggedFromScene));
                    whiteboard.clearSelection();
                    draggedFromScene = null;
                    return;
                }

                // Toolbar → ajoute comme prototype
                try {
                    Point tbLoc = toolbar.getLocationOnScreen();
                    if (abs.x >= tbLoc.x && abs.x <= tbLoc.x + toolbar.getWidth()
                     && abs.y >= tbLoc.y && abs.y <= tbLoc.y + toolbar.getHeight()) {
                        toolbar.addPrototypeFromScene(draggedFromScene);
                    }
                } catch (Exception ex) { /* ignore */ }

                draggedFromScene = null;
            }
        });

        whiteboard.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (!draggingFromScene || draggedFromScene == null) return;
                Point abs = e.getLocationOnScreen();
                try {
                    Point tbLoc = toolbar.getLocationOnScreen();
                    boolean overToolbar =
                        abs.x >= tbLoc.x && abs.x <= tbLoc.x + toolbar.getWidth()
                     && abs.y >= tbLoc.y && abs.y <= tbLoc.y + toolbar.getHeight();
                    boolean overTrash = toolbar.getTrash().contains(abs.x, abs.y);
                    whiteboard.setCursor(overToolbar || overTrash
                        ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
                        : Cursor.getDefaultCursor());
                } catch (Exception ex) { /* ignore */ }
            }
        });
    }

    private void dropOnWhiteboard(int x, int y, Form prototype) {
        Form clone = prototype.clone();

        if (clone instanceof FormComposite) {
            // Pour un groupe : calcule le décalage depuis le centre
            int cx = clone.x();
            int cy = clone.y();
            int dx = x - cx;
            int dy = y - cy;
            clone.set(clone.x() + dx, clone.y() + dy);
        } else {
            clone.set(x, y);
        }

        clone.setColor(new Color(30, 120, 200).getRGB());
        manager.executeCommand(new AddShapeCommand(scene, clone));
        toolbar.clearSelection();
    }
}