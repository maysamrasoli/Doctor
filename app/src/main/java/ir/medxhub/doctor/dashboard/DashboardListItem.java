package ir.medxhub.doctor.dashboard;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by mohammad on 1/1/2016.
 */
public class DashboardListItem {
    public int id, views, follows, count_like, count_follow, count_comment;
    public String title, text, summary_text, specialty, user, date, health_tip;
    public String[] images;
    public int[] accept_list, thank_list;
//    accept,
//    thanks,

    public DashboardListItem(JSONObject data) {
        try {
            id = data.getInt("id");
            views = data.getInt("views");
            follows = data.getInt("follows");
            count_like = data.getInt("count_like");
            count_follow = data.getInt("count_follow");
            count_comment = data.getInt("count_comment");

            title = data.getString("title");
            text = data.getString("text");
            summary_text = data.getString("summary_text");
            specialty = data.getString("specialty");
            user = data.getString("user");
            date = data.getString("date");
            health_tip = data.getString("health_tip");


            images = new String[]{data.getString("images")};
            Log.i("test", "~~~" + data.getString("images"));
            JSONArray tArray = data.getJSONArray("images");
            if (tArray.length() > 0) {
                images = new String[tArray.length()];
                for (int i = 0; i < tArray.length(); i++)
                    images[i] = tArray.getString(i);
            }
            Log.i("test", "~~~" + images);

            accept_list = new int[]{data.getInt("accept_list")};
            Log.i("test", "~~~" + data.getString("accept_list"));
            JSONArray alArray = data.getJSONArray("accept_list");
            if (alArray.length() > 0) {
                accept_list = new int[alArray.length()];
                for (int i = 0; i < alArray.length(); i++)
                    accept_list[i] = alArray.getInt(i);
            }
            Log.i("test", "~~~" + accept_list);

            thank_list = new int[]{data.getInt("thank_list")};
            Log.i("test", "~~~" + data.getString("thank_list"));
            JSONArray tlArray = data.getJSONArray("thank_list");
            if (tlArray.length() > 0) {
                thank_list = new int[tlArray.length()];
                for (int i = 0; i < tlArray.length(); i++)
                    thank_list[i] = tlArray.getInt(i);
            }
            Log.i("test", "~~~" + thank_list);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
