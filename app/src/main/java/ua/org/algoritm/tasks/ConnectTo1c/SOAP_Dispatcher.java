package ua.org.algoritm.tasks.ConnectTo1c;

import android.content.Context;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;

import ua.org.algoritm.tasks.DetailsOrder;
import ua.org.algoritm.tasks.Password;
import ua.org.algoritm.tasks.MainActivity;
import ua.org.algoritm.tasks.SharedData;
import ua.org.algoritm.tasks.User;

public class SOAP_Dispatcher extends Thread {

    public static final Integer soapParam_timeout = 220000;
    public static String soapParam_pass = "31415926";
    public static String soapParam_user = "Администратор";
    public static String soapParam_URL;
    public String string_Inquiry;
    public Context mContext;

    int timeout;
    String URL;
    String user;
    String pass;
    int ACTION;
    SoapObject soap_Response;
    String soap_ResponseString;
    final String NAMESPACE = "www.URI.com";//"ReturnPhones_XDTO";
    String mSoapParam_URL;
    int attempt;
    Boolean thisGet;

    public SOAP_Dispatcher(int SOAP_ACTION, String sParam_user, String sParam_pass, Context context) {
        setSoapParamURL();

        timeout = soapParam_timeout;
        URL = soapParam_URL;
        user = sParam_user;
        pass = sParam_pass;
        ACTION = SOAP_ACTION;
        mSoapParam_URL = soapParam_URL;
        mContext = context;
        attempt = 0;
        thisGet = false;
    }

    public SOAP_Dispatcher(int SOAP_ACTION, Context context) {
        setSoapParamURL();

        timeout = soapParam_timeout;
        URL = soapParam_URL;
        user = soapParam_user;
        pass = soapParam_pass;
        ACTION = SOAP_ACTION;
        mSoapParam_URL = soapParam_URL;
        mContext = context;
        attempt = 0;
        thisGet = false;
    }

    private void setSoapParamURL() {
        String server = "";
        server = SharedData.API;
        soapParam_URL = server + "/ws/tasks.1cws";
    }

    @Override
    public void run() {

        switch (ACTION) {
            case Password.ACTION_VERIFY:
                login();
                break;

            case Password.ACTION_LOGIN_LIST:
                getLoginList();
                break;

            case Password.ACTION_UPDATE:
                checkUpdate();
                break;

            case Password.ACTION_UPDATE_NEW_VERSION:
                getApplication();
                break;

            case MainActivity.ACTION_GET_ORDER:
                getOrder();
                break;

            case DetailsOrder.ACTION_SET_STATUS:
                setStatusOrder();
                break;
        }

        if (ACTION == Password.ACTION_VERIFY | ACTION == Password.ACTION_LOGIN_LIST
                | ACTION == Password.ACTION_UPDATE | ACTION == Password.ACTION_UPDATE_NEW_VERSION) {

            if (soap_Response != null & ACTION == Password.ACTION_VERIFY) {
                Password.soapParam_Response = soap_Response;
                Password.soapHandler.sendEmptyMessage(ACTION);

            } else if (soap_Response != null & ACTION == Password.ACTION_LOGIN_LIST) {
                Password.soapHandler.sendEmptyMessage(ACTION);

            } else if (soap_Response != null & ACTION == Password.ACTION_UPDATE
                    | ACTION == Password.ACTION_UPDATE_NEW_VERSION) {
                Password.soapParam_Response_Update = soap_Response;
                Password.soapHandler.sendEmptyMessage(ACTION);

            } else {
                Password.soapHandler.sendEmptyMessage(Password.ACTION_ConnectionError);
            }
        } else if (ACTION == MainActivity.ACTION_GET_ORDER) {
            if (soap_Response != null) {
                MainActivity.soapParam_Response = soap_Response;
                MainActivity.soapHandler.sendEmptyMessage(ACTION);

            } else {
                MainActivity.soapHandler.sendEmptyMessage(Password.ACTION_ConnectionError);
            }
        } else if (ACTION == DetailsOrder.ACTION_SET_STATUS) {
            if (soap_Response != null) {
                DetailsOrder.soapParam_Response = soap_Response;
                DetailsOrder.soapHandler.sendEmptyMessage(ACTION);

            } else {
                DetailsOrder.soapHandler.sendEmptyMessage(DetailsOrder.ACTION_ConnectionError);
            }
        }
    }

