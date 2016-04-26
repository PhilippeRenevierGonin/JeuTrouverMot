package gonin.renevier.philippe.jeumot_v0.sauvegarde;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Philippe on 11/02/2016.
 */
public class EntreesSortiesFichier<E> {


    protected ArrayList<E> chargerDepuisStream(FileInputStream fis,  Convertisseur<E> convertisseur) {
        ArrayList<E> loaded = new ArrayList<E>();

        try {
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader buffreader = new BufferedReader(isr);

            String readString = buffreader.readLine();
            while (readString != null) {
                if (!readString.equals("")) {
                    E donnéesLues = convertisseur.lireDepuisFichier(readString);
                    loaded.add(donnéesLues);
                }

                readString = buffreader.readLine();
            }

            isr.close();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return loaded;

    }



    protected void enregisterVersStream(List<E> mesItems, FileOutputStream fos, Convertisseur<E> convertisseur) {
        try {
            OutputStreamWriter osw = new OutputStreamWriter( fos ) ;

            String save = "/* saved to do list */\n";

            for(E i : mesItems) {
                save += ""+convertisseur.ecrireVersFichier(i)+"\n";
            }

            osw.write ( save ) ;
            osw.close() ;
        } catch (IOException e) {
            e.printStackTrace();

        }

    }


}
