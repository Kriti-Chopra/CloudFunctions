package kriti.somevalue.com.cloudfunctions;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    Button btnFunction;
    FirebaseFunctions mFunctions;
    EditText mMessageInputField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnFunction=(Button) findViewById(R.id.btnFunction);
        mFunctions=FirebaseFunctions.getInstance();
        mMessageInputField=(EditText) findViewById(R.id.edtText);

        btnFunction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAddMessageClicked();
            }
        });

    }

    private Task<String> addMessage(String text) {
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();
        data.put("text", text);
        data.put("push", true);
        return mFunctions
                .getHttpsCallable("addMessage")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        String result = (String) task.getResult().getData();
                        return result;
                    }
                });
    }


    private void onAddMessageClicked() {
        String inputMessage = mMessageInputField.getText().toString();
        if (TextUtils.isEmpty(inputMessage)) {
            showSnackbar("Please enter a message.");
            return;
        }

        // [START call_add_message]
        addMessage(inputMessage)
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {

                        if (!task.isSuccessful()) {
                            Exception e = task.getException();
                            if (e instanceof FirebaseFunctionsException) {
                                FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                                FirebaseFunctionsException.Code code = ffe.getCode();
                                Object details = ffe.getDetails();
                            }
                            // [START_EXCLUDE]
                            Log.w("TAG", "addMessage:onFailure", e);
                            showSnackbar("An error occurred.");
                            return;
                            // [END_EXCLUDE]
                        }
                        // [START_EXCLUDE]
                        String result = task.getResult();
                       //mMessageOutputField.setText(result);
                        Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show();
                        // [END_EXCLUDE]
                    }
                });
        // [END call_add_message]
    }

    private void showSnackbar(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
