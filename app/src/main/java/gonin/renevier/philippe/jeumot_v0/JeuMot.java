package gonin.renevier.philippe.jeumot_v0;


import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.text.DecimalFormat;

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
    View suivant, charger;


    FaireApparaitre decompte;
    long decompteRestant = -1;


    TextView score, chrono;
    DecimalFormat df = new DecimalFormat("00.0");


    int nbMotProposé = 0;
    int nbMotTrouvé = 0;

    InputMethodManager imm ;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e("RETOUR", "onCreate");
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_jeux_mot);

        devin = new CompareAMot(this);

        suivant = findViewById(R.id.suivant);
        charger = findViewById(R.id.charger);

        mot = (Mot) getFragmentManager().findFragmentById(R.id.motARemplir);
        mot.setFournisseur(this);

        reponse = (EditText) findViewById(R.id.reponse);
        reponse.addTextChangedListener(devin);
        score = (TextView) findViewById(R.id.score);
        chrono = (TextView) findViewById(R.id.tempsrestant);




        initMot(savedInstanceState);
    }


    protected void initMot(Bundle savedInstanceState) {

        reponse.setText("");
        chrono.setText("");

        // etat par defaut : on ne peut rien faire
        suivant.setEnabled(false);
        reponse.setEnabled(false);
        charger.setEnabled(true);

        String motDepart = "Toucher pour démarrer";
        boolean cache = false;
        decompteRestant = -1;
        // long decompte = DELAI;

        if (savedInstanceState != null)   {
            listeMots = savedInstanceState.getStringArray("liste");

            indiceCourant = savedInstanceState.getInt("indice");
            if ((indiceCourant >= 0) && (indiceCourant < listeMots.length))  {
                motDepart = listeMots[indiceCourant];
                cache = true;
            }
            else indiceCourant = -1;


            decompteRestant = savedInstanceState.getLong("decompte", -1);

            // autres restaurations
            nbMotProposé = savedInstanceState.getInt("nbMotProposé", 0);
            nbMotTrouvé = savedInstanceState.getInt("nbMotTrouvé", 0);
        }

        programmerCalculMot(motDepart, cache);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_jeu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.lancerChargement) {
            lancementActivitéChoixListe();
            return true;
        }
        return false;
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
        Log.e("RETOUR", "onResume");

        super.onResume();

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        raffraichirScore();
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
        // outState.putInt("taille", mot.getDerniereTaille());
        outState.putInt("nbMotProposé", nbMotProposé);
        outState.putInt("nbMotTrouvé", nbMotTrouvé);
        outState.putStringArray("liste", listeMots);

        // stop du countdowntimer
        if (decompte != null) {
            decompte.cancel();
            // dans la rotation, on ne connait pas le délai écoulé depuis la dernière version, du coup...
            // il faudrait retirer l'écart entre deux lettres, et cela montre une lettre tout de suite...
            // (heureusement la rotation prend du temps) [permet la triche sur les mots les plus petits]
            outState.putLong("decompte", decompte.getRemaining() - decompte.getDelai());
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
        Log.e("RETOUR", "motTrouve **************** "+decompteRestant);

        if (reponse.isEnabled()) {
            Log.e("RETOUR", "motTrouve reponse enabled**************** "+decompteRestant);
            if (decompte != null) decompte.cancel();
            decompte = null;

            suivant.setEnabled(true);
            reponse.setEnabled(false);
            charger.setEnabled(true);

            for(int i = 0; i < listeMots[indiceCourant].length(); i++) mot.montrerLettre(i);

            chrono.setText("Mot trouvé"); //  en "+df.format((DELAI-decompte.getRemaining())/1000)+"s");

            nbMotTrouvé += 1;
            decompteRestant = -1;

            raffraichirScore();
        }


    }

    protected void raffraichirScore() {
        if (nbMotTrouvé > 1) score.setText(""+nbMotTrouvé+" mots trouvés sur "+nbMotProposé);
        else if (nbMotTrouvé ==  1) score.setText(""+nbMotTrouvé+" mot trouvé sur "+nbMotProposé);
        else score.setText("aucun mot trouvé sur "+nbMotProposé);
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

       // Log.e("MOTS", "lettre "+indice+" ( "+listeMots[indiceCourant].charAt(indice)+")  il reste... "+decompte.getRemaining());
        chrono.setText("Temps restant : "+df.format(decompte.getRemaining()/1000)+"s");
    }

    @Override
    public void motNonTrouve() {
        if (decompte != null) decompte.cancel();

        suivant.setEnabled(true);
        reponse.setEnabled(false);
        charger.setEnabled(true);
        chrono.setText("Temps imparti écoulé");
        decompteRestant = -1;

        raffraichirScore();

    }

    @Override
    public boolean estLettreCachee(int i) {
        boolean resultat = false;

        if (mot != null) resultat = mot.estLettreCache(i);

        return resultat;
    }

    @Override
    public void motPret() {

        Log.e("MOTS", "motPRET **************** decompteRestant = "+ decompteRestant + " indiceCourant = "+indiceCourant);
        if (decompteRestant >= 0) {
            mot.post(new Runnable() {
                @Override
                public void run() {

                    demarrerDecompte(decompteRestant);
                }
            });
        }
        else {
            suivant.setEnabled(true);
            charger.setEnabled(true);
            reponse.setEnabled(false);

            Log.e("MOTS", "motPRET **************** mot.getDernierMot() = "+ mot.getDernierMot());


            if ((indiceCourant >= 0) && (indiceCourant < listeMots.length) && (mot.getDernierMot() == "") ) {
                mot.setMotAvecCalculDeTaillePolice(listeMots[indiceCourant], false);

            }
            else {
                mot.montrerMot();
            }
        }

    }

    protected void activerReponse() {
        nbMotProposé += 1;

        suivant.setEnabled(false);
        charger.setEnabled(false);

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
            suivant.setEnabled(false);
            charger.setEnabled(false);
            reponse.setEnabled(true);

            int longueur = listeMots[indiceCourant].length() -1; // car le premier onTick est imediat
            if (longueur <= 0) longueur = 1;
            decompte = new FaireApparaitre(delai+500L, DELAI/longueur); // +500L pour avoir le dernier onTick (approximatif) car si le delai restant n'est pas suffisant le onTick est "saute"
            decompte.setFournisseurDeMot(this);
            decompte.start();
        }
    }


    public void charger(View view) {
        lancementActivitéChoixListe();
    }




    public static final int CODE_CHOIX_LISTE = 2412;
    public static final String CLEF_NOUVELLE_LISTE = "nouvelle_liste";

    protected void lancementActivitéChoixListe() {
        Intent choix = new Intent(this, ChoisirListe.class);
        choix.putExtra(ChoisirListe.CLEF_RETOUR, CLEF_NOUVELLE_LISTE);
        startActivityForResult(choix, CODE_CHOIX_LISTE);
    }

    protected void onActivityResult(int requestcode, int resultcode, Intent data) {
        if (requestcode == CODE_CHOIX_LISTE) {
            Log.e("MOTS", "resultcode = "+resultcode);
            if (resultcode == RESULT_OK) {
                String[] newList = data.getStringArrayExtra(CLEF_NOUVELLE_LISTE);
                if ((newList != null) && (newList.length > 0)) {
                    listeMots = newList;
                    indiceCourant = -1;

                    nbMotProposé = 0;
                    nbMotTrouvé = 0;


                    initMot(null);

                    // il reste à changer le mot afficher
                    // changer le score
                    // changer le temps restant
                    // faire attention au element active / desactive

                    // on passe dans le onResume juste après

                }
            }
        }
    }

}
