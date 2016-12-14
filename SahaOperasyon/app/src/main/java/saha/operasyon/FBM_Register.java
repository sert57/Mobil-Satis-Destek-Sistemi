package saha.operasyon;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

// Firebase Kaydı Tamamlandığında Calisacak Sınıf
public class FBM_Register extends FirebaseInstanceIdService
{
    public void onTokenRefresh()
    {
        String id = FirebaseInstanceId.getInstance().getToken();
        Log.e("x","FCM ID : "+id);
        SP sp = new SP(this);
        sp.setFCM(id);
    }
}
