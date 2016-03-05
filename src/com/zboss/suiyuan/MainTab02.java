package com.zboss.suiyuan;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import com.zboss.suiyuan.bean.PictureObj;
import com.zboss.suiyuan.chat.ConnectServer;
import com.zboss.suiyuan.newspic.CardsAdapter;
import com.zboss.suiyuan.newspic.OnRefreshListener;
import com.zboss.suiyuan.newspic.RefreshListView;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MainTab02 extends Fragment {

    private RefreshListView cardsList;

    private CardsAdapter adapter;
    
    private static RequestQueue requestQueue;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.main_tab_02, container, false);
        cardsList = (RefreshListView) rootView.findViewById(R.id.cards_list);
        requestQueue = Volley.newRequestQueue(getActivity());
        setupList();
        return rootView;
    }

    private void setupList() {
        createAdapter();
        getMoreImages(false);
        cardsList.setAdapter(adapter);
        cardsList.setOnRefreshListener(new onDownPullRefresh());
        cardsList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity(), "Clicked on List Item ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private CardsAdapter createAdapter() {
        ArrayList<PictureObj> items = new ArrayList<PictureObj>();
        if (adapter == null) {
            adapter = new CardsAdapter(getActivity(), items);
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
        List<DisplayItem> items = new ArrayList<DisplayItem>();

        String JSONDataUrl = ConnectServer.getMoreImages(PushApplication.DISPLAY_TYPE);
        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "加载图片...", "请稍等...", true, false);

        JsonObjectRequest jsonObjectRequest =
                new JsonObjectRequest(Request.Method.GET, JSONDataUrl, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // 处理返回的JSON数据
                        if (progressDialog.isShowing() && progressDialog != null) {

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
                                    adapter = new CardsAdapter(getActivity(), items);
                                }
                                if (preOrNot) {
                                    adapter.getItems().addAll(items);
                                } else {
                                    adapter.getItems().addAll(0, items);
                                }
                                adapter.notifyDataSetChanged();
                                progressDialog.dismiss();
                            } catch (JSONException e) {
                                Toast.makeText(getActivity(), "抱歉，图片加载失败！", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError arg0) {
                        Toast.makeText(getActivity(), "抱歉，图片加载失败！", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
        requestQueue.add(jsonObjectRequest);
    }
    
    private void loadMoreImages(final boolean preOrNot) {
        List<DisplayItem> items = new ArrayList<DisplayItem>();

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
                                    adapter = new CardsAdapter(getActivity(), items);
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

}
