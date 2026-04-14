package fr.ubordeaux.geometriceditor.model;

import java.util.ArrayList;
import java.util.HashSet;

public abstract class Form implements Cloneable {

    // --- Méthodes abstraites ---
    public abstract void accept(FormVisitor visitor);
    public abstract Form setColor(int color);
    public abstract Form set(int x, int y);
    public abstract int x();
    public abstract int y();
    public abstract int getColor();
    public abstract FormEditor createEditor();

    // --- Pattern Prototype ---
    public Form clone() {
        try {
            return (Form) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    // --- Pattern Observer (intégré comme le prof) ---
    private ArrayList<FormObserver> observers = new ArrayList<>();
    private HashSet<FormObserver> observersSet = new HashSet<>();
    private boolean isNotifying = false;

    public void addObserver(FormObserver observer) {
        if (observersSet.contains(observer)) return;
        observers.add(observer);
        observersSet.add(observer);
    }

    public void removeObserver(FormObserver observer) {
        observers.remove(observer);
        observersSet.remove(observer);
    }

    public void notifyObservers() {
        if (isNotifying)
            throw new IllegalStateException("Cannot notify while notifying");
        isNotifying = true;
        for (FormObserver o : (ArrayList<FormObserver>) observers.clone())
            o.update(this);
        isNotifying = false;
    }
}