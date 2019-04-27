package sample;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;

public class IO {

    private String path;


    public IO(String path) {
        try {
            this.path = path;

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    static void ecrire(FileWriter writer, String S) {
        try {
            writer.write(S);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void ecrireMode(FileWriter writer, int i) {
        try {
            writer.write(Integer.toString(i));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    static void ecrireSeparateurPrincipal(FileWriter writer) {
        try {


            writer.write("%");


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    static String lireToutFichier(FileReader reader) {
        BufferedReader buffIn = new BufferedReader(reader);
        StringBuilder totalString = new StringBuilder();
        String line = "";

        try {
            while ((line = buffIn.readLine()) != null) {
                totalString.append(line);
            }

            buffIn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return totalString.toString();
    }


    static void ecrireParticule(FileWriter writer, SingleParticle p) {
        try {

            p.writeParticle(writer);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
