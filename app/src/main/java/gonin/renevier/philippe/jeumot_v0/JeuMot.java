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

import gonin.renevier.philippe.jeumot_v0.decompter.FaireApparaitre;
import gonin.renevier.philippe.jeumot_v0.ecouter.CompareAMot;
import gonin.renevier.philippe.jeumot_v0.mots.FournisseurDeMot;
import gonin.renevier.philippe.jeumot_v0.mots.Mot;


public class JeuMot extends AppCompatActivity implements FournisseurDeMot {

    final long DELAI = 10000 ;  // 10 sec

    Mot mot;
    // String [] listeMots = {"pas vu", "AA", "L3", "L30", "LLL", "worker", "nouveaux", "mots", "année", "miage", "programmation", "informatique","java", "L3", "semestre", "graphique", "thread", "android", "POO", "COO", "nouveaux", "mots", "année", "miage", "programmation", "informatique","java", "L3", "semestre", "graphique", "héritage", "délégation", "thread", "android", "worker", "timer", "synchronized", "POO", "COO"};
    String [] listeMots = {"car", "celui-ci", "celui-là", "cependant", "chaque", "presque", "surtout", "trop", "dessous", "depuis", "souvent"};
    int indiceCourant = -1;


    EditText reponse ;
    CompareAMot devin;
    View suivant;


    FaireApparaitre decompte;
    long decompteRestant = -1;

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
        boolean cache = false;
        decompteRestant = -1;
        // long decompte = DELAI;

        if (savedInstanceState != null)   {
            indiceCourant = savedInstanceState.getInt("indice");
            if ((indiceCourant >= 0) && (indiceCourant < listeMots.length))  {
                motDepart = listeMots[indiceCourant];
                cache = true;
            }
            else indiceCourant = -1;


            decompteRestant = savedInstanceState.getLong("decompte", DELAI);
        }

        programmerCalculMot(motDepart, cache);
        mot.setFournisseur(this);

    }


    protected void programmerCalculMot(final String word, final boolean cache) {
        mot.post(new Runnable() {
            @Override
            public void run() {
                mot.setMotAvecCalculDeTaillePolice(word.toUpperCase(), cache);

                // demarrerDecompte(decompte);
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

        indiceCourant = (indiceCourant +1)%listeMots.length;
        mot.setMotAvecCalculDeTaillePolice(listeMots[indiceCourant].toUpperCase(), true);
        // mot.setMotAvecTaillePoliceSpecifiee(listeMots[indiceCourant].toUpperCase(), 50);

        activerReponse();   }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("indice", indiceCourant);
        outState.putInt("taille", mot.getDerniereTaille());

        // stop du countdowntimer
        if (decompte != null) {
            decompte.cancel();
            // dans la rotation, on ne connait pas le délai écoulé depuis la dernière version, du coup...
            // il faudrait retirer l'écart entre deux lettres, et cela montre une lettre tout de suite...
            // (heureusement la rotation prend du temps) [permet la triche sur les mots les plus petits]
            outState.putLong("decompte", decompte.getRemaining()-decompte.getDelai());
        }

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
        if (decompte != null) decompte.cancel();
        suivant.setEnabled(true);
        reponse.setEnabled(false);

        for(int i = 0; i < listeMots[indiceCourant].length(); i++) mot.montrerLettre(i);
    }

    @Override
    public void demarrer() {
        if ((indiceCourant < 0) || (indiceCourant >= listeMots.length)) {

            indiceCourant = 0;
            mot.setMotAvecCalculDeTaillePolice(listeMots[indiceCourant].toUpperCase(), true);

            activerReponse();
        }
    }

    @Override
    public void montrerLettre(int indice) {
        if (mot.estLettreCache(indice) ) mot.montrerLettre(indice);
        else         Log.e("MOTS AZERTY", "ON MONTRE UNE LETTRE DEJA VISIBLE");

       Log.e("MOTS", "lettre "+indice+" ( "+listeMots[indiceCourant].charAt(indice)+")  il reste... "+decompte.getRemaining());
    }

    @Override
    public void motNonTrouve() {
        suivant.setEnabled(true);
        reponse.setEnabled(false);

    }

    @Override
    public boolean estLettreCachee(int i) {
        boolean resultat = false;

        if (mot != null) resultat = mot.estLettreCache(i);

        return resultat;
    }

    @Override
    public void motPret() {
        Log.e("MOTS", "motPRET ****************");
        if (decompteRestant >= 0) {
            mot.post(new Runnable() {
                @Override
                public void run() {

                    demarrerDecompte(decompteRestant);
                }
            });
        }

    }

    protected void activerReponse() {

        suivant.setEnabled(false);

        reponse.setEnabled(true);
        reponse.setText("");
        reponse.requestFocus();
        imm.showSoftInput(reponse, InputMethodManager.SHOW_IMPLICIT);

        decompteRestant = DELAI;

        // demarrerDecompte(DELAI);
    }


    protected void demarrerDecompte(long delai) {
        if (decompte != null) decompte.cancel();

        if ((indiceCourant >= 0) && (indiceCourant < listeMots.length) ) {
            int longueur = listeMots[indiceCourant].length() -1; // car le premier onTick est imediat
            if (longueur <= 0) longueur = 1;
            decompte = new FaireApparaitre(delai+500L, DELAI/longueur); // +500L pour avoir le dernier onTick (approximatif) car si le delai restant n'est pas suffisant le onTick est "saute"
            decompte.setFournisseurDeMot(this);
            decompte.start();
        }
    }
}
