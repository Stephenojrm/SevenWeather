package com.stephen.sevenday.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.stephen.sevenday.R;
import com.stephen.sevenday.db.SevenDayDB;
import com.stephen.sevenday.util.HttpUtil;
import com.stephen.sevenday.util.Utility;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private static final int LEVEL_PROVINCE = 0;
    private static final int LEVEL_CITY = 1;
    private static final int LEVEL_DISTRICT = 2;

    private String selectedProvince;
    private String selectedCity;
    private String selectDistrict;

    private String lastSelected;
    private int currentLevel;

    private SevenDayDB sevenDayDB;

    private ListView listView;
    private TextView textView;
    private ProgressDialog progressDialog;
    private ArrayAdapter adapter;
    private List<String> dataList;
    private List<String> provinceList;
    private List<String> cityList;
    private List<String> districtList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.list_view);
        textView = (TextView) findViewById(R.id.title);
        dataList = new ArrayList<>();
        progressDialog = new ProgressDialog(this);
        sevenDayDB = SevenDayDB.getInstance(this);
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = provinceList.get(position);
                    lastSelected = selectedProvince;
                    queryCities(selectedProvince);
                } else if (currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(position);
                    queryDistricts(selectedCity);
                } else if (currentLevel == LEVEL_DISTRICT) {
                    Intent intent = new Intent(MainActivity.this,WeatherActivity.class);
                    intent.putExtra("selectDistrict", selectDistrict);
                    startActivity(intent);
                }
            }
        });
        queryProvince();
    }


    /**
     * 查询全国所有的省份，优先从数据库中查找，没有的话去服务器上查询。
     */
    private void queryProvince() {
        provinceList = SevenDayDB.loadProvinces();
        if (provinceList.size() > 0) {
            dataList.clear();
            for (String provinceName : provinceList) {
                dataList.add(provinceName);
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            textView.setText("中国");
            currentLevel = LEVEL_PROVINCE;
        } else {
            //请求网络数据
            queryFromServer("province");
        }
    }

    /**
     * 查询选中的省份内所有的城市
     */
    private void queryCities(String selectedProvince) {
        cityList = SevenDayDB.loadCities(selectedProvince);
        if (cityList.size() > 0) {
            dataList.clear();
            for (String cityName : cityList) {
                dataList.add(cityName);
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            textView.setText(selectedProvince);
            currentLevel = LEVEL_CITY;
        } else {
            //请求网络数据
            queryFromServer(selectedProvince);
        }
    }

    /**
     * 查询选中的城市内所有的县
     */
    private void queryDistricts(String selectedCity) {
        districtList = SevenDayDB.loadDistricts(selectedCity);
        if (districtList.size() > 0) {
            dataList.clear();
            for (String districtName : districtList) {
                dataList.add(districtName);
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            textView.setText(selectedCity);
            currentLevel = LEVEL_DISTRICT;
        } else {
            //请求网络数据
            queryFromServer(selectedCity);
        }
    }

    /**
     * 从服务器查询数据
     */
    private void queryFromServer(final String type) {
        String address = "http://v.juhe.cn/weather/citys?key=5ac669f9d2389ba5ff602110186a2339";
        showProgressDialog();
        HttpUtil.sendHttpRequest(address, new HttpUtil.HttpCallBackListener() {
            @Override
            public void onFinish(String response) {
                boolean result;
                result = Utility.handlePosition(sevenDayDB, response);
                if (result) {
                    //通过runOnUiThread()方法回到主线程处理逻辑
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            closeProgressDialog();
                            progressDialog.dismiss();
                            if ("province".equals(type)) {
                                queryProvince();
                            } else if ("selectedProvince".equals(type)) {
                                queryCities(type);
                            } else if ("selectedCity".equals(type)) {
                                queryDistricts(type);
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        closeProgressDialog();
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    /**
     * 显示进度对话框
     */
    private void showProgressDialog() {
        if (progressDialog != null) {
            progressDialog.setMessage("第一次加载时间较长。。。");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    /**
     * 关闭进度对话框??????此方法调用失败
     */
//    private void closeProgressDialog(){}{
//        if(progressDialog!=null){
//            progressDialog.dismiss();
//        }
//    }

    /**
     * 捕获Back按键，根据当前的级别来判断，应该返回哪一个列表，或是直接退出
     */
    public void onBackPressed() {
        if (currentLevel == LEVEL_DISTRICT) {
            queryCities(lastSelected);
        } else if (currentLevel == LEVEL_CITY) {
            queryProvince();
        } else {
            finish();
        }
    }

}
