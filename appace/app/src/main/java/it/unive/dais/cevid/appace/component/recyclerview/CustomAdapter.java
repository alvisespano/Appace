package it.unive.dais.cevid.appace.component.recyclerview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import it.unive.dais.cevid.appace.R;
import it.unive.dais.cevid.appace.component.MapsActivity;
import it.unive.dais.cevid.appace.component.SiteActivity;
import it.unive.dais.cevid.appace.geo.Site;


public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {
    private static final String TAG = "CustomAdapter";
    @NonNull
    private final List<Site> sites;
    @NonNull
    private final Context ctx;


    public CustomAdapter(@NonNull Context ctx, @NonNull List<Site> sites) {
        this.ctx = ctx;
        this.sites = sites;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final TextView textView, idTextView;
        final ImageView imageView;

        public ViewHolder(View v) {
            super(v);
            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(v1 -> {
                int i = getAdapterPosition();
                Log.d(TAG, "Element " + i + " clicked.");
                MapsActivity.startSiteActivity(ctx, sites.get(i));
            });
            idTextView = v.findViewById(R.id.list_id_textview);
            textView = v.findViewById(R.id.list_textview);
            imageView = v.findViewById(R.id.list_imageview);
        }

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerview_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
        Site site = sites.get(position);
        viewHolder.idTextView.setText(site.getPathId());
        viewHolder.textView.setText(site.getTitle());
        viewHolder.imageView.setImageDrawable(SiteActivity.getPhoto(ctx, site));
//        Typeface mantinia = Typeface.create(ResourcesCompat.getFont(viewHolder.getContext(), R.font.mantinia), Typeface.NORMAL);
//        v.set(mantinia);
//        v.setCollapsedTitleTypeface(mantinia);
    }

    @Override
    public int getItemCount() {
        return sites.size();
    }
}
