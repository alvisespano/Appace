package it.unive.dais.cevid.appace.component;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.support.annotation.IdRes;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;

import it.unive.dais.cevid.appace.R;

@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity {

    void setTitleFont() {
        CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        Typeface mantinia = Typeface.create(ResourcesCompat.getFont(this, R.font.mantinia), Typeface.NORMAL);
        collapsingToolbar.setExpandedTitleTypeface(mantinia);
        collapsingToolbar.setCollapsedTitleTypeface(mantinia);
    }
}
