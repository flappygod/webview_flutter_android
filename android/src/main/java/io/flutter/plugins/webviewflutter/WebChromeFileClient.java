package io.flutter.plugins.webviewflutter;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.PluginRegistry;

//file upload
public class WebChromeFileClient extends WebChromeClient implements PluginRegistry.ActivityResultListener {

    //upload
    private ValueCallback<Uri> mUploadMessage;

    public ValueCallback<Uri[]> mUploadMessageForAndroid5;

    public final static int FILECHOOSER_RESULTCODE_ANDROID_EARLY = 1;

    public final static int FILECHOOSER_RESULTCODE_ANDROID_NOWER = 2;


    public WebChromeFileClient() {
        super();
        initBinding();
    }

    //init Binding
    private void initBinding() {
        ActivityPluginBinding binding = WebViewFlutterPlugin.actBinding;
        if (binding != null) {
            binding.addActivityResultListener(this);
        }
    }


    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == FILECHOOSER_RESULTCODE_ANDROID_EARLY) {
            if (null == mUploadMessage)
                return false;
            Uri result = intent == null || resultCode != RESULT_OK ? null
                    : intent.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;

        } else if (requestCode == FILECHOOSER_RESULTCODE_ANDROID_NOWER) {
            if (null == mUploadMessageForAndroid5)
                return false;
            Uri result = (intent == null || resultCode != RESULT_OK) ? null : intent.getData();
            if (result != null) {
                mUploadMessageForAndroid5.onReceiveValue(new Uri[]{result});
            } else {
                mUploadMessageForAndroid5.onReceiveValue(new Uri[]{});
            }
            mUploadMessageForAndroid5 = null;
        }
        return false;
    }


    //For Early Android
    private void openFileChooserImpl(ValueCallback<Uri> uploadMsg) {
        mUploadMessage = uploadMsg;
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        Activity activity = WebViewFlutterPlugin.activity;
        if (activity != null) {
            activity.startActivityForResult(Intent.createChooser(intent, "Choose file"), FILECHOOSER_RESULTCODE_ANDROID_EARLY);
        }
    }

    //For android5.0
    private void openFileChooserImplForAndroid5(ValueCallback<Uri[]> uploadMsg) {
        mUploadMessageForAndroid5 = uploadMsg;
        Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
        contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
        contentSelectionIntent.setType("image/*");
        Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
        chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
        chooserIntent.putExtra(Intent.EXTRA_TITLE, "Choose file");
        Activity activity = WebViewFlutterPlugin.activity;
        if (activity != null) {
            activity.startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE_ANDROID_NOWER);
        }
    }


    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback,
                                     FileChooserParams fileChooserParams) {
        openFileChooserImplForAndroid5(filePathCallback);
        return true;
    }

    public void openFileChooser(ValueCallback<Uri> uploadMsg,
                                String acceptType,
                                String capture) {
        openFileChooserImpl(uploadMsg);
    }

    public void openFileChooser(ValueCallback<Uri> uploadMsg,
                                String acceptType) {
        openFileChooserImpl(uploadMsg);
    }

    public void openFileChooser(ValueCallback<Uri> uploadMsg) {
        openFileChooserImpl(uploadMsg);
    }


}
