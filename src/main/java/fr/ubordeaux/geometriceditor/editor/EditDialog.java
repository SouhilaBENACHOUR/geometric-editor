package fr.ubordeaux.geometriceditor.editor;

import fr.ubordeaux.geometriceditor.model.Form;
import fr.ubordeaux.geometriceditor.model.Rectangle;
import fr.ubordeaux.geometriceditor.model.RegularPolygon;

import java.awt.Button;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;

public class EditDialog extends Dialog {

    private boolean confirmed = false;

    public EditDialog(Frame parent, Form form) {
        super(parent, "Editer la forme", true);
        setLayout(new GridLayout(0, 2, 8, 8));
        setSize(320, 280);
        setLocationRelativeTo(parent);

        // Champs communs
        TextField tfX = addField("X :", String.valueOf(form.x()));
        TextField tfY = addField("Y :", String.valueOf(form.y()));

        // Champs spécifiques
        TextField tfW = null, tfH = null, tfArc = null;
        TextField tfSides = null, tfLen = null;

        if (form instanceof Rectangle) {
            Rectangle r = (Rectangle) form;
            tfW   = addField("Largeur :",  String.valueOf(r.getWidth()));
            tfH   = addField("Hauteur :",  String.valueOf(r.getHeight()));
            tfArc = addField("Arrondi :",  String.valueOf(r.getArcRadius()));
        } else if (form instanceof RegularPolygon) {
            RegularPolygon p = (RegularPolygon) form;
            tfSides = addField("Côtés :",        String.valueOf(p.getSides()));
            tfLen   = addField("Longueur côté :", String.valueOf(p.getSideLength()));
        }

        // Couleur (valeur hex)
        TextField tfColor = addField("Couleur (hex) :",
            String.format("%06X", form.getColor() & 0xFFFFFF));

        // Boutons
        final TextField finalTfW = tfW, finalTfH = tfH, finalTfArc = tfArc;
        final TextField finalTfSides = tfSides, finalTfLen = tfLen;

        Button ok = new Button("OK");
        Button cancel = new Button("Annuler");

        ok.addActionListener(e -> {
            try {
                form.set(Integer.parseInt(tfX.getText()),
                         Integer.parseInt(tfY.getText()));

                if (form instanceof Rectangle) {
                    Rectangle r = (Rectangle) form;
                    r.setWidth    (Integer.parseInt(finalTfW.getText()));
                    r.setHeight   (Integer.parseInt(finalTfH.getText()));
                    r.setArcRadius(Integer.parseInt(finalTfArc.getText()));
                } else if (form instanceof RegularPolygon) {
                    RegularPolygon p = (RegularPolygon) form;
                    p.setSides     (Integer.parseInt(finalTfSides.getText()));
                    p.setSideLength(Integer.parseInt(finalTfLen.getText()));
                }

                int color = (int) Long.parseLong(tfColor.getText(), 16);
                form.setColor(color);

                confirmed = true;
            } catch (NumberFormatException ex) {
                // Valeur invalide — on ignore
            }
            dispose();
        });

        cancel.addActionListener(e -> dispose());

        Panel btnPanel = new Panel(new FlowLayout());
        btnPanel.add(ok);
        btnPanel.add(cancel);
        add(new Label()); // spacer
        add(btnPanel);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosing(java.awt.event.WindowEvent e) { dispose(); }
        });
    }

    private TextField addField(String labelText, String value) {
        add(new Label(labelText));
        TextField tf = new TextField(value, 10);
        add(tf);
        return tf;
    }

    public boolean isConfirmed() { return confirmed; }
}