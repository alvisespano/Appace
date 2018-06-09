package it.unive.dais.cevid.appace.component;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.List;
import java.util.concurrent.ExecutionException;

import it.unive.dais.cevid.appace.R;
import it.unive.dais.cevid.datadroid.lib.parser.CsvParser;
import it.unive.dais.cevid.datadroid.lib.progress.ProgressBarManager;
import it.unive.dais.cevid.datadroid.lib.progress.ProgressCounter;

@SuppressWarnings("FieldCanBeLocal")
public class HomeActivity extends AppCompatActivity {

    static final String KEY_ROWS = "ROWS";
    private static final String TAG = "HomeActivity";

    private Button button_map, button_credits, button_presentation;

    @SuppressWarnings("unused")
    private ImageButton button_en, button_it;
    @Nullable
    private ProgressBarManager progressBarManager;
    @Nullable
    private List<CsvParser.Row> rows = null;
    @Nullable
    private AsyncTask<Void, ProgressCounter, List<CsvParser.Row>> parserAsyncTask = null;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        progressBarManager = new ProgressBarManager(this, new ProgressBar[]{(ProgressBar) findViewById(R.id.progress_bar_1)});

        Log.d(TAG, "savedInstanceState == null: " + (savedInstanceState == null ? "yes" : "no"));

//        if (savedInstanceState != null) {
//            //noinspection unchecked
//            rows = (List<CsvParser.Row>) savedInstanceState.getSerializable(KEY_ROWS);
//            Log.d(TAG, "rows restored from instance state");
//        }
        if (rows == null) {
            Log.d(TAG, "parsing CSV....");
            CsvParser parser = new CsvParser(new InputStreamReader(getResources().openRawResource(R.raw.luoghi)), true, ";", progressBarManager);
            parserAsyncTask = parser.getAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

        button_map = (Button) findViewById(R.id.luoghi);
        button_map.setOnClickListener(v -> {
            Log.d(TAG, "starting MapsActivity...");
            Intent intent = new Intent(HomeActivity.this, MapsActivity.class);
            intent.putExtra(KEY_ROWS, (Serializable) getCsvRows());
            startActivity(intent);
        });

        button_credits = (Button) findViewById(R.id.credits);
        button_credits.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, AboutActivity.class)));

        button_presentation = (Button) findViewById(R.id.presentazione);
        button_presentation.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, PresentationActivity.class)));

        /*
        TODO: 06/06/2018 i due bottoni della lingua inglese e italiano provvisoriamente li ho messi a fianco al bottone "luoghi di pace", poi quando funziona  lo switch della lingua, allora gli sposto
        TODO: 06/06/2018 devo capire perchè dentro l'onClick non mi funziona appunto il codice per la traduzione, che di default ora è sempre inglese
        */
//        button_en = (ImageButton) findViewById(R.id.enButton);
//        button_en.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Locale locale = new Locale("en");
//                Configuration config = getBaseContext().getResources().getConfiguration();
//                config.locale = locale;
//                getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
//            }
//        });
//
//        button_it = (ImageButton) findViewById(R.id.itButton);
//        button_it.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Locale locale = new Locale("it");
//                Configuration config = getBaseContext().getResources().getConfiguration();
//                config.locale = locale;
//                getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
//            }
//        });
    }

    // ciclo di vita
    //

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //noinspection unchecked
        rows = (List<CsvParser.Row>) savedInstanceState.getSerializable(KEY_ROWS);
        Log.d(TAG, "rows restored from instance state");
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putSerializable(KEY_ROWS, (Serializable) getCsvRows());
        Log.d(TAG, "rows saved into instance state");
        super.onSaveInstanceState(savedInstanceState);
    }

    @NonNull
    private List<CsvParser.Row> getCsvRows() {
        if (rows == null) {
            assert parserAsyncTask != null;
            try {
                rows = parserAsyncTask.get();
            } catch (InterruptedException | ExecutionException e) {
                Log.e(TAG, "CSV parser async task failed");
                e.printStackTrace();
            }
        }
        return rows;
    }

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
