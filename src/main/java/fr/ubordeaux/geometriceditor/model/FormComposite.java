package fr.ubordeaux.geometriceditor.model;

import fr.ubordeaux.geometriceditor.editor.GroupFormEditor;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class FormComposite extends Form implements Iterable<Form> {

    private ArrayList<Form> forms    = new ArrayList<>();
    private Set<Form>       formsSet = new HashSet<>();

    public void draw() {
    for (Form f : forms) {
        if (f instanceof FormAbstract) {
            ((FormAbstract) f).draw();
        } else if (f instanceof FormComposite) {
            ((FormComposite) f).draw();
        }
    }
}
    public void add(Form f) {
        if (formsSet.contains(f)) return;
        forms.add(f);
        formsSet.add(f);
        notifyObservers();
    }

    public void remove(Form f) {
        if (!formsSet.contains(f)) return;
        forms.remove(f);
        formsSet.remove(f);
        notifyObservers();
    }

    public boolean contains(Form f) { return formsSet.contains(f); }
    public boolean isEmpty()        { return forms.isEmpty(); }
    public int size()               { return forms.size(); }

    @Override
    public Iterator<Form> iterator() { return forms.iterator(); }

    @Override
    public void accept(FormVisitor visitor) { visitor.visit(this); }

    @Override
    public Form set(int x, int y) {
        int dx = x - x(), dy = y - y();
        for (Form f : forms) f.set(f.x() + dx, f.y() + dy);
        notifyObservers();
        return this;
    }

    @Override
    public Form setColor(int color) {
        for (Form f : forms) f.setColor(color);
        notifyObservers();
        return this;
    }

    @Override
    public int x() {
        if (forms.isEmpty()) return 0;
        int min = Integer.MAX_VALUE, max = Integer.MIN_VALUE;
        for (Form f : forms) { min = Math.min(min, f.x()); max = Math.max(max, f.x()); }
        return (min + max) / 2;
    }

    @Override
    public int y() {
        if (forms.isEmpty()) return 0;
        int min = Integer.MAX_VALUE, max = Integer.MIN_VALUE;
        for (Form f : forms) { min = Math.min(min, f.y()); max = Math.max(max, f.y()); }
        return (min + max) / 2;
    }

    @Override
    public int getColor() {
        return forms.isEmpty() ? 0 : forms.get(0).getColor();
    }

    @Override
    public FormEditor createEditor() { return new GroupFormEditor(this); }

    @Override
    public FormComposite clone() {
        FormComposite g = (FormComposite) super.clone();
        g.forms    = new ArrayList<>();
        g.formsSet = new HashSet<>();
        for (Form f : forms) {
            Form c = f.clone();
            g.forms.add(c);
            g.formsSet.add(c);
        }
        return g;
    }
}