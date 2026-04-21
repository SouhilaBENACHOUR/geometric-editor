package fr.ubordeaux.geometriceditor.view;

import fr.ubordeaux.geometriceditor.controller.DragController;
import fr.ubordeaux.geometriceditor.model.Form;
import fr.ubordeaux.geometriceditor.model.FormComposite;
import fr.ubordeaux.geometriceditor.model.Rectangle;
import fr.ubordeaux.geometriceditor.model.RegularPolygon;
import fr.ubordeaux.geometriceditor.serialization.ToolbarSerializer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Panel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ToolbarPanel extends Panel {

    private final List<ToolbarButton> buttons = new ArrayList<>();
    private ToolbarButton             selected = null;
    private final TrashButton         trash;
    private DragController            dragController;

    public ToolbarPanel() {
    setLayout(new BorderLayout());
    setBackground(new Color(245, 245, 245));
    setPreferredSize(new Dimension(82, 600));

    Panel btnPanel = new Panel(new FlowLayout(FlowLayout.CENTER, 4, 6));
    btnPanel.setBackground(new Color(245, 245, 245));

    // Charge les défauts seulement si pas de sauvegarde
    // Si sauvegarde vide → on recharge les défauts aussi
    java.util.List<Form> saved = ToolbarSerializer.load();
    if (saved.isEmpty()) {
        addPrototype(btnPanel, new Rectangle(0, 0, 80, 50, 0),  "rect.png",    "Rect");
        addPrototype(btnPanel, new RegularPolygon(0, 0, 6, 30), "polygon.png", "Hexa");
        addPrototype(btnPanel, new RegularPolygon(0, 0, 3, 40), "triangle.png","Tri");
    }

    trash = new TrashButton();
    add(btnPanel, BorderLayout.CENTER);
    add(trash,    BorderLayout.SOUTH);
}

    public void setDragController(DragController dc) {
        this.dragController = dc;
    }

    private void addPrototype(Panel panel, Form f, String icon, String label) {
        ToolbarButton btn = new ToolbarButton(f, icon, label);
        btn.setOnClick(() -> selectButton(btn));
        buttons.add(btn);
        panel.add(btn);
    }

    private void selectButton(ToolbarButton btn) {
        if (selected != null) selected.setSelected(false);
        selected = btn;
        btn.setSelected(true);
    }

    public void clearSelection() {
        if (selected != null) {
            selected.setSelected(false);
            selected = null;
        }
    }

    public Form getSelectedPrototype() {
        return selected != null ? selected.getPrototype() : null;
    }

    public TrashButton getTrash() { return trash; }

    public List<ToolbarButton> getButtons() {
        return Collections.unmodifiableList(buttons);
    }

    public void addPrototypeFromScene(Form f) {
        Panel btnPanel = (Panel) getComponent(0);

        String icon, label;
        if (f instanceof Rectangle) {
            icon  = "rect.png";
            label = "Rect*";
        } else if (f instanceof RegularPolygon) {
            RegularPolygon p = (RegularPolygon) f;
            icon  = p.getSides() == 3 ? "triangle.png" : "polygon.png";
            label = "Poly" + p.getSides() + "*";
        } else if (f instanceof FormComposite) {
            icon  = "polygon.png";
            label = "Grp*";
        } else {
            icon  = "rect.png";
            label = "Form*";
        }

        // Clone sans toucher à la position pour les groupes
        Form proto = f.clone();

        if (!(proto instanceof FormComposite)) {
            proto.set(0, 0);
        }

        ToolbarButton btn = new ToolbarButton(proto, icon, label);
        btn.setOnClick(() -> selectButton(btn));
        buttons.add(btn);
        btnPanel.add(btn);

        // Enregistre dans le DragController pour que le drag fonctionne
        if (dragController != null) {
            dragController.registerButton(btn);
        }

        btnPanel.revalidate();
        btnPanel.repaint();
        repaint();
    }

    public void removeButton(ToolbarButton btn) {
        Panel btnPanel = (Panel) getComponent(0);
        btnPanel.remove(btn);
        buttons.remove(btn);
        if (selected == btn) selected = null;
        System.out.println("Bouton supprimé, buttons.size() = " + buttons.size());
        btnPanel.revalidate();
        btnPanel.repaint();
        repaint();
        
    }

    // Retourne la liste des prototypes pour la sauvegarde
public List<Form> getPrototypes() {
    List<Form> protos = new ArrayList<>();
    for (ToolbarButton btn : buttons) {
        protos.add(btn.getPrototype());
    }
    return protos;
}

    // Charge des prototypes sauvegardés
    public void loadPrototypes(java.util.List<Form> forms) {
        Panel btnPanel = (Panel) getComponent(0);
        for (Form f : forms) {
            String icon, label;
            if (f instanceof Rectangle) {
                icon = "rect.png"; label = "Rect*";
            } else if (f instanceof RegularPolygon) {
                RegularPolygon p = (RegularPolygon) f;
                icon  = p.getSides() == 3 ? "triangle.png" : "polygon.png";
                label = "Poly" + p.getSides() + "*";
            } else if (f instanceof FormComposite) {
                icon = "polygon.png"; label = "Grp*";
            } else {
                icon = "rect.png"; label = "Form*";
            }
            ToolbarButton btn = new ToolbarButton(f, icon, label);
            btn.setOnClick(() -> selectButton(btn));
            buttons.add(btn);
            btnPanel.add(btn);
            if (dragController != null) dragController.registerButton(btn);
        }
        btnPanel.revalidate();
        btnPanel.repaint();
        repaint();
    }
}