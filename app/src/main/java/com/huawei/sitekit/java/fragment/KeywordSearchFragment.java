package com.huawei.sitekit.java.fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.InputFilter;
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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.hms.site.api.SearchResultListener;
import com.huawei.hms.site.api.SearchService;
import com.huawei.hms.site.api.SearchServiceFactory;
import com.huawei.hms.site.api.model.Coordinate;
import com.huawei.hms.site.api.model.LocationType;
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

public class KeywordSearchFragment extends Fragment implements SiteAdapter.SiteCallback {

    private final static String TAG = "KEYWORD_SEARCH_FRAGMENT";

    // Declare a SearchService object.
    private SearchService searchService;

    private EditText etQuery;
    private EditText etLocationLatitude;
    private EditText etLocationLongitude;
    private EditText etRadius;
    private Spinner spLocationType;

    private Map<String, LocationType> converterLocationType;

    private SiteAdapter adapterResult;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_keyword_search, container, false);
        Context context = Objects.requireNonNull(getContext());

        etQuery = view.findViewById(R.id.editTextKeywordQuery);
        etLocationLatitude = view.findViewById(R.id.editTextLocationLatitude);
        etLocationLongitude = view.findViewById(R.id.editTextLocationLongitude);
        etRadius = view.findViewById(R.id.editTextRadius);
        etRadius.setFilters(new InputFilter[]{new InputFilterMinMax(1, 50000)});

        view.findViewById(R.id.buttonSearch).setOnClickListener(button -> search());

        adapterResult = new SiteAdapter();
        adapterResult.setCallback(this);

        RecyclerView rvResult = view.findViewById(R.id.recyclerViewResult);
        rvResult.setAdapter(adapterResult);
        rvResult.setLayoutManager(new LinearLayoutManager(context));

        converterLocationType = new LinkedHashMap<>();
        String[] data = new String[LocationType.values().length + 1];
        int i = 0;
        for (LocationType type : LocationType.values()) {
            converterLocationType.put(type.name(), type);
            data[i++] = type.name();
        }

        data[0] = Config.DEFAULT_LOCATION_TYPE;
        converterLocationType.put(Config.DEFAULT_LOCATION_TYPE, null);

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
        String queryText = etQuery.getText().toString().trim();

        if (queryText.isEmpty()) {
            Toast.makeText(getContext(), R.string.enter_query, Toast.LENGTH_SHORT).show();
            return;
        }

        String latitude = etLocationLatitude.getText().toString();
        String longitude = etLocationLongitude.getText().toString();
        String radius = etRadius.getText().toString();

        TextSearchRequest request = new TextSearchRequest();
        request.setQuery(queryText);

        if (!latitude.isEmpty() && !longitude.isEmpty()) {
            Coordinate location = new Coordinate(
                    Double.parseDouble(latitude),
                    Double.parseDouble(longitude)
            );
            request.setLocation(location);
        }

        if (!radius.isEmpty()) {
            request.setRadius(Integer.parseInt(radius));
        }

        String selectedItem = (String) spLocationType.getSelectedItem();

        if (!selectedItem.equals(Config.DEFAULT_LOCATION_TYPE)) {
            LocationType type = converterLocationType.get(selectedItem);
            request.setPoiType(type);
        }

        request.setCountryCode(Config.DEFAULT_COUNTRY_CODE);
        request.setLanguage(Config.DEFAULT_LANGUAGE);
        request.setPageSize(Config.DEFAULT_PAGE_COUNT);

        searchService.textSearch(request, resultListener);
    }


    final SearchResultListener<TextSearchResponse> resultListener = new SearchResultListener<TextSearchResponse>() {
        @Override
        public void onSearchResult(TextSearchResponse results) {
            if (results == null || results.getTotalCount() <= 0) {
                adapterResult.setList(new ArrayList<>());
                Toast.makeText(getContext(), R.string.empty_response, Toast.LENGTH_SHORT).show();
                return;
            }

            List<Site> sites = results.getSites();

            if (sites == null || sites.isEmpty()) {
                adapterResult.setList(new ArrayList<>());
                Toast.makeText(getContext(), R.string.empty_response, Toast.LENGTH_SHORT).show();
                return;
            }

            adapterResult.setList(convertSites(results.getSites()));
        }

        @Override
        public void onSearchError(SearchStatus status) {
            String errorMessage = "Error: " + status.getErrorCode();
            Log.e(TAG, errorMessage);
            Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            adapterResult.setList(new ArrayList<>());
        }
    };

    @Override
    public void onSiteItemClicked(SiteObservable observable) {
        String siteId = observable.getSiteId();
        String message = "Site ID " + siteId + " has been saved to clipboard.";
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();

        // Save Site Id to clipboard
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
