package gonin.renevier.philippe.jeumot_v0;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import gonin.renevier.philippe.jeumot_v0.mots.Mot;


public class JeuMot extends AppCompatActivity {

    Mot mot;
    String [] listeMots = {"pas vu", "AA", "L3", "L30", "LLL", "worker", "nouveaux", "mots", "année", "miage", "programmation", "informatique","java", "L3", "semestre", "graphique", "thread", "android", "POO", "COO", "nouveaux", "mots", "année", "miage", "programmation", "informatique","java", "L3", "semestre", "graphique", "héritage", "délégation", "thread", "android", "worker", "timer", "synchronized", "POO", "COO"};
    int indiceCourant = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.e("MOTS", "onCreate JeuMot");


        setContentView(R.layout.activity_jeux_mot);


//        mot = new Mot();
//
//        FragmentManager fragmentManager = this.getFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.replace(R.id.motARemplir, mot);
//        fragmentTransaction.commit();

        Log.e("MOTS", "onCreate JeuMot setContentView");

        mot = (Mot) getFragmentManager().findFragmentById(R.id.motARemplir);

        if (savedInstanceState == null) {
          mot.setMotAvecTaillePoliceSpecifiee("cliquez sur démarrer", 50);
        } else if (savedInstanceState.containsKey("indice"))
        {
            indiceCourant = savedInstanceState.getInt("indice");
            if (indiceCourant < 0) {
                mot.setMotAvecTaillePoliceSpecifiee("cliquez sur démarrer", 50);
            }
            else mot.setMotAvecTaillePoliceSpecifiee(listeMots[indiceCourant].toUpperCase(), savedInstanceState.getInt("taille"));

            mot.post(new Runnable() {
                @Override
                public void run() {
                    mot.setMotAvecCalculDeTaillePolice(listeMots[indiceCourant].toUpperCase());
                }
           });
        }

    }


    protected void onResume() {
        super.onResume();

        Log.e("MOTS", "onResume JeuMot");

//        mot.setMotAvecTaillePoliceSpecifiee("cliquez sur démarrer", 50);
//       mot.post(new Runnable() {
//           @Override
//           public void run() {
//               mot.setMotAvecTaillePoliceSpecifiee("cliquez sur démarrer", 50);
//           }
//       });

    }



    public void demarrerJeux(View v) {
        Log.e("MOTS", "demarrerJeux JeuMot");

        indiceCourant = (indiceCourant +1)%listeMots.length;
        mot.setMotAvecCalculDeTaillePolice(listeMots[indiceCourant].toUpperCase());
        // mot.setMotAvecTaillePoliceSpecifiee(listeMots[indiceCourant].toUpperCase(), 50);




    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("indice", indiceCourant);
        outState.putInt("taille", mot.getDerniereTaille());
    }




}
