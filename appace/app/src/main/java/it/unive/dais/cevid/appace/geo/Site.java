package it.unive.dais.cevid.appace.geo;

import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

import it.unive.dais.cevid.datadroid.lib.component.MapItem;

public class Site implements MapItem {

    @NonNull
    private final LatLng pos;

    @NonNull
    private String title, author, address;

    public Site(@NonNull String title, @NonNull String author, @NonNull String address, @NonNull LatLng pos) {
        this.title = title;
        this.author = author;
        this.address = address;
        this.pos = pos;
    }

    @Override
    public LatLng getPosition() throws Exception {
        return pos;
    }

    @Override
    @NonNull
    public String getTitle() throws Exception {
        return title;
    }

    @Override
    @NonNull
    public String getDescription() throws Exception {
        return String.format("%s\n%s", getAuthor(), getAddress());
    }

    @NonNull
    public String getAuthor() {
        return author;
    }

    @NonNull
    public String getAddress() {
        return address;
    }
}
