package ir.medxhub.doctor.dashboard;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ir.medxhub.doctor.R;
import ir.medxhub.doctor.adapter.DashboardListAdapter;

/**
 * Created by mohammad on 12/30/2015.
 */
public class Articles extends Fragment {
    View rootView;
    Dashboard_tb parent;

    public Articles() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.articles_fragment, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        parent = (Dashboard_tb) getParentFragment();

        RecyclerView rv = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);

        final DashboardListAdapter adapter = new DashboardListAdapter(getActivity(), parent.articleListItem);
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                if (adapter.getItemCount() == 0)
                    rootView.findViewById(R.id.not_found).setVisibility(View.VISIBLE);
                else
                    rootView.findViewById(R.id.not_found).setVisibility(View.GONE);
                super.onChanged();
            }
        });
        rv.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}
