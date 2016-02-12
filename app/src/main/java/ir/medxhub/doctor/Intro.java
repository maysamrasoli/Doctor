package ir.medxhub.doctor;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.viewpagerindicator.CirclePageIndicator;

import ir.medxhub.doctor.util.glider.ParallaxContainer;

public class Intro extends Activity {


    public static ParallaxContainer parallaxContainer;
    public static CirclePageIndicator indicator;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        Intent intent = new Intent(this, GuestMainActivity.class);
//        intent.putExtra("doctor_id", "5");
//        intent.putExtra("date", "1394/6/");
//        startActivity(intent);
//        finish();
//        if (true) return;
        setContentView(R.layout.intro);
        context = this;
        parallaxContainer = (ParallaxContainer) findViewById(R.id.parallaxcontainer);

        if (parallaxContainer != null) {

            parallaxContainer.setLooping(false);
            parallaxContainer.setOnPageChangeListener(new ParallaxContainer.OnPageChangeListener() {
                @Override
                public void onPageSelected(int position) {
                    if (position + 1 == parallaxContainer.getPageCount()) {
                        startActivity(new Intent(Intro.this, Login.class));
                        Intro.this.finish();
                        Intro.this.overridePendingTransition(R.anim.fade_in, R.anim.nude);
                    }
                }
            });

            indicator = (CirclePageIndicator) findViewById(R.id.indicators);


            final ViewPager vp = parallaxContainer.setupChildren(getLayoutInflater(), R.drawable.intro_background,
                    R.layout.intro_1,
                    R.layout.intro_2,
                    R.layout.intro_3,
                    R.layout.intro_4);

            indicator.setOnPageChangeListener(parallaxContainer);

            indicator.setViewPager(vp);
        }

    }
}
