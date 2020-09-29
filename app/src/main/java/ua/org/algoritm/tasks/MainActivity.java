package ua.org.algoritm.tasks;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageButton;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;

import java.lang.ref.WeakReference;

import ua.org.algoritm.tasks.ConnectTo1c.SOAP_Dispatcher;
import ua.org.algoritm.tasks.ConnectTo1c.UIManager;

public class MainActivity extends AppCompatActivity {
    private ImageButton mImageButtonScanBarCode;
    private static final int REQUEST_CODE_SCAN = 0x0000c0de;
    private ProgressDialog mDialog;

    public static final int ACTION_GET_ORDER = 100;
    public static final int ACTION_ConnectionError = 0;
    public static final int ACTION_Connection = 1111;
    public static final int ACTION_Connection_Lost = 2222;

    public static UIManager uiManager;
    public static SoapObject soapParam_Response;
    public static SoapFault responseFault;
    public static Handler soapHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        uiManager = new UIManager(this);
        soapHandler = new incomingHandler(this);

        mImageButtonScanBarCode = findViewById(R.id.scanBarCode);
        mImageButtonScanBarCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanBarCode();
            }
        });
    }

    private void scanBarCode() {
        IntentIntegrator intentIntegrator = new IntentIntegrator(this);
        intentIntegrator.setCaptureActivity(ScannerActivity.class);
        intentIntegrator.setDesiredBarcodeFormats(intentIntegrator.ALL_CODE_TYPES);
        intentIntegrator.setBeepEnabled(false);
        intentIntegrator.setCameraId(0);
        intentIntegrator.setPrompt(getString(R.string.camera_to_the_barcode));
        intentIntegrator.setBarcodeImageEnabled(false);
        intentIntegrator.setOrientationLocked(false);
        intentIntegrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE_SCAN) {
            IntentResult Result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (Result != null) {
                if (Result.getContents() == null) {

                } else {
                    mDialog = new ProgressDialog(this);
                    mDialog.setMessage(getString(R.string.wait_update));
                    mDialog.setCancelable(false);
                    mDialog.show();

                    String tBarCode = Result.getContents();

//                    tBarCode = SharedData.clearBarcode(tBarCode);
//                    barCode.setText(tBarCode);

                    String login = QueryPreferences.getLogin(this);
                    String password = QueryPreferences.getPassword(this);

                    SOAP_Dispatcher dispatcherUpdate = new SOAP_Dispatcher(ACTION_GET_ORDER, login, password, getApplicationContext());
                    dispatcherUpdate.string_Inquiry = tBarCode;
                    dispatcherUpdate.start();
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    class incomingHandler extends Handler {
        private final WeakReference<MainActivity> mTarget;

        public incomingHandler(MainActivity context) {
            mTarget = new WeakReference<>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity target = mTarget.get();

            if (mDialog != null && mDialog.isShowing()) {
                mDialog.dismiss();
            }

            switch (msg.what) {

                case ACTION_ConnectionError:
                    uiManager.showToast(getString(R.string.errorConnection) + getSoapErrorMessage());
                    break;
                case ACTION_GET_ORDER:
                    target.orderData();
                    break;
                case ACTION_Connection_Lost:
                    uiManager.showToast(getString(R.string.lost_for_internet));
                    break;
            }
        }
    }

    private void orderData() {
        Boolean isSaveSuccess = Boolean.parseBoolean(soapParam_Response.getPropertyAsString("Result"));

        if (isSaveSuccess) {
            String order = soapParam_Response.getPropertyAsString("Description");
            Intent intent = new Intent(this, DetailsOrder.class);
            intent.putExtra("order", order);
            startActivity(intent);

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
}
