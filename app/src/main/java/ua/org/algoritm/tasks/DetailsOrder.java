package ua.org.algoritm.tasks;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ua.org.algoritm.tasks.ConnectTo1c.JsonParser;
import ua.org.algoritm.tasks.ConnectTo1c.SOAP_Dispatcher;
import ua.org.algoritm.tasks.ConnectTo1c.SOAP_Objects;
import ua.org.algoritm.tasks.ConnectTo1c.UIManager;

public class DetailsOrder extends AppCompatActivity {
    private Order order;

    private TextView description;
    private Spinner status;
    private Button setStatus;

    public static final int ACTION_SET_STATUS = 21;
    public static final int ACTION_ConnectionError = 0;

    public static UIManager uiManager;
    public static SoapFault responseFault;

    public static SoapObject soapParam_Response;
    public static Handler soapHandler;

    private ProgressDialog mDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_order);

        uiManager = new UIManager(this);
        soapHandler = new incomingHandler(this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        setStatus = findViewById(R.id.setStatus);
        setStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCB();
            }
        });

        Intent intent = getIntent();

        try {
            description = findViewById(R.id.textDescription);
            status = findViewById(R.id.status);

            order = Order.newOrder(intent.getStringExtra("order"));
            description.setText(order.getDescription());

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.item_spinner, order.getStringStatus());
            status.setAdapter(adapter);

            int spinnerPosition = adapter.getPosition(order.getStatus());
            status.setSelection(spinnerPosition);

            status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String mStatus = order.getStringStatus().get(position);

                    HashMap<String, String> map = order.getAvailableStatus();
                    for(Map.Entry entry: map.entrySet()) {
                        if (entry.getValue().equals(mStatus)){
                            order.setStatusID(entry.getKey().toString());
                            order.setStatus(entry.getValue().toString());
                            break;
                        };
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void setCB() {
        mDialog = new ProgressDialog(this);
        mDialog.setMessage(getString(R.string.wait_sending));
        mDialog.setCancelable(false);
        mDialog.show();

        SharedPreferences preferences = getSharedPreferences("MyPref", MODE_PRIVATE);
        String login = preferences.getString("Login", "");
        String password = preferences.getString("Password", "");

        SOAP_Dispatcher dispatcher = new SOAP_Dispatcher(ACTION_SET_STATUS, login, password, getApplicationContext());
        String string = SOAP_Objects.getOrderStatus(order.getID(), order.getStatusID());
        dispatcher.string_Inquiry = string;

        dispatcher.start();

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    class incomingHandler extends Handler {
        private final WeakReference<DetailsOrder> mTarget;

        public incomingHandler(DetailsOrder context) {
            mTarget = new WeakReference<>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            DetailsOrder target = mTarget.get();

            if (mDialog != null && mDialog.isShowing()) {
                mDialog.dismiss();
            }

            switch (msg.what) {
                case ACTION_ConnectionError:
                    uiManager.showToast(getString(R.string.errorConnection) + getSoapErrorMessage());
                    break;
                case ACTION_SET_STATUS: {
                    target.checkSetStatus();
                }
                break;
            }
        }
    }

    private void checkSetStatus() {
        Boolean isSaveSuccess = Boolean.parseBoolean(soapParam_Response.getPropertyAsString("Result"));

        if (isSaveSuccess) {

            uiManager.showToast(getString(R.string.success));
            finish();

        } else {
            uiManager.showToast(soapParam_Response.getPropertyAsString("Description"));
        }
    }

    private String getSoapErrorMessage() {

        String errorMessage;

        if (responseFault == null)
            errorMessage = getString(R.string.textNoInternet);
        else {
            try {
                errorMessage = responseFault.faultstring;
            } catch (Exception e) {
                e.printStackTrace();
                errorMessage = getString(R.string.unknownError);
            }
        }
        return errorMessage;
    }

    public static class Order {
        String ID;
        String Description;
        String StatusID;
        String Status;
        HashMap<String, String> availableStatus = new HashMap<>();
        ArrayList<String> stringStatus = new ArrayList<>();

        public Order() {
        }

        public String getID() {
            return ID;
        }

        public void setID(String ID) {
            this.ID = ID;
        }

        public String getDescription() {
            return Description;
        }

        public void setDescription(String description) {
            Description = description;
        }

        public String getStatusID() {
            return StatusID;
        }

        public void setStatusID(String statusID) {
            StatusID = statusID;
        }

        public String getStatus() {
            return Status;
        }

        public void setStatus(String status) {
            Status = status;
        }

        public HashMap<String, String> getAvailableStatus() {
            return availableStatus;
        }

        public void setAvailableStatus(HashMap<String, String> availableStatus) {
            this.availableStatus = availableStatus;
        }

        public static Order newOrder(String order) throws JSONException {
            return JsonParser.getOrder(order);
        }

        public ArrayList<String> getStringStatus() {
            return stringStatus;
        }

        public void setStringStatus(ArrayList<String> stringStatus) {
            this.stringStatus = stringStatus;
        }
    }
}
