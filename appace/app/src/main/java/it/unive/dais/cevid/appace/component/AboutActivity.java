package it.unive.dais.cevid.appace.component;

import android.os.Bundle;

import it.unive.dais.cevid.appace.R;


/**
 * Activity per la schermata di crediti e about.
 *
 * @author Alvise Spanò, Università Ca' Foscari
 */
public class AboutActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_about);
    }

}
