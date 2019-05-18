package com.android.audiorecorder;

import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class RecordingService extends Service {

    private String mFileName = null;
    private String mFilePath = null;

    private MediaRecorder mRecorder = null;

    private DataBaseOpener mDatabase;

    private long mStartingTimeMillis = 0;
    private long mElapsedMillis = 0;
    private int mElapsedSeconds = 0;
    private OnTimerChangedListener onTimerChangedListener = null;
    private static final SimpleDateFormat mTimerFormat = new SimpleDateFormat("mm:ss", Locale.getDefault());

    private Timer mTimer = null;
    private TimerTask mIncrementTimerTask = null;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public interface OnTimerChangedListener {
        void onTimerChanged(int seconds);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mDatabase = new DataBaseOpener(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            startRecording();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (mRecorder != null) {
            stopRecording();
        }

        super.onDestroy();
    }

    public void startRecording() throws IOException {
        setFileNameAndPath();

        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mRecorder.setOutputFile(mFilePath);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        String channel = MySharedPreferences.loadStringSavedPreferences(MySharedPreferences.spChannel,
                getBaseContext());
        if(channel.equals("Mono")) {
            mRecorder.setAudioChannels(1);//1 for mono & 2 for stereo
        } else if (channel.equals("Stereo")){
            mRecorder.setAudioChannels(2);//1 for mono & 2 for stereo
        }

        String quality = MySharedPreferences.loadStringSavedPreferences(MySharedPreferences.spQuality,
                getBaseContext());

        try {
            mRecorder.prepare();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mRecorder.start();
                }
            },500);
            mStartingTimeMillis = System.currentTimeMillis();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setFileNameAndPath() throws IOException {
        int count = 0;
        File f;

        do{
            count++;

            mFileName = getString(R.string.default_file_name)
                    + "_" + (mDatabase.getCount() + count);
            mFilePath = Environment.getExternalStorageDirectory().getAbsolutePath();
            String recordingLocation = MySharedPreferences.loadStringSavedPreferences(MySharedPreferences.spStorageLocation,
                    getBaseContext());
            mFilePath += "/"+recordingLocation + mFileName;

            f = new File(mFilePath);

        }while (f.exists() && !f.isDirectory());
    }

    public void stopRecording() {
        try {
            mRecorder.stop();
            mElapsedMillis = (System.currentTimeMillis() - mStartingTimeMillis);
            mRecorder.release();
            Toast.makeText(this, "Recording saved"/*getString(R.string.toast_recording_finish) + " " + mFilePath*/,
                    Toast.LENGTH_LONG).show();

            //remove notification
            if (mIncrementTimerTask != null) {
                mIncrementTimerTask.cancel();
                mIncrementTimerTask = null;
            }

            mRecorder = null;

            mDatabase.addRecording(mFileName, mFilePath, mElapsedMillis);

        } catch (Exception e){
            Toast.makeText(this, "Oops! An error occurred.", Toast.LENGTH_SHORT).show();
        }
    }

    private void startTimer() {
        mTimer = new Timer();
        mIncrementTimerTask = new TimerTask() {
            @Override
            public void run() {
                mElapsedSeconds++;
                if (onTimerChangedListener != null)
                    onTimerChangedListener.onTimerChanged(mElapsedSeconds);
            }
        };
        mTimer.scheduleAtFixedRate(mIncrementTimerTask, 1000, 1000);
    }
}
