package gonin.renevier.philippe.jeumot_v0;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import gonin.renevier.philippe.jeumot_v0.sauvegarde.Sauvegarde;
import gonin.renevier.philippe.jeumot_v0.sauvegarde.SauvegardeEncapsulee;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
@RunWith(AndroidJUnit4.class)
public class ChoisirListeTest {

    @Rule
    public ActivityTestRule<ChoisirListe> mActivityRule = new ActivityTestRule(ChoisirListe.class , true , false);
    @Mock
    Sauvegarde<String> mockSauvegarde;

    Context choisir;

    HashMap<String, String[]> listes = new HashMap<>();
    String[] tab1 = {"mot1"};

    @Before
    public void initTest() {
        Intent startIntent = new Intent();
        startIntent.putExtra(ChoisirListe.MUSTLOAD, false);
        mActivityRule.launchActivity(startIntent);

        MockitoAnnotations.initMocks( this);
        choisir = mActivityRule.getActivity();

        listes.put("fichier1", tab1);

        when(mockSauvegarde.toutCharger(choisir)).thenReturn(listes);


    }

    @Test
    public void verifierNombreDelement() throws InterruptedException {
        final ChoisirListe activity = mActivityRule.getActivity();
        final Object synchro = new Object();

        activity.runOnUiThread((new Runnable() {
            @Override
            public void run() {
                activity.charger(mockSauvegarde);
                synchronized(synchro) {
                    synchro.notify();
                }
            }
        }));

        synchronized(synchro) {
            synchro.wait();
        }


        assertEquals(activity.listes.size(),1);

        onView(withText("mot1")).perform(click());

        assertEquals(activity.isFinishing(), true);


    }
}