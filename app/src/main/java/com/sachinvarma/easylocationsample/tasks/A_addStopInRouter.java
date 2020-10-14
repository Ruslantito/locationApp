package com.sachinvarma.easylocationsample.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.widget.Spinner;
import android.widget.Toast;

import com.sachinvarma.easylocationsample.MainActivity;
import com.sachinvarma.easylocationsample.objects.Stops;
import com.sachinvarma.easylocationsample.tools.HTTPHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.sachinvarma.easylocationsample.tools.MyData.myUrl;


public class A_addStopInRouter extends AsyncTask<Void, Void, Void> {
    public Context context;
    //public String recordType;
    //public String tempInfoText;
    public Integer transportTypeId;
    public String stop_coordX;
    public String stop_coordY;

    public Integer route_id;
    public Integer stop_id;
    public String name;
    public Spinner spinnerStops;
    public ArrayList<Stops> routeStopsArray;

    String backendURL;
    String infoText;

    public MainActivity activity;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        routeStopsArray = new ArrayList<>();
        backendURL = myUrl + "/addRouteStop/" + route_id + "/" + stop_id;
        infoText = "Остановка была добавлена к маршруту";

    }

    @Override
    protected Void doInBackground(Void... arg0) {
        try {
            HTTPHandler handler = new HTTPHandler();
            String json = handler.makeServiceCall(backendURL);
            if (json != null) {
                JSONObject jsonObject  = new JSONObject(json);
                //JSONObject jsonObject2 = new JSONObject(jsonObject.get("response").toString());
                //new_id = jsonObject2.get("insertId").toString();
                Log.e("list", "New Stop was added to Route!");
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

        Toast toast = Toast.makeText(context, infoText, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP, 0, 0);
        toast.show();

        //подгрузить остановки для выбранного маршрута
        LoadAdmRouteStops loadRouteStops;
        loadRouteStops = new LoadAdmRouteStops();
        loadRouteStops.routeId = route_id;
        loadRouteStops.context = context;

        loadRouteStops.stop_id = stop_id;

        loadRouteStops.activity = activity;
        loadRouteStops.spinnerStops = spinnerStops;
        loadRouteStops.routeStopsArray = routeStopsArray;
        loadRouteStops.execute();

    }
}