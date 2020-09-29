package ua.org.algoritm.tasks.ConnectTo1c;

import android.content.Context;
import android.widget.Toast;


public class UIManager extends Toast {

    Context mContext;

    public UIManager(Context context) {
        super(context);
        mContext = context;
    }

    public void showToast(String message){
        Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();

    }
}
