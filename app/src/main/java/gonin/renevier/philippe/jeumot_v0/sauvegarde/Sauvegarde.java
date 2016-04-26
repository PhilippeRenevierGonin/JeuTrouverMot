package gonin.renevier.philippe.jeumot_v0.sauvegarde;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;

import java.util.HashMap;
import java.util.List;


/**
 * Created by Philippe on 12/02/2015.
 */
public abstract class Sauvegarde<E> {

//    static final String SEPARATOR = "#;#";
//    final String FILENAME = "save.txt";



//     public static abstract  Sauvegarde getInstance() ;




    public abstract HashMap<String, E[]> toutCharger(Context c);

    public abstract E[] charger(Context c, String fichier) ;


    public abstract void sauvegarder(List<E> mesItems, Context c, String fichier) ;



    public // pour faire apparaitre le fichier plus vite sur les ordinateurs connectés
    void scanFile(Context c, String path) {

        MediaScannerConnection.scanFile(c,  new String[]{path}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(String path, Uri uri) {

                    }
                });
    }


}
