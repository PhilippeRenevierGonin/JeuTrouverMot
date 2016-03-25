package gonin.renevier.philippe.jeumot_v0;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import gonin.renevier.philippe.jeumot_v0.mots.Mot;


public class JeuMot extends AppCompatActivity {

    Mot mot;
    String [] listeMots = {"pas vu", "AA", "L3", "L30", "LLL", "worker", "nouveaux", "mots", "année", "miage", "programmation", "informatique","java", "L3", "semestre", "graphique", "thread", "android", "POO", "COO", "nouveaux", "mots", "année", "miage", "programmation", "informatique","java", "L3", "semestre", "graphique", "héritage", "délégation", "thread", "android", "worker", "timer", "synchronized", "POO", "COO"};
    int indiceCourant = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.e("MOTS", "onCreate JeuMot");


        setContentView(R.layout.activity_jeux_mot);


//        mot = new Mot();
//
//        FragmentManager fragmentManager = this.getFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.replace(R.id.motARemplir, mot);
//        fragmentTransaction.commit();

        Log.e("MOTS", "onCreate JeuMot setContentView");

        mot = (Mot) getFragmentManager().findFragmentById(R.id.motARemplir);

        if (savedInstanceState == null) {
          mot.setMotAvecTaillePoliceSpecifiee("cliquez sur démarrer", 50);
        } else if (savedInstanceState.containsKey("indice"))
        {
            indiceCourant = savedInstanceState.getInt("indice");
            if (indiceCourant < 0) {
                mot.setMotAvecTaillePoliceSpecifiee("cliquez sur démarrer", 50);
            }
            else mot.setMotAvecTaillePoliceSpecifiee(listeMots[indiceCourant].toUpperCase(), savedInstanceState.getInt("taille"));

            mot.post(new Runnable() {
                @Override
                public void run() {
                    mot.setMotAvecCalculDeTaillePolice(listeMots[indiceCourant].toUpperCase());
                }
           });
        }

    }


    protected void onResume() {
        super.onResume();

        Log.e("MOTS", "onResume JeuMot");

//        mot.setMotAvecTaillePoliceSpecifiee("cliquez sur démarrer", 50);
//       mot.post(new Runnable() {
//           @Override
//           public void run() {
//               mot.setMotAvecTaillePoliceSpecifiee("cliquez sur démarrer", 50);
//           }
//       });

    }



    public void demarrerJeux(View v) {
        Log.e("MOTS", "demarrerJeux JeuMot");

        indiceCourant = (indiceCourant +1)%listeMots.length;
        mot.setMotAvecCalculDeTaillePolice(listeMots[indiceCourant].toUpperCase());
        // mot.setMotAvecTaillePoliceSpecifiee(listeMots[indiceCourant].toUpperCase(), 50);


        if (mSocket != null) {
            JSONObject obj = new JSONObject();
            try {
                obj.put("userName", "toto");
                obj.put("message", listeMots[indiceCourant].toUpperCase());

            } catch (JSONException e) {
                e.printStackTrace();
            }
            mSocket.emit("chatevent", obj);
        }

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("indice", indiceCourant);
        outState.putInt("taille", mot.getDerniereTaille());
    }




    /* pour le chat */
    Socket mSocket;


    public void chat(View v)  {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e("SOCKET", "clic");


                try {
                    // mSocket = IO.socket("http://192.168.1.14:10101");
                    mSocket = IO.socket("http://192.168.0.103:10101");



                    mSocket.connect();






                    Emitter.Listener onNewMessage = new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {



                            JSONObject data = (JSONObject) args[0];
                            String username;
                            String message;
                            try {
                                username = data.getString("userName");
                                message = data.getString("message");


                                Log.e("SOCKET", username+" / "+message);
                            } catch (JSONException e) {
                                Log.e("SOCKET", "JSONException");

                                return;
                            }
                        }};

                    mSocket.on("chatevent", onNewMessage);


            } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }});

    t.run();

    }
}
