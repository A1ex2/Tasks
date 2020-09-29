package ua.org.algoritm.tasks;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Formatter;

import ua.org.algoritm.tasks.ConnectTo1c.UIManager;
import ua.org.algoritm.tasks.ConnectTo1c.SOAP_Dispatcher;

public class Password extends AppCompatActivity {
    private EditText login;
    private EditText password;
    private Button ok;

    private ArrayList<String> loginList = new ArrayList<>();
    private SharedPreferences preferences;

    private ProgressDialog mDialog;

    private static final int REQUEST_CODE_EDIT_API = 2;

    public static String mLogin;
    public static String mPassword;

    public static final int ACTION_VERIFY = 10;
    public static final int ACTION_LOGIN_LIST = 11;
    public static final int ACTION_UPDATE = 14;
    public static final int ACTION_UPDATE_NEW_VERSION = 15;

    public static final int ACTION_ConnectionError = 0;
    public static UIManager uiManager;
    public static SoapFault responseFault;

    public static SoapObject soapParam_Response;
    public static SoapObject soapParam_Response_Update;
    public static Handler soapHandler;
    public static String wsParam_PassHash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        preferences = getSharedPreferences("MyPref", MODE_PRIVATE);
        SharedData.API = preferences.getString("Api", "");
        SharedData.VERSION = getString(R.string.nav_header_version);

        if (!SharedData.API.equals("")) {
            SOAP_Dispatcher dispatcherUpdate = new SOAP_Dispatcher(ACTION_UPDATE, getApplicationContext());
            dispatcherUpdate.start();
        }

        uiManager = new UIManager(this);
        soapHandler = new incomingHandler(this);

