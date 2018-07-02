package it.unive.dais.cevid.appace.component;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import it.unive.dais.cevid.appace.R;

public class SourcesActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sources);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setCollapsingToolbarFont();
    }
}
