package gonin.renevier.philippe.jeumot_v0.mots;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;

import gonin.renevier.philippe.jeumot_v0.R;

/**
 * Created by Philippe on 22/03/2016.
 */
public class Mot extends Fragment {


    LinearLayout container;
    ArrayList<Lettre> lettres;
    int derniereTaille = 50;
    String dernierMot = "";
    Typeface monospace ;

    int nbChargees = 0;


    boolean [] etatsApresRotation = null ;
    boolean [] etatsAvantRotation = null ;
    protected FournisseurDeMot fournisseur;

    @Override
    public void onCreate(Bundle saved) {
        monospace = Typeface.create("monospace", Typeface.NORMAL);

        super.onCreate(saved);
        if (saved != null) {
            if (saved.containsKey("mot")) dernierMot = saved.getString("mot");
            if (saved.containsKey("taille")) derniereTaille = saved.getInt("taille");

            if (saved.containsKey("etats")) etatsApresRotation = saved.getBooleanArray("etats");
        }
    }



    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.mot, parent, false);
        container = (LinearLayout) result; // .findViewWithTag("interieurMot");
        // density = getResources().getDisplayMetrics().density;

        return result;
    }


    public void vider() {

        if ((lettres == null) || (lettres.size() == 0)) return;


        if ((lettres != null) && (lettres.size() > 0))   {
            etatsAvantRotation = new boolean[lettres.size()];
            for(int i = 0; i < etatsAvantRotation.length; i++){
                etatsAvantRotation[i] = lettres.get(i).getEtat();
            }
        }

        if (getFragmentManager() != null) {

            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            for (int i = lettres.size() - 1; i >= 0; i--) {
                transaction.remove(lettres.get(i));
                lettres.remove(i);
            }
            transaction.commit();
        }
    }


    public void setMotAvecTaillePoliceSpecifiee(String s, int size, boolean cache) {
        nbChargees = 0;

        if ((s == null) || (s.length() == 0)) return;

        vider();
        lettres = new ArrayList<Lettre>();

        derniereTaille = size;
        creerFragments(s, derniereTaille, ! cache);

    }


    public void setMotAvecCalculDeTaillePolice(String s, boolean cache) {
        nbChargees = 0;

        Log.e("MOTS", "mot recu  = "+s);

        if ((s == null) || (s.length() == 0)) return;

        vider();
        lettres = new ArrayList<Lettre>();


        derniereTaille = 0;
        int length = -1;
        float height = -1;
        int max = container.getWidth();
        int maxH = container.getHeight();

        Paint p = new Paint();
        p.setTypeface(monospace);


        Rect bounds = new Rect();
        while ((length < max) && (height < maxH)){
            derniereTaille++;
            // calcul de la taille de police
            p.setTextSize(derniereTaille);
            p.getTextBounds(s, 0, s.length(), bounds);
            length = bounds.width();
            height = Math.max(p.getFontSpacing(), bounds.height());


        }

        derniereTaille = derniereTaille -1;
//        float ratio = (float) derniereTaille / density;
//        derniereTaille = (int) (derniereTaille/density);

        creerFragments(s, derniereTaille, ! cache);

        }



    private void creerFragments(String s, int size, boolean etat) {

        if (getFragmentManager() != null) {

            dernierMot = new String(s);

            int tailleMot = s.length();

            for (int i = 0; i < tailleMot; i++) {
                String l = "" + s.charAt(i);
                Lettre lettre ;


                if ((etatsApresRotation != null) && (etatsApresRotation.length == tailleMot)) {
                    lettre = Lettre.createLetter(l, etatsApresRotation[i], size);

                }
                else {
                   lettre = Lettre.createLetter(l, etat, size);
                }


                lettre.setMot(this);

                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.add(container.getId(), lettre);
                transaction.commit();



                lettres.add(lettre);
            }

            container.invalidate();

        }




            etatsApresRotation = null; // on ne le fait qu'une fois


    }


    public int getDerniereTaille() {
        return derniereTaille;
    }


    @Override
    public void onPause()
    {
        super.onPause();
        vider();
    }



    public void onResume() {
        super.onResume();


        if (lettres != null) {
            if (lettres.size() > 0) {
                for(Lettre l : lettres) {

                    if (l.isDetached()) {
                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
                        transaction.add(container.getId(), l);
                        transaction.commit();
                    }
                }
            }
            else {
                setMotAvecTaillePoliceSpecifiee(dernierMot, derniereTaille, true); // true car si rotation ... etatsApresRotation existe
            }
        }


//        Log.e("MOT", "On resume : apres tests sur lettres... lettres.. " + lettres);
//
//        Log.e("MOT", "On resume : restoring.. " + etatsApresRotation);
//
//        if ((lettres != null) && (etatsApresRotation != null) && (etatsApresRotation.length == lettres.size())) {
//            for(int i = 0; i < etatsApresRotation.length; i++) {
//                lettres.get(i).setEtat(etatsApresRotation[i]);
//            }
//
//            etatsApresRotation = null; // on ne le fait qu'une fois
//        }

    }



    public void post(Runnable r) {
        if (container != null) container.post(r);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("mot", dernierMot);
        outState.putInt("taille", getDerniereTaille());

        if (etatsAvantRotation!= null)   {
            outState.putBooleanArray("etats", etatsAvantRotation);
        }

    }



    public void setFournisseur( FournisseurDeMot fournisseur) {
        this.fournisseur = fournisseur;
        if (container != null) {
            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Mot.this.fournisseur.demarrer();
                }
            });
        }
    }


    public void montrerLettre(int indice) {

        if ((lettres != null) && (lettres.size() > indice) && (indice >= 0)) lettres.get(indice).setEtat(true);
    }

    public boolean estLettreCache(int i) {

        boolean resultat = false;

        if ((lettres != null) && (lettres.size() > i) && (i >= 0))  resultat = ! lettres.get(i).getEtat();

//        Log.e("MOTS", "size = "+lettres.size()+" et i = "+i+" et resultat = "+resultat + " / "+lettres.get(i).getEtat());

            return resultat;
    }



    public void lettreChargee() {
        nbChargees++;
        if ((lettres != null) && (nbChargees >= lettres.size()) && (dernierMot != "") && (nbChargees >= dernierMot.length()) ) {
            if (fournisseur != null) fournisseur.motPret();
        }
    }
}
