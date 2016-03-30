package gonin.renevier.philippe.jeumot_v0;


import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import gonin.renevier.philippe.jeumot_v0.ecouter.CompareAMot;
import gonin.renevier.philippe.jeumot_v0.mots.FournisseurDeMot;
import gonin.renevier.philippe.jeumot_v0.mots.Mot;


public class JeuMot extends AppCompatActivity implements FournisseurDeMot {

    Mot mot;
    // String [] listeMots = {"pas vu", "AA", "L3", "L30", "LLL", "worker", "nouveaux", "mots", "année", "miage", "programmation", "informatique","java", "L3", "semestre", "graphique", "thread", "android", "POO", "COO", "nouveaux", "mots", "année", "miage", "programmation", "informatique","java", "L3", "semestre", "graphique", "héritage", "délégation", "thread", "android", "worker", "timer", "synchronized", "POO", "COO"};
    String [] listeMots = {"car", "celui-ci", "celui-là", "cependant", "chaque", "presque", "surtout", "trop", "dessous", "depuis", "souvent"};
    int indiceCourant = -1;


    EditText reponse ;
    CompareAMot devin;
    View suivant;

    InputMethodManager imm ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_jeux_mot);

        devin = new CompareAMot(this);

        suivant = findViewById(R.id.suivant);
        mot = (Mot) getFragmentManager().findFragmentById(R.id.motARemplir);
        reponse = (EditText) findViewById(R.id.reponse);
        reponse.addTextChangedListener(devin);



        String motDepart = "Toucher pour démarrer";

        if (savedInstanceState != null)   {
            indiceCourant = savedInstanceState.getInt("indice");
            if ((indiceCourant >= 0) && (indiceCourant < listeMots.length))  {
                motDepart = listeMots[indiceCourant];
            }
            else indiceCourant = -1;
        }

        programmerCalculMot(motDepart);
        mot.setFournisseur(this);

    }


    protected void programmerCalculMot(final String word) {
        mot.post(new Runnable() {
            @Override
            public void run() {
                mot.setMotAvecCalculDeTaillePolice(word.toUpperCase());
            }
        });
    }


    protected void onResume() {
        super.onResume();

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        //         android:windowSoftInputMode="stateUnchanged"
        // pour fermer le clavier qui s'ouvre apres rotation

    }



    public void demarrerJeux(View v) {
        Log.e("MOTS", "demarrerJeux JeuMot");

        indiceCourant = (indiceCourant +1)%listeMots.length;
        mot.setMotAvecCalculDeTaillePolice(listeMots[indiceCourant].toUpperCase());
        // mot.setMotAvecTaillePoliceSpecifiee(listeMots[indiceCourant].toUpperCase(), 50);

        activerReponse();   }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("indice", indiceCourant);
        outState.putInt("taille", mot.getDerniereTaille());
    }


    @Override
    public String getMotCourant() {
        if ((indiceCourant >= 0) && (indiceCourant < listeMots.length)) {
            return listeMots[indiceCourant];
        }
        else {
            return null;
        }
    }

    @Override
    public void motTrouve() {
        suivant.setEnabled(true);
        reponse.setEnabled(false);
    }

    @Override
    public void demarrer() {
        if ((indiceCourant < 0) || (indiceCourant >= listeMots.length)) {

            indiceCourant = 0;
            mot.setMotAvecCalculDeTaillePolice(listeMots[indiceCourant].toUpperCase());

            activerReponse();
        }
    }

    protected void activerReponse() {
        reponse.setEnabled(true);
        reponse.setText("");
        reponse.requestFocus();
        imm.showSoftInput(reponse, InputMethodManager.SHOW_IMPLICIT);
    }
}
