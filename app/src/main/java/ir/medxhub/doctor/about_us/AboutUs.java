package ir.medxhub.doctor.about_us;

import android.os.Bundle;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.TextView;
import ir.medxhub.doctor.R;
import ir.medxhub.doctor.database.DBHelper;

import java.util.ArrayList;

public class AboutUs extends ActionBarActivity implements MemberListener {
    private SlidingPaneLayout pane;
    public ArrayList<MemberInfo> developersTeam;
    public ArrayList<MemberInfo> managementTeam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_us);

        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AboutUs.this.finish();
                overridePendingTransition(R.anim.nude, R.anim.slide_out_to_right);
            }
        });
        ((TextView) findViewById(R.id.action_bar_title)).setText(getString(R.string.about_us));

        developersTeam = (new DBHelper(this)).getUserInfoes(false);
        managementTeam = (new DBHelper(this)).getUserInfoes(true);
        pane = (SlidingPaneLayout) findViewById(R.id.sp);
        InfoFragment f = (InfoFragment) getSupportFragmentManager().findFragmentById(R.id.leftpane);
        f.selectMember(developersTeam.get(0));

        if (!pane.isSlideable()) {
            getSupportFragmentManager().findFragmentById(R.id.leftpane).setHasOptionsMenu(false);
            getSupportFragmentManager().findFragmentById(R.id.rightpane).setHasOptionsMenu(true);
        }
    }

    private void togglePane() {
        if (pane.isOpen()) {
            pane.closePane();
        } else {
            pane.openPane();
        }
    }

    @Override
    public void onMemberSelected(MemberInfo memberInfo) {
        InfoFragment f = (InfoFragment) getSupportFragmentManager().findFragmentById(R.id.leftpane);
        f.selectMember(memberInfo);
        togglePane();
    }
}