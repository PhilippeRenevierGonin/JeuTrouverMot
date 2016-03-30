package gonin.renevier.philippe.jeumot_v0.mots;

import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import gonin.renevier.philippe.jeumot_v0.R;

/**
 * Created by Philippe on 22/03/2016.
 */
public class Lettre extends Fragment {


    boolean etat;
    String lettre;
    TextView monTexte;
    int taille;




    public void onCreate(Bundle saved) {
        super.onCreate(saved);

        /** recuperation des parametres : ici ou dans onCreateView... **/
        // les params
        Bundle args = getArguments();
        lettre = args.getString("lettre");
        etat = args.getBoolean("etat");
        taille = args.getInt("taille");


        if (saved != null) {
            if (saved.containsKey("etatSauve"))  etat = saved.getBoolean("etatSauve");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.lettre, container, false);
        monTexte = (TextView) v.findViewWithTag("lettre");
        monTexte.setTextSize(TypedValue.COMPLEX_UNIT_PX, taille);

        setEtat(etat);


        return v;
    }


    public void setEtat(boolean value) {
        etat = value;

        if (monTexte != null) {
            if (etat) monTexte.setText(lettre);
            // else  monTexte.setText("\u00a0");
            else  monTexte.setText("X");

            monTexte.invalidate();
        }
    }



    public static Lettre createLetter(String lettre, boolean visible, int taillepolice) {
        Lettre l = new Lettre();

        Bundle args = new Bundle();
        args.putString("lettre",lettre);
        args.putBoolean("etat", visible);
        args.putInt("taille", taillepolice);

        l.setArguments(args);
        return l;
    }




    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean("etatSauve", etat);
    }


}
