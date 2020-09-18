package com.huawei.sitekit.java.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.sitekit.java.R;

import java.util.ArrayList;
import java.util.List;

public class SiteAdapter extends RecyclerView.Adapter<SiteAdapter.SiteViewHolder> {

    public interface SiteCallback {
        void onSiteItemClicked(SiteObservable observable);
    }

    private List<SiteObservable> mObservables = new ArrayList<>();
    private SiteCallback mCallback = null;

    public void setList(List<SiteObservable> observables) {
        mObservables = observables;
        notifyDataSetChanged();
    }

    public void setCallback(SiteCallback callback) {
        mCallback = callback;
    }

    @NonNull
    @Override
    public SiteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_site, parent, false);
        return new SiteViewHolder(view, mCallback);
    }

    @Override
    public void onBindViewHolder(@NonNull SiteViewHolder holder, int position) {
        holder.bind(mObservables.get(position));
    }

    @Override
    public int getItemCount() {
        return mObservables.size();
    }

    public static class SiteViewHolder extends RecyclerView.ViewHolder {

        private final TextView textView;
        private final SiteCallback mCallback;

        public SiteViewHolder(@NonNull View itemView, SiteCallback callback) {
            super(itemView);
            textView = itemView.findViewById(R.id.textViewItem);
            mCallback = callback;
        }

        @SuppressLint("DefaultLocale")
        public void bind(SiteObservable observable) {
            CharSequence message = String.format(
                    "%d. %s\nName: %s\nLocation: %s\n%s\nPOI types: %s",
                    getAdapterPosition() + 1,
                    observable.getSiteId(),
                    observable.getName(),
                    observable.getLocation(),
                    observable.getCoordinates(),
                    observable.getPoiTypes()
            );

            textView.setText(message);

            itemView.setOnClickListener(view -> {
                if (mCallback != null) {
                    mCallback.onSiteItemClicked(observable);
                }
            });
        }
    }
}
