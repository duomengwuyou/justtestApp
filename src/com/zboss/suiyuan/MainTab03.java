package com.zboss.suiyuan;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.zboss.suiyuan.bean.DisplayItem;
import com.zboss.suiyuan.bean.NewsObj;
import com.zboss.suiyuan.bean.PictureObj;
import com.zboss.suiyuan.chat.ConnectServer;
import com.zboss.suiyuan.newspic.CardsAdapter;
import com.zboss.suiyuan.newspic.NewsCardsAdapter;
import com.zboss.suiyuan.newspic.OnRefreshListener;
import com.zboss.suiyuan.newspic.RefreshListView;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MainTab03 extends Fragment {

    public static RefreshListView cardsList;

    public static NewsCardsAdapter adapter;

    public static RequestQueue requestQueue;

    public static FragmentActivity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.main_tab_03, container, false);
        cardsList = (RefreshListView) rootView.findViewById(R.id.news_cards_list);
        activity = getActivity();
        requestQueue = Volley.newRequestQueue(activity);
        setupList();
        return rootView;
    }

    private void setupList() {
        // 初始化新闻种类
        initNewsTypes();

        createAdapter();
        getMoreNews(true);
        cardsList.setAdapter(adapter);
        cardsList.setOnRefreshListener(new onDownPullRefresh());
    }

    private NewsCardsAdapter createAdapter() {
        ArrayList<NewsObj> items = new ArrayList<NewsObj>();
        if (adapter == null) {
            adapter = new NewsCardsAdapter(activity, items);
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
                    loadMoreNews(true);
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
     * 获取更多新闻
     * 
     * @param preOrNot true 前面插入 false 后面插入
     */
    private void getMoreNews(final boolean preOrNot) {
        String JSONDataUrl = ConnectServer.getMoreNews(PushApplication.NEWS_DISPLAY_TYPE);
        JsonObjectRequest jsonObjectRequest =
                new JsonObjectRequest(Request.Method.GET, JSONDataUrl, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // 处理返回的JSON数据

                        try {
                            ArrayList<NewsObj> items = new ArrayList<NewsObj>();

                            String jsonImages = response.getString("data");
                            JSONArray array = new JSONArray(jsonImages);
                            int imageSize = array.length();
                            for (int i = 0; i < imageSize; i++) {
                                JSONObject obj = array.getJSONObject(i);
                                NewsObj news= new NewsObj();
                                news.setContent(obj.getString("content"));
                                news.setDatetime(obj.getString("contentDate"));
                                news.setSrcpath(obj.getString("sourceLink"));
                                news.setWebsite(obj.getString("title"));
                                items.add(news);

                            }
                            if (adapter == null) {
                                adapter = new NewsCardsAdapter(activity, items);
                            }
                            if (preOrNot) {
                                adapter.getItems().addAll(items);
                            } else {
                                adapter.getItems().addAll(0, items);
                            }
                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            Toast.makeText(getActivity(), "抱歉，新闻加载失败！", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError arg0) {
                        Toast.makeText(getActivity(), "抱歉，新闻加载失败！", Toast.LENGTH_SHORT).show();
                    }
                });
        requestQueue.add(jsonObjectRequest);
    }

    public static void resetNewsList() {
        if (adapter != null) {
            adapter.getItems().clear();
            adapter.notifyDataSetChanged();
            loadMoreNews(false);
        }
    }

    public static void loadMoreNews(final boolean preOrNot) {
        String JSONDataUrl = ConnectServer.getMoreNews(PushApplication.NEWS_DISPLAY_TYPE);

        JsonObjectRequest jsonObjectRequest =
                new JsonObjectRequest(Request.Method.GET, JSONDataUrl, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // 处理返回的JSON数据

                        try {
                            ArrayList<NewsObj> items = new ArrayList<NewsObj>();
                            String jsonImages = response.getString("data");
                            JSONArray array = new JSONArray(jsonImages);
                            int imageSize = array.length();
                            for (int i = 0; i < imageSize; i++) {
                                JSONObject obj = array.getJSONObject(i);
                                NewsObj news= new NewsObj();
                                news.setContent(obj.getString("content"));
                                news.setDatetime(obj.getString("contentDate"));
                                news.setSrcpath(obj.getString("sourceLink"));
                                news.setWebsite(obj.getString("title"));
                                items.add(news);
                            }
                            if (adapter == null) {
                                adapter = new NewsCardsAdapter(activity, items);
                            }
                            if (preOrNot) {
                                adapter.getItems().addAll(items);
                            } else {
                                adapter.getItems().addAll(0, items);
                            }

                            // 设置通知
                            // 红点提示
                            if (MainActivity.currentIndex != 2) {
                                MainActivity.mTabTongxunlu.removeView(MainActivity.mBadgeViewforTongxunlu);
                                int nowCount = MainActivity.mBadgeViewforTongxunlu.getBadgeCount();
                                MainActivity.mBadgeViewforTongxunlu.setBadgeCount(nowCount + imageSize);
                                MainActivity.mTabTongxunlu.addView(MainActivity.mBadgeViewforTongxunlu);
                            }

                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            Toast.makeText(activity, "抱歉，新闻加载失败！", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError arg0) {
                        Toast.makeText(activity, "抱歉，新闻加载失败！", Toast.LENGTH_SHORT).show();
                    }
                });
        requestQueue.add(jsonObjectRequest);
    }

    private void initNewsTypes() {
        String JSONDataUrl = ConnectServer.getNewsTypes();
        JsonObjectRequest jsonObjectRequest =
                new JsonObjectRequest(Request.Method.GET, JSONDataUrl, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // 处理返回的JSON数据

                        try {
                            String jsonImages = response.getString("newstypes");
                            JSONArray array = new JSONArray(jsonImages);
                            int imageSize = array.length();
                            int arraySize = imageSize + 1;
                            // 初始化数组
                            PushApplication.newsids = new Integer[arraySize];
                            PushApplication.newstypes = new String[arraySize];
                            PushApplication.newsids[0] = -1;
                            PushApplication.newstypes[0] = "全部";

                            for (int i = 0; i < imageSize; i++) {
                                JSONObject obj = array.getJSONObject(i);
                                int crawlTypeId = obj.getInt("id");
                                String crawlType = obj.getString("crawltype");
                                PushApplication.newsids[i + 1] = crawlTypeId;
                                PushApplication.newstypes[i + 1] = crawlType;
                            }
                        } catch (JSONException e) {
                            Toast.makeText(activity, "抱歉，新闻类型加载失败！", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError arg0) {
                        Toast.makeText(activity, "抱歉，新闻类型加载失败！", Toast.LENGTH_SHORT).show();
                    }
                });
        requestQueue.add(jsonObjectRequest);
    }

}
