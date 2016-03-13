package com.zboss.suiyuan;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.zboss.suiyuan.bean.PictureObj;
import com.zboss.suiyuan.chat.ConnectServer;
import com.zboss.suiyuan.newspic.CardsAdapter;
import com.zboss.suiyuan.newspic.OnRefreshListener;
import com.zboss.suiyuan.newspic.RefreshListView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class MainTab02 extends Fragment {

    public static RefreshListView cardsList;

    public static CardsAdapter adapter;

    public static RequestQueue requestQueue;

    public static FragmentActivity activity;
    
    public static View rootView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.main_tab_02, container, false);
        cardsList = (RefreshListView) rootView.findViewById(R.id.cards_list);
        if(activity == null) {
            activity = getActivity();
        }
        if(requestQueue == null) {
            requestQueue = Volley.newRequestQueue(activity);
        }
        
        ViewGroup parent = (ViewGroup) rootView.getParent();  
        if (parent != null) {  
            parent.removeView(rootView);  
        } 
        
        setupList();
        return rootView;
    }

    private void setupList() {
        // 初始化图片种类
        initPicTypes();

        createAdapter();
        getMoreImages(true);
        cardsList.setAdapter(adapter);
        cardsList.setOnRefreshListener(new onDownPullRefresh());
    }
    

    private CardsAdapter createAdapter() {
        ArrayList<PictureObj> items = new ArrayList<PictureObj>();
        if (adapter == null) {
            adapter = new CardsAdapter(activity, items);
        }
        return adapter;
    }

    private final class onDownPullRefresh implements OnRefreshListener {
        @Override
        public void onLoadingMore() {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void...params) {
                    SystemClock.sleep(1000);
                    loadMoreImages(true);
                    return null;
                }

                @Override
                protected void onPostExecute(Void result) {
                    adapter.notifyDataSetChanged();
                    cardsList.hideFooterView();
                }
            }.execute(new Void[] {});
        }
    }

    /**
     * 获取更多图片
     * 
     * @param preOrNot true 前面插入 false 后面插入
     */
    private void getMoreImages(final boolean preOrNot) {
        String JSONDataUrl = ConnectServer.getMoreImages(PushApplication.DISPLAY_TYPE);

        JsonObjectRequest jsonObjectRequest =
                new JsonObjectRequest(Request.Method.GET, JSONDataUrl, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // 处理返回的JSON数据

                        try {
                            ArrayList<PictureObj> items = new ArrayList<PictureObj>();

                            String jsonImages = response.getString("data");
                            JSONArray array = new JSONArray(jsonImages);
                            int imageSize = array.length();
                            for (int i = 0; i < imageSize; i++) {
                                JSONObject obj = array.getJSONObject(i);
                                PictureObj newPic = new PictureObj();
                                newPic.setTitle(obj.getString("title"));
                                newPic.setPic(obj.getString("imageUrl"));
                                items.add(newPic);

                            }
                            if (adapter == null) {
                                adapter = new CardsAdapter(activity, items);
                            }
                            if (preOrNot) {
                                adapter.getItems().addAll(items);
                            } else {
                                adapter.getItems().addAll(0, items);
                            }
                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            Toast.makeText(getActivity(), "抱歉，图片加载失败！", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError arg0) {
                        Toast.makeText(getActivity(), "抱歉，图片加载失败！", Toast.LENGTH_SHORT).show();
                    }
                });
        requestQueue.add(jsonObjectRequest);
    }

    public static void resetPicList() {
        if (adapter != null) {
            adapter.getItems().clear();
            adapter.notifyDataSetChanged();
            loadMoreImages(true);
        }
    }

    public static void loadMoreImages(final boolean preOrNot) {
        String JSONDataUrl = ConnectServer.getMoreImages(PushApplication.DISPLAY_TYPE);

        JsonObjectRequest jsonObjectRequest =
                new JsonObjectRequest(Request.Method.GET, JSONDataUrl, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // 处理返回的JSON数据

                        try {
                            ArrayList<PictureObj> items = new ArrayList<PictureObj>();

                            String jsonImages = response.getString("data");
                            JSONArray array = new JSONArray(jsonImages);
                            int imageSize = array.length();
                            for (int i = 0; i < imageSize; i++) {
                                JSONObject obj = array.getJSONObject(i);
                                PictureObj newPic = new PictureObj();
                                newPic.setTitle(obj.getString("title"));
                                newPic.setPic(obj.getString("imageUrl"));
                                items.add(newPic);

                            }
                            if (adapter == null) {
                                adapter = new CardsAdapter(activity, items);
                            }
                            if (preOrNot) {
                                adapter.getItems().addAll(items);
                            } else {
                                adapter.getItems().addAll(0, items);
                            }

                            // 设置通知
                            // 红点提示
                            if (MainActivity.currentIndex != 1) {
                                MainActivity.mTabFaxian.removeView(MainActivity.mBadgeViewforFaxian);
                                int nowCount = MainActivity.mBadgeViewforFaxian.getBadgeCount();
                                MainActivity.mBadgeViewforFaxian.setBadgeCount(nowCount + imageSize);
                                MainActivity.mTabFaxian.addView(MainActivity.mBadgeViewforFaxian);
                            }

                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            Toast.makeText(activity, "抱歉，图片加载失败！", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError arg0) {
                        Toast.makeText(activity, "抱歉，图片加载失败！", Toast.LENGTH_SHORT).show();
                    }
                });
        requestQueue.add(jsonObjectRequest);
    }

    private void initPicTypes() {
        String JSONDataUrl = ConnectServer.getPicTypes();
        JsonObjectRequest jsonObjectRequest =
                new JsonObjectRequest(Request.Method.GET, JSONDataUrl, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // 处理返回的JSON数据

                        try {
                            String jsonImages = response.getString("pictypes");
                            JSONArray array = new JSONArray(jsonImages);
                            int imageSize = array.length();
                            int arraySize = imageSize + 1;
                            // 初始化数组
                            PushApplication.picids = new Integer[arraySize];
                            PushApplication.pictypes = new String[arraySize];
                            PushApplication.picids[0] = -1;
                            PushApplication.pictypes[0] = "全部";

                            for (int i = 0; i < imageSize; i++) {
                                JSONObject obj = array.getJSONObject(i);
                                int crawlTypeId = obj.getInt("id");
                                String crawlType = obj.getString("crawltype");
                                PushApplication.picids[i + 1] = crawlTypeId;
                                PushApplication.pictypes[i + 1] = crawlType;
                            }
                        } catch (JSONException e) {
                            Toast.makeText(activity, "抱歉，图片类型加载失败！", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError arg0) {
                        Toast.makeText(activity, "抱歉，图片类型加载失败！", Toast.LENGTH_SHORT).show();
                    }
                });
        requestQueue.add(jsonObjectRequest);
    }

}
