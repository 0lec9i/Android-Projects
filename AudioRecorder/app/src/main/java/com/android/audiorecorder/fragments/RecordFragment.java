package com.android.audiorecorder.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;
import com.android.audiorecorder.MySharedPreferences;
import com.android.audiorecorder.R;
import com.android.audiorecorder.RecordingService;
import com.melnykov.fab.FloatingActionButton;

import java.io.File;


public class RecordFragment extends Fragment {

    private FloatingActionButton mRecordButton = null;
    private TextView mRecordingPrompt;
    private int mRecordPromptCount = 0;
    private boolean mStartRecording = true;
    private Chronometer mChronometer = null;
    long timeWhenPaused = 0;

    public RecordFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View recordView = inflater.inflate(R.layout.fragment_record, container, false);

        mChronometer = recordView.findViewById(R.id.chronometer);
        mRecordingPrompt = recordView.findViewById(R.id.recording_status_text);
        mRecordButton = recordView.findViewById(R.id.btnRecord);
        mRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((ActivityCompat.checkSelfPermission(getContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) &&
                        (ActivityCompat.checkSelfPermission(getContext(),
                                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)) {
                    requestPermissions(
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.RECORD_AUDIO},
                            10
                    );
                } else {
                    onRecord(mStartRecording);
                    mStartRecording = !mStartRecording;
                }
            }
        });

        return recordView;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 10) {
            if (permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && permissions[1].equals(Manifest.permission.RECORD_AUDIO)
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                onRecord(mStartRecording);
                mStartRecording = !mStartRecording;
            }
        }
    }

    private void onRecord(boolean start){
        Intent intent = new Intent(getActivity(), RecordingService.class);
        if (start) {
            mRecordButton.setImageResource(R.drawable.ic_media_stop);
            Toast.makeText(getActivity(),R.string.toast_recording_start,Toast.LENGTH_SHORT).show();
            String recordingLocation = MySharedPreferences.loadStringSavedPreferences(MySharedPreferences.spStorageLocation,
                    getActivity());
            File folder = new File(Environment.getExternalStorageDirectory() +"/"+ recordingLocation);
            if (!folder.exists()) {
                folder.mkdir();
            }
            mChronometer.setBase(SystemClock.elapsedRealtime());
            mChronometer.start();
            mChronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
                @Override
                public void onChronometerTick(Chronometer chronometer) {
                    if (mRecordPromptCount == 0) {
                        mRecordingPrompt.setText(getString(R.string.record_in_progress) + ".");
                    } else if (mRecordPromptCount == 1) {
                        mRecordingPrompt.setText(getString(R.string.record_in_progress) + "..");
                    } else if (mRecordPromptCount == 2) {
                        mRecordingPrompt.setText(getString(R.string.record_in_progress) + "...");
                        mRecordPromptCount = -1;
                    }

                    mRecordPromptCount++;
                }
            });
            getActivity().startService(intent);
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            mRecordingPrompt.setText(getString(R.string.record_in_progress) + ".");
            mRecordPromptCount++;

        } else {
            mRecordButton.setImageResource(R.drawable.ic_mic_white_36dp);
            mChronometer.stop();
            mChronometer.setBase(SystemClock.elapsedRealtime());
            timeWhenPaused = 0;
            mRecordingPrompt.setText(getString(R.string.record_prompt));
            getActivity().stopService(intent);
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }
}