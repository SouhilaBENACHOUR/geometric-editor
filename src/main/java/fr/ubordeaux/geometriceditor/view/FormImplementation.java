package fr.ubordeaux.geometriceditor.view;

import fr.ubordeaux.geometriceditor.model.Form;

public abstract class FormImplementation {

    private static FormImplementation instance = null;

    // Méthode abstraite : chaque implémentation sait dessiner une forme
    public abstract void draw_impl(Form f);

    public static FormImplementation getInstance() {
        if (instance == null)
            instance = new FormTxtImplementation(); // défaut console
        return instance;
    }

    public static void setInstance(FormImplementation impl) {
        instance = impl;
    }
}