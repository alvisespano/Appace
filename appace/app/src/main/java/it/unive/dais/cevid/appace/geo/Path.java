package it.unive.dais.cevid.appace.geo;

import android.support.annotation.NonNull;

import java.util.List;

public class Path {

    @NonNull
    private final List<? extends Site> sites;

    public Path(@NonNull List<? extends Site> sites) {
        this.sites = sites;
    }
}