    private void getOrder() {
        thisGet = true;

        String method = "getOrder";
        String action = NAMESPACE + "#getOrder:" + method;
        SoapObject request = new SoapObject(NAMESPACE, method);
        request.addProperty("barCode", string_Inquiry);
        soap_Response = callWebService(request, action);
    }

    private void checkUpdate() {
        thisGet = true;

        String method = "checkUpdate";
        String action = NAMESPACE + "#checkUpdate:" + method;
        SoapObject request = new SoapObject(NAMESPACE, method);
        request.addProperty("Version", SharedData.VERSION);
        soap_Response = callWebService(request, action);

    }

    private void getApplication() {
        thisGet = true;

        String method = "getApplication";
        String action = NAMESPACE + "#getApplication:" + method;
        SoapObject request = new SoapObject(NAMESPACE, method);
        soap_Response = callWebService(request, action);

    }

    void getLoginList() {
        thisGet = true;

        String method = "GetLoginList";
        String action = NAMESPACE + "#returnLoginList:" + method;
        SoapObject request = new SoapObject(NAMESPACE, method);
        soap_Response = callWebService(request, action);

        try {
            int count = soap_Response.getPropertyCount();
            ArrayList<User> users = SharedData.USERS;
            users.clear();

            for (int i = 0; i < count; i++) {
                SoapObject login = (SoapObject) soap_Response.getProperty(i);

                User user = new User();
                user.setName(login.getPropertyAsString("Description"));
                users.add(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void login() {
        thisGet = true;

        String method = "Login";
        String action = NAMESPACE + "#Login:" + method;
        SoapObject request = new SoapObject(NAMESPACE, method);

        request.addProperty("Login", Password.mLogin);

        String wsParam_PassHash = AeSimpleSHA1.getPassHash(Password.mPassword);
        request.addProperty("Password", wsParam_PassHash);
        soap_Response = callWebService(request, action);

    }

    void setStatusOrder() {
        String method = "setStatusOrder";
        String action = NAMESPACE + "#setStatusOrder:" + method;
        SoapObject request = new SoapObject(NAMESPACE, method);
        request.addProperty("data", string_Inquiry);
        soap_Response = callWebService(request, action);

    }

    private SoapObject callWebService(SoapObject request, String action) {

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        envelope.dotNet = true;
        envelope.implicitTypes = true;
        HttpTransportSE androidHttpTransport = new HttpTransportBasicAuthSE(URL, user, pass, timeout);
        androidHttpTransport.debug = false;

        try {
            androidHttpTransport.call(action, envelope);
            attempt = 100;

            return (SoapObject) envelope.getResponse();
        } catch (Exception e) {
            e.printStackTrace();

            Log.d("myLogsTasks", "" + attempt + " / " + action + " / " + e.toString());
            attempt++;
            if (SharedData.isOnline(mContext)) {
                if (attempt < 50 && thisGet) {
                    return callWebService(request, action);
                }
            }
        }

        return null;
    }

    private String callWebServiceString(SoapObject request, String action) {

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        envelope.dotNet = true;
        envelope.implicitTypes = true;
        HttpTransportSE androidHttpTransport = new HttpTransportBasicAuthSE(URL, user, pass, timeout);
        androidHttpTransport.debug = false;

        try {
            androidHttpTransport.call(action, envelope);
            attempt = 100;

            return envelope.getResponse().toString();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("myLogsTerminal", "" + attempt + " / " + action + " / " + e.toString());
            attempt++;
            if (SharedData.isOnline(mContext)) {
                if (attempt < 50 && thisGet) {
                    return callWebServiceString(request, action);
                }
            }
        }

        return "";
    }
}

