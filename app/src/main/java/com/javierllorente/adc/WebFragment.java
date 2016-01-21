/*
 * Copyright (C) 2016 Javier Llorente <javier@opensuse.org>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.javierllorente.adc;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebViewFragment;

public class WebFragment extends WebViewFragment {

    WebView mWebView = null;
    WebSettings webSettings = null;
    DRAE drae = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = super.onCreateView(inflater, container, savedInstanceState);

        mWebView = getWebView();
        webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDefaultTextEncodingName("UTF-8");
        mWebView.setWebViewClient(new MiniBrowser());
        boolean isTablet = getResources().getBoolean(R.bool.isTablet);
        int scale = isTablet ? 150 : 200;
        mWebView.setInitialScale(scale);

        drae = new DRAE();

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private boolean isOnline() {
        ConnectivityManager conMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        return conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isConnected();
    }

    private void autoCacheMode() {
        // Use cache if there's no Internet connection
        int cacheMode = isOnline() ? WebSettings.LOAD_DEFAULT : WebSettings.LOAD_CACHE_ELSE_NETWORK;
        mWebView.getSettings().setCacheMode(cacheMode);
    }

    public void query(String query) {

        SearchView searchView = (SearchView) getActivity().findViewById(R.id.action_search);

        autoCacheMode();
        mWebView.loadUrl(DRAE.RAE_URL + drae.encode(query));
        searchView.clearFocus();
    }

    private class MiniBrowser extends WebViewClient {

        private static final String TAG = "MINIBROWSER";

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (!url.contains("rae.es")) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                browserIntent.setData(Uri.parse(url));
                startActivity(browserIntent);
            } else {
                view.loadUrl(url);
                Log.i(TAG, "shouldOverrideUrlLoading()");
            }
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // TODO: show loading progress;
            Log.i(TAG, "onPageStarted()");
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Log.i(TAG, "onPageFinished()");
        }
    }
}