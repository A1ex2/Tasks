package ua.org.algoritm.tasks.ConnectTo1c;

import android.content.Context;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import ua.org.algoritm.tasks.SharedData;

public class Message {
    public static final Integer soapParam_timeout = 220000;
    public static String soapParam_pass = "31415926";
    public static String soapParam_user = "Администратор";
    public static String soapParam_URL;
    public String string_Inquiry;
    public Context mContext;

    public Boolean isMessage = false;
    public String text = "";

    int timeout;
    String URL;
    String user;
    String pass;
    int ACTION;
    SoapObject soap_Response;
    String soap_ResponseString;
    final String NAMESPACE = "www.URI.com";
    String mSoapParam_URL;
    int attempt;
    Boolean thisGet;

    public Message(String server, String sParam_user, String sParam_pass, Context context) {
        soapParam_URL = server + "/ws/tasks.1cws";
        timeout = soapParam_timeout;
        URL = soapParam_URL;
        user = sParam_user;
        pass = sParam_pass;
        mSoapParam_URL = soapParam_URL;
        mContext = context;
        attempt = 0;
        thisGet = false;
    }

    public void getNotifications() {
        thisGet = true;

        String method = "getNotifications";
        String action = NAMESPACE + "#getNotifications:" + method;
        SoapObject request = new SoapObject(NAMESPACE, method);
        soap_Response = callWebService(request, action);

        if (soap_Response == null) {
            isMessage = false;
        } else {
            Boolean isSuccess = Boolean.parseBoolean(soap_Response.getPropertyAsString("Result"));

            if (isSuccess) {
                isMessage = true;
                text = soap_Response.getPropertyAsString("Description");
            } else {
                isMessage = false;
            }
        }
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

}
