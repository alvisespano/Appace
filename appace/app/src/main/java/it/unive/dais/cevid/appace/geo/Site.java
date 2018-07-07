package it.unive.dais.cevid.appace.geo;

import android.location.Location;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.TreeMap;

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
    private static final String ADDRESS = "Address";
    private static final String ERA = "Era";

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
        return getRow(DESCRIPTION).replaceAll("\\\\n", System.getProperty("line.separator"));
    }

    @Override
    public String toString() {
        return String.format("Site[%s]", getTitle());
    }

    @NonNull
    public String getPhotoName() {
        return getRow(PHOTO);
    }

    public enum Era {
        PreXX, XX, XXI
    }

    @NonNull
    public Era getEra() {
        String s = getRow(ERA);
        switch (s) {
            case "1":
                return Era.PreXX;
            case "2":
                return Era.XX;
            case "3":
                return Era.XXI;
            default:
                throw new UnexpectedException(String.format("unknown Era identifier: %s", s));
        }
    }

    @NonNull
    public String getRomanOrdinal() {
        return RomanNumber.toRoman(row.getLine() - 1);  // header counts as first line
    }

    @NonNull
    public String getAddress() {
        return getRow(ADDRESS);
    }

    private static class RomanNumber {

        private final static TreeMap<Integer, String> map = new TreeMap<>();

        static {
            map.put(1000, "M");
            map.put(900, "CM");
            map.put(500, "D");
            map.put(400, "CD");
            map.put(100, "C");
            map.put(90, "XC");
            map.put(50, "L");
            map.put(40, "XL");
            map.put(10, "X");
            map.put(9, "IX");
            map.put(5, "V");
            map.put(4, "IV");
            map.put(1, "I");
        }

        public static String toRoman(int number) {
            int l = map.floorKey(number);
            if (number == l) {
                return map.get(number);
            }
            return map.get(l) + toRoman(number - l);
        }
    }

}
