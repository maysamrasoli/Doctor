package ir.medxhub.doctor.dashboard;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import ir.medxhub.doctor.MainActivity;
import ir.medxhub.doctor.R;
import ir.medxhub.doctor.util.Tools;
import ir.medxhub.doctor.util.snack_bar.SnackBar;

/**
 * Created by mohammad on 12/30/2015.
 */
public class Home extends Fragment {

    private MainActivity parent;
    private RelativeLayout free_question, consultation, appointment, remember;

    public Home() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflater1;
        parent = (MainActivity) getActivity();
        inflater1 = inflater.inflate(R.layout.fragment_dashboard_home1, container, false);
        free_question = (RelativeLayout) inflater1.findViewById(R.id.free_question);
        consultation = (RelativeLayout) inflater1.findViewById(R.id.consultation);
        appointment = (RelativeLayout) inflater1.findViewById(R.id.appointment);
        remember = (RelativeLayout) inflater1.findViewById(R.id.remember);

        try {
            free_question.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    parent.Sft(R.string.questions);
                }
            });
            consultation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    parent.Sft(R.string.year);
                }
            });
            appointment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    parent.Sft(R.string.search_apointments);
                }
            });
            remember.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    parent.Sft(R.string.year);
                }
            });
        } catch (Exception e) {
            Tools.showSnack(getActivity(), e.getMessage(), SnackBar.LONG_SNACK, 10);
        }
        return inflater1;
    }
}

