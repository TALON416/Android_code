package com.example.musicservice;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;

public class MyMusicService extends Service {

    private MediaPlayer mediaPlayer;
    private Handler handler;
    private Runnable updateProgressRunnable;
    private boolean isPaused = false;

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
        updateProgressRunnable = new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    sendBroadcast(new Intent("UPDATE_PROGRESS").putExtra("progress", mediaPlayer.getCurrentPosition()));
                }
                handler.postDelayed(this, 1000); // Update progress every second
            }
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getStringExtra("action");

        switch (action) {
            case "play":
                if (mediaPlayer == null) {
                    mediaPlayer = MediaPlayer.create(this, R.raw.newyear);
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            stopMusic();
                        }
                    });
                    if (!isPaused) {
                        mediaPlayer.start();
                    }
                    handler.postDelayed(updateProgressRunnable, 1000);
                    sendBroadcast(new Intent("UPDATE_MAX_PROGRESS").putExtra("max_progress", mediaPlayer.getDuration()));
                } else {
                    if (isPaused) {
                        mediaPlayer.start();
                        isPaused = false;
                        handler.postDelayed(updateProgressRunnable, 1000);
                    }
                }
                break;
            case "stop":
                stopMusic();
                break;
            case "pause":
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    handler.removeCallbacks(updateProgressRunnable);
                    isPaused = true;
                }
                break;
            case "seek":
                int progress = intent.getIntExtra("progress", 0);
                if (mediaPlayer != null) {
                    mediaPlayer.seekTo(progress);
                }
                break;
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void stopMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
            handler.removeCallbacks(updateProgressRunnable);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
