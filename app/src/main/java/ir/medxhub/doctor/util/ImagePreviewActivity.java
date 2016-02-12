package ir.medxhub.doctor.util;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import ir.medxhub.doctor.R;
import uk.co.senab.photoview.PhotoView;

/**
 * Created by Ali on 11/26/2015.
 */
public class ImagePreviewActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preview_image);
        CustomImageLoader imageLoader = new CustomImageLoader(this);
        final String imageUrl = getIntent().getStringExtra("url");

        PhotoView photoView = (PhotoView) findViewById(R.id.iv_photo);
        imageLoader.DisplayImage(imageUrl, R.drawable.default_image, photoView);

        findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePreviewActivity.this.finish();
            }
        });
    }
}
