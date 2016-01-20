package com.example.tommy.bettercity;



import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Picture;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.esri.android.map.Callout;
import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.LocationDisplayManager;
import com.esri.android.map.MapOnTouchListener;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISFeatureLayer;
import com.esri.android.map.event.OnSingleTapListener;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.android.map.ogc.WMTSLayer;
import com.esri.android.map.popup.Popup;
import com.esri.android.map.popup.PopupContainer;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.Line;
import com.esri.core.geometry.LinearUnit;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polyline;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.PictureMarkerSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.esri.core.symbol.TextSymbol;
import com.scgis.mmap.helper.AddressT;
import com.scgis.mmap.helper.TileCacheDBManager;
import com.scgis.mmap.map.SCGISNameSearchService;
import com.scgis.mmap.map.SCGISTiledMapServiceLayer;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    //创建4个fragment的对象
    private MapFragment mMapFragment;
    private PhotoFragment mPhotoFragment;
    private MessageFragment mMessageFragment;
    private MeFragment mMeFragment;
    //创建下方各tab的对象
    private ImageView mMainImage;
    private ImageView mPhotoImage;
    private ImageView mMessageImage;
    private ImageView mMeImage;
    private TextView mMainText;
    private TextView mPhotoText;
    private TextView mMessageText;
    private TextView mMeText;
    //界面布局对象
    private View mMapLayout;
    private View mPhotoLayout;
    private View mMessageLayout;
    private View mMeLayout;
    //MapView对象
    private MapView mMapView;
    //FragmentManager
    private FragmentManager fragmentManager;
    private ImageButton zoomIn;
    private ImageButton zoomOut;
    private ImageButton findLoc;
    private ImageButton getLength;
    private Graphic drawGraphic;
    private GraphicsLayer graphicsLayer;
    private Point startPoint;
    private Polyline polyline;
    private double scale;
    private SpatialReference spatialReference;
    private Envelope ev;
    private PopupContainer popupContainer;
    private TextView calloutTw;
    private Button calloutBtn;
    private View calloutView;
    private Callout callout;
    private boolean isFirstClicked;
    private SCGISNameSearchService nameSearchService;
    private String mToken;
    private ArrayList<AddressT> addressTArrayList;
    private SearchView searchView;
    private PopupWindow popupWindow;
    private View popupView;
    private MyDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //获取toolbar实例
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar)findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        //设置toolbar标题颜色
        toolbar.setTitleTextColor(Color.WHITE);
        int dlgTileCompress=75;
        mMapView = (MapView)findViewById(R.id.map);
        String dlgUrl="http://www.scgis.net.cn/iMap/iMapServer/DefaultRest/services/newtianditudlg/";
        mToken="2iIlcBpfBA1cV4Mno20f41k7YjzhWfCay9osJTSaiItO6DrWPL2V3ZFKP_BYt_fG";
        //实现切片缓存管理器
        TileCacheDBManager mDLGTileDBManager=new TileCacheDBManager(this,"itile.db");
        //实例化地图切片图层
        SCGISTiledMapServiceLayer mDLGTileMapServiceLayer=new SCGISTiledMapServiceLayer(this,dlgUrl,mToken,true,mDLGTileDBManager);
        //切片图层的缓冲文件大小设为100M
        mDLGTileMapServiceLayer.setCacheSize(100);
        //对瓦片进行压缩
        mDLGTileMapServiceLayer.setTileCompressAndQuality(true, dlgTileCompress);
        graphicsLayer = new GraphicsLayer();
        //将图层添加到地图中
        mMapView.addLayer(mDLGTileMapServiceLayer);
        mMapView.addLayer(graphicsLayer);
        mDLGTileMapServiceLayer.setVisible(true);
        mMapView.setResolution(0.56441802978516E-05);
        mMapView.centerAt(new Point(104.071791, 30.665534), true);
        final LocationDisplayManager locationDisplayManager = mMapView.getLocationDisplayManager();
        locationDisplayManager.setAutoPanMode(LocationDisplayManager.AutoPanMode.LOCATION);
        locationDisplayManager.start();
        zoomIn = (ImageButton)findViewById(R.id.zoom_in);
        zoomOut = (ImageButton)findViewById(R.id.zoom_out);
        findLoc = (ImageButton)findViewById(R.id.find_loc);
        getLength = (ImageButton)findViewById(R.id.get_length);
        //自定义缩放
        zoomIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMapView.zoomin();
            }
        });
        zoomOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMapView.zoomout();
            }
        });
        //手动定位
        findLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Point currentLocation = locationDisplayManager.getPoint();
                mMapView.centerAt(currentLocation, true);
                mMapView.setResolution(0.56441802978516E-05);
            }
        });
        //获取空间信息数据
        mMapView.setOnStatusChangedListener(new OnStatusChangedListener() {
            @Override
            public void onStatusChanged(Object source, STATUS status) {
                if (STATUS.INITIALIZED == status && source == mMapView) {
                    scale = mMapView.getScale();
                    spatialReference = mMapView.getSpatialReference();
                    //本地图采用WGS-84坐标系
//                    Log.d("spatial","spatial reference:"+mMapView.getSpatialReference());
                }
            }
        });
        //设置最小外包矩形
       // ev = new Envelope(mMapView.getCenter(),mMapView.getWidth(),mMapView.getHeight());
        //设置地图单位
