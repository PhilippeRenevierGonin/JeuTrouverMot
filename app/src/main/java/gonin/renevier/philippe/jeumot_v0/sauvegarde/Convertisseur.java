package gonin.renevier.philippe.jeumot_v0.sauvegarde;

/**
 * Created by Philippe on 11/04/2016.
 */
public interface Convertisseur<E> {

    public E lireDepuisFichier(String ligne);

    public String ecrireVersFichier(E données);
}
