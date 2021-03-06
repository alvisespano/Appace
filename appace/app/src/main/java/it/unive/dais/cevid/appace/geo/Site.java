package it.unive.dais.cevid.appace.geo;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import it.unive.dais.cevid.appace.R;
import it.unive.dais.cevid.datadroid.lib.parser.CsvParser;
import it.unive.dais.cevid.datadroid.lib.parser.ParserException;
import it.unive.dais.cevid.datadroid.lib.util.Function;
import it.unive.dais.cevid.datadroid.lib.util.MapItem;
import it.unive.dais.cevid.datadroid.lib.util.UnexpectedException;

public class Site implements MapItem {

    private static final String TAG = "Site";
    private static final String LATITUDE = "Latitude";
    private static final String LONGITUDE = "Longitude";
    private static final String ROOT = "Root";
    private static final String ADDRESS = "Address";
    private static final String ERA = "Era";

    @NonNull
    private final Context ctx;
    @NonNull
    private final CsvParser.Row row;

    public Site(@NonNull Context ctx, @NonNull CsvParser.Row row) {
        this.ctx = ctx;
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

    @NonNull
    @Override
    public String getTitle() {
        return ctx.getString(getTitleResId());
    }

    @StringRes
    public int getStringRedId(String suffix) {
        final String s = String.format("%s_%s", getRoot(), suffix);
        try {
            return getStringResourceByName(ctx, s);
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, String.format("cannot find string resource: %s", s));
            return R.string.site_string_not_found_error;
        }
    }

    @StringRes
    public int getTitleResId() {
        return getStringRedId("title");
    }

    @StringRes
    public int getDescriptionResId() {
        return getStringRedId("text");
    }

    @Override
    public String toString() {
        return String.format("Site[%s]", getRoot());
    }

    @NonNull
    public String getRoot() {
        return getRow(ROOT);
    }

    public CsvParser.Row getCsvRow() {
        return row;
    }

    public enum Era {
        PreXX, XX, XXI
    }

    @NonNull
    public Era getEra() {
        String s = getRow(ERA);
        switch (s) {
            case "1":
            case "PreXX":
            case "XIX":
                return Era.PreXX;
            case "2":
            case "XX":
                return Era.XX;
            case "3":
            case "XXI":
                return Era.XXI;
            default:
                throw new UnexpectedException(String.format("unknown Era identifier: %s", s));
        }
    }

    @NonNull
    public String getRomanOrdinal() {
        return Roman.toRoman(row.getLine() - 1);  // header counts as first line
    }

    @Override
    @NonNull
    public String getDescription() {
        return getRow(ADDRESS);
    }

    @NonNull
    public String getAddress() {
        return getDescription();
    }

    @StringRes
    public static int getStringResourceByName(Context ctx, String name) {
        int r = ctx.getResources().getIdentifier(name, "string", ctx.getPackageName());
        if (r == 0) throw new Resources.NotFoundException("invalid resource ID #0x0");  // ogni tanto ritorna 0 invece dell'eccezione, quindi bisogna controllare
        else return r;
    }

    @NonNull
    public List<BitmapDrawable> getPhotos() {
        String name = getRoot();
        List<BitmapDrawable> r = new ArrayList<>();
        boolean stop = false;
        for (int i = 1; !stop; ++i) {
            BitmapDrawable d;
            if (i == 1) {
                d = getDrawableByName(name);
                if (d == null) d = getDrawableByName(name + 1);
            } else d = getDrawableByName(name + i);
            if (d != null) r.add(d);
            else if (i >= 2) stop = true;
        }
        Log.d(TAG, String.format("found %d photos with root name: %s", r.size(), name));
        if (r.size() <= 0)
            throw new UnexpectedException(String.format("no photos available for name: %s", name));
        return r;
    }

    @Nullable
    private BitmapDrawable getDrawableByName(String name) {
        Resources resources = ctx.getResources();
        @DrawableRes final int rid = resources.getIdentifier(name, "drawable", ctx.getPackageName());
        try {
            BitmapDrawable r = (BitmapDrawable) resources.getDrawable(rid, null);
            Log.d(TAG, String.format("photo name exists: %s", name));
            return r;
        } catch (Resources.NotFoundException e) {
            Log.d(TAG, String.format("photo name does not exist: %s", name));
            return null;
        }
    }

    @NonNull
    public Drawable getMainPhoto() {
        return getPhotos().get(0);
    }

    private static class Roman {
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
