package com.huawei.sitekit.java.fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.hms.site.api.SearchResultListener;
import com.huawei.hms.site.api.SearchService;
import com.huawei.hms.site.api.SearchServiceFactory;
import com.huawei.hms.site.api.model.Coordinate;
import com.huawei.hms.site.api.model.HwLocationType;
import com.huawei.hms.site.api.model.SearchStatus;
import com.huawei.hms.site.api.model.Site;
import com.huawei.hms.site.api.model.TextSearchRequest;
import com.huawei.hms.site.api.model.TextSearchResponse;
import com.huawei.sitekit.java.R;
import com.huawei.sitekit.java.adapter.SiteAdapter;
import com.huawei.sitekit.java.adapter.SiteObservable;
import com.huawei.sitekit.java.common.AndroidUtils;
import com.huawei.sitekit.java.common.Config;
import com.huawei.sitekit.java.common.InputFilterMinMax;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class QuerySuggestionFragment extends Fragment implements SiteAdapter.SiteCallback {

    private static String TAG = "KEYWORD_SEARCH_FRAGMENT";

    // Declare a SearchService object.
    private SearchService searchService;

    private EditText etQuery;
    private EditText etLocationLatitude;
    private EditText etLocationLongitude;
    private EditText etRadius;
    private Spinner spLocationType;

    private Map<String, HwLocationType> converterLocationType;

    private SiteAdapter adapterResult;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_query_suggestion, container, false);
        Context context = Objects.requireNonNull(getContext());

        etQuery = view.findViewById(R.id.editTextKeywordQuery);
        etLocationLatitude = view.findViewById(R.id.editTextLocationLongitude);
        etLocationLongitude = view.findViewById(R.id.editTextLocationLatitude);
        etRadius = view.findViewById(R.id.editTextRadius);
        etRadius.setFilters(new InputFilter[] { new InputFilterMinMax(1, 50000)});

        View clFilter = view.findViewById(R.id.constraintLayoutFilter);
        ConstraintLayout clContent = view.findViewById(R.id.constraintLayoutKeyword);

        view.findViewById(R.id.buttonFilter).setOnClickListener(view1 ->
            AndroidUtils.changeFilterVisible(clContent, clFilter)
        );

        adapterResult = new SiteAdapter();
        adapterResult.setCallback(this);

        RecyclerView rvResult = view.findViewById(R.id.recyclerViewResult);
        rvResult.setAdapter(adapterResult);
        rvResult.setLayoutManager(new LinearLayoutManager(getContext()));

        etQuery.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                search();
            }
        });

        converterLocationType = new LinkedHashMap<>();
        String[] data = new String[HwLocationType.values().length];
        int i = 0;
        for (HwLocationType type: HwLocationType.values()) {
            converterLocationType.put(type.name(), type);
            data[i++] = type.name();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spLocationType = view.findViewById(R.id.spinnerLocationType);
        spLocationType.setAdapter(adapter);

        // Instantiate the SearchService object.
        searchService = SearchServiceFactory.create(context, Config.getAgcApiKey(context));

        return view;
    }

    /**
     * Search method
     */
    private void search() {
        String latitude = etLocationLatitude.getText().toString();
        String longitude = etLocationLongitude.getText().toString();
        String radius = etRadius.getText().toString();

        TextSearchRequest request = new TextSearchRequest();

        request.setQuery(etQuery.getText().toString().trim());

        if (!latitude.isEmpty() && !longitude.isEmpty()) {
            Coordinate location = new Coordinate(
                    Double.parseDouble(latitude), Double.parseDouble(longitude)
            );
            request.setLocation(location);
        }

        if (!radius.isEmpty()) {
            request.setRadius(Integer.parseInt(radius));
        }

        String selectedItem = (String) spLocationType.getSelectedItem();
        try {
            HwLocationType type = converterLocationType.get(selectedItem);
            request.setHwPoiType(type);
        } catch (Exception ignored) {}

        request.setCountryCode("En");
        request.setLanguage("en");

        request.setPageSize(10);

        searchService.textSearch(request, resultListener);
    }


    SearchResultListener<TextSearchResponse> resultListener = new SearchResultListener<TextSearchResponse>() {

        @Override
        public void onSearchResult(TextSearchResponse results) {
            if (results == null || results.getTotalCount() <= 0) {
                adapterResult.setList(new ArrayList<>());
                return;
            }

            List<Site> sites = results.getSites();

            if (sites == null || sites.size() == 0) {
                adapterResult.setList(new ArrayList<>());
                return;
            }

            adapterResult.setList(convertSites(results.getSites()));
        }

        @Override
        public void onSearchError(SearchStatus status) {
            Log.e(TAG, "Error: " + status.getErrorCode() + " - " + status.getErrorMessage());
            adapterResult.setList(new ArrayList<>());
        }
    };

    @Override
    public void onSiteItemClicked(SiteObservable observable) {
        String siteId = observable.getSiteId();
        String message = "Site ID " + siteId + " has been saved to clipboard.";
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
        AndroidUtils.saveTextToClipboard(Objects.requireNonNull(getContext()), siteId);
    }

    private List<SiteObservable> convertSites(List<Site> sites) {
        List<SiteObservable> list = new ArrayList<>();

        for (Site site : sites) {
            list.add(SiteObservable.fromSite(site));
        }

        return list;
    }
}
