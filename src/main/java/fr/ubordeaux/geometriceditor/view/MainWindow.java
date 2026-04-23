package fr.ubordeaux.geometriceditor.view;

import fr.ubordeaux.geometriceditor.controller.DragController;
import fr.ubordeaux.geometriceditor.command.AddShapeCommand;
import fr.ubordeaux.geometriceditor.command.CommandManager;
import fr.ubordeaux.geometriceditor.command.GroupCommand;
import fr.ubordeaux.geometriceditor.command.RemoveShapeCommand;
import fr.ubordeaux.geometriceditor.command.UngroupCommand;
import fr.ubordeaux.geometriceditor.editor.EditDialog;
import fr.ubordeaux.geometriceditor.model.Form;
import fr.ubordeaux.geometriceditor.model.FormComposite;
import fr.ubordeaux.geometriceditor.serialization.SceneSerializer;
import fr.ubordeaux.geometriceditor.serialization.ToolbarSerializer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Label;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;

public class MainWindow extends Frame {

    private final FormComposite  scene      = new FormComposite();
    private final CommandManager manager    = new CommandManager();
    private final Whiteboard     whiteboard;
    private final ToolbarPanel   toolbar;
    private final TopToolbar     topToolbar;
    private final Label          statusBar;

    public MainWindow() {
        super("Geometric Editor");
        setSize(900, 650);
        setLayout(new BorderLayout());

        // 1. Crée toolbar et whiteboard
        whiteboard = new Whiteboard(scene);
        whiteboard.setPreferredSize(new Dimension(700, 600));
        toolbar    = new ToolbarPanel(); // charge ou non les défauts selon hasSavedToolbar()
        topToolbar = new TopToolbar();

        statusBar = new Label("Prêt — sélectionnez une forme dans la toolbar");
        statusBar.setBackground(new Color(230, 230, 230));

        // 2. DragController
        DragController dragCtrl = new DragController(scene, manager, whiteboard, toolbar);

        // 3. Enregistre les boutons existants (défauts ou vides)
        for (ToolbarButton btn : toolbar.getButtons()) {
            dragCtrl.registerButton(btn);
        }

        // 4. Donne le dragController à la toolbar
        toolbar.setDragController(dragCtrl);

        // 5. Charge les prototypes sauvegardés
        java.util.List<Form> saved = ToolbarSerializer.load();
        if (!saved.isEmpty()) {
            toolbar.loadPrototypes(saved);
        }
        // Ajout dans la fenêtre
        add(topToolbar, BorderLayout.NORTH);
        add(toolbar,    BorderLayout.WEST);
        add(whiteboard, BorderLayout.CENTER);
        add(statusBar,  BorderLayout.SOUTH);

        // Actions TopToolbar
        topToolbar.setOnUndo(() -> {
            manager.undo();
            statusBar.setText("Undo effectué");
            whiteboard.repaint();
        });
        topToolbar.setOnRedo(() -> {
            manager.redo();
            statusBar.setText("Redo effectué");
            whiteboard.repaint();
        });
        topToolbar.setOnSave(() -> {
            FileDialog fd = new FileDialog(this, "Sauvegarder", FileDialog.SAVE);
            fd.setFile("scene.txt");
            fd.setVisible(true);
            if (fd.getFile() != null) {
                try {
                    SceneSerializer.save(scene, fd.getDirectory() + fd.getFile());
                    statusBar.setText("Sauvegardé : " + fd.getFile());
                } catch (IOException ex) {
                    statusBar.setText("Erreur de sauvegarde");
                }
            }
        });
        topToolbar.setOnLoad(() -> {
            FileDialog fd = new FileDialog(this, "Charger", FileDialog.LOAD);
            fd.setVisible(true);
            if (fd.getFile() != null) {
                try {
                    SceneSerializer.load(scene, fd.getDirectory() + fd.getFile());
                    manager.clear();
                    whiteboard.repaint();
                    statusBar.setText("Chargé : " + fd.getFile());
                } catch (IOException ex) {
                    statusBar.setText("Erreur de chargement");
                }
            }
        });

        // Menu contextuel
        ContextMenu contextMenu = new ContextMenu();
        whiteboard.add(contextMenu);

        contextMenu.setOnEdit(() -> {
         Form sel = whiteboard.getSelectedForm();
         if (sel != null) {
          EditDialog dialog = new EditDialog(MainWindow.this, sel);
          dialog.setOnApply(() -> whiteboard.repaint());
          dialog.setVisible(true);
          whiteboard.repaint();
          statusBar.setText("Forme éditée");
    }
});

        contextMenu.setOnGroup(() -> {
            java.util.List<Form> sel = new ArrayList<>(whiteboard.getSelection());
            if (sel.size() >= 2) {
                manager.executeCommand(new GroupCommand(scene, sel));
                whiteboard.clearSelection();
                statusBar.setText("Groupe créé (" + sel.size() + " formes)");
            } else {
                statusBar.setText("Sélectionnez au moins 2 formes pour grouper");
            }
        });

        contextMenu.setOnUngroup(() -> {
            Form sel = whiteboard.getSelectedForm();
            if (sel instanceof FormComposite) {
                manager.executeCommand(
                    new UngroupCommand(scene, (FormComposite) sel));
                whiteboard.clearSelection();
                statusBar.setText("Groupe dissocié");
            } else {
                statusBar.setText("Sélectionnez un groupe pour dégrouper");
            }
        });

        contextMenu.setOnDelete(() -> {
            Form sel = whiteboard.getSelectedForm();
            if (sel != null) {
                manager.executeCommand(new RemoveShapeCommand(scene, sel));
                whiteboard.clearSelection();
                statusBar.setText("Forme supprimée");
            }
        });

        // Clic / drag sur le whiteboard
        final int[] pressX    = {0};
        final int[] pressY    = {0};
        final boolean[] isDragging = {false};

        whiteboard.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                whiteboard.requestFocus();
                pressX[0]     = e.getX();
                pressY[0]     = e.getY();
                isDragging[0] = false;
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1 && isDragging[0]) {
                    whiteboard.endRectSelection();
                    java.util.List<Form> sel = whiteboard.getSelection();
                    if (!sel.isEmpty())
                        statusBar.setText("Sélectionné : " + sel.size() + " forme(s)");
                }
                isDragging[0] = false;
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                whiteboard.requestFocus();
                if (e.getButton() == MouseEvent.BUTTON1) {
                    Form proto = toolbar.getSelectedPrototype();
                    if (proto != null) {
                        Form clone = proto.clone();
                        clone.set(e.getX(), e.getY());
                        clone.setColor(Color.BLUE.getRGB());
                        manager.executeCommand(new AddShapeCommand(scene, clone));
                        toolbar.clearSelection();
                        statusBar.setText("Forme ajoutée à ("
                            + e.getX() + "," + e.getY() + ")");
                    } else {
                        whiteboard.selectAt(e.getX(), e.getY());
                        java.util.List<Form> sel = whiteboard.getSelection();
                        statusBar.setText(sel.isEmpty()
                            ? "Aucune forme ici"
                            : "Sélectionné : " + sel.size() + " forme(s)");
                    }
                } else if (e.getButton() == MouseEvent.BUTTON3) {
                    whiteboard.selectAtRightClick(e.getX(), e.getY());
                    contextMenu.show(whiteboard, e.getX(), e.getY());
                }
            }
        });

        whiteboard.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int dx = Math.abs(e.getX() - pressX[0]);
                int dy = Math.abs(e.getY() - pressY[0]);
                if (dx > 5 || dy > 5) {
                    if (!isDragging[0]) {
                        isDragging[0] = true;
                        whiteboard.startRectSelection(pressX[0], pressY[0]);
                    }
                    whiteboard.updateRectSelection(e.getX(), e.getY());
                }
            }
        });

        // Fermeture — sauvegarde toolbar
    addWindowListener(new WindowAdapter() {
        @Override
        public void windowClosing(WindowEvent e) {
            java.util.List<Form> protos = toolbar.getPrototypes();
            System.out.println("Sauvegarde toolbar : " + protos.size() + " prototypes");
            ToolbarSerializer.save(protos);
            dispose();
            System.exit(0);
        }
    });
    }

    public static void main(String[] args) {
        new MainWindow().setVisible(true);
    }
}