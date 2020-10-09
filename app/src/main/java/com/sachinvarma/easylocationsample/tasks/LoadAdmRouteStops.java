package com.sachinvarma.easylocationsample.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.util.SparseIntArray;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.sachinvarma.easylocationsample.MainActivity;
import com.sachinvarma.easylocationsample.objects.Stops;
import com.sachinvarma.easylocationsample.tools.HTTPHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.sachinvarma.easylocationsample.tools.MyData.myUrl;

public class LoadAdmRouteStops extends AsyncTask<Void, Void, Void> {
    public Context context;
    public ArrayList<Stops> routeStopsArray;
    public ListView lvMain;
    public int routeId;
    public String choiceMode;
    //public TextView labelStopsListFRS;

    public Spinner spinnerStops;

    public MainActivity activity;
    List<String> routeStopsList;


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        routeStopsArray = new ArrayList<>();
        routeStopsList = new ArrayList<>();

        routeStopsList.add("Выберите остановку");
    }

    @Override
    protected Void doInBackground(Void... arg0) {
        try {
            HTTPHandler handler = new HTTPHandler();
            String json = handler.makeServiceCall(myUrl + "/stops/" + routeId);
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
                    routeStopsArray.add(stop);
                    routeStopsList.add(stop.name);
                }
            } else {
                Log.e("list", "could not get JSON routes from server");
            }
        } catch (final JSONException e) {
            Log.e("list", "JSON routes parsing error: " + e.getMessage());
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);


/////////////
        activity.routeStopsArray = routeStopsArray;
        //labelStopsListFRS.setText("Остановки для машрута (" + stopsList.size() + ")");

        // устанавливаем режим выбора пунктов списка
        lvMain.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_single_choice, routeStopsList);
        lvMain.setAdapter(adapter);



        // выпадающий список для маршрутов
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, routeStopsList);
        spinnerStops.setAdapter(adapter2);
/////////////


    }
}
