package com.sachinvarma.easylocationsample.tasks;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.sachinvarma.easylocationsample.LogsActivity;
import com.sachinvarma.easylocationsample.MainActivity;
import com.sachinvarma.easylocationsample.R;
import com.sachinvarma.easylocationsample.objects.Stops;
import com.sachinvarma.easylocationsample.objects.Team;
import com.sachinvarma.easylocationsample.tools.HTTPHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.sachinvarma.easylocationsample.tools.MyData.myUrl;

public class LoadAdmLogs extends AsyncTask<Void, Void, Void>{
    public Context context;
    public ListView lvMain;
    public LogsActivity activity;

    public ArrayList<Stops> stopsArray;
    //public String choiceMode;
    //public String activ_name;

    public TextView labelStopsList;
    public TextView labelStopsListFRS;

    List<String> stopSimpleArray;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        stopsArray = new ArrayList<>();//.clear();
        stopSimpleArray = new ArrayList<>();
    }

    @Override
    protected Void doInBackground(Void... arg0) {
        try {
            HTTPHandler handler;
            handler = new HTTPHandler();
            String json = handler.makeServiceCall(myUrl + "/stops");
            if (json != null) {
                JSONObject jsonObject = new JSONObject(json);
                JSONArray jsonArray = jsonObject.getJSONArray("response");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject newJSONObject = jsonArray.getJSONObject(i);
                    Stops stop = new Stops();
                    stop.id = newJSONObject.getInt("id");
                    stop.name = newJSONObject.getString("name");
                    stop.x = newJSONObject.getDouble("coord_x");
                    stop.y = newJSONObject.getDouble("coord_y");
                    stop.teamId = newJSONObject.getInt("teamId");
                    stopsArray.add(stop);
                    stopSimpleArray.add(stop.name);
                }
            } else {
                Log.e("list", "could not get JSON from server");
            }
        } catch (final JSONException e) {
            Log.e("list", "JSON parsing error" + e.getMessage());
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);

        //подгружаем список в переменную находящуюся в Activity
        //activity.teamsArray = teamsArray;


/*        // устанавливаем режим выбора пунктов списка
        lvMain.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_single_choice, stopSimpleArray);
        lvMain.setAdapter(adapter);
 */

    }

}
