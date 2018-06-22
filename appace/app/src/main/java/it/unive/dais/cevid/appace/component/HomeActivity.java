package it.unive.dais.cevid.appace.component;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

    private Button button_map, button_credits, button_presentation, button_sources;

    @SuppressWarnings("unused")
    private ImageButton button_en, button_it;
    @Nullable
    private ProgressBarManager progressBarManager;
    @Nullable
    private List<CsvParser.Row> rows = null;
    @Nullable
    private AsyncTask<Void, ProgressCounter, List<CsvParser.Row>> parserAsyncTask = null;


    @SuppressLint("Range")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        findViewById(R.id.home_image).setAlpha(((float) R.integer.home_image_alpha) / 100.f);

        progressBarManager = new ProgressBarManager(this, new ProgressBar[]{findViewById(R.id.progress_bar)});

        if (rows == null) {
            Log.d(TAG, "parsing CSV....");
            CsvParser parser = new CsvParser(new InputStreamReader(getResources().openRawResource(R.raw.luoghi)), true, ";", progressBarManager);
            parserAsyncTask = parser.getAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

        button_map = findViewById(R.id.sites);
        button_map.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, MapsActivity.class);
            intent.putExtra(KEY_ROWS, (Serializable) getCsvRows());
            startActivity(intent);
        });

        button_sources = findViewById(R.id.sources);
        button_sources.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, SourcesActivity.class)));

        button_credits = findViewById(R.id.credits);
        button_credits.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, AboutActivity.class)));

        button_presentation = findViewById(R.id.presentazione);
        button_presentation.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, PresentationActivity.class)));

    }

    // ciclo di vita
    //


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.MENU_LANG1:
                startActivity(new Intent(this, SelectLanguageActivity.class));
                break;
            case R.id.MENU_LANG2:
                startActivity(new Intent(this, SelectLanguageActivity.class));
                break;

        }
        return false;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_with_language, menu);
        return true;
    }



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
