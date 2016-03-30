package gonin.renevier.philippe.jeumot_v0.decompter;

import android.os.CountDownTimer;
import android.util.Log;

import java.util.ArrayList;

import gonin.renevier.philippe.jeumot_v0.mots.FournisseurDeMot;

/**
 * Created by Philippe on 30/03/2016.
 */
public class FaireApparaitre extends CountDownTimer {
    protected FournisseurDeMot  fournisseur;

    ArrayList<Integer> listeIndice ;

    public long getRemaining() {
        return remaining;
    }

    protected long remaining;

    /**
     * @param millisInFuture    The number of millis in the future from the call
     *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
     *                          is called.
     * @param countDownInterval The interval along the way to receive
     *                          {@link #onTick(long)} callbacks.
     */
    public FaireApparaitre(long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
        remaining = millisInFuture;
    }


    public void setFournisseurDeMot(FournisseurDeMot fournisseur) {
        this.fournisseur = fournisseur;

        String mot = fournisseur.getMotCourant();
        listeIndice = new ArrayList<Integer>();
        for(int i = 0; i < mot.length(); i++) {
            boolean cachee = fournisseur.estLettreCachee(i);
            Log.e("MOTS","setFournisseurDeMot loading.. "+i+" / " + cachee);

            if (cachee) listeIndice.add(i);
        }
    }


    @Override
    public void onTick(long millisUntilFinished) {
        remaining = millisUntilFinished;

        Log.e("MOTS", "decompte, il reste  = " + millisUntilFinished);


        if (listeIndice.size() > 0) {
            int random = (int) (Math.random()*listeIndice.size());
            int indice = listeIndice.remove(random);
            fournisseur.montrerLettre(indice);
        }



    }

    @Override
    public void onFinish() {
        Log.e("MOTS", "decompte, finish "+listeIndice.size());

        remaining = 0;
        for(int i  : listeIndice)   fournisseur.montrerLettre(i);  // il devrait en rester une...
        fournisseur.motNonTrouve();

    }
}
