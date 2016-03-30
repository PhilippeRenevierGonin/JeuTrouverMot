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


    @Override
    public void onCreate(Bundle saved) {
        monospace = Typeface.create("monospace", Typeface.NORMAL);

        super.onCreate(saved);
        if (saved != null) {
            if (saved.containsKey("mot")) dernierMot = saved.getString("mot");
            if (saved.containsKey("taille")) derniereTaille = saved.getInt("taille");
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

        if (getFragmentManager() != null) {

            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            for (int i = lettres.size() - 1; i >= 0; i--) {
                transaction.remove(lettres.get(i));
                lettres.remove(i);
            }
            transaction.commit();
        }
    }


    public void setMotAvecTaillePoliceSpecifiee(String s, int size) {

        if ((s == null) || (s.length() == 0)) return;

        vider();
        lettres = new ArrayList<Lettre>();

        derniereTaille = size;
        creerFragments(s, derniereTaille, true);

    }


    public void setMotAvecCalculDeTaillePolice(String s) {

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

        creerFragments(s, derniereTaille, true);

        }



    private void creerFragments(String s, int size, boolean etat) {

        if (getFragmentManager() != null) {

            dernierMot = new String(s);

            for (int i = 0; i < s.length(); i++) {
                String l = "" + s.charAt(i);
                Lettre lettre = Lettre.createLetter(l, etat, size);

                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.add(container.getId(), lettre);
                transaction.commit();

                lettres.add(lettre);
            }


            container.invalidate();
        }
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
                setMotAvecTaillePoliceSpecifiee(dernierMot, derniereTaille);
            }
        }
    }



    public void post(Runnable r) {
        if (container != null) container.post(r);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("mot", dernierMot);
        outState.putInt("taille", getDerniereTaille());
    }



    public void setFournisseur(final FournisseurDeMot fournisseur) {
        if (container != null) {
            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fournisseur.demarrer();
                }
            });
        }
    }


}
