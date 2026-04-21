package fr.ubordeaux.geometriceditor;

import fr.ubordeaux.geometriceditor.view.FormAwtImplementation;
import fr.ubordeaux.geometriceditor.view.FormImplementation;
import fr.ubordeaux.geometriceditor.view.MainWindow;

public class Main {
    public static void main(String[] args) {
        // On passe au rendu AWT
        FormImplementation.setInstance(new FormAwtImplementation());
        new MainWindow().setVisible(true);
    }
}