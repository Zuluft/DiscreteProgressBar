package com.zuluft.discreteprogressbar;

import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.zuluft.lib.DiscreteProgressBar;

@SuppressWarnings("FieldCanBeLocal")
public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    private DiscreteProgressBar mDiscreteProgressBar;
    private ViewPager mViewPager;

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
        if (savedInstanceState == null) {
            mDiscreteProgressBar
                    .setActiveIndicatorColor(ContextCompat.getColor(this, R.color.colorPrimaryDark),
                            PorterDuff.Mode.SRC_IN);
        }
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
