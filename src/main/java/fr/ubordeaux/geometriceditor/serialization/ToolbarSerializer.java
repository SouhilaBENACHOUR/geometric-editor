package fr.ubordeaux.geometriceditor.serialization;

import fr.ubordeaux.geometriceditor.model.Form;
import fr.ubordeaux.geometriceditor.model.FormComposite;
import fr.ubordeaux.geometriceditor.model.Rectangle;
import fr.ubordeaux.geometriceditor.model.RegularPolygon;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ToolbarSerializer {

    // Fichier de sauvegarde dans le répertoire home de l'utilisateur
    private static final String PATH =
        System.getProperty("user.home") + "/.geometric-editor-toolbar.txt";

    public static void save(List<Form> prototypes) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(PATH))) {
            for (Form f : prototypes) {
                pw.println(serialize(f));
            }
        } catch (IOException e) {
            System.err.println("Erreur sauvegarde toolbar : " + e.getMessage());
        }
    }

    public static List<Form> load() {
        List<Form> result = new ArrayList<>();
        File file = new File(PATH);
        if (!file.exists()) return result;

        try (BufferedReader br = new BufferedReader(new FileReader(PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                Form f = deserialize(line);
                if (f != null) result.add(f);
            }
        } catch (IOException e) {
            System.err.println("Erreur chargement toolbar : " + e.getMessage());
        }
        return result;
    }

    private static String serialize(Form f) {
        if (f instanceof Rectangle) {
            Rectangle r = (Rectangle) f;
            return "RECT," + r.x() + "," + r.y() + ","
                + r.getWidth() + "," + r.getHeight() + ","
                + r.getArcRadius() + "," + r.getColor();
        } else if (f instanceof RegularPolygon) {
            RegularPolygon p = (RegularPolygon) f;
            return "POLY," + p.x() + "," + p.y() + ","
                + p.getSides() + "," + p.getSideLength() + ","
                + p.getColor();
        } else if (f instanceof FormComposite) {
            StringBuilder sb = new StringBuilder("GROUP");
            for (Form child : (FormComposite) f) {
                sb.append("|").append(serialize(child));
            }
            return sb.toString();
        }
        return "";
    }

    private static Form deserialize(String line) {
        if (line.startsWith("RECT")) {
            String[] p = line.split(",");
            Rectangle r = new Rectangle(
                Integer.parseInt(p[1]), Integer.parseInt(p[2]),
                Integer.parseInt(p[3]), Integer.parseInt(p[4]),
                Integer.parseInt(p[5])
            );
            r.setColor(Integer.parseInt(p[6]));
            return r;
        } else if (line.startsWith("POLY")) {
            String[] p = line.split(",");
            RegularPolygon poly = new RegularPolygon(
                Integer.parseInt(p[1]), Integer.parseInt(p[2]),
                Integer.parseInt(p[3]), Integer.parseInt(p[4])
            );
            poly.setColor(Integer.parseInt(p[5]));
            return poly;
        } else if (line.startsWith("GROUP")) {
            FormComposite group = new FormComposite();
            String[] parts = line.substring(5).split("\\|");
            for (String part : parts) {
                if (!part.isEmpty()) {
                    Form child = deserialize(part);
                    if (child != null) group.add(child);
                }
            }
            return group;
        }
        return null;
    }
    public static boolean hasSavedToolbar() {
        return new File(PATH).exists();
    }
}