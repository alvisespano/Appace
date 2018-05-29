package it.unive.dais.cevid.appace.component;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import it.unive.dais.cevid.appace.R;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        Button vaiMappa = (Button) findViewById(R.id.luoghi);
        vaiMappa.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                runMappa();
//PROVA 
            }
        });

        Button vaiCrediti = (Button) findViewById(R.id.credits);
        vaiCrediti.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                runCredits();

            }
        });


    }

    // ciclo di vita della app
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



    protected void runMappa(){
        startActivity(new Intent(this, MapsActivity.class));
    }

    protected void runCredits(){
        startActivity(new Intent(this, AboutActivity.class));
    }

}
