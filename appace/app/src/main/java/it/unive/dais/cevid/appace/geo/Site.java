package it.unive.dais.cevid.appace.geo;

import android.location.Location;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

import it.unive.dais.cevid.datadroid.lib.parser.CsvParser;
import it.unive.dais.cevid.datadroid.lib.parser.ParserException;
import it.unive.dais.cevid.datadroid.lib.util.Function;
import it.unive.dais.cevid.datadroid.lib.util.MapItem;
import it.unive.dais.cevid.datadroid.lib.util.UnexpectedException;

public class Site implements MapItem, Serializable {

    private static final String LATITUDE = "Latitude";
    private static final String LONGITUDE = "Longitude";
    private static final String TITLE = "Title";
    private static final String DESCRIPTION = "Description";
    private static final String PHOTO = "Photo";
    private static final String PATH_ID = "PathId";
    private static final String ADDRESS = "Address";

    @NonNull
    private final CsvParser.Row row;

    public Site(@NonNull CsvParser.Row row) {
        this.row = row;
    }

    @NonNull
    protected String getRow(String name) {
        try {
            return row.get(name);
        } catch (ParserException e) {
            e.printStackTrace();
            throw new UnexpectedException("unexpected exception: %s", e);
        }
    }

    @NonNull
    private LatLng makeLatLng(Function<String, Double> f) {
        return new LatLng(f.apply(getRow(LATITUDE)), f.apply(getRow(LONGITUDE)));
    }

    @Override
    @NonNull
    public LatLng getPosition() {
        try {
            return makeLatLng(Double::parseDouble);
        } catch (NumberFormatException e) {
            return makeLatLng(Location::convert);
        }
    }

    @Override
    @NonNull
    public String getTitle() {
        return getRow(TITLE);
    }

    @Override
    @NonNull
    public String getDescription() {
        return getRow(DESCRIPTION);
    }

    @Override
    public String toString() {
        return String.format("Site[%s]", getTitle());
    }

    @NonNull
    public String getPhotoName() {
        return getRow(PHOTO);
    }

    @NonNull
    public String getPathId() {
        return getRow(PATH_ID);
    }

    @NonNull
    public String getAddress() throws ParserException {
        return row.get(ADDRESS);
    }

}
