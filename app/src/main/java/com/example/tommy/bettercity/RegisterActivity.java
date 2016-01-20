package com.example.tommy.bettercity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by tommy on 2015/11/23.
 */
public class RegisterActivity extends AppCompatActivity{

    private EditText mIdInput;
    private EditText mPasswordInput;
    private EditText mUserNameInput;
    private Button mRegButton;
    private UserInfo mUserInfo;
    private MyDatabaseHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);
        //toolbar设置

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        //获取控件的实例
        mIdInput = (EditText)findViewById(R.id.id_reg);
        mPasswordInput = (EditText)findViewById(R.id.password_reg);
        mUserNameInput = (EditText)findViewById(R.id.user_name_reg);
        mRegButton = (Button)findViewById(R.id.btn_register);
        mUserInfo = new UserInfo(null,null,null);

        mRegButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUserInfo.setmId(mIdInput.getText().toString());
                mUserInfo.setmPassword(mPasswordInput.getText().toString());
                mUserInfo.setmUserName(mUserNameInput.getText().toString());
                dbHelper = new MyDatabaseHelper(RegisterActivity.this,"UserInfo.db",null,4);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                db.execSQL("insert into userinfo(loginname,password,username,is_login) values(?,?,?,1)",
                        new String[]{mUserInfo.getmId(), mUserInfo.getmPassword(), mUserInfo.getmUserName()});  //将注册信息写入本机数据库
                //查询结果部分
                Cursor cursor = db.rawQuery("select * from userinfo",null);
               if(cursor.moveToFirst()){
                    do{
                        String loginname = cursor.getString(cursor.getColumnIndex("loginname"));
                        String password = cursor.getString(cursor.getColumnIndex("password"));
                        String username = cursor.getString(cursor.getColumnIndex("username"));
                        String islogin = cursor.getString(cursor.getColumnIndex("is_login"));
                        Log.d("RegisterActivity","name: "+loginname);
                        Log.d("RegisterActivity","password: "+password);
                        Log.d("RegisterActivity","username: "+username);
                        Log.d("RegisterActivity","islogin: "+islogin);
                    }while(cursor.moveToNext());
                }
                cursor.close();

                //使用POST方式向服务器发起请求
                String url = "http://gsee.swjtu.edu.cn/weixin/better_city/bettercity.php";
//                try {
//                    RequestQueue mQueue = Volley.newRequestQueue(RegisterActivity.this);
//                    JSONObject params = new JSONObject();
//                    params.put("loginname", loginname);
//                    params.put("password", password);
//                    params.put("username",username);
//                    //json post请求
//                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,url, params, new Response.Listener<JSONObject>() {
//                        @Override
//                        public void onResponse(JSONObject response) {
//                            Log.d("registerActivity", response.toString());
//                        }
//                    }, new Response.ErrorListener() {
//                        @Override
//                        public void onErrorResponse(VolleyError error) {
//                            Log.e("registeractivity", error.getMessage(), error);
//                        }
//                    });
//                    mQueue.add(jsonObjectRequest);
//                }catch (Exception e ){
//
//                }
                //向服务器请求
                RequestQueue mQueue = Volley.newRequestQueue(RegisterActivity.this);  //创建请求队列
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("registerActivity", response + "");  //获取返回信息
                        Toast.makeText(RegisterActivity.this, "注册成功！", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
                        intent.putExtra("is register", true);
                        startActivity(intent);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.d("registerActicity",volleyError.getMessage(),volleyError);  //返回错误信息
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String,String> map = new HashMap<String, String>();
                        String loginname = mUserInfo.getmId();
                        String password = mUserInfo.getmPassword();
                        String username = mUserInfo.getmUserName();
                        map.put("loginname", loginname);
                        map.put("password", password);
                        map.put("username",username);
                        return map;
                    }
                };
                mQueue.add(stringRequest); //将字符请求添加到队列中

            }
        });


    }
}
