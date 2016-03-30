package gonin.renevier.philippe.jeumot_v0.ecouter;

import android.text.Editable;
import android.text.TextWatcher;

import gonin.renevier.philippe.jeumot_v0.mots.FournisseurDeMot;

/**
 * Created by Philippe on 30/03/2016.
 */
public class CompareAMot implements TextWatcher {


    protected FournisseurDeMot fournisseur;

    public CompareAMot(FournisseurDeMot provider) {
        this.fournisseur = provider;
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String aCompare  = fournisseur.getMotCourant();
        if (aCompare != null) {
            if (aCompare.equalsIgnoreCase(s.toString())) {
                fournisseur.motTrouve();
            }
        }
    }
}
