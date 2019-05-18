package com.android.audiorecorder;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.audiorecorder.R;
import com.android.audiorecorder.fragments.RecordFragment;
import java.util.Objects;



public class MainActivity extends AppCompatActivity {

    private TextView bottomSheetTitle;
    private BottomSheetBehavior bottomSheetBehavior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setPopupTheme(R.style.ThemeOverlay_AppCompat_Light);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        bottomSheetTitle = findViewById(R.id.bottomsheet_header);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        RecordFragment fragment = new RecordFragment();
        fragmentTransaction.add(R.id.container, fragment);
        fragmentTransaction.commit();
        LinearLayout bottomSheetLayout = findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN: {
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

                    }
                    break;
                    case BottomSheetBehavior.STATE_EXPANDED: {
                        bottomSheetTitle.setText(R.string.bottomsheet_down);
                    }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: {
                        bottomSheetTitle.setText(R.string.bottomsheet_up);
                    }
                    break;
                    case BottomSheetBehavior.STATE_DRAGGING: {
                    }
                    break;
                    case BottomSheetBehavior.STATE_SETTLING: {
                    }
                    break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        if(bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }
}

