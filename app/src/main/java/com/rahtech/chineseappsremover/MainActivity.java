package com.rahtech.chineseappsremover;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    PackageManager packageManager;
    int function_count=0;
    ArrayList<String> appsArrayList;
    private RecyclerView app_names;
    ArrayList<item> itemArrayList;
    SharedPreferences sharedPreferences;
    private SwipeRefreshLayout swipeRefreshLayout;
    private  static Map<String,String[]> chinese_alternatives;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swipeRefreshLayout=findViewById(R.id.swiperefershlayout);
        chinese_alternatives =new HashMap<>();
        itemArrayList=new ArrayList<>();
        app_names= findViewById(R.id.app_names_list);
        app_names.setLayoutManager(new LinearLayoutManager(this));
        appsArrayList=new ArrayList<>();
        sharedPreferences = getSharedPreferences(getString(R.string.package_name), MODE_PRIVATE);

        if(!haveNetworkConnection()) {

            String json=sharedPreferences.getString("json", "");
            if(json.equals("")){
                Toast.makeText(this, "Turn on Internet and restart app", Toast.LENGTH_SHORT).show();
            }
            else{
            Gson gson = new Gson();
            Type collectionType = new TypeToken<Map<String, String[]>>() {
            }.getType();
            chinese_alternatives = gson.fromJson(json, collectionType);
            function();
            }
        }
        else{
            FirebaseAuth.getInstance().signInAnonymously();
            FirebaseFirestore.getInstance().document("/chinese_alternatives/china").get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            String json=documentSnapshot.get("json").toString();
                            Gson gson=new Gson();
                            Type collectionType = new TypeToken<Map<String,String[]>>() {}.getType();
                            chinese_alternatives=gson.fromJson(json,collectionType);
                            sharedPreferences.edit().putString("json",json).apply();
                            function();

                        }
                    });


        }
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                function();
            }
        });


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.activitymain,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.mssingapp){
                startActivity(new Intent(MainActivity.this,add_missing_app.class));
        }
        if(item.getItemId() == R.id.feedback){
            startActivity(new Intent(MainActivity.this,feedback.class));
        }
        if(item.getItemId() == R.id.register){
            startActivity(new Intent(MainActivity.this,registr.class));
        }
        if(item.getItemId() == R.id.share_app){

            FirebaseFirestore.getInstance().document("/chinese_alternatives/AppLink").get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Intent intent=new Intent(Intent.ACTION_SEND);
                            intent.setType("text/plain");
                            intent.putExtra(Intent.EXTRA_SUBJECT,"Chinese Apps Remover & Alternatives Suggestor");
                            String msg="Chinese Apps Remover & Alternatives Suggestor \n\n I did.. What About You..? \n\n";
                            msg+=documentSnapshot.get("Link").toString();
                            intent.putExtra(Intent.EXTRA_TEXT,msg);
                            startActivity(Intent.createChooser(intent,"Select to share.."));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.this, "Some error occurred..Try Again!!", Toast.LENGTH_SHORT).show();
                }
            });

        }
        return super.onOptionsItemSelected(item);
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void function() {
        function_count++;
            swipeRefreshLayout.setBackgroundColor(getResources().getColor(R.color.background));
            swipeRefreshLayout.setRefreshing(true);
            packageManager = getPackageManager();
            itemArrayList.clear();
            final List<PackageInfo> packageInfoList = packageManager.getInstalledPackages(PackageManager.GET_SIGNATURES);
            if (!packageInfoList.isEmpty()) {
                for (PackageInfo packageInfo : packageInfoList) {
                    ApplicationInfo applicationInfo;
                    applicationInfo = packageInfo.applicationInfo;
                    if ((applicationInfo.flags & 1) == 0) {
                        if (chinese_alternatives.containsKey(packageInfo.packageName)) {
                            itemArrayList.add(new item(applicationInfo.loadLabel(packageManager).toString(),
                                            packageInfo.packageName, chinese_alternatives.get(packageInfo.packageName),
                                            applicationInfo.loadIcon(getPackageManager())
                                    )
                            );
                        }
                    }
                }
            }
            item_dapater adapter = new item_dapater(this, itemArrayList);
            app_names.setAdapter(adapter);
            swipeRefreshLayout.setRefreshing(false);
            if (itemArrayList.size() == 0 && function_count>1) {
                swipeRefreshLayout.setBackground(getDrawable(R.drawable.tick));
            }


        }

    @Override
    protected void onStart() {
        super.onStart();
        swipeRefreshLayout.setBackgroundColor(getResources().getColor(R.color.background));

    }

    @Override
    public void onResume()
    {  // After a pause OR at startup
        super.onResume();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                function();
            }

    }

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = new NetworkInfo[0];
        if (cm != null) {
            netInfo = cm.getAllNetworkInfo();
        }
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

}
