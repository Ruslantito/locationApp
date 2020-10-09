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


/*
    public A_StopsFragment activity;
    public A_RouteStopsFragment activity2;
*/


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

        /*
        Integer choosedStops = 0;
        ArrayAdapter<String> adapter;

        if (choiceMode == "none"){
            // устанавливаем режим выбора пунктов списка
            lvMain2.setChoiceMode(ListView.CHOICE_MODE_NONE);
            adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_activated_1, stopSimpleArray);
            lvMain2.setAdapter(adapter);

        }else if (choiceMode == "multiple"){
            lvMain2.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_multiple_choice, stopSimpleArray);
            lvMain2.setAdapter(adapter);
            for (int i = 0 ; i < stopsArray.size() ; i++){
                for (int i2 = 0 ; i2 < activity2.routeStopsArray.size() ; i2++) {
                    if (stopsArray.get(i).id == activity2.routeStopsArray.get(i2).id) {
                        activity2.lvMain2.setItemChecked(i, true);
                        choosedStops += 1;
                        break;
                    } else {
                        activity2.lvMain2.setItemChecked(i, false);
                    }}}

        }else if (choiceMode == "single"){
            lvMain2.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_single_choice, stopSimpleArray);
            lvMain2.setAdapter(adapter);

        }else {
            lvMain2.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_activated_2, stopSimpleArray);
            lvMain2.setAdapter(adapter);
        }

        labelStopsList.setText("Список остановок (" + stopsArray.size() + ")");

        if (activ_name == "activity2"){
            activity2.stopsArray = stopsArray;
            activity2.labelStopsListFRS.setText("Список остановок (" + choosedStops + "/" + stopsArray.size() + ")");
        } else if (activ_name == "activity") {
            activity.stopsArray = stopsArray;
        }
    */


    }


}
