package com.itheima.appstore.vm.fragment;

import android.support.v7.widget.RecyclerView;

import com.google.gson.reflect.TypeToken;
import com.itheima.appstore.Constants;
import com.itheima.appstore.MyApplication;
import com.itheima.appstore.model.net.AppInfo;
import com.itheima.appstore.utils.HttpUtils;
import com.itheima.appstore.views.RecyclerViewFactory;
import com.itheima.appstore.vm.BaseCallBack;
import com.itheima.appstore.vm.CommonCacheProcess;

import com.itheima.appstore.vm.adapter.GameAdapter;

import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Request;

/**
 * Created by itheima.
 * 游戏界面
 */
public class GameFragment extends BaseFragment {
    private String key;
    private List<AppInfo> apps;

    private RecyclerView recyclerView;

    @Override
    protected void showSuccess() {
        recyclerView = RecyclerViewFactory.createVertical();
        recyclerView.setAdapter(new GameAdapter(apps));
        pager.changeViewTo(recyclerView);
    }

    @Override
    protected void loadData() {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("index", 0);

        key = Constants.Http.GAME + ".0";
        String json = CommonCacheProcess.getLocalJson(key);
        if (json != null) {
            parserJson(json);
            pager.runOnUiThread();
        } else {
            loadNetData(params);
        }
    }

    private void loadNetData(HashMap<String, Object> params) {

        Request request = HttpUtils.getRequest(Constants.Http.GAME, params);

        Call call = HttpUtils.getClient().newCall(request);
        // call.download():同步
        // 异步
        call.enqueue(new BaseCallBack(pager) {
            @Override
            protected void onSuccess(String json) {
                // 需要在内存存一份文件
                MyApplication.getProtocolCache().put(key, json);
                CommonCacheProcess.cacheToFile(key, json);
                parserJson(json);
            }
        });
    }

    private void parserJson(String json) {
        pager.isReadData = true;

        apps = MyApplication.getGson().fromJson(json, new TypeToken<List<AppInfo>>() {
        }.getType());

        if (apps != null && apps.size() > 0) {
            pager.isNullData = false;
        } else {
            pager.isNullData = true;
        }
    }
}
