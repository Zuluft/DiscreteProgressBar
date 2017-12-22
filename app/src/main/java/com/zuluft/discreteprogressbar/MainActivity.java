package com.zuluft.discreteprogressbar;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.zuluft.lib.DiscreteProgressBar;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    private DiscreteProgressBar mDiscreteProgressBar;
    private ViewPager mViewPager;

    public static final String KEY_CURRENT_PROGRESS = "KEY_CURRENT_PROGRESS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDiscreteProgressBar = findViewById(R.id.discreteProgressBar);
        mViewPager = findViewById(R.id.viewPager);
        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(pagerAdapter);
        mDiscreteProgressBar.setMaxProgress(pagerAdapter.getCount());
        mDiscreteProgressBar.setCurrentProgress(mViewPager.getCurrentItem());
        mViewPager.addOnPageChangeListener(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mDiscreteProgressBar.setCurrentProgress(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
