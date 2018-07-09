package it.unive.dais.cevid.appace.component;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import it.unive.dais.cevid.appace.R;
import it.unive.dais.cevid.appace.geo.Site;

public class PresentationActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_presentation);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setCollapsingToolbarFont();

        // TODO: contirnuare ad indagare il mistero dei tag html
//        TextView tv = findViewById(R.id.presentation_textview);
//        CharSequence s = Site.getHtmlStringResourceByName(this, "presentation_text");
//        tv.setText(s);
    }


}
