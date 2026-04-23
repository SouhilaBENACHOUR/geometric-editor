package fr.ubordeaux.geometriceditor.editor;

import fr.ubordeaux.geometriceditor.model.Form;
import fr.ubordeaux.geometriceditor.model.FormAbstract;
import fr.ubordeaux.geometriceditor.model.FormComposite;
import fr.ubordeaux.geometriceditor.model.Rectangle;
import fr.ubordeaux.geometriceditor.model.RegularPolygon;

import java.awt.Button;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class EditDialog extends Dialog {

    private final int    snapX, snapY, snapColor;
    private final double snapRot;
    private final int    snapW, snapH, snapArc;
    private final int    snapSides, snapLen;

    private final Form form;
    private Runnable   onApply;

    private final TextField tfX, tfY, tfColor, tfRot, tfTx, tfTy;
    private final TextField tfW, tfH, tfArc;
    private final TextField tfSides, tfLen;

    public EditDialog(Frame parent, Form form) {
        super(parent, "Éditer la forme", true);
        this.form = form;

        snapX     = form.x();
        snapY     = form.y();
        snapColor = form.getColor();
        snapRot   = form.getRotation();

        if (form instanceof Rectangle) {
            Rectangle r = (Rectangle) form;
            snapW = r.getWidth(); snapH = r.getHeight(); snapArc = r.getArcRadius();
            snapSides = 0; snapLen = 0;
        } else if (form instanceof RegularPolygon) {
            RegularPolygon p = (RegularPolygon) form;
            snapSides = p.getSides(); snapLen = p.getSideLength();
            snapW = 0; snapH = 0; snapArc = 0;
        } else {
            snapW = snapH = snapArc = snapSides = snapLen = 0;
        }

        int rows = 7;
        if (form instanceof Rectangle)      rows += 3;
        if (form instanceof RegularPolygon) rows += 2;

        setLayout(new GridLayout(rows, 2, 8, 6));
        setSize(400, rows * 42 + 60);
        setLocationRelativeTo(parent);

        tfX     = addField("Position X :",     String.valueOf(form.x()));
        tfY     = addField("Position Y :",     String.valueOf(form.y()));
        tfRot   = addField("Rotation (°) :",   String.valueOf((int) snapRot));
        tfTx    = addField("Translation X :",  "0");
        tfTy    = addField("Translation Y :",  "0");
        tfColor = addField("Couleur (hex) :",
            String.format("%06X", form.getColor() & 0xFFFFFF));

        if (form instanceof Rectangle) {
            Rectangle r = (Rectangle) form;
            tfW   = addField("Largeur :",  String.valueOf(r.getWidth()));
            tfH   = addField("Hauteur :",  String.valueOf(r.getHeight()));
            tfArc = addField("Arrondi :",  String.valueOf(r.getArcRadius()));
        } else {
            tfW = tfH = tfArc = null;
        }

        if (form instanceof RegularPolygon) {
            RegularPolygon p = (RegularPolygon) form;
            tfSides = addField("Côtés :",         String.valueOf(p.getSides()));
            tfLen   = addField("Longueur côté :", String.valueOf(p.getSideLength()));
        } else {
            tfSides = tfLen = null;
        }

        Button btnApply  = new Button("Appliquer");
        Button btnOk     = new Button("OK");
        Button btnCancel = new Button("Annuler");

        btnApply.addActionListener(e -> {
            applyChanges();
            if (onApply != null) onApply.run();
        });

        btnOk.addActionListener(e -> {
            applyChanges();
            if (onApply != null) onApply.run();
            dispose();
        });

        btnCancel.addActionListener(e -> {
            restoreSnapshot();
            if (onApply != null) onApply.run();
            dispose();
        });

        // GridLayout 1x3 pour que les 3 boutons tiennent sur une ligne
        Panel btnPanel = new Panel(new GridLayout(1, 3, 6, 0));
        btnPanel.add(btnApply);
        btnPanel.add(btnOk);
        btnPanel.add(btnCancel);

        add(new Label());
        add(btnPanel);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                restoreSnapshot();
                if (onApply != null) onApply.run();
                dispose();
            }
        });
    }

    public void setOnApply(Runnable r) { this.onApply = r; }

    private TextField addField(String label, String value) {
        add(new Label(label));
        TextField tf = new TextField(value, 12);
        add(tf);
        return tf;
    }

    private void applyChanges() {
        try {
            int x      = Integer.parseInt(tfX.getText().trim());
            int y      = Integer.parseInt(tfY.getText().trim());
            int tx     = Integer.parseInt(tfTx.getText().trim());
            int ty     = Integer.parseInt(tfTy.getText().trim());
            double rot = Double.parseDouble(tfRot.getText().trim());
            int color  = (int) Long.parseLong(tfColor.getText().trim(), 16);

            form.set(x + tx, y + ty);
            form.setColor(color);
            form.setRotation(rot);

            tfTx.setText("0");
            tfTy.setText("0");
            tfX.setText(String.valueOf(form.x()));
            tfY.setText(String.valueOf(form.y()));

            if (form instanceof Rectangle && tfW != null) {
                Rectangle r = (Rectangle) form;
                r.setWidth    (Integer.parseInt(tfW.getText().trim()));
                r.setHeight   (Integer.parseInt(tfH.getText().trim()));
                r.setArcRadius(Integer.parseInt(tfArc.getText().trim()));
            }

            if (form instanceof RegularPolygon && tfSides != null) {
                RegularPolygon p = (RegularPolygon) form;
                p.setSides     (Integer.parseInt(tfSides.getText().trim()));
                p.setSideLength(Integer.parseInt(tfLen.getText().trim()));
            }

        } catch (NumberFormatException ex) {
            // Valeur invalide ignorée
        }
    }

    private void restoreSnapshot() {
        form.set(snapX, snapY);
        form.setColor(snapColor);
        form.setRotation(snapRot);

        if (form instanceof Rectangle) {
            Rectangle r = (Rectangle) form;
            r.setWidth(snapW);
            r.setHeight(snapH);
            r.setArcRadius(snapArc);
        }

        if (form instanceof RegularPolygon) {
            RegularPolygon p = (RegularPolygon) form;
            p.setSides(snapSides);
            p.setSideLength(snapLen);
        }
    }
}