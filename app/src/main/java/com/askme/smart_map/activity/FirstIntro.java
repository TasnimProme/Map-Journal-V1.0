package com.askme.smart_map.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.askme.smart_map.activity.HomePageBuilder.NearbySlide;
import com.github.paolorotolo.appintro.AppIntro;
import com.yalantis.guillotine.sample.R;

public class FirstIntro extends AppIntro {
    @Override
    public void init(Bundle savedInstanceState) {
        addSlide(NearbySlide.newInstance(R.layout.intro));
        addSlide(NearbySlide.newInstance(R.layout.intro2));
        addSlide(NearbySlide.newInstance(R.layout.intro3));
        addSlide(NearbySlide.newInstance(R.layout.intro4));
    }

    private void loadMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onNextPressed() {
    }

    @Override
    public void onSkipPressed() {
        loadMainActivity();
    }

    @Override
    public void onDonePressed() {
        loadMainActivity();
    }

    @Override
    public void onSlideChanged() {
    }

    public void getStarted(View v) {
        loadMainActivity();
    }
}
