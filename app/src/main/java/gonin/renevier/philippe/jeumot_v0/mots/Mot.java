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

    public String getDernierMot() {
        return dernierMot;
    }

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
        container = (LinearLayout) result;
        return result;
    }


    /**
     *   methode pour effacer tous les fragments (pour qu'ils ne soient pas recréer avec des pertes de liens ou d'ordre...)
     *   mais surtout pour éviter de "violer" les règles de Android sur l'ordre de création des fragments (un fragment qui n'est pas initialiser ne peut pas créer de fragment).
     */
    public void vider() {

        if ((lettres == null) || (lettres.size() == 0)) return;


        // sauvegarde de l'état avant de se séparer des fragements "Lettre"
        if ((lettres != null) && (lettres.size() > 0))   {
            etatsAvantRotation = new boolean[lettres.size()];
            for(int i = 0; i < etatsAvantRotation.length; i++){
                etatsAvantRotation[i] = lettres.get(i).getEtat();
            }
        }

        // on se sépare des Fragements "Lettre"
        if (getFragmentManager() != null) {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            for (int i = lettres.size() - 1; i >= 0; i--) {
                transaction.remove(lettres.get(i));
                lettres.remove(i);
            }
            transaction.commit();
        }
    }


    /**
     * pour afficher un mot, mais sans calculer la taille de la police de caractère
     * @param s  : le mot à afficher
     * @param size : la taille (en pixel) de la police de caractère (forcément monospace)
     * @param cache : pour indiquer si la lettre est cachée ou non
     */
    public void setMotAvecTaillePoliceSpecifiee(String s, int size, boolean cache) {
        nbChargees = 0;

        if ((s == null) || (s.length() == 0)) return;

        vider();
        lettres = new ArrayList<Lettre>();

        derniereTaille = size;
        creerFragments(s, derniereTaille, ! cache);

    }


    /**
     * pour afficher un mot avec un calcul de la taille de police la plus adéquate (la plus grande possible sans déborder)
     * @param s : le mot à afficher
     * @param cache : pour indiquer si la lettre est cachée ou non
     */
    public void setMotAvecCalculDeTaillePolice(String s, boolean cache) {
        nbChargees = 0;

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

        creerFragments(s, derniereTaille, ! cache);

        }


    /**
     * méthode interne pour créer les Fragments "Lettre", une fois la taille de police décider
     * @param s
     * @param size
     * @param etat
     */
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


    /**
     * pour obenir la dernière taille utilisée
     * @return la taille utilisée en pixel
     */
    public int getDerniereTaille() {
        return derniereTaille;
    }


    @Override
    public void onPause()
    {
        super.onPause();
        // pour éviter des erreurs, car ce fragment ne peut pas créer des fragments n'importe quand
        // il faut effacer toutes les Lettres
        vider();
    }



    @Override
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


    }


    /**
     * pour poster une tâche à effactuer sur le thread graphique, le post est fait à la view du fragment
     * @param r la tâche (Runnable) à effectuer
     */
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


    /**
     * Pour faire la liaison entre le fragment et l'activité (ou l'objet) qui l'utilise
     * @param fournisseur l'entité qui utilise ce fragment
     */
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


    /**
     * pour rendre une lettre visible
     * @param indice l'indice de la lettre dans le mot (de 0 à longueur-1)
     */
    public void montrerLettre(int indice) {

        if ((lettres != null) && (lettres.size() > indice) && (indice >= 0)) lettres.get(indice).setEtat(true);
    }


    /**
     * pour savoir si une lettre est cachée
     * @param i l'indice de la lettre dont on veut savoir si elle est cachée ou non
     * @return true si la lettre est cachée, false sinon
     */
    public boolean estLettreCache(int i) {

        boolean resultat = false;

        if ((lettres != null) && (lettres.size() > i) && (i >= 0))  resultat = ! lettres.get(i).getEtat();

            return resultat;
    }


    /**
     * méthode rappeler par les fragments Lettre pour dire qu'ils sont initialisés.
     * Cela permet de savoir quand le mot est pret.
     */
    public void lettreChargee() {
        nbChargees++;
        if ((lettres != null) && (nbChargees >= lettres.size()) && (dernierMot != "") && (nbChargees >= dernierMot.length()) ) {
            if (fournisseur != null) fournisseur.motPret();
        }
    }

    /**
     * méthode pour montrer toutes les Lettre
     */
    public void montrerMot() {
        for(Lettre l : lettres) l.setEtat(true);
    }
}
