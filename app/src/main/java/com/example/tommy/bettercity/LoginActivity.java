package com.example.tommy.bettercity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Created by tommy on 2015/11/22.
 */
public class LoginActivity extends AppCompatActivity {
    private TextView mLoginTextView;
    private Button mLoginButton;
    private String responseUtf8;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitle(R.string.login);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        mLoginTextView = (TextView)findViewById(R.id.register);
        mLoginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });
        
        mLoginButton = (Button)findViewById(R.id.btn_login);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
*                 1. 创建一个RequestQueue对象。
*                 2. 创建一个StringRequest对象。
*                 3. 将StringRequest对象添加到RequestQueue里面。
*/
                RequestQueue mQueue = Volley.newRequestQueue(LoginActivity.this);
//                StringRequest stringRequest = new StringRequest("http://www.baidu.com", new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        Log.d("TAG", response);
//                    }
//                }, new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Log.e("TAG",error.getMessage(),error);
//                    }
//                });
//                mQueue.add(stringRequest);
//                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("http://api.map.baidu.com/telematics/v3/weather?location=%E5%8C%97%E4%BA%AC&output=json&ak=E4805d16520de693a3fe707cdc962045", null, new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        try {
//                              responseUtf8 = URLDecoder.decode(response.toString(), "utf-8");
//                        } catch (UnsupportedEncodingException e) {
//                            e.printStackTrace();
//                        }
//                        Log.d("TAG", responseUtf8);
//                    }
//                }, new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Log.d("TAG",error.getMessage(),error);
//                    }
//                });
//                mQueue.add(jsonObjectRequest);
            }
        });
    }
}
