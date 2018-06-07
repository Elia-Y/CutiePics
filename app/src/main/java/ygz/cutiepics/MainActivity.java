package ygz.cutiepics;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button sticker, frame, layout, doubleExpose, signIn;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sticker = (Button) findViewById(R.id.stickers);
        sticker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ChoosePhotoActivity.class);
                intent.putExtra("type", "sticker");
                startActivity(intent);
            }
        });

        frame = (Button) findViewById(R.id.frames);
        frame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ChoosePhotoActivity.class);
                intent.putExtra("type", "frame");
                startActivity(intent);            }
        });

        layout = (Button) findViewById(R.id.layouts);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MultiPhotoSelectActivity.class);
                intent.putExtra("type", "layout");
                startActivity(intent);
            }
        });

        doubleExpose = (Button) findViewById(R.id.doubleExposure);
        doubleExpose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MultiPhotoSelectActivity.class);
                intent.putExtra("type", "doubleExposure");
                startActivity(intent);            }
        });

        signIn = (Button) findViewById(R.id.signIn);
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
