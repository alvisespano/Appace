package it.unive.dais.cevid.appace.component;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

import it.unive.dais.cevid.appace.R;
import it.unive.dais.cevid.appace.geo.Site;
import it.unive.dais.cevid.datadroid.lib.util.UnexpectedException;

public class SiteActivity extends BaseActivity {

    private static final String TAG = "SiteActivity";
    public static final String BUNDLE_KEY_SITE = "site";

    protected FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setCollapsingToolbarFont();

        Intent intent = getIntent();
        Site site = (Site) intent.getSerializableExtra(BUNDLE_KEY_SITE);

        // title
        String title = site.getTitle();
        toolbar.setTitle(title);
        toolbar.setSubtitle(title);
        this.<TextView>findViewById(R.id.site_title_textview).setText(title);

        // photos
        List<Drawable> photos = getPhotos(this, site.getPhotoName());
        LinearLayout layout = findViewById(R.id.site_imageview_layout);
        int i = 0;
        for (Drawable d : photos) {
            ImageView iv = new ImageView(this);
            iv.setId(i++);
            iv.setImageDrawable(d);
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            iv.setPadding(7, 0, 7, 0);
            iv.setAdjustViewBounds(true);
            layout.addView(iv);
        }
//        ImageView iv = findViewById(R.id.site_imageview);
//        iv.setImageDrawable(getPhotos(this, site));

        // text
        TextView tv = findViewById(R.id.site_textview);
        tv.setText(site.getDescription());

        // other stuff
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        ImageButton goGMaps = findViewById(R.id.gotomaps_site_button);
        goGMaps.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(SiteActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(SiteActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            fusedLocationClient.getLastLocation().addOnSuccessListener(SiteActivity.this,
                    (@NonNull Location loc) -> navigate(site.getPosition(), site.getTitle()));
        });
    }

    @NonNull
    public static List<Drawable> getPhotos(Context ctx, String name) {
        List<Drawable> r = new ArrayList<>();
        boolean stop = false;
        for (int i = 1; !stop; ++i) {
            Drawable d;
            if (i == 1) {
                d = getPhoto(ctx, name);
                if (d == null) d = getPhoto(ctx, name + 1);
            }
            else d = getPhoto(ctx, name + i);
            if (d != null) r.add(d);
            else if (i >= 2) stop = true;
        }
        Log.d(TAG, String.format("found %d photos with root name: %s", r.size(), name));
        if (r.size() <= 0)
            throw new UnexpectedException(String.format("no photos available for name: %s", name));
        return r;
    }

    @Nullable
    public static Drawable getPhoto(Context ctx, String name) {
        Resources resources = ctx.getResources();
        final int resourceId = resources.getIdentifier(name, "drawable", ctx.getPackageName());
        try {
            Drawable r = resources.getDrawable(resourceId, null);
            Log.d(TAG, String.format("photo name exists: %s", name));
            return r;
        } catch (Resources.NotFoundException e) {
            Log.d(TAG, String.format("photo name does not exist: %s", name));
            return null;
        }
    }

    @NonNull
    public static Drawable getMainPhoto(Context ctx, String name) {
        return getPhotos(ctx, name).get(0);
    }

//    @Nullable
//    @Deprecated
//    public static Drawable getPhotoForSure(Context ctx, String name) {
//        Resources resources = ctx.getResources();
//        final int resourceId = resources.getIdentifier(name, "drawable", ctx.getPackageName());
//        try {
//            return resources.getDrawable(resourceId, null);
//        } catch (Resources.NotFoundException e) {
//            Log.e(TAG, String.format("SiteActivity.getDrawable(): cannot find resource by name '%s'", name));
//            Toast.makeText(ctx, String.format("Questa immagine è sbagliata: '%s' non esiste. Correggere il CSV", name), Toast.LENGTH_LONG).show();
//            return ctx.getDrawable(R.drawable.beolco);
//        }
//    }

}
