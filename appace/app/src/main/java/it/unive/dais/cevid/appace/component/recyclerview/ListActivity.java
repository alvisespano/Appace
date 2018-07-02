package it.unive.dais.cevid.appace.component.recyclerview;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

import it.unive.dais.cevid.appace.R;
import it.unive.dais.cevid.appace.component.BaseActivity;
import it.unive.dais.cevid.appace.geo.Site;
import it.unive.dais.cevid.datadroid.lib.parser.CsvParser;

public class ListActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setCollapsingToolbarFont();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        RecyclerViewFragment fragment = new RecyclerViewFragment();

        List<CsvParser.Row> rows = getCsvRowsFromIntent();
        ArrayList<Site> sites = new ArrayList<>();
        for (CsvParser.Row row : rows) sites.add(new Site(row));
        Bundle b = new Bundle();
        b.putSerializable(BUNDLE_KEY_SITES, sites);
        fragment.setArguments(b);

        transaction.replace(R.id.content_fragment, fragment);
        transaction.commit();
    }
}
