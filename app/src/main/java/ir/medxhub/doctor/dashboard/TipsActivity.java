package ir.medxhub.doctor.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import ir.medxhub.doctor.R;
import ir.medxhub.doctor.network.AppController;
import ir.medxhub.doctor.util.Tools;
import ir.medxhub.doctor.util.views.JustifiedTextView;

/**
 * Created by Ali Arasteh on 12/14/2014.
 */
public class TipsActivity extends ActionBarActivity {
    ImageLoader imageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_tips_activity);
        Intent intent = getIntent();
        imageLoader = AppController.getInstance().getImageLoader(this);

        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TipsActivity.this.finish();
                overridePendingTransition(R.anim.nude, R.anim.slide_out_to_right);
            }
        });
        ((TextView) findViewById(R.id.action_bar_title)).setText(getString(R.string.health_tips));

        ((NetworkImageView) findViewById(R.id.tip_image)).setImageUrl(intent.getStringExtra("image"), imageLoader);
        ((TextView) findViewById(R.id.title)).setText(intent.getStringExtra("title"));
        JustifiedTextView content = (JustifiedTextView) findViewById(R.id.content);
        content.setText(intent.getStringExtra("content"));
    }
}
