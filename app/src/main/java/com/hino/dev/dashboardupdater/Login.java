package com.hino.dev.dashboardupdater;

import android.app.ActionBar;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONObject;

public class Login extends AppCompatActivity {

    private TextView txt_username;
    private TextView txt_password;
    private Button btn_login;
    private RequestQueue requestQueue;
    private Gson gson;
    private Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        session = new Session(this);

        if(checkUserSession() != null){
            Intent intent = new Intent(this,MOList.class);
            startActivity(intent);
            finish();
        }

        requestQueue = Volley.newRequestQueue(this);
        gson = new Gson();

        txt_username = findViewById(R.id.txt_username);
        txt_password = findViewById(R.id.txt_password);

        btn_login = findViewById(R.id.btn_login);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!txt_username.equals("") || !txt_password.equals("")){
                    final String url = getResources().getString(R.string.api_login)
                            .replace("[username]",txt_username.getText())
                            .replace("[password]",txt_password.getText());

                    JsonObjectRequest request = new JsonObjectRequest(
                            JsonObjectRequest.Method.GET,
                            url,
                            null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    if(response != null){
                                        User user = gson.fromJson(response.toString(),User.class);
                                        session.setUser(user);
                                        Intent intent = new Intent(getApplicationContext(),MOList.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    NetworkResponse response = error.networkResponse;
                                    if(response != null){
                                        if(response.statusCode == 400){
                                            // TODO: 02/11/2018 implement 400 handler
                                        }else{
                                            // TODO: 02/11/2018 implement FATAL error
                                        }
                                    }
                                }
                            }
                    );

                    requestQueue.add(request);
                }
            }
        });
    }

    private User checkUserSession() {
        User user = session.getUser();
        return user;
    }
}
