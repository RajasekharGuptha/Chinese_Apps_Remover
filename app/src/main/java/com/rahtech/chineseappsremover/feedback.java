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

public class feedback extends AppCompatActivity {
    String feedback;
    EditText fed;
    Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        fed=findViewById(R.id.feedback_et);
        btn=findViewById(R.id.submit_btn_2);
    }
    public void submit(View view) {
        feedback = fed.getText().toString();
        closeKeyBoard();
        if (!feedback.equals("")) {
           btn.setText(R.string.submitting);
            FirebaseDatabase.getInstance().getReference().child("Feedback").push().setValue(feedback)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            fed.setText("");
                            btn.setText(R.string.submit);
                            Toast.makeText(feedback.this, "Thank You", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    fed.setText(feedback);
                    btn.setText(R.string.submit);
                    Toast.makeText(feedback.this, "Failed!Try Again", Toast.LENGTH_SHORT).show();
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
