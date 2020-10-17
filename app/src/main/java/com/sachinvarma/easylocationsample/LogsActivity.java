package com.sachinvarma.easylocationsample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.sachinvarma.easylocationsample.objects.Stops;
import com.sachinvarma.easylocationsample.tasks.LoadAdmLogs;
import com.sachinvarma.easylocationsample.tasks.LoadAdmRouteStops;

import java.util.ArrayList;

public class LogsActivity extends AppCompatActivity implements View.OnClickListener {

    public MainActivity activity;

    public ListView lvMain;
    TextView txtTeamName;
    //public TextView labelRoutesListFRS, labelStopsListFRS;

    Button btnGoToMainActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logs);


        btnGoToMainActivity = findViewById(R.id.btnGoToMainActivity);
        lvMain = findViewById(R.id.lvMainLog);
        txtTeamName = findViewById(R.id.txtTeamName);

        btnGoToMainActivity.setOnClickListener(this);
        //loadDataLogs(activity.teamsArray.get(activity.spinnerTeams.getSelectedItemPosition()-1).id);
    }



    //подгрузить остановки для выбранного маршрута
    public void loadDataLogs(int selId){
        LoadAdmLogs loadLogs;
        loadLogs = new LoadAdmLogs();
        loadLogs.lvMain = lvMain;
        loadLogs.context = getApplicationContext();
        loadLogs.activity = this;
        loadLogs.execute();
    }


    //для перехода на главную страницу
    public void goToActivity(){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startActivity(intent);
    }


    @Override
    public void onClick(View view) {
        goToActivity();
    }
}