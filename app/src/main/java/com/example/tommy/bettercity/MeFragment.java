package com.example.tommy.bettercity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.List;

/**
 * Created by tommy on 2015/10/25.
 */
public class MeFragment extends Fragment {
    private static final String DIALOG_DATE="date";
    //listview数据
    private String[] data = {"性别","生日","家乡","E-mail","使用帮助","关于我们"};
    private MyDatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private View view;
    private Button exitLogin;
    private TextView username;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //从registerActivity返回MainActivity
        Intent intent = this.getActivity().getIntent();
        Boolean isRegister = intent.getBooleanExtra("is register", false);
        Log.d("me",isRegister+"");
        //查询数据库
        //为简化，只允许一个用户登录
        dbHelper = new MyDatabaseHelper(this.getActivity(),"UserInfo.db",null,4);
        db = dbHelper.getWritableDatabase();
        final Cursor cursor = db.rawQuery("select * from userinfo", null);   //查询该用户所有信息
        cursor.moveToLast();
        String isLogin = cursor.getString(cursor.getColumnIndex("is_login"));
        //如果没有登录，直接弹出登录界面,否则，保持在原界面
        if(isLogin.equals("0")) {
            cursor.close();
            Intent intent2 = new Intent(this.getActivity(), LoginActivity.class);
            startActivity(intent2);
        }else {
            //listview设置
            view = inflater.inflate(R.layout.me_layout, container, false);
            ListView listView = (ListView) view.findViewById(R.id.help_list_view);
            LinearLayout user = (LinearLayout) view.findViewById(R.id.name);
             username = (TextView) view.findViewById(R.id.username);
            String mUserName = cursor.getString(cursor.getColumnIndex("username"));
            cursor.close();
            username.setText(mUserName);
            //这里的context参数，使用getActivity方法获取当前的活动
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_list_item_1, data);
            listView.setAdapter(adapter);
            //列表项点击事件
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    if(position == 2){
                        FragmentManager fm = getActivity().getFragmentManager();
                        DatePickerFragment dialog = new DatePickerFragment();
                        dialog.show(fm,DIALOG_DATE);
                    }
                }
            });
            //退出登录点击事件
            exitLogin = (Button)view.findViewById(R.id.exit_account);
            exitLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    db.execSQL("UPDATE USERINFO SET IS_LOGIN=\'0\'");   //将islogin字段设置为未登录
                    Cursor cursor1 =db.rawQuery("SELECT * FROM USERINFO", null);
                    cursor1.moveToLast();
                    String isLogin = cursor1.getString(cursor1.getColumnIndex("is_login"));
//                    Log.d("MeFragment","islogin= "+isLogin);
                    cursor1.close();
                    if(isLogin == "0"){
                        Toast.makeText(getActivity(),"退出成功！",Toast.LENGTH_SHORT).show();
                    }
                    username.setText(R.string.user_name);
                    exitLogin.setText(R.string.login);
                    exitLogin.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(MeFragment.this.getActivity(), LoginActivity.class);
                            startActivity(intent);
                        }
                    });
                }
            });

        }
            return view;
    }


}