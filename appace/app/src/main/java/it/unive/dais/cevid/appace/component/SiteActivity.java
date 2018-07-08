package it.unive.dais.cevid.appace.component;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.Toolbar;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.List;

import it.unive.dais.cevid.appace.R;
import it.unive.dais.cevid.appace.geo.Site;
import it.unive.dais.cevid.datadroid.lib.parser.CsvParser;

public class SiteActivity extends BaseActivity {

    @SuppressWarnings("unused")
    private static final String TAG = "SiteActivity";
    public static final String BUNDLE_KEY_SITE = "site";

    protected FusedLocationProviderClient fusedLocationClient;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setCollapsingToolbarFont();

        Intent intent = getIntent();
        final Site site = new Site (this, (CsvParser.Row) intent.getSerializableExtra(BUNDLE_KEY_SITE));

        // title
        @StringRes int title = site.getTitleResId();
        toolbar.setTitle(title);
        toolbar.setSubtitle(title);
        this.<TextView>findViewById(R.id.site_title_textview).setText(title);

        // photos
        List<Drawable> photos = site.getPhotos();
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

        // text
        TextView tv = findViewById(R.id.site_textview);
        tv.setText(site.getDescriptionResId());   // append newlines for making textview scroll down a bit

//        return r.toString().replaceAll("\\\\n", System.getProperty("line.separator"));


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
            fusedLocationClient.getLastLocation().addOnSuccessListener(SiteActivity.this, (@NonNull Location loc) -> navigate(site));
        });
    }



}
