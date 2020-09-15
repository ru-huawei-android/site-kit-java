package com.huawei.sitekit.java.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.huawei.hms.site.api.SearchResultListener;
import com.huawei.hms.site.api.SearchService;
import com.huawei.hms.site.api.SearchServiceFactory;
import com.huawei.hms.site.api.model.DetailSearchRequest;
import com.huawei.hms.site.api.model.DetailSearchResponse;
import com.huawei.hms.site.api.model.SearchStatus;
import com.huawei.hms.site.api.model.Site;
import com.huawei.sitekit.java.R;
import com.huawei.sitekit.java.common.Config;

import java.util.Objects;

public class PlaceDetailFragment extends Fragment {

    private static String TAG = "PLACE_DETAIL_FRAGMENT";

    private EditText etSiteId;
    private Button btnSearch;
    private TextView tvResult;

    // Declare a SearchService object.
    private SearchService searchService;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_place_detail, container, false);
        Context context = Objects.requireNonNull(getContext());

        etSiteId = view.findViewById(R.id.editTextSiteId);
        tvResult = view.findViewById(R.id.textViewDetailResult);

        view.findViewById(R.id.buttonSearch).setOnClickListener(view12 -> search());

        // Instantiate the SearchService object.
        searchService = SearchServiceFactory.create(context, Config.getAgcApiKey(context));

        return view;
    }

    /**
     * Search method
     */
    private void search() {
        String siteId = etSiteId.getText().toString().trim();

        DetailSearchRequest request = new DetailSearchRequest();
        request.setLanguage("en");

        if (!siteId.isEmpty()) {
            request.setSiteId(siteId);
        }

        searchService.detailSearch(request, resultListener);
    }

    SearchResultListener<DetailSearchResponse> resultListener = new SearchResultListener<DetailSearchResponse>() {

        @Override
        public void onSearchResult(DetailSearchResponse results) {
            Site site = results.getSite();
            Log.i(TAG, "Site: " + site.getSiteId());

            StringBuilder builderPoi = new StringBuilder();
            for (String poi: site.getPoi().getHwPoiTypes()) {
                builderPoi.append(poi).append(" ");
            }

            String message = "SITE ID: " + site.getSiteId() +
                "\nNAME: " + site.getName() +
                "\nADDRESS: " + site.getFormatAddress() +
                "\nLAT: " + site.getLocation().getLat() +
                "LNG: " + site.getLocation().getLng() +
                "\nPOI: " + builderPoi.toString() +
                "\nDISTANCE: " + site.getDistance();

            tvResult.setText(message);
        }

        @Override
        public void onSearchError(SearchStatus status) {
            Log.e(TAG, "Error: " + status.getErrorCode() + " - " + status.getErrorMessage());
        }
    };
}
