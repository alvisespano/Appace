package it.unive.dais.cevid.appace.component.recyclerview;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import it.unive.dais.cevid.appace.R;
import it.unive.dais.cevid.appace.component.HomeActivity;
import it.unive.dais.cevid.appace.component.MapsActivity;
import it.unive.dais.cevid.appace.geo.Site;


public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {
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
            HomeActivity.setAnimatedOnClickListener(v, v1 -> {
                int i = getAdapterPosition();
                MapsActivity.startSiteActivity(ctx, sites.get(i));
            });
            idTextView = v.findViewById(R.id.list_id_textview);
            textView = v.findViewById(R.id.list_textview);
            imageView = v.findViewById(R.id.list_imageview);
            Typeface tf = Typeface.create(ResourcesCompat.getFont(ctx, R.font.mantinia), Typeface.NORMAL);
            idTextView.setTypeface(tf);
            textView.setTypeface(tf);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerview_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder vh, final int position) {
        Site site = sites.get(position);
        vh.idTextView.setText(site.getRomanOrdinal());
        vh.textView.setText(site.getTitleResId());
        @ColorRes int cid;
        switch (site.getEra()) {
            case PreXX: cid =  R.color.era_yellow_overlay; break;
            case XX: cid =  R.color.era_red_overlay; break;
            default: cid =  R.color.era_green_overlay; break;
        }
        vh.idTextView.setBackgroundColor(ContextCompat.getColor(ctx, cid));
        vh.imageView.setImageDrawable(site.getMainPhoto());
    }

    @Override
    public int getItemCount() {
        return sites.size();
    }
}