//        unit = new LinearUnit(4326);
        //添加与底图相同空间参考的Graphic图层

        startPoint = null;
        isFirstClicked = true;
        //测距功能
        getLength.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //定义地图点击监听器
                mMapView.setOnSingleTapListener(new OnSingleTapListener() {
                        @Override
                        public void onSingleTap(float x, float y) {
                            if (startPoint == null) {
                                startPoint = mMapView.toMapPoint(x, y);
                                SimpleMarkerSymbol symbol = new SimpleMarkerSymbol(Color.BLUE, 10, SimpleMarkerSymbol.STYLE.CIRCLE);
                                drawGraphic = new Graphic(startPoint, symbol);
                                graphicsLayer.addGraphic(drawGraphic);
                                polyline = new Polyline();
                                polyline.startPath(startPoint);
                            } else {
                                startPoint = mMapView.toMapPoint(x, y);
                                SimpleMarkerSymbol symbol = new SimpleMarkerSymbol(Color.BLUE, 10, SimpleMarkerSymbol.STYLE.CIRCLE);
                                graphicsLayer.addGraphic(new Graphic(startPoint, symbol));
                                polyline.lineTo(startPoint);
                                drawGraphic = new Graphic(polyline, new SimpleLineSymbol(Color.BLUE, 2, SimpleLineSymbol.STYLE.DASH));
                                graphicsLayer.addGraphic(drawGraphic);
                                double length = GeometryEngine.geodesicLength(polyline, spatialReference, null);
                                callout = mMapView.getCallout();
                                //创建calloutView
                                calloutView = getLayoutInflater().inflate(R.layout.callout_layout, null);
                                //将气泡的内容设置成calloutView
                                callout.setContent(calloutView);
                                //获取calloutTw对象
                                calloutTw = (TextView) calloutView.findViewById(R.id.callout_text);
                                //获取calloutButton对象
                                calloutBtn = (Button) calloutView.findViewById(R.id.callout_button);
                                //设置calloutTw内容
                                calloutTw.setText(length + "米");
                                //                            calloutBtn = (Button)findViewById(R.id.callout_button);
                                callout.show(startPoint, calloutView);
                                //为calloutBtn设置监听器,用于清除当前图层上绘制的图形
                                calloutBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        clearGraphic();
                                    }
                                });
                            }
                        }
                    }
                );
            }
        });

        initViews();
        //创建用户数据库
        dbHelper = new MyDatabaseHelper(this,"UserInfo.db",null,4);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        //弹窗功能
        showPopupWindow();