        login = findViewById(R.id.login);
        password = findViewById(R.id.password);
        password.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {

                    verify();
                    return true;
                }
                return false;

            }
        });

        ok = findViewById(R.id.butt_OK);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verify();
            }
        });

        login.setText(preferences.getString("Login", ""));

        if (!SharedData.API.equals("")) {
            SOAP_Dispatcher dispatcher = new SOAP_Dispatcher(ACTION_LOGIN_LIST, getApplicationContext());
            dispatcher.start();
        }

        if (hasPermission(Manifest.permission.GET_ACCOUNTS)) {
        } else {
            requestPermissions();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                Intent intent = new Intent(Password.this, ApiSettings.class);
                startActivityForResult(intent, REQUEST_CODE_EDIT_API);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_EDIT_API) {
            if (resultCode == Activity.RESULT_OK) {
                SOAP_Dispatcher dispatcher = new SOAP_Dispatcher(ACTION_LOGIN_LIST, getApplicationContext());
                dispatcher.start();
            }
        }
    }

    private boolean validateLogin(String mLogin) {
        if (mLogin.isEmpty()) {
            login.setError(getString(R.string.error_login));
            return false;
        } else {
            login.setError(null);
            return true;
        }
    }

    private boolean validatePassword(String mPassword) {
        if (mPassword.isEmpty()) {
            password.setError(getString(R.string.error_password));
            return false;
        } else {
            password.setError(null);
            return true;
        }
    }

    private void verify() {
        mLogin = login.getText().toString();
        mPassword = password.getText().toString();

        if (!validateLogin(mLogin) | !validatePassword(mPassword)) {
            return;
        }

        login.setEnabled(false);
        password.setEnabled(false);
        ok.setEnabled(false);

        SOAP_Dispatcher dispatcher = new SOAP_Dispatcher(ACTION_VERIFY, getApplicationContext());
        dispatcher.start();

    }

    private boolean hasPermission(String permission) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
        }

        return true;
    }

    private void requestPermissions() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permissions = {
//                    Manifest.permission.GET_ACCOUNTS,
//                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
            };
            requestPermissions(permissions, 0);
        }
    }

    class incomingHandler extends Handler {
        private final WeakReference<Password> mTarget;

        public incomingHandler(Password context) {
            mTarget = new WeakReference<>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            login.setEnabled(true);
            password.setEnabled(true);
            ok.setEnabled(true);

            Password target = mTarget.get();

            switch (msg.what) {
                case ACTION_ConnectionError:
                    uiManager.showToast(getString(R.string.errorConnection) + getSoapErrorMessage());
                    break;
                case ACTION_VERIFY: {
                    target.checkLoginResult();
                }
                break;

                case ACTION_LOGIN_LIST: {
                    target.checkLoginListResult();
                }
                break;

                case ACTION_UPDATE: {
                    checkUpdate();
                }
                break;

                case ACTION_UPDATE_NEW_VERSION: {
                    update();
                }
                break;
            }
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

    public void checkLoginResult() {

        Boolean isLoginSuccess = Boolean.parseBoolean(soapParam_Response.getPropertyAsString("Result"));

        if (isLoginSuccess) {
            String hostFTP = "";
            int portFTP = 21;
            String usernameFTP = "";
            String passwordFTP = "";
            boolean thisSFTP = false;

            try {
                hostFTP = soapParam_Response.getPropertyAsString("host");
                thisSFTP = Integer.parseInt(soapParam_Response.getPropertyAsString("thisSFTP")) == 1;
                portFTP = Integer.parseInt(soapParam_Response.getPropertyAsString("port"));
                usernameFTP = soapParam_Response.getPropertyAsString("username");
                passwordFTP = soapParam_Response.getPropertyAsString("password");
            } catch (Exception e) {
                e.printStackTrace();
            }

            SharedData.LOGIN = mLogin;
            SharedData.PASSWORD = mPassword;

            SharedData.hostFTP = hostFTP;
            SharedData.thisSFTP = thisSFTP;
            SharedData.portFTP = portFTP;
            SharedData.usernameFTP = usernameFTP;
            SharedData.passwordFTP = passwordFTP;

            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("Login", mLogin);
            editor.putString("Password", mPassword);

            QueryPreferences.setLogin(this, mLogin);
            QueryPreferences.setPassword(this, mPassword);

            editor.putString("hostFTP", hostFTP);
            editor.putBoolean("thisSFTP", thisSFTP);
            editor.putInt("portFTP", portFTP);
            editor.putString("usernameFTP", usernameFTP);
            editor.putString("passwordFTP", passwordFTP);
            editor.apply();

            uiManager.showToast(getString(R.string.passwordIncorrect) + soapParam_Response.getPropertyAsString("Name"));

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();

        } else {
            uiManager.showToast(getString(R.string.passwordNotIncorrect));
        }

    }

    public void checkLoginListResult() {
        try {

            loginList.clear();

            for (int i = 0; i < SharedData.USERS.size(); i++) {
                loginList.add(SharedData.USERS.get(i).getName());
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_singlechoice, loginList);
            AutoCompleteTextView acTextView = (AutoCompleteTextView) login;
            acTextView.setThreshold(1);
            acTextView.setAdapter(adapter);

        } catch (Exception e) {

            e.printStackTrace();

        }
    }

    public void checkUpdate() {
        try {

            Boolean isLoginSuccess = Boolean.parseBoolean(soapParam_Response_Update.getPropertyAsString("Result"));

            if (isLoginSuccess) {
                String lastAppVersion = soapParam_Response_Update.getPropertyAsString("Description");
                final String apkUrl = soapParam_Response_Update.getPropertyAsString("URL");

                String message = new Formatter().format(getString(R.string.update_new_version), lastAppVersion).toString();

                AlertDialog.Builder builder = new AlertDialog.Builder(Password.this);
                builder.setMessage(message)
                        .setCancelable(true)
                        .setPositiveButton(getString(R.string.butt_Yes), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                try {

                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    intent.setData(Uri.parse(apkUrl));
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

//                            SOAP_Dispatcher dispatcher = new SOAP_Dispatcher(ACTION_UPDATE_NEW_VERSION);
//                            dispatcher.start();
//
//                            dialog.dismiss();
//
//                            mDialog = new ProgressDialog(Password.this);
//                            mDialog.setMessage(getString(R.string.wait_update));
//                            mDialog.setCancelable(false);
//                            mDialog.show();

                            }
                        })
                        .setNegativeButton(getString(R.string.butt_Not), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        } catch (Exception e) {

            e.printStackTrace();

        }
    }

    private void update() {
        try {
//            if (mDialog != null && mDialog.isShowing()) {
//                mDialog.dismiss();
//            }
//
//            String app = soapParam_Response_Update.getPropertyAsString("App");


        } catch (Exception e) {

            e.printStackTrace();

        }
    }

}