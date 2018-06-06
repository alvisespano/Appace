package it.unive.dais.cevid.appace.component;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.content.res.Configuration;
import java.util.Locale;


import it.unive.dais.cevid.appace.R;

@SuppressWarnings("FieldCanBeLocal")
public class HomeActivity extends AppCompatActivity {

    private Button button_map, button_credits, button_presentation;
    private ImageButton button_en, button_it;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        button_map = (Button) findViewById(R.id.luoghi);
        button_map.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivity(new Intent(HomeActivity.this, MapsActivity.class));
            }
        });

        button_credits = (Button) findViewById(R.id.credits);
        button_credits.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivity(new Intent(HomeActivity.this, AboutActivity.class));
            }
        });

        button_presentation = (Button) findViewById(R.id.presentazione);
        button_presentation.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivity(new Intent(HomeActivity.this, PresentationActivity.class));
            }
        });

        /*
        TODO: 06/06/2018 i due bottoni della lingua inglese e italiano provvisoriamente li ho messi a fianco al bottone "luoghi di pace", poi quando funziona  lo switch della lingua, allora gli sposto
        TODO: 06/06/2018 devo capire perchè dentro l'onClick non mi funziona appunto il codice per la traduzione, che di default ora è sempre inglese
        */
        button_en = (ImageButton) findViewById(R.id.enButton);
        button_en.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Locale locale = new Locale("en");
                Configuration config = getBaseContext().getResources().getConfiguration();
                config.locale = locale;
                getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
            }
        });

        button_it = (ImageButton) findViewById(R.id.itButton);
        button_it.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Locale locale = new Locale("it");
                Configuration config = getBaseContext().getResources().getConfiguration();
                config.locale = locale;
                getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
            }
        });


    }

    // ciclo di vita
    //

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}
