package ua.org.algoritm.tasks;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ApiSettings extends AppCompatActivity {
    private TextView api;
    private Button ok;
    private Button cancel;
    private SharedPreferences preferences;
    private Switch switchPolling;
    private TextView intervalPoll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_api_settings);

        api = findViewById(R.id.editServer);
        ok = findViewById(R.id.buttonOk);
        cancel = findViewById(R.id.buttonCancel);
        intervalPoll = findViewById(R.id.intervalPoll);

        preferences = getSharedPreferences("MyPref", MODE_PRIVATE);
        api.setText(preferences.getString("Api", ""));
        intervalPoll.setText(preferences.getString("intervalPoll", "1"));

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedData.API = api.getText().toString();
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("Api", api.getText().toString());
                editor.putString("intervalPoll", intervalPoll.getText().toString());
                editor.apply();

                QueryPreferences.setApi(getApplicationContext(), api.getText().toString());

                PollService.setServiceAlarm(getApplicationContext(), switchPolling.isChecked(), Integer.parseInt(intervalPoll.getText().toString()));

                QueryPreferences.setAlarmOn(getApplicationContext(), switchPolling.isChecked());
                QueryPreferences.setIntervalPoll(getApplicationContext(), Integer.parseInt(intervalPoll.getText().toString()));

                setResult(Activity.RESULT_OK);
                finish();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        switchPolling = findViewById(R.id.switchPolling);
        switchPolling.setChecked(PollService.isServiceAlarmOn(this));
        switchPolling.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setTextSwitchPolling();
            }
        });
        setTextSwitchPolling();
    }

    private void setTextSwitchPolling() {
        if (switchPolling.isChecked()){
            switchPolling.setText(R.string.stop_polling);
        } else {
            switchPolling.setText(R.string.start_polling);
        }
    }
}
