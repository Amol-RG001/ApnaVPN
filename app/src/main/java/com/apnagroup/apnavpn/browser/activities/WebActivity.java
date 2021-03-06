package com.apnagroup.apnavpn.browser.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.GeolocationPermissions;
import android.webkit.MimeTypeMap;
import android.webkit.PermissionRequest;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.TextSize;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.preference.PreferenceManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.webkit.WebSettingsCompat;
import androidx.webkit.WebViewFeature;

import com.cocosw.bottomsheet.BottomSheet;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.apnagroup.apnavpn.R;
import com.apnagroup.apnavpn.browser.utils.ThemeUtils;
import com.apnagroup.apnavpn.browser.view.CenteredToolbar;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WebActivity extends AppCompatActivity {

    private WebView mWeb;
    private CenteredToolbar mToolbar;
    private SharedPreferences mSharedPreferences;
    private Context mContext;
    private RelativeLayout main;
    private FloatingActionButton button;
    private static final String TAG = WebActivity.class.getSimpleName();
    private String mCM;
    private ValueCallback<Uri> mUM;
    private ValueCallback<Uri[]> mUMA;
    private final static int FCR=1;
    SwipeRefreshLayout swipeRefreshLayout;
    ProgressBar progressBar;
    String current_page_url = "https://google.com/search?q=";

    public static final String PREFERENCES = "PREFERENCES_NAME";
    public static final String WEB_LINKS = "links";
    public static final String WEB_TITLE = "title";

    public static boolean hasPermission(Context context, String... permissions)
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                context!=null && permissions!=null)
        {
            for(String permission : permissions)
            {
                if(ActivityCompat.checkSelfPermission(context,permission) != PackageManager.PERMISSION_GRANTED)
                {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){

        super.onActivityResult(requestCode, resultCode, intent);
        if (Build.VERSION.SDK_INT >= 21) {
            Uri[] results = null;

            if (resultCode== Activity.RESULT_OK) {
                if (requestCode == FCR) {
                    if (null == mUMA) {
                        return;
                    }
                    if (intent == null) {

                        if (mCM != null) {
                            results = new Uri[]{Uri.parse(mCM)};
                        }
                    } else {
                        String dataString = intent.getDataString();
                        if (dataString != null) {
                            results = new Uri[]{Uri.parse(dataString)};
                        }
                    }
                }
            }
            mUMA.onReceiveValue(results);
            mUMA = null;
        } else {
            if (requestCode == FCR) {
                if(null == mUM) return;
                Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
                mUM.onReceiveValue(result);
                mUM = null;
            }
        }
    }
    private class WebViewer extends WebViewClient {

        private String url;

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            String partialUrl = "/store/apps/details?id=";
            if (url.contains(partialUrl)) {
                int pos = url.indexOf(partialUrl) + partialUrl.length();
                String appId = url.substring(pos);

                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("market://details?id=" + appId));
                    WebActivity.this.startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    return true;

                } catch (ActivityNotFoundException e) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    WebActivity.this.startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    return true;
                }
            }

                if (url.contains("geo:")) {
                    Uri gmmIntentUri = Uri.parse(url);
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");

                    if (mapIntent.resolveActivity(getPackageManager()) != null) {
                        startActivity(mapIntent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }
                    return true;
                }

                if (url.contains("https://www.google.com/maps/")) {
                    Uri IntentUri = Uri.parse(url);
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, IntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");

                    if (mapIntent.resolveActivity(getPackageManager()) != null) {
                        startActivity(mapIntent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }
                    return true;
                }

                if (url.startsWith("mailto:")) {
                    startActivity(new Intent(Intent.ACTION_SENDTO, Uri.parse(url)));
                }

                if (url.startsWith("tel:")) {
                    startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(url)));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                } else {
                    view.loadUrl(url);
                }
                if (url.startsWith("sms:")) {
                     handleSMSLink(url);
                     return true;
                }
                return true;
            }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            progressBar.setVisibility(View.VISIBLE);
            current_page_url = url;
            invalidateOptionsMenu();
        }


        @Override
        public void onPageFinished(WebView view, String url) {
            current_page_url = url;
            swipeRefreshLayout.setRefreshing(false);
            invalidateOptionsMenu();
            super.onPageFinished(view, url);
            WebActivity.this.mToolbar.setTitle(view.getTitle());
            invalidateOptionsMenu();
        }


        public void onReceivedError(WebView mWeb, int errorCode, String description, String failingUrl) {
            FrameLayout error = findViewById(R.id.error_page);
            error.setVisibility(View.VISIBLE);
            invalidateOptionsMenu();
            super.onReceivedError(mWeb, errorCode, description, failingUrl);
        }
    }

    @SuppressLint({"SetJavaScriptEnabled", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(ThemeUtils.getCurrentTheme());
        setContentView(R.layout.activity_web);
        main = findViewById(R.id.main);
        FrameLayout error = findViewById(R.id.error_page);
        button = findViewById(R.id.reload);
        button.setOnClickListener(v -> {
            error.setVisibility(View.GONE);
            mWeb.reload();
        });
        mToolbar = findViewById(R.id.toolbar);
        mToolbar.inflateMenu(R.menu.menu_main);
        mToolbar.getMenu().findItem(R.id.turbo).setOnMenuItemClickListener(item -> {
            Snackbar.make(main, R.string.fast_web_descp, Snackbar.LENGTH_SHORT).show();
            return false;
        });
        mToolbar.getMenu().findItem(R.id.incognito).setOnMenuItemClickListener(item -> {
            Snackbar.make(main, R.string.incognito_descp, Snackbar.LENGTH_SHORT).show();
            return false;
        });
        mToolbar.getMenu().findItem(R.id.star).setOnMenuItemClickListener(item -> {
            String message;
            SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
            String jsonLink = sharedPreferences.getString(WEB_LINKS, null);
            String jsonTitle = sharedPreferences.getString(WEB_TITLE, null);

            if (jsonLink != null && jsonTitle != null) {
                Gson gson = new Gson();
                ArrayList<String> linkList = gson.fromJson(jsonLink, new TypeToken<ArrayList<String>>() {
                }.getType());
                ArrayList<String> titleList = gson.fromJson(jsonTitle, new TypeToken<ArrayList<String>>() {
                }.getType());

                if (linkList.contains(current_page_url)) {
                    linkList.remove(current_page_url);
                    titleList.remove(mWeb.getTitle().trim());
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(WEB_LINKS, new Gson().toJson(linkList));
                    editor.putString(WEB_TITLE, new Gson().toJson(titleList));
                    editor.apply();
                    message = getString(R.string.bookmark_remove);

                } else {
                    linkList.add(current_page_url);
                    titleList.add(mWeb.getTitle().trim());
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(WEB_LINKS, new Gson().toJson(linkList));
                    editor.putString(WEB_TITLE, new Gson().toJson(titleList));
                    editor.apply();
                    message = getString(R.string.bookmark_plus);
                }

            } else {
                ArrayList<String> linkList = new ArrayList<>();
                ArrayList<String> titleList = new ArrayList<>();
                linkList.add(current_page_url);
                titleList.add(mWeb.getTitle());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(WEB_LINKS, new Gson().toJson(linkList));
                editor.putString(WEB_TITLE, new Gson().toJson(titleList));
                editor.apply();
                message = getString(R.string.bookmark_plus);
            }
            Snackbar snackbar = Snackbar.make(main, message, Snackbar.LENGTH_LONG);
            snackbar.show();
            invalidateOptionsMenu();
            return true;
        });
        mToolbar.getMenu().findItem(R.id.copy_link).setOnMenuItemClickListener(item -> {
            mWeb.findViewById(R.id.web);
            String url = mWeb.getUrl();
            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("label", url);
            clipboard.setPrimaryClip(clip);
            Snackbar.make(main, R.string.copy, Snackbar.LENGTH_SHORT).show();
            return true;
        });
        mToolbar.getMenu().findItem(R.id.setting).setOnMenuItemClickListener(item -> {
            startActivity(new Intent(this, SettingsActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            return false;
        });
        mToolbar.setNavigationIcon(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("night", false)
                ? R.drawable.ic_arrow_back_white_24dp : R.drawable.ic_arrow_back_black_24dp);
        mToolbar.setNavigationOnClickListener(v -> finish());

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mWeb = findViewById(R.id.web);
        progressBar = findViewById(R.id.progressBar);
        registerForContextMenu(mWeb);
        mWeb.getSettings().setJavaScriptEnabled(true);
        mWeb.getSettings().setLoadsImagesAutomatically(true);
        mWeb.getSettings().setSupportZoom(false);
        mWeb.getSettings().setBuiltInZoomControls(false);
        mWeb.getSettings().setDisplayZoomControls(false);
        mWeb.getSettings().setLoadWithOverviewMode(true);
        mWeb.getSettings().setUseWideViewPort(true);
        mWeb.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        mWeb.setFocusable(true);
        mWeb.setFocusableInTouchMode(true);
        mWeb.setBackgroundColor(Color.TRANSPARENT);
        mWeb.clearCache(false);
        mWeb.getSettings().setLoadWithOverviewMode(true);
        mWeb.getSettings().setUseWideViewPort(true);
        mWeb.getSettings().setLoadsImagesAutomatically(true);
        mWeb.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWeb.getSettings().setAppCacheEnabled(false);
        swipeRefreshLayout = findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(() -> mWeb.reload());
        if (Build.VERSION.SDK_INT >= 23 && (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(WebActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.RECORD_AUDIO}, 1);
        }
        mWeb.setWebViewClient(new WebViewer());
        mWeb.setWebChromeClient(new WebChromeClient() {
            private View mCustomView;
            private CustomViewCallback mCustomViewCallback;
            private int mOriginalOrientation;
            private int mOriginalSystemUiVisibility;

            public Bitmap getDefaultVideoPoster()
            {
                if (mCustomView == null) {
                    return null;
                }
                return BitmapFactory.decodeResource(getApplicationContext().getResources(), 2130837573);
            }

            public void onHideCustomView()
            {
                ((FrameLayout)getWindow().getDecorView()).removeView(this.mCustomView);
                this.mCustomView = null;
                getWindow().getDecorView().setSystemUiVisibility(this.mOriginalSystemUiVisibility);
                setRequestedOrientation(this.mOriginalOrientation);
                this.mCustomViewCallback.onCustomViewHidden();
                this.mCustomViewCallback = null;
            }

            public void onShowCustomView(View paramView, CustomViewCallback paramCustomViewCallback)
            {
                if (this.mCustomView != null)
                {
                    onHideCustomView();
                    return;
                }
                this.mCustomView = paramView;
                this.mOriginalSystemUiVisibility = getWindow().getDecorView().getSystemUiVisibility();
                this.mOriginalOrientation = getRequestedOrientation();
                this.mCustomViewCallback = paramCustomViewCallback;
                ((FrameLayout)getWindow().getDecorView()).addView(this.mCustomView, new FrameLayout.LayoutParams(-1, -1));
                getWindow().getDecorView().setSystemUiVisibility(3846);
            }

            @Override
            public void onProgressChanged(WebView view, int progress) {
                progressBar.setProgress(progress);

                if(progress < 100 && error.getVisibility() == View.GONE) {
                    progressBar.setVisibility(View.VISIBLE);
                } else if (progress == 100) {
                    progressBar.setVisibility(View.GONE);
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onPermissionRequest(final PermissionRequest request) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    request.grant(request.getResources());
                }
            }

            public boolean onShowFileChooser(
                    WebView webView, ValueCallback<Uri[]> filePathCallback,
                    FileChooserParams fileChooserParams) {
                if (mUMA != null) {
                    mUMA.onReceiveValue(null);
                }
                mUMA = filePathCallback;
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(WebActivity.this.getPackageManager()) != null) {
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                        takePictureIntent.putExtra("PhotoPath", mCM);
                    } catch (IOException ex) {
                        Log.e(TAG, "Image file creation failed", ex);
                    }
                    if (photoFile != null) {
                        mCM = "file:" + photoFile.getAbsolutePath();
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                    } else {
                        takePictureIntent = null;
                    }
                }
                Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
                contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
                contentSelectionIntent.setType("*/*");
                Intent[] intentArray;
                if (takePictureIntent != null) {
                    intentArray = new Intent[]{takePictureIntent};
                } else {
                    intentArray = new Intent[0];
                }

                Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
                chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
                chooserIntent.putExtra(Intent.EXTRA_TITLE, R.string.img_chooser);
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
                startActivityForResult(chooserIntent, FCR);
                return true;
            }

            private File createImageFile() throws IOException {
                @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageFileName = "img_" + timeStamp + "_";
                File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                return File.createTempFile(imageFileName, ".jpg", storageDir);
            }

            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
            }

        });
        CookieManager.getInstance().setAcceptCookie(true);
        WebSettings webset = mWeb.getSettings();
        webset.setJavaScriptEnabled(true);
        webset.setAllowFileAccess(true);
        webset.setBuiltInZoomControls(false);
        webset.setSupportZoom(true);
        webset.setUseWideViewPort(false);
        webset.setDomStorageEnabled(true);
        webset.setAllowFileAccess(true);
        Intent intents = getIntent();
        String search_bar = intents.getStringExtra("text");
        mWeb.loadUrl(current_page_url + search_bar);
        Intent intent_url = getIntent();
        String url_open = intent_url.getStringExtra("page_url");
        mWeb.loadUrl(url_open);
        String loadurl = getIntent().getDataString();
        if (loadurl != null) {
            mWeb.loadUrl(loadurl);
        } else {
            mWeb.loadUrl("https://");
        }
        mWeb.setDownloadListener((url, userAgent, contentDisposition, mimeType, contentLength) -> {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setMimeType(mimeType);
            String cookies = CookieManager.getInstance().getCookie(url);
            request.addRequestHeader("cookie", cookies);
            request.addRequestHeader("User-Agent", userAgent);
            request.setTitle(URLUtil.guessFileName(url, contentDisposition, mimeType));
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(url, contentDisposition, mimeType));
            DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
            dm.enqueue(request);
            Snackbar.make(main, R.string.download_toast, Snackbar.LENGTH_SHORT).show();
        });

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        final WebView.HitTestResult wavierHottestResult = mWeb.getHitTestResult();
        final String DownloadImageUrl = wavierHottestResult.getExtra();
        if(wavierHottestResult.getType() == WebView.HitTestResult.IMAGE_TYPE ||
                wavierHottestResult.getType() == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE)
        {
            if(URLUtil.isNetworkUrl(DownloadImageUrl))
            {

                new BottomSheet.Builder(this, ThemeUtils.getCurrentBottomTheme()).sheet(R.menu.menu_press).listener((dialog, which) -> {
                    switch (which) {
                        case R.id.copy_link_url:
                            String copyimageurl = wavierHottestResult.getExtra();
                            ClipboardManager manager = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("label",copyimageurl);
                            manager.setPrimaryClip(clip);
                            Snackbar.make(main, R.string.copy, Snackbar.LENGTH_SHORT).show();
                            break;
                        case R.id.send_img:
                            Picasso.get().load(DownloadImageUrl).into(new Target() {
                                @Override
                                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                    Intent i = new Intent(Intent.ACTION_SEND);
                                    i.setType("image/*");
                                    i.putExtra(Intent.EXTRA_STREAM, geologicalBitmaUri(bitmap));
                                    startActivity(Intent.createChooser(i,getString(R.string.send_image)));
                                }
                                @Override
                                public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                                }
                                @Override
                                public void onPrepareLoad(Drawable placeHolderDrawable) {
                                }
                            });
                            break;
                        case R.id.download_img:
                            int Permission_all = 1;
                            String Permission[] = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
                            if(!hasPermission(WebActivity.this,Permission))
                            {
                                ActivityCompat.requestPermissions(WebActivity.this,Permission,Permission_all);
                            }
                            else
                            {
                                String filename = "";
                                String type = null;
                                String Mimetype = MimeTypeMap.getFileExtensionFromUrl(DownloadImageUrl);
                                filename = URLUtil.guessFileName(DownloadImageUrl,DownloadImageUrl,Mimetype);
                                if(Mimetype!=null)
                                {
                                    type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(Mimetype);
                                }
                                if(type==null)
                                {
                                    filename = filename.replace(filename.substring(filename.lastIndexOf(".")),".png");
                                    type = "image/*";
                                }
                                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(DownloadImageUrl));
                                request.allowScanningByMediaScanner();
                                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,filename);
                                DownloadManager managers = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
                                managers.enqueue(request);
                                Snackbar.make(main, R.string.download, Snackbar.LENGTH_SHORT).show();
                            }
                            break;
                    }
                }).show();

        }
        }
    }

    public Uri geologicalBitmaUri(Bitmap bmp)
    {
        Uri bmpuri = null;
        try{
            File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),"scrimmage"+ System.currentTimeMillis()+".png");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG,90,out);
            out.close();
            bmpuri = FileProvider.getUriForFile(getApplicationContext(),"com.ultraspeed.vpnultra.browser.provider",file);
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        return bmpuri;
    }

    protected void handleSMSLink(String url) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        String phoneNumber = url.split("[:?]")[1];

        if (!TextUtils.isEmpty(phoneNumber)){
            intent.setData(Uri.parse("smsto:" + phoneNumber));

        } else {
            intent.setData(Uri.parse("smsto:"));
        }

        if (url.contains("body=")) {
            String smsBody = url.split("body=")[1];

            try {
                smsBody = URLDecoder.decode(smsBody,"UTF-8");
            }catch (UnsupportedEncodingException e){
                e.printStackTrace();
            }

            if (!TextUtils.isEmpty(smsBody)){
                intent.putExtra("sms_body",smsBody);
            }
        }

        if (intent.resolveActivity(getPackageManager())!=null) {
            startActivity(intent);
        } else {
            Snackbar.make(main, R.string.sms_no, Snackbar.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onResume() {
        super.onResume();

        Boolean orientation = mSharedPreferences.getBoolean(getString(R.string.orientation_t), false);
        Boolean statusbar = mSharedPreferences.getBoolean(getString(R.string.status_t), false);
        Boolean screen = mSharedPreferences.getBoolean(getString(R.string.screen_t), false);
        Boolean pc_mode = mSharedPreferences.getBoolean(getString(R.string.pc_t), false);
        Boolean incognito = mSharedPreferences.getBoolean(getString(R.string.incognito_t), false);
        Boolean geo = mSharedPreferences.getBoolean(getString(R.string.geo_t), true);
        Boolean password = mSharedPreferences.getBoolean(getString(R.string.pass_t), false);
        Boolean textsize = mSharedPreferences.getBoolean(getString(R.string.font_t), false);
        Boolean zoom = mSharedPreferences.getBoolean(getString(R.string.zoom_t), true);
        Boolean dark = mSharedPreferences.getBoolean(getString(R.string.web_dark), false);
        Boolean web_fast = mSharedPreferences.getBoolean(getString(R.string.fast_web), false);
        Boolean web_cache = mSharedPreferences.getBoolean(getString(R.string.web_cache), false);
        Boolean text_wrap = mSharedPreferences.getBoolean(getString(R.string.text_wrap_t), false);
        Boolean img_no_load = mSharedPreferences.getBoolean(getString(R.string.img_no_load_t), false);
        Boolean supportJavaScript = mSharedPreferences.getBoolean(getString(R.string.js_t), true);
        Boolean cookies = mSharedPreferences.getBoolean(getString(R.string.cookie_t), true);
        if (orientation) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        }
        if (statusbar) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        if (screen) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        if (pc_mode) {
            mWeb.getSettings().setUserAgentString("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.87 Safari/537.36");
        } else {
            mWeb.getSettings().setUserAgentString("Mozilla/5.0 (Linux; Android 10) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.111 Mobile Safari/537.36");
        }
        if (incognito) {
            mWeb.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
            mWeb.getSettings().setAppCacheEnabled(false);
            mWeb.clearHistory();
            mWeb.clearCache(true);
            mWeb.clearFormData();
            mWeb.getSettings().setSavePassword(false);
            mWeb.getSettings().setSaveFormData(false);
            mWeb.getSettings().setGeolocationEnabled(true);
            MenuItem incg = mToolbar.getMenu().findItem(R.id.incognito);
            incg.setVisible(true);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        } else {
            mWeb.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
            mWeb.getSettings().setAppCacheEnabled(true);
            mWeb.clearCache(false);
            mWeb.getSettings().setSavePassword(true);
            mWeb.getSettings().setSaveFormData(true);
            mWeb.getSettings().setGeolocationEnabled(true);
            MenuItem incg = mToolbar.getMenu().findItem(R.id.incognito);
            incg.setVisible(false);
            getWindow().setFlags(WindowManager.LayoutParams.MEMORY_TYPE_NORMAL, WindowManager.LayoutParams.MEMORY_TYPE_NORMAL);
        }
        if (geo) {
            mWeb.getSettings().setGeolocationEnabled(true);
        } else {
            mWeb.getSettings().setGeolocationEnabled(false);
        }
        if (password) {
            mWeb.getSettings().setSavePassword(false);
        } else {
            mWeb.getSettings().setSavePassword(true);
        }
        if (textsize) {
            WebSettings webset = mWeb.getSettings();
            webset.setTextSize(TextSize.LARGER);
        } else {
            WebSettings webset = mWeb.getSettings();
            webset.setTextSize(TextSize.NORMAL);
        }
        if (zoom) {
            mWeb.getSettings().setSupportZoom(true);
            mWeb.getSettings().setBuiltInZoomControls(true);
            mWeb.getSettings().setDisplayZoomControls(false);
        } else {
            mWeb.getSettings().setSupportZoom(false);
            mWeb.getSettings().setBuiltInZoomControls(false);
            mWeb.getSettings().setDisplayZoomControls(false);
        }
        if (dark) {
            if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
                WebSettingsCompat.setForceDark(mWeb.getSettings(), WebSettingsCompat.FORCE_DARK_ON);
            }
        } else {
            if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
                WebSettingsCompat.setForceDark(mWeb.getSettings(), WebSettingsCompat.FORCE_DARK_OFF);
            }
        }
        if (web_fast) {
            mWeb.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
            mWeb.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
            mWeb.getSettings().setAppCacheEnabled(true);
            MenuItem turbo = mToolbar.getMenu().findItem(R.id.turbo);
            turbo.setVisible(true);
        } else {
            mWeb.getSettings().setRenderPriority(WebSettings.RenderPriority.NORMAL);
            mWeb.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
            mWeb.getSettings().setAppCacheEnabled(false);
            MenuItem turbo = mToolbar.getMenu().findItem(R.id.turbo);
            turbo.setVisible(false);
        }
        if (web_cache) {
            mWeb.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
            mWeb.getSettings().setAppCacheEnabled(true);
        } else {
            mWeb.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
            mWeb.getSettings().setAppCacheEnabled(false);
        }
        if (text_wrap) {
            mWeb.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
        } else {
            mWeb.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        }
        if (img_no_load) {
            mWeb.getSettings().setDomStorageEnabled(false);
            mWeb.getSettings().setLoadsImagesAutomatically(false);
        } else {
            mWeb.getSettings().setDomStorageEnabled(true);
            mWeb.getSettings().setLoadsImagesAutomatically(true);
        }
        if (cookies) {
            CookieManager.getInstance().setAcceptCookie(true);
        } else {
            CookieManager.getInstance().setAcceptCookie(false);
        }
        mWeb.getSettings().setJavaScriptEnabled(supportJavaScript);
        mWeb.getSettings().setJavaScriptCanOpenWindowsAutomatically(supportJavaScript);

    }

    @Override
    public void onBackPressed() {
        if (mWeb.canGoBack()) {
            mWeb.goBack();
            FrameLayout error = findViewById(R.id.error_page);
            error.setVisibility(View.GONE);
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

}