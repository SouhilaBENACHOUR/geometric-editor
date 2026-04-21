package fr.ubordeaux.geometriceditor.view;

import java.awt.Image;
import java.awt.Toolkit;
import java.net.URL;

public class IconLoader {

    public static Image load(String name) {
        URL url = IconLoader.class.getClassLoader()
                      .getResource("icons/" + name);
        if (url == null) {
            System.err.println("Icone introuvable : " + name);
            return null;
        }
        return Toolkit.getDefaultToolkit().getImage(url);
    }
}