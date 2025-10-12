package com.comixa.data.rss;

public interface ProgressReporter {
    void onStart();
    void onProgress(int percent);
    void onStop();
}