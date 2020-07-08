package com.rahtech.chineseappsremover;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class suggestions extends AppCompatActivity {

    ListView listView;
    ArrayList<String> suggestions;
    String[] suggestions_list;
    Map<String,String> appnames;
    SharedPreferences sharedPreferences;
    TextView header;
    String pkg;
    String an;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestions);
        listView=findViewById(R.id.listView);
        header=findViewById(R.id.suggs_header);
        sharedPreferences=getSharedPreferences(getString(R.string.package_name),MODE_PRIVATE);
        suggestions_list=getIntent().getStringArrayExtra("suggs");
        pkg=getIntent().getStringExtra("pkg");
        an=getIntent().getStringExtra("app_name");
        appnames=new HashMap<>();
        suggestions=new ArrayList<>();

        if (!haveNetworkConnection()){
            String json=sharedPreferences.getString("json1", "");
            if(json.equals("")){
                Toast.makeText(this, "Turn on Internet and restart app", Toast.LENGTH_SHORT).show();
            }
            else{
                Gson gson = new Gson();
                Type collectionType = new TypeToken<Map<String, String>>() {
                }.getType();
                appnames = gson.fromJson(json, collectionType);
                functionn();
            }
        }
        else {
            FirebaseAuth.getInstance().signInAnonymously();

            FirebaseFirestore.getInstance().document("/chinese_alternatives/china1").get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            String json = documentSnapshot.get("json").toString();
                            Gson gson = new Gson();
                            Type collectionType = new TypeToken<Map<String, String>>() {
                            }.getType();
                            sharedPreferences.edit().putString("json1",json).apply();
                            appnames = gson.fromJson(json, collectionType);
                            functionn();
                        }
                    });
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent1=new Intent(Intent.ACTION_VIEW,Uri.parse("market://details?id="+suggestions_list[position]));
                intent1.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent1);
            }
        });

    }
    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    private void functionn(){
        header.setText(String.format("%s \"%s\"", getString(R.string.alternatives_for), an));
        for(String s:suggestions_list){
            suggestions.add(appnames.get(s));
        }
        ArrayAdapter arrayadapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1,suggestions);
        listView.setAdapter(arrayadapter);
        if(getIntent().getBooleanExtra("uninstall",false)){

            Intent deleteIntent=new Intent(Intent.ACTION_DELETE);
            deleteIntent.setData(Uri.parse("package:"+pkg));
            startActivity(deleteIntent);

            /*
            Intent intent1=new Intent(Intent.ACTION_VIEW,Uri.parse("market://details?id="+pkg));
            intent1.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK
                    | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            startActivity(intent1);
             */
        }


    }
}
