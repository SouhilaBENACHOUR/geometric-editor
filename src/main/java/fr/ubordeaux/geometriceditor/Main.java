package fr.ubordeaux.geometriceditor;

import fr.ubordeaux.geometriceditor.model.*;
import fr.ubordeaux.geometriceditor.view.FormImplementation;
import fr.ubordeaux.geometriceditor.view.FormTxtImplementation;

public class Main {
    public static void main(String[] args) {

        FormImplementation.setInstance(new FormTxtImplementation());

        Rectangle rect = new Rectangle(10, 20, 100, 50, 0);
        rect.setColor(0xFF0000);

        RegularPolygon hex = new RegularPolygon(200, 200, 6, 40);
        hex.setColor(0x0000FF);

        FormComposite group = new FormComposite();
        group.add(rect);
        group.add(hex);

        Rectangle rectClone = (Rectangle) rect.clone();
        rectClone.set(300, 300);

        System.out.println("-- formes individuelles --");
        rect.draw();
        hex.draw();

        System.out.println("-- groupe --");
        group.draw();

        System.out.println("-- clone --");
        rectClone.draw();

        System.out.println("OK");
    }
}