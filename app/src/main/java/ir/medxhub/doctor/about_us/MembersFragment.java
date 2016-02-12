package ir.medxhub.doctor.about_us;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import ir.medxhub.doctor.R;
import ir.medxhub.doctor.util.views.RoundedImageView;

public class MembersFragment extends Fragment {
    View rootView;
    AboutUs aboutUs;
    RoundedImageView[] members;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.about_us_members_pane, container, false);
        members = new RoundedImageView[]{
                (RoundedImageView) rootView.findViewById(R.id.avatar_1)
                , (RoundedImageView) rootView.findViewById(R.id.avatar_2)
                , (RoundedImageView) rootView.findViewById(R.id.avatar_3)};
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        aboutUs = (AboutUs) getActivity();

        for (int i = 0; i < members.length; i++) {
            int resourceId = this.getResources().getIdentifier(aboutUs.developersTeam.get(i).avatar, "drawable", getActivity().getPackageName());
            members[i].setImageResource(resourceId);
            final int userId = i;
            members[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    aboutUs.onMemberSelected(aboutUs.developersTeam.get(userId));
                }
            });
        }
    }
}
