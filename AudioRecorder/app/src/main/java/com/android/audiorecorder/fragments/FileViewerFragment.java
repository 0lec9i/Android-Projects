package com.android.audiorecorder.fragments;

import android.os.Bundle;
import android.os.FileObserver;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.audiorecorder.MySharedPreferences;
import com.android.audiorecorder.R;
import com.android.audiorecorder.FileViewerAdapter;

public class FileViewerFragment extends Fragment {

    private FileViewerAdapter mFileViewerAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int position = 1;
        observer.startWatching();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_file_viewer, container, false);

        RecyclerView mRecyclerView = v.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        llm.setReverseLayout(true);
        llm.setStackFromEnd(true);

        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mFileViewerAdapter = new FileViewerAdapter(getActivity(), llm);
        mRecyclerView.setAdapter(mFileViewerAdapter);

        return v;
    }

    String storageLoc = MySharedPreferences.loadStringSavedPreferences(MySharedPreferences.spStorageLocation,
            getActivity());
    FileObserver observer =
            new FileObserver(android.os.Environment.getExternalStorageDirectory().toString()
                    + storageLoc) {
                @Override
                public void onEvent(int event, String file) {
                    if (event == FileObserver.DELETE) {
                        String filePath = android.os.Environment.getExternalStorageDirectory().toString()
                                + storageLoc + file + "]";
                    }
                }
            };
}