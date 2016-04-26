package gonin.renevier.philippe.jeumot_v0.sauvegarde;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by Philippe on 11/02/2016.
 */
public class SauvegardeEncapsulee extends Sauvegarde<String> implements Convertisseur<String> {


    EntreesSortiesFichier io;
    String extension = ".txt";

    private static SauvegardeEncapsulee currentInstance = null;

    public static Sauvegarde getInstance() {
        if (currentInstance == null) currentInstance = new SauvegardeEncapsulee();
        return currentInstance;
    }

    protected SauvegardeEncapsulee() {
        io = new EntreesSortiesFichier();
    }

    @Override
    public HashMap<String, String[]> toutCharger(Context c) {
        HashMap<String, String[]> resultat = new HashMap<String, String[]>();

        File dossier = c.getExternalFilesDir(null);
        String[] listefichiers = dossier.list();
        for (int i = 0; i < listefichiers.length; i++) {
            if (listefichiers[i].endsWith(extension)) {
                String clef = listefichiers[i].substring(0, listefichiers[i].length()-extension.length());
                resultat.put(clef, charger(c,listefichiers[i] ));
            }
        }


        return resultat;
    }

    public String[] charger(Context c, String fichier) {
        ArrayList<String> loaded = null;

        FileInputStream fis = null;
        try {
            File aCharger = new File(c.getExternalFilesDir(null), fichier);

            fis = new FileInputStream(aCharger);
            loaded = io.chargerDepuisStream(fis, this);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (loaded != null) {
            String[] result = new String[loaded.size()];
            return loaded.toArray(result);
        } else return new String[0];
    }


    public void sauvegarder(List<String> mesItems, Context c, String fichier) {
        FileOutputStream fos = null;
        try {
            File aEcrire = new File(c.getExternalFilesDir(null), fichier);

            fos = new FileOutputStream(aEcrire);
            io.enregisterVersStream(mesItems, fos, this);
        } catch (FileNotFoundException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }
    }


    @Override
    public String lireDepuisFichier(String ligne) {
        return ligne;
    }

    @Override
    public String ecrireVersFichier(String données) {
        return données;
    }
}