//        geoCoding();
//        Log.d("address", addressTArrayList.get(0) + " ");
//        new SearchTask().execute();
        fragmentManager = getFragmentManager();
        //第一次选中第0个tab
        setTabSelection(0);
    }

    private void showPopupWindow() {
        //点击下方导航栏加号弹出popup
        ImageView plus = (ImageView)findViewById(R.id.plus_button);
        popupView = getLayoutInflater().inflate(R.layout.popup_layout,null);
        popupWindow = new PopupWindow(popupView,mMapView.getLayoutParams().width,600);
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.setAnimationStyle(-1);
                popupWindow.showAtLocation(mMapView, Gravity.LEFT | Gravity.BOTTOM, 0, 130);
            }
        });
        LinearLayout closeButton = (LinearLayout)popupView.findViewById(R.id.close_popup);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
                popupWindow.setAnimationStyle(-1);
            }
        });
    }

    private void initViews() {
        mMapLayout = findViewById(R.id.index);
        mPhotoLayout = findViewById(R.id.photo);
        mMessageLayout = findViewById(R.id.msg);
        mMeLayout = findViewById(R.id.me);
        mMeImage = (ImageView)findViewById(R.id.image_me);
        mMainImage = (ImageView)findViewById(R.id.image_index);
        mMessageImage = (ImageView)findViewById(R.id.image_msg);
        mPhotoImage = (ImageView)findViewById(R.id.image_photo);
        mMainText = (TextView)findViewById(R.id.text_index);
        mPhotoText = (TextView)findViewById(R.id.text_photo);
        mMessageText = (TextView)findViewById(R.id.text_msg);
        mMeText = (TextView)findViewById(R.id.text_me);
        mMapLayout.setOnClickListener(this);
        mPhotoLayout.setOnClickListener(this);
        mMessageLayout.setOnClickListener(this);
        mMeLayout.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.index:
                setTabSelection(0);
                break;
            case R.id.photo:
                setTabSelection(1);
                askPhoto();
                break;
            case R.id.msg:
                setTabSelection(2);
                break;
            case R.id.me:
                setTabSelection(3);
                break;
            default:
                break;
        }
    }

    private void clearGraphic() {
        graphicsLayer.removeAll();
        callout.hide();
        startPoint=null;
    }

    private void askPhoto() {
       PopupMenu popupMenu = new PopupMenu(MainActivity.this,mPhotoLayout);
       popupMenu.inflate(R.menu.menu_popup);
        final MenuItem mTakePhotoItem = popupMenu.getMenu().findItem(R.id.take_photo);
        final MenuItem mSelectPhotoItem = popupMenu.getMenu().findItem(R.id.select_photo);
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (menuItem == mTakePhotoItem) {
                    File outputImage = new File(Environment.getExternalStorageDirectory(), "output_image.jpg");
                    try {
                        if (outputImage.exists()) {
                            outputImage.delete();
                        }
                        outputImage.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Uri imageUri = Uri.fromFile(outputImage);
                    Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivity(intent);
                }
                if (menuItem == mSelectPhotoItem) {
                    File outputImage = new File(Environment.getExternalStorageDirectory(), "outputImage.jpg");
                    try {
                        if (outputImage.exists()) {
                            outputImage.delete();
                        }
                        outputImage.createNewFile();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Uri imageUri = Uri.fromFile(outputImage);

                    Intent intent = new Intent("android.intent.action.GET_CONTENT");
                    intent.setType("image/*");
                    startActivity(intent);
                }
                return false;
            }
        });
    }

    private void setTabSelection(int index) {
        clearSelection();
        //开启一个Fragment事务
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        //隐藏所有的Fragment
        hideFragments(transaction);
        switch (index){
            case 0:
                mMainImage.setImageResource(R.mipmap.index_selected);
                mMainText.setTextColor(Color.parseColor("#006ee9"));
                if(mMapFragment==null){
                    mMapFragment = new MapFragment();
                    transaction.add(R.id.main_view,mMapFragment);
                }else{
                    transaction.show(mMapFragment);
                }
                break;
            case 1:
                mPhotoImage.setImageResource(R.mipmap.photo_selected);
                mPhotoText.setTextColor(Color.parseColor("#006ee9"));
                if(mPhotoFragment==null){
                    mPhotoFragment = new PhotoFragment();
                    transaction.add(R.id.main_view,mPhotoFragment);
                }else{
                    transaction.show(mPhotoFragment);
                }
                break;
            case 2:
                mMessageImage.setImageResource(R.mipmap.message_selected);
                mMessageText.setTextColor(Color.parseColor("#006ee9"));
                if(mMessageFragment==null){
                    mMessageFragment = new MessageFragment();
                    transaction.add(R.id.main_view,mMessageFragment);
                }else{
                    transaction.show(mMessageFragment);
                }
                break;
            case 3:
                mMeImage.setImageResource(R.mipmap.own_selected);
                mMeText.setTextColor(Color.parseColor("#006ee9"));
                if(mMeFragment==null){
                    mMeFragment = new MeFragment();
                    transaction.add(R.id.main_view,mMeFragment);
                }else{
                    transaction.show(mMeFragment);
                }
                break;
        }
        transaction.commit();
    }

    private void hideFragments(FragmentTransaction transaction) {
        if(mMapFragment!=null){
           transaction.hide(mMapFragment);
        }
        if(mPhotoFragment!=null){
            transaction.hide(mPhotoFragment);
        }
        if(mMessageFragment!=null){
            transaction.hide(mMessageFragment);
        }
        if(mMeFragment!=null){
            transaction.hide(mMeFragment);
        }
    }

    private void clearSelection() {
        mMainImage.setImageResource(R.mipmap.index_page);
        mMainText.setTextColor(Color.GRAY);
        mPhotoImage.setImageResource(R.mipmap.photo);
        mPhotoText.setTextColor(Color.GRAY);
        mMessageImage.setImageResource(R.mipmap.message);
        mMessageText.setTextColor(Color.GRAY);
        mMeImage.setImageResource(R.mipmap.me);
        mMeText.setTextColor(Color.GRAY);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        MenuItem searchItem = menu.findItem(R.id.search_button);
//        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
//            @Override
//            public boolean onMenuItemActionExpand(MenuItem menuItem) {
////                Intent intent = new Intent(MainActivity.this,searchAcitvity.class);
//                Log.d("tag","hello world");
////                startActivity(intent);
//                return true;
//            }
//
//            @Override
//            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
//                Log.d("tag","bye");
//                return true;
//            }
//        });
        searchView = (SearchView)searchItem.getActionView();
        searchView.setQueryHint("搜索...");
        //设置searchView监听器
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                onSearchButtonClicked();    //执行搜索时所触发的方法
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void onSearchButtonClicked() {
        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(mMapView.getWindowToken(),0);

        String address = searchView.getQuery().toString();
        new SearchTask().execute(address);  //执行searchTask
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    //创建SearchTask类继承自AsyncTask，用于在后台执行搜索任务
    private class SearchTask extends AsyncTask<String,Void,ArrayList<AddressT>>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        //doInBackground()方法，在子线程中执行所有的耗时操作
        @Override
        protected ArrayList<AddressT> doInBackground(String... params) {
            nameSearchService = new SCGISNameSearchService(mToken);     //获取天地图四川地名搜索服务的对象
            nameSearchService.setBaseURL("http://www.scgis.net.cn/imap/imapserver/defaultrest/services/Newscnamesearch");
            nameSearchService.setRegion("5101");
            nameSearchService.setCounty("510124");  //搜索范围暂时定在郫县
//        nameSearchService.setTypeCode("酒店");
            nameSearchService.setStartIndex(0);
            nameSearchService.setStopIndex(10);     //搜索结果显示10个
            addressTArrayList = null;
            try{
                addressTArrayList = nameSearchService.getNameSearchResult(params[0]);
            }catch(Exception e){
                e.printStackTrace();
            }
            return addressTArrayList;
        }

        @Override
        protected void onPostExecute(final ArrayList<AddressT> addressTs) {
            if(addressTs.size()==0){
                Toast.makeText(MainActivity.this,getString(R.string.no_place),Toast.LENGTH_LONG).show();
            }else
            {
                //循环遍历addressTs(也就是在doInBackground方法中返回的对象)
                for ( int i = 0; i < addressTs.size() ; i++){
                    double x = addressTs.get(i).get_X();
                    double y = addressTs.get(i).get_Y();
                    Point resultPoint = new Point(x,y);
                    SimpleMarkerSymbol resultSymbol = new SimpleMarkerSymbol(Color.RED,16, SimpleMarkerSymbol.STYLE.CROSS);
                    Graphic resultGraphic = new Graphic(resultPoint,resultSymbol);
                    graphicsLayer.addGraphic(resultGraphic);
                    mMapView.zoomToResolution(resultPoint, 0.76441802978516E-05);
                }
//                Log.d("addressTs", "" + addressTs);
//                Toast.makeText(MainActivity.this,""+addressTs.get(0).get_X()+","+addressTs.get(0).get_Y(),Toast.LENGTH_LONG).show();

            }
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
