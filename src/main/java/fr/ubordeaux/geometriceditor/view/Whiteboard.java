package fr.ubordeaux.geometriceditor.view;

import fr.ubordeaux.geometriceditor.model.Form;
import fr.ubordeaux.geometriceditor.model.FormComposite;
import fr.ubordeaux.geometriceditor.model.FormObserver;
import fr.ubordeaux.geometriceditor.model.Rectangle;
import fr.ubordeaux.geometriceditor.model.RegularPolygon;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Collections;

public class Whiteboard extends Canvas implements FormObserver {

    private final FormComposite         scene;
    private final FormAwtImplementation renderer;
    private Form                        selectedForm = null;
    private Form ghostForm = null;
    private int  ghostX = -1, ghostY = -1;
    private final List<Form> selection = new ArrayList<>();
    private boolean          ctrlHeld  = false;

    // Sélection par rectangle
    private int     selStartX = -1, selStartY = -1;
    private int     selEndX   = -1, selEndY   = -1;
    private boolean selectingRect = false;

    public interface FormAction {
        void run(Form f);
    }

    private FormAction onRightClickAction;

    public Whiteboard(FormComposite scene) {
        this.scene    = scene;
        this.renderer = new FormAwtImplementation();
        this.scene.addObserver(this);
        setBackground(Color.WHITE);
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_CONTROL) ctrlHeld = true;
            }
            @Override public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_CONTROL) ctrlHeld = false;
            }
        });
    }

    public void setOnRightClick(FormAction action) {
        this.onRightClickAction = action;
    }

   public void selectAt(int x, int y) {
    Form f = getFormAt(x, y);
    if (ctrlHeld) {
        // Ctrl+clic : ajoute ou retire de la sélection
        if (f != null) {
            if (selection.contains(f)) selection.remove(f);
            else selection.add(f);
        }
    } else {
        // Clic simple : remplace la sélection
        selection.clear();
        if (f != null) selection.add(f);
        selectedForm = f;
    }
    repaint();
}

