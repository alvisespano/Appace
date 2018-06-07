package it.unive.dais.cevid.appace.component;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.content.res.Configuration;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Locale;


import it.unive.dais.cevid.appace.R;

@SuppressWarnings("FieldCanBeLocal")
public class HomeActivity extends AppCompatActivity {

    private Button button_map, button_credits, button_presentation, button_fonts;

    Spinner mLanguage;
    Button btn;
    Locale myLocale;

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

        button_fonts = (Button) findViewById(R.id.fonti);
        button_fonts.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivity(new Intent(HomeActivity.this, FontsActivity.class));
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
