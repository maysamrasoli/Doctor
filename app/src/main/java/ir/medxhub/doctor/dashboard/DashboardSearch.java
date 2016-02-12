package ir.medxhub.doctor.dashboard;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.TextView;

import ir.medxhub.doctor.R;

/**
 * Created by Ali on 9/21/2015.
 */
public class DashboardSearch extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_search);

        ((TextView)findViewById(R.id.action_bar_title)).setText(getString(R.string.search));
        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DashboardSearch.this.finish();
                overridePendingTransition(R.anim.nude, R.anim.slide_out_to_right);
            }
        });
    }
}
