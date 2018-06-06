package it.unive.dais.cevid.appace.component;

import android.app.Application;
import android.content.Context;

import it.unive.dais.cevid.appace.LocaleHelper;

public class Home extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base, "it"));
    }
}