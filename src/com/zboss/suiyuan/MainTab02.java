package com.zboss.suiyuan;

import java.util.ArrayList;

import com.zboss.suiyuan.bean.PictureObj;
import com.zboss.suiyuan.newspic.CardsAdapter;
import com.zboss.suiyuan.newspic.OnRefreshListener;
import com.zboss.suiyuan.newspic.RefreshListView;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.main_tab_02, container, false);
        cardsList = (RefreshListView) rootView.findViewById(R.id.cards_list);
        setupList();
        return rootView;
    }

    private void setupList() {
        createAdapter();
        cardsList.setAdapter(adapter);
        cardsList.setOnItemClickListener(new ListItemClickListener());
        cardsList.setOnRefreshListener(new onDownPullRefresh());
    }

    private CardsAdapter createAdapter() {
        ArrayList<PictureObj> items = new ArrayList<PictureObj>();

        for (int i = 0; i < 5; i++) {
            PictureObj obj = new PictureObj();
            obj.setTitle("Text for List Item " + i);
            obj.setPic("http://content.52pk.com/files/100623/2230_102437_1_lit.jpg");
            items.add(obj);
        }

        if (adapter == null) {
            adapter = new CardsAdapter(getActivity(), items, new ListItemButtonClickListener());
        }
        return adapter;
    }

    private final class ListItemButtonClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            // for (int i = cardsList.getFirstVisiblePosition(); i <= cardsList
            // .getLastVisiblePosition(); i++) {
            // if (v == cardsList.getChildAt(
            // i - cardsList.getFirstVisiblePosition()).findViewById(
            // R.id.list_item_card_button_1)) {
            // // PERFORM AN ACTION WITH THE ITEM AT POSITION i
            // Toast.makeText(getActivity(),
            // "Clicked on Left Action Button of List Item " + i,
            // Toast.LENGTH_SHORT).show();
            // } else if (v == cardsList.getChildAt(
            // i - cardsList.getFirstVisiblePosition()).findViewById(
            // R.id.list_item_card_button_2)) {
            // // PERFORM ANOTHER ACTION WITH THE ITEM AT POSITION i
            // Toast.makeText(getActivity(),
            // "Clicked on Right Action Button of List Item " + i,
            // Toast.LENGTH_SHORT).show();
            // }
            // }
        }
    }

    private final class ListItemClickListener implements OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Toast.makeText(getActivity(), "Clicked on List Item " + position, Toast.LENGTH_SHORT).show();
        }
    }

    private final class onDownPullRefresh implements OnRefreshListener {
        @Override
        public void onDownPullRefresh() {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void...params) {
                    SystemClock.sleep(2000);
                    PictureObj obj = new PictureObj();
                    obj.setTitle("加载的新项目");
                    obj.setPic("http://h.hiphotos.baidu.com/zhidao/wh%3D450%2C600/sign=3dc4538262d0f703e6e79dd83dca7d0b/7a899e510fb30f24f570e996c895d143ac4b03b8.jpg");
                    adapter.getItems().add(0, obj);
                    return null;
                }

                @Override
                protected void onPostExecute(Void result) {
                    adapter.notifyDataSetChanged();
                    cardsList.hideHeaderView();
                }
            }.execute(new Void[] {});
        }

        @Override
        public void onLoadingMore() {
            new AsyncTask<Void, Void, Void>() {

                @Override
                protected Void doInBackground(Void...params) {
                    SystemClock.sleep(2000);
                    PictureObj obj = new PictureObj();
                    obj.setTitle("加载的新项目");
                    obj.setPic("http://h.hiphotos.baidu.com/zhidao/wh%3D450%2C600/sign=3dc4538262d0f703e6e79dd83dca7d0b/7a899e510fb30f24f570e996c895d143ac4b03b8.jpg");
                    adapter.getItems().add(obj);
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

}
