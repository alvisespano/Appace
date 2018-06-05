package it.unive.dais.cevid.appace.component;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import it.unive.dais.cevid.appace.R;

public class HomeActivity extends AppCompatActivity {

    @NonNull
    private Button button_map, button_credits, button_presentation;

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
