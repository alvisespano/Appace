package it.unive.dais.cevid.appace.component;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.q42.android.scrollingimageview.ScrollingImageView;

import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.List;
import java.util.concurrent.ExecutionException;

import it.unive.dais.cevid.appace.R;
import it.unive.dais.cevid.appace.component.recyclerview.ListActivity;
import it.unive.dais.cevid.datadroid.lib.parser.CsvParser;
import it.unive.dais.cevid.datadroid.lib.progress.ProgressBarManager;
import it.unive.dais.cevid.datadroid.lib.progress.ProgressCounter;

public class HomeActivity extends BaseActivity {

    private static final String TAG = "HomeActivity";

    @Nullable
    private List<CsvParser.Row> rows = null;
    @Nullable
    private AsyncTask<Void, ProgressCounter, List<CsvParser.Row>> parserAsyncTask = null;


    @SuppressLint("Range")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ProgressBarManager progressBarManager = new ProgressBarManager(this, new ProgressBar[]{findViewById(R.id.progress_bar)});

        // animations
        ScrollingImageView blackBg = findViewById(R.id.home_black_bg), redBg = findViewById(R.id.home_red_bg);
        blackBg.stop();
        redBg.stop();
        blackBg.start();
        redBg.start();

        AlphaAnimation blackBgAnim = new AlphaAnimation(0.5f, 0.8f);
        AlphaAnimation redBgAnim = new AlphaAnimation(0.6f, 0.1f);

        blackBgAnim.setDuration(11 * 1000);
        redBgAnim.setDuration(23 * 1000);

        blackBgAnim.setRepeatCount(Animation.INFINITE);
        redBgAnim.setRepeatCount(Animation.INFINITE);

        blackBgAnim.setRepeatMode(Animation.REVERSE);
        redBgAnim.setRepeatMode(Animation.REVERSE);

        blackBg.startAnimation(blackBgAnim);
        redBg.startAnimation(redBgAnim);

        ValueAnimator colorAnim = ValueAnimator.ofArgb(
                getResources().getColor(R.color.mainBright_overlay),
                0x88000000
        );
        TextView tv = findViewById(R.id.home_scrolling_textview);
        colorAnim.setDuration(2 * 1000);
        colorAnim.setRepeatCount(ValueAnimator.INFINITE);
        colorAnim.setRepeatMode(ValueAnimator.REVERSE);
        colorAnim.addUpdateListener(an -> {
            int x = (int) an.getAnimatedValue();
            tv.setBackgroundColor(x);
        });
        colorAnim.start();


//        FrameLayout layout = findViewById(R.id.home_background_layout);
//        // doppia rotation animata
//        ValueAnimator an1 = ValueAnimator.ofFloat(0.0f, 360.0f);
//        an1.addUpdateListener(an -> {
//            float a = (float) an.getAnimatedValue();
////            blackBg.setRotation(a);
////            redBgAnim.setRotation(a * 2.3);
////            layout.setRotation(a);    // anche l'intero layout si può ruotare, volendo
//        });
//        an1.setDuration(100 * 1000);
//        an1.setRepeatCount(ValueAnimator.INFINITE);
//        an1.start();

        // state recovery
        if (rows == null) {
            Log.d(TAG, "parsing CSV....");
            CsvParser parser = new CsvParser(new InputStreamReader(getResources().openRawResource(R.raw.luoghi)), true, ";", progressBarManager);
            parserAsyncTask = parser.getAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

        setAnimatedOnClickListener(R.id.map, v -> startCsvRowsActivity(MapsActivity.class));
        setAnimatedOnClickListener(R.id.list, v -> startCsvRowsActivity(ListActivity.class));
        setAnimatedOnClickListener(R.id.sources, v -> startActivity(new Intent(HomeActivity.this, SourcesActivity.class)));
        setAnimatedOnClickListener(R.id.credits, v -> startActivity(new Intent(HomeActivity.this, AboutActivity.class)));
        setAnimatedOnClickListener(R.id.presentation, v -> startActivity(new Intent(HomeActivity.this, PresentationActivity.class)));
    }

    private <V extends View> void setAnimatedOnClickListener(@IdRes int rid, View.OnClickListener l) {
        V b = findViewById(rid);
        setAnimatedOnClickListener(b, l);
    }

    private void startCsvRowsActivity(Class<? extends BaseActivity> cl) {
        Intent intent = new Intent(HomeActivity.this, cl);
        intent.putExtra(BaseActivity.BUNDLE_KEY_ROWS, (Serializable) getCsvRows());
        startActivity(intent);
    }

    // ciclo di vita
    //

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //noinspection unchecked
        rows = (List<CsvParser.Row>) savedInstanceState.getSerializable(BaseActivity.BUNDLE_KEY_ROWS);
        Log.d(TAG, "rows restored from instance state");
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putSerializable(BaseActivity.BUNDLE_KEY_ROWS, (Serializable) getCsvRows());
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
