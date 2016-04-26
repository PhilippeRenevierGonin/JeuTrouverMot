package gonin.renevier.philippe.jeumot_v0;

import android.app.Activity;
import android.content.Intent;
import android.database.DataSetObserver;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.HashMap;

import gonin.renevier.philippe.jeumot_v0.sauvegarde.Sauvegarde;
import gonin.renevier.philippe.jeumot_v0.sauvegarde.SauvegardeEncapsulee;

public class ChoisirListe extends AppCompatActivity implements AdapterView.OnItemClickListener {

    public static String CLEF_RETOUR = "clef_pour_retour";
    protected String _clef_retour;


    HashMap<String, String[]> listes = new HashMap<String, String[]> ();
    String [] clefs = {};

    ListView liste;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choisir_liste);


        _clef_retour = getIntent().getStringExtra(CLEF_RETOUR);
        if (_clef_retour == null) _clef_retour = CLEF_RETOUR;

        charger();
    }


    protected void charger() {
        Sauvegarde<String> sauvegarde = SauvegardeEncapsulee.getInstance();

        listes = sauvegarde.toutCharger(this);
        clefs = new String[listes.size()];
        listes.keySet().toArray(clefs);


        String [] tab = clefs;
        String msg = tab[0];
        for(int i = 1; i < tab.length; i++) {
            msg += ", "+tab[i];
        }
        Log.e("JEUMOT", msg+ "clefs = "+clefs.length+" "+clefs[0]);

        liste = (ListView) findViewById(R.id.listView);
        liste.setAdapter(obtenirAdapter());
        // liste.invalidate();

        liste.setOnItemClickListener(this);

    }



    protected String[] getListe(int position) {
        return listes.get(clefs[position]);
    }


    protected ListAdapter obtenirAdapter() {
        return new ListAdapter() {

            @Override
            public void registerDataSetObserver(DataSetObserver observer) {

            }

            @Override
            public void unregisterDataSetObserver(DataSetObserver observer) {

            }

            @Override
            public int getCount() {
                return listes.size();
            }

            @Override
            public Object getItem(int position) {
                return getListe(position);
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public boolean hasStableIds() {
                return false;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = getLayoutInflater().inflate(R.layout.item_liste, liste, false);
                TextView txt = (TextView) v.findViewWithTag("texte_item");

                String [] tab = listes.get(clefs[position]);
                String msg = tab[0];
                for(int i = 1; i < tab.length; i++) {
                    msg += ", "+tab[i];
                }

                txt.setText(msg);

                return v;
            }

            @Override
            public int getItemViewType(int position) {
                return 0;
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public boolean isEmpty() {
                return (listes.size() == 0);
            }

            @Override
            public boolean areAllItemsEnabled() {
                return (listes.size() > 0);
            }

            @Override
            public boolean isEnabled(int position) {
                return true;
            }
        };
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(_clef_retour, getListe(position));
        setResult(Activity.RESULT_OK, resultIntent);
        finish();

    }
}
