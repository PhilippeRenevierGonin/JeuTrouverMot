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
    protected Mot monMot;


    public void onCreate(Bundle saved) {
        super.onCreate(saved);

        /** recuperation des parametres : ici ou dans onCreateView... **/
        // les params
        Bundle args = getArguments();
        lettre = args.getString("lettre");
        etat = args.getBoolean("etat");
        taille = args.getInt("taille");

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.lettre, container, false);
        monTexte = (TextView) v.findViewWithTag("lettre");
        monTexte.setTextSize(TypedValue.COMPLEX_UNIT_PX, taille);

        setEtat(etat);


        if (monMot != null) monMot.lettreChargee();

        return v;
    }


    public void setEtat(boolean value) {
        etat = value;

        if (monTexte != null) {
            if (etat) monTexte.setText(lettre);
            else  monTexte.setText(" ");

            monTexte.invalidate();
        }
    }


    public boolean getEtat() {
        return etat;
    }



    public static Lettre createLetter(String lettre, boolean visible, int taillepolice) {
        Lettre l = new Lettre();

        Bundle args = new Bundle();
        args.putString("lettre", lettre);
        args.putBoolean("etat", visible);
        args.putInt("taille", taillepolice);

        l.setArguments(args);
        return l;
    }


    public void setMot(Mot monMot) {
        this.monMot = monMot;
    }


}
