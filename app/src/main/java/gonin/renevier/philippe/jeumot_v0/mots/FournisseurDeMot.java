package gonin.renevier.philippe.jeumot_v0.mots;

/**
 * Created by Philippe on 30/03/2016.
 */
public interface FournisseurDeMot {

    public String getMotCourant();

    public void motTrouve();


    public void demarrer();


    public void montrerLettre(int indice);


    public  void motNonTrouve();

    public boolean estLettreCachee(int i);

    public void motPret();

}
