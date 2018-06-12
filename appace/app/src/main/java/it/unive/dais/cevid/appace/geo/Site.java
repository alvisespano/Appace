package it.unive.dais.cevid.appace.geo;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

import it.unive.dais.cevid.datadroid.lib.parser.CsvParser;
import it.unive.dais.cevid.datadroid.lib.parser.ParserException;
import it.unive.dais.cevid.datadroid.lib.util.Function;
import it.unive.dais.cevid.datadroid.lib.util.MapItem;

public class Site implements MapItem, Serializable {

    private static final String LATITUDE = "Latitude";
    private static final String LONGITUDE = "Longitude";
    private static final String TITLE = "Title";
    private static final String DESCRIPTION = "Description";
    private static final String PHOTO = "Photo";
    private static final String PATH_ID = "PathId";

    private static final String TAG = "Site";

    @NonNull
    private final CsvParser.Row row;

    public Site(@NonNull CsvParser.Row row) {
        this.row = row;
    }

    // TODO: usare questo metodo e togliere le ParserException
    @NonNull
    protected String getRow(String name) {
        try {
            return row.get(name);
        } catch (ParserException e) {
            e.printStackTrace();
            throw new RuntimeException("unexpected exception: %s", e);
        }
    }

    @NonNull
    private LatLng makeLatLng(Function<String, Double> f) throws ParserException {
        return new LatLng(f.apply(row.get(LATITUDE)), f.apply(row.get(LONGITUDE)));
    }

    @Override
    @NonNull
    public LatLng getPosition() throws ParserException {
        try {
            return makeLatLng(Double::parseDouble);
        } catch (NumberFormatException e) {
            return makeLatLng(Location::convert);
        }
    }

    @Override
    @NonNull
    public String getTitle() throws ParserException {
        return row.get(TITLE);
    }

    @Override
    @NonNull
    public String getDescription() throws ParserException {
        return row.get(DESCRIPTION);
    }

    @Override
    public String toString() {
        try {
            return String.format("Site[%s]", getTitle());
        } catch (ParserException e) {
            e.printStackTrace();
            return "Site[UNKNOWN]";
        }
    }

    @NonNull
    public String getPhoto() throws ParserException {
        return row.get(PHOTO);
    }

    @NonNull
    public String getPathId() throws ParserException {
        return row.get(PATH_ID);
    }
}
