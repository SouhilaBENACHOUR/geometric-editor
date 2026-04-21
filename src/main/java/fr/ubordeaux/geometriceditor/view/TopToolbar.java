package fr.ubordeaux.geometriceditor.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Panel;

public class TopToolbar extends Panel {

    private final IconButton btnUndo;
    private final IconButton btnRedo;
    private final IconButton btnSave;
    private final IconButton btnLoad;

    public TopToolbar() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 4, 4));
        setBackground(new Color(235, 235, 235));
        setPreferredSize(new Dimension(900, 48));

        btnSave = new IconButton("save.png", "Sauvegarder");
        btnLoad = new IconButton("load.png", "Charger");
        btnUndo = new IconButton("undo.png",   "Undo");
        btnRedo = new IconButton("redo.png",   "Redo");

        add(btnSave);
        add(btnLoad);
        // séparateur visuel
        Panel sep = new Panel();
        sep.setPreferredSize(new Dimension(12, 40));
        sep.setBackground(new Color(235, 235, 235));
        add(sep);
        add(btnUndo);
        add(btnRedo);
    }

    public void setOnSave(Runnable r) { btnSave.setOnClick(r); }
    public void setOnLoad(Runnable r) { btnLoad.setOnClick(r); }
    public void setOnUndo(Runnable r) { btnUndo.setOnClick(r); }
    public void setOnRedo(Runnable r) { btnRedo.setOnClick(r); }
}