public List<Form> getSelection()  { return Collections.unmodifiableList(selection); }
public void clearSelection()      { selection.clear(); selectedForm = null; repaint(); }

    public Form getSelectedForm() { return selectedForm; }

    private Form getFormAt(int x, int y) {
        List<Form> list = new ArrayList<>();
        for (Form f : scene) list.add(0, f);
        for (Form f : list) {
            if (isInside(f, x, y)) return f;
        }
        return null;
    }

    private boolean isInside(Form f, int x, int y) {
        if (f instanceof Rectangle) {
            Rectangle r = (Rectangle) f;
            return x >= r.x() && x <= r.x() + r.getWidth()
                && y >= r.y() && y <= r.y() + r.getHeight();
        } else if (f instanceof RegularPolygon) {
            RegularPolygon p = (RegularPolygon) f;
            double dist = Math.sqrt(
                Math.pow(x - p.x(), 2) + Math.pow(y - p.y(), 2));
            return dist <= p.getCircumradius();
        } else if (f instanceof FormComposite) {
            for (Form child : (FormComposite) f)
                if (isInside(child, x, y)) return true;
        }
        return false;
    }

    @Override
    public void paint(Graphics g) {
        renderer.setGraphics(g);
        for (Form f : scene) {
            renderer.draw_impl(f);
            drawAllSelectionBoxes(g);
        }
        // Aperçu fantôme pendant le drag
        if (ghostForm != null) {
            Form tmp = ghostForm.clone();
            tmp.set(ghostX, ghostY);
            tmp.setColor(new Color(100, 160, 220, 120).getRGB());
            g.setColor(new Color(100, 160, 220, 80));
            if (tmp instanceof Rectangle) {
                Rectangle r = (Rectangle) tmp;
                g.fillRect(r.x(), r.y(), r.getWidth(), r.getHeight());
                g.setColor(new Color(30, 100, 200));
                g.drawRect(r.x(), r.y(), r.getWidth(), r.getHeight());
            } else if (tmp instanceof RegularPolygon) {
                RegularPolygon p = (RegularPolygon) tmp;
                int rad = (int) p.getCircumradius();
                g.fillOval(p.x() - rad, p.y() - rad, rad * 2, rad * 2);
                g.setColor(new Color(30, 100, 200));
                g.drawOval(p.x() - rad, p.y() - rad, rad * 2, rad * 2);
            }
        }
        // Rectangle de sélection en cours
        if (selectingRect && selStartX >= 0) {
            g.setColor(new Color(0, 120, 215, 60));
            int rx = Math.min(selStartX, selEndX);
            int ry = Math.min(selStartY, selEndY);
            int rw = Math.abs(selEndX - selStartX);
            int rh = Math.abs(selEndY - selStartY);
            g.fillRect(rx, ry, rw, rh);
            g.setColor(new Color(0, 120, 215));
            g.drawRect(rx, ry, rw, rh);
        }
    }

    private void drawSelectionBox(Graphics g, Form f) {
        g.setColor(new Color(0, 120, 215));
        if (f instanceof Rectangle) {
            Rectangle r = (Rectangle) f;
            g.drawRect(r.x() - 2, r.y() - 2,
                       r.getWidth() + 4, r.getHeight() + 4);
            drawHandles(g, r.x() - 2, r.y() - 2,
                           r.getWidth() + 4, r.getHeight() + 4);
        } else if (f instanceof RegularPolygon) {
            RegularPolygon p = (RegularPolygon) f;
            int rad = (int) p.getCircumradius();
            g.drawOval(p.x() - rad - 2, p.y() - rad - 2,
                       (rad + 2) * 2, (rad + 2) * 2);
        }
    }
    private void drawAllSelectionBoxes(Graphics g) {
            for (Form f : selection) drawSelectionBox(g, f);
    }

    // Clic droit : sélectionne SEULEMENT si rien n'est déjà sélectionné
    public void selectAtRightClick(int x, int y) {
        Form f = getFormAt(x, y);
        if (selection.isEmpty() && f != null) {
            selection.add(f);
            selectedForm = f;
            repaint();
        }
    }
    private void drawHandles(Graphics g, int x, int y, int w, int h) {
        int s = 6;
        g.fillRect(x - s/2,     y - s/2,     s, s);
        g.fillRect(x + w - s/2, y - s/2,     s, s);
        g.fillRect(x - s/2,     y + h - s/2, s, s);
        g.fillRect(x + w - s/2, y + h - s/2, s, s);
    }

    public void setGhost(Form f, int x, int y) {
        this.ghostForm = f;
        this.ghostX    = x;
        this.ghostY    = y;
        repaint();
    }

    private boolean isInsideRect(Form f, int x1, int y1, int x2, int y2) {
        int minX = Math.min(x1, x2), maxX = Math.max(x1, x2);
        int minY = Math.min(y1, y2), maxY = Math.max(y1, y2);

        if (f instanceof Rectangle) {
            Rectangle r = (Rectangle) f;
            return r.x() >= minX && r.x() + r.getWidth()  <= maxX
                && r.y() >= minY && r.y() + r.getHeight() <= maxY;
        } else if (f instanceof RegularPolygon) {
            RegularPolygon p = (RegularPolygon) f;
            return p.x() >= minX && p.x() <= maxX
                && p.y() >= minY && p.y() <= maxY;
        } else if (f instanceof FormComposite) {
            for (Form child : (FormComposite) f)
                if (isInsideRect(child, x1, y1, x2, y2)) return true;
        }
        return false;
    }

    public void startRectSelection(int x, int y) {
    selStartX = x; selStartY = y;
    selEndX   = x; selEndY   = y;
    selectingRect = true;
    selection.clear();
    selectedForm  = null;
    repaint();
}

public void updateRectSelection(int x, int y) {
    selEndX = x; selEndY = y;
    repaint();
}

public void endRectSelection() {
    selectingRect = false;
    selection.clear();
    selectedForm = null;
    for (Form f : scene) {
        if (isInsideRect(f, selStartX, selStartY, selEndX, selEndY)) {
            selection.add(f);
        }
    }
    if (selection.size() == 1) selectedForm = selection.get(0);
    selStartX = selStartY = selEndX = selEndY = -1;
    repaint();
}

public Form getFormAtPublic(int x, int y) {
    return getFormAt(x, y);
}

    @Override
    public void update(Form form) { repaint(); }
}