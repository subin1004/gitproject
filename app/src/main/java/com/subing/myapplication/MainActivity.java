package com.subing.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.subing.myapplication.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    static final int PROGRESSBAR_START=1;
    static final int WHAT_HANDLER_MSG_COUNT =2;

    private ActivityMainBinding binding;

    int result; // 결과 출력

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    // 버튼으로 답 입력
    public void onclick(View view) {
        binding.etA.append(((Button)view).getText());
    }

    public void onClickStart(View view) {
        binding.progressBar.setProgress(0);
        handler.sendEmptyMessage(PROGRESSBAR_START);

        // 시작과 동시에 게임 세팅
        binding.etCount.setText("0");
        //result = makeQuiz();

        // 프로그래스 바 Thread
        Thread th_count = new Thread("count thread"){
            @Override
            public void run(){
                for(int i=60; i>=0; i--){
                    Message msg = handler.obtainMessage(WHAT_HANDLER_MSG_COUNT, i, 0);
                    handler.sendMessage(msg);

                    try {
                        Thread.sleep(1000);
                    }catch (Exception e){}

                }
            }
        };

        th_count.start();
    }

    Handler handler =  new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            // tv: Count & 점수 출력
            if(msg.what==PROGRESSBAR_START){
                if(binding.progressBar.getProgress() < binding.progressBar.getMax()){
                    binding.btnOk.setClickable(true);
                    binding.btnStart.setClickable(false);
                    binding.progressBar.setProgress(binding.progressBar.getProgress()+1);
                    sendEmptyMessageDelayed(PROGRESSBAR_START, 1000);
                }
                else
                {
                    binding.btnOk.setClickable(false);
                    binding.btnStart.setClickable(true);
                }
            }

            // tv_last: 남은 시간 출력
            if(msg.what==WHAT_HANDLER_MSG_COUNT){
                binding.tvLast.setTextColor(Color.rgb(0, 0, 0));
                binding.tvLast.setText(msg.arg1 + "초");

                // 10초 이하면 글씨 빨강색으로
                if(msg.arg1==0) {
                    binding.tvLast.setText("당신의 점수는 " + binding.etCount.getText().toString() + "점!!");
                    //bt_ok.setClickable(false);
                }

                else if(msg.arg1<=10) {
                    binding.tvLast.setTextColor(Color.rgb(255, 0, 0));
                }
            }
        }
    };
}