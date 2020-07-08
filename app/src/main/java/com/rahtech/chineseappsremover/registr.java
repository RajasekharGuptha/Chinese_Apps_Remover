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

import java.util.HashMap;
import java.util.Map;

public class registr extends AppCompatActivity {

    EditText alt_for;
    EditText alt;
    EditText alt_link;
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registr);
        
        alt=findViewById(R.id.alt);
        alt_for=findViewById(R.id.alt_for);
        alt_link=findViewById(R.id.alt_link);
        button=findViewById(R.id.submit_btn_3);
    }
    public void submit(View view){
        final String altfor_txt=alt_for.getText().toString();
        final String alt_txt=alt.getText().toString();
        final String altlink_txt=alt_link.getText().toString();
        closeKeyBoard();
        if (!(alt_txt.equals("")&& altfor_txt.equals(""))) {
            button.setText(R.string.submitting);
            button.setClickable(false);
            Map<String,String> m= new HashMap<>();
            m.put(altfor_txt, String.format("%s\n link:%s", alt_txt, altlink_txt));
            FirebaseDatabase.getInstance().getReference().child("alternative").push().setValue(m)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            alt.setText("");
                            alt_link.setText("");
                            alt_for.setText("");
                            button.setText(R.string.submit);
                            button.setClickable(true);
                            Toast.makeText(registr.this, "Thank You", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    alt.setText(alt_txt);
                    alt_link.setText(altlink_txt);
                    alt_for.setText(altfor_txt);
                    button.setText(R.string.submit);
                    button.setClickable(true);
                    Toast.makeText(registr.this, "Failed!Try Again", Toast.LENGTH_SHORT).show();
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