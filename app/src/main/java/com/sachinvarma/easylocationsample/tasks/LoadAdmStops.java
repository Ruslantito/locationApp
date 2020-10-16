package com.sachinvarma.easylocationsample.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.sachinvarma.easylocationsample.objects.Stops;
import com.sachinvarma.easylocationsample.tools.HTTPHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.sachinvarma.easylocationsample.tools.MyData.myUrl;

/*
import tj.ilmhona.map.activities.A_RouteStopsFragment;
import tj.ilmhona.map.activities.A_StopsFragment;
*/

public class LoadAdmStops extends AsyncTask<Void, Void, Void> {
    public Context context;
    public ArrayList<Stops> stopsArray;
    public ListView lvMain2;
    public String choiceMode;
    public String activ_name;
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
    }

}
