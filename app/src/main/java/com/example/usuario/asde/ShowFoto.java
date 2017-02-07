package com.example.usuario.asde;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class ShowFoto extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_foto);

        Bitmap bitmap = (Bitmap) getIntent().getParcelableExtra("imgBitmap");
        ImageView img = (ImageView) findViewById(R.id.img_full);
        img.setImageBitmap(bitmap);
    }

}
