package com.huawei.sitekit.java;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.huawei.sitekit.java.fragment.KeywordSearchFragment;
import com.huawei.sitekit.java.fragment.NearbySearchFragment;
import com.huawei.sitekit.java.fragment.PlaceDetailFragment;
import com.huawei.sitekit.java.fragment.QuerySuggestionFragment;

public class MainActivity extends AppCompatActivity implements TabLayoutMediator.TabConfigurationStrategy {

    private String[] tabTitles;

    private ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        // TabLayout titles
        tabTitles = getResources().getStringArray(R.array.tabs_title);

        SearchViewPagerAdapter adapter = new SearchViewPagerAdapter(this);

        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        new TabLayoutMediator(tabLayout, viewPager, this).attach();
    }

    @Override
    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
        tab.setText(tabTitles[position]);
        viewPager.setCurrentItem(tab.getPosition(), true);
    }

    static class SearchViewPagerAdapter extends FragmentStateAdapter {

        private static int PAGE_COUNT = 4;

        public SearchViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position)
            {
                case 0: return new KeywordSearchFragment();
                case 1: return new NearbySearchFragment();
                case 2: return new PlaceDetailFragment();
                default: return new QuerySuggestionFragment();
            }
        }

        @Override
        public int getItemCount() {
            return PAGE_COUNT;
        }
    }
}