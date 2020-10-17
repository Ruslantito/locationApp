package com.sachinvarma.easylocationsample.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.sachinvarma.easylocationsample.MainActivity;
import com.sachinvarma.easylocationsample.R;
import com.sachinvarma.easylocationsample.objects.Team;
import com.sachinvarma.easylocationsample.tools.HTTPHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.sachinvarma.easylocationsample.tools.MyData.myUrl;


public class LoadAdmTeams extends AsyncTask<Void, Void, Void> {
    public Context context;
    public ArrayList<Team> teamsArray;
    public MainActivity activity;
    public Spinner spinnerTeams;
    //public ListView lvMain;

    List<String> teamsList;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        teamsArray = new ArrayList<>();
        teamsList = new ArrayList<>();
        teamsList.add("Выберите команду");
    }

    @Override
    protected Void doInBackground(Void... arg0) {
        try {
            HTTPHandler handler = new HTTPHandler();
            String json = handler.makeServiceCall(myUrl + "/teams");
            if (json != null) {
                JSONObject jsonObject = new JSONObject(json);
                JSONArray jsonArray = jsonObject.getJSONArray("response");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject newJSONObject = jsonArray.getJSONObject(i);
                    Team team = new Team();
                    team.id = newJSONObject.getInt("id");
                    team.name = newJSONObject.getString("name");
                    team.details = newJSONObject.getString("details");
                    teamsArray.add(team);
                    teamsList.add(team.name);
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

        //подгружаем список в переменную находящуюся в Activity
        activity.teamsArray = teamsArray;

        // выпадающий список для маршрутов
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(context, R.layout.spinner_teams, teamsList);
        spinnerTeams.setAdapter(adapter2);
    }

}
