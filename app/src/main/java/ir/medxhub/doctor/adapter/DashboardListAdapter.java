package ir.medxhub.doctor.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import ir.medxhub.doctor.R;
import ir.medxhub.doctor.dashboard.DashboardListItem;

/**
 * Created by mohammad on 12/30/2015.
 */
public class DashboardListAdapter extends RecyclerView.Adapter<DashboardListAdapter.MyViewHolder> {
    private List<DashboardListItem> listItems;
    private Activity activity;

    public DashboardListAdapter(Activity activity, List<DashboardListItem> items) {
        this.activity = activity;
        this.listItems = items;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.news_item, viewGroup, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder myViewHolder, final int position) {
        final DashboardListItem listItem = listItems.get(position);
        myViewHolder.fullName.setText(listItem.user);
        myViewHolder.specialty.setText(listItem.specialty);
        myViewHolder.date.setText(listItem.date);
        myViewHolder.title.setText(listItem.title);
        myViewHolder.summary_text.setText(listItem.summary_text);
        myViewHolder.date.setText(listItem.date);
        myViewHolder.countAccept.setText(listItem.count_follow);
        myViewHolder.countComment.setText(listItem.count_comment);
        myViewHolder.countLike.setText(listItem.count_like);

        myViewHolder.btnFlag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        myViewHolder.btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        myViewHolder.ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView btnFlag,btnShare,avatar,image;
        LinearLayout ll;
        TextView fullName, specialty,date, countLike, countComment, countAccept, summary_text, title;

        MyViewHolder(View itemView) {
            super(itemView);

            fullName = (TextView) itemView.findViewById(R.id.name);
            specialty = (TextView) itemView.findViewById(R.id.specialty);
            date = (TextView) itemView.findViewById(R.id.date);
            title = (TextView) itemView.findViewById(R.id.title);
            summary_text = (TextView) itemView.findViewById(R.id.summary_text);
            countLike = (TextView) itemView.findViewById(R.id.count_like);
            countComment = (TextView) itemView.findViewById(R.id.count_comment);
            countAccept = (TextView) itemView.findViewById(R.id.count_accept);

            btnFlag = (ImageView) itemView.findViewById(R.id.flag);
            btnShare = (ImageView) itemView.findViewById(R.id.share);
            avatar = (ImageView) itemView.findViewById(R.id.user_avatar);
            image = (ImageView) itemView.findViewById(R.id.image);

            ll = (LinearLayout) itemView.findViewById(R.id.articles);
        }
    }

}
