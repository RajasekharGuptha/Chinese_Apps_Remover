package com.rahtech.chineseappsremover;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;

public class add_missing_app extends AppCompatActivity {
    String misssed_app;
    EditText missing;
    Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_missing_app);
        missing=findViewById(R.id.missing_app_name);
        btn=findViewById(R.id.submit_btn);
    }
    public void submit(View view) {
        misssed_app = missing.getText().toString();
        closeKeyBoard();
        if (!misssed_app.equals("")) {
            btn.setText(R.string.submitting);
            btn.setClickable(false);
            FirebaseDatabase.getInstance().getReference().child("suggs").push().setValue(misssed_app)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    missing.setText("");
                    btn.setText(R.string.submit);
                    btn.setClickable(true);
                    Toast.makeText(add_missing_app.this, "Thank You", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    missing.setText(misssed_app);
                    btn.setText(R.string.submit);
                    btn.setClickable(true);
                    Toast.makeText(add_missing_app.this, "Failed!Try Again", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    private void closeKeyBoard(){
        View view=this.getCurrentFocus();
        if(view!=null){
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
    }
}
