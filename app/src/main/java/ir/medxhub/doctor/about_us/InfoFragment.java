package ir.medxhub.doctor.about_us;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import ir.medxhub.doctor.Globals;
import ir.medxhub.doctor.R;
import ir.medxhub.doctor.util.views.RoundedImageView;

public class InfoFragment extends Fragment {
    View rootView;
    LayoutInflater inflater;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.about_us_info_pane, container, true);

        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void selectMember(final MemberInfo memberInfo) {
        int resourceId = this.getResources().getIdentifier(memberInfo.avatar, "drawable", getActivity().getPackageName());
        ((RoundedImageView)rootView.findViewById(R.id.avatar)).setImageResource(resourceId);
        ((TextView) rootView.findViewById(R.id.name)).setText(memberInfo.fName + " " + memberInfo.lName);
        ((TextView) rootView.findViewById(R.id.speciality)).setText(memberInfo.specialty);
        ((TextView) rootView.findViewById(R.id.about_member)).setText(memberInfo.about);
        ((TextView) rootView.findViewById(R.id.mail_address)).setText(memberInfo.mail);

        rootView.findViewById(R.id.linked_in).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(memberInfo.linkedIn)));
            }
        });

        rootView.findViewById(R.id.mail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:" + memberInfo.mail));
                startActivity(intent);
            }
        });

        rootView.findViewById(R.id.web_link).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Globals.aboutUs + memberInfo.webLink)));
            }
        });
    }
}