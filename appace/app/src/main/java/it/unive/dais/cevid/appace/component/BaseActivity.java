package it.unive.dais.cevid.appace.component;

import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;

import java.util.List;

import it.unive.dais.cevid.appace.R;
import it.unive.dais.cevid.datadroid.lib.parser.CsvParser;

public abstract class BaseActivity extends AppCompatActivity {

    public static final String BUNDLE_KEY_ROWS = "ROWS";
    public static final String BUNDLE_KEY_SITES = "SITES";

    @NonNull
    public List<CsvParser.Row> getCsvRowsFromIntent() {
        //noinspection unchecked
        return (List<CsvParser.Row>) getIntent().getSerializableExtra(BUNDLE_KEY_ROWS);
    }

    protected void setTitleFont() {
        CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        Typeface mantinia = Typeface.create(ResourcesCompat.getFont(this, R.font.mantinia), Typeface.NORMAL);
        collapsingToolbar.setExpandedTitleTypeface(mantinia);
        collapsingToolbar.setCollapsedTitleTypeface(mantinia);
    }
}
