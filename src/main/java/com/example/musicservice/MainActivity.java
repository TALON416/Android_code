package com.example.musicservice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView tv_1;
    SeekBar seekBar;
    boolean isTracking = false;
    BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_1 = findViewById(R.id.tv_1);
        seekBar = findViewById(R.id.seekBar);

        tv_1.setText("播放状态11：停止播放。。。");

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("UPDATE_PROGRESS")) {
                    int progress = intent.getIntExtra("progress", 0);
                    if (!isTracking) {
                        seekBar.setProgress(progress);
                    }
                } else if (intent.getAction().equals("UPDATE_MAX_PROGRESS")) {
                    int maxProgress = intent.getIntExtra("max_progress", 0);
                    seekBar.setMax(maxProgress);
                }
            }
        };
        registerReceiver(broadcastReceiver, new IntentFilter("UPDATE_PROGRESS"));
        registerReceiver(broadcastReceiver, new IntentFilter("UPDATE_MAX_PROGRESS"));

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (isTracking) {
                    Intent intent = new Intent(MainActivity.this, MyMusicService.class);
                    intent.putExtra("action", "seek");
                    intent.putExtra("progress", progress);
                    startService(intent);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isTracking = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isTracking = false;
            }
        });
    }

    public void play_onclick(View view) {
        Intent intent = new Intent(this, MyMusicService.class);
        intent.putExtra("action", "play");
        startService(intent);
        tv_1.setText("播放状态11：正在播放。。。");
    }

    public void stop_onclick(View view) {
        Intent intent = new Intent(this, MyMusicService.class);
        intent.putExtra("action", "stop");
        startService(intent);
        tv_1.setText("播放状态11：停止播放。。。");
    }

    public void pause_onclick(View view) {
        Intent intent = new Intent(this, MyMusicService.class);
        intent.putExtra("action", "pause");
        startService(intent);
        tv_1.setText("播放状态11：暂停播放。。。");
    }

    public void exit_onclick(View view) {
        stop_onclick(view);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }
}
