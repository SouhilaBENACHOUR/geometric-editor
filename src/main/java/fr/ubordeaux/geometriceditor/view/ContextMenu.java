package fr.ubordeaux.geometriceditor.view;

import java.awt.Frame;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.PopupMenu;

public class ContextMenu extends PopupMenu {

    private Runnable onEdit;
    private Runnable onGroup;
    private Runnable onUngroup;
    private Runnable onDelete;

    public ContextMenu() {
        MenuItem edit     = new MenuItem("Éditer");
        MenuItem group    = new MenuItem("Grouper");
        MenuItem ungroup  = new MenuItem("Dégrouper");
        MenuItem delete   = new MenuItem("Supprimer");

        edit.addActionListener    (e -> { if (onEdit     != null) onEdit.run(); });
        group.addActionListener   (e -> { if (onGroup    != null) onGroup.run(); });
        ungroup.addActionListener (e -> { if (onUngroup  != null) onUngroup.run(); });
        delete.addActionListener  (e -> { if (onDelete   != null) onDelete.run(); });

        add(edit);
        add(group);
        add(ungroup);
        addSeparator();
        add(delete);
    }

    public void setOnEdit    (Runnable r) { onEdit     = r; }
    public void setOnGroup   (Runnable r) { onGroup    = r; }
    public void setOnUngroup (Runnable r) { onUngroup  = r; }
    public void setOnDelete  (Runnable r) { onDelete   = r; }
}