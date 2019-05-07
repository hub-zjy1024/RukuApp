package com.zjy.north.rukuapp.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.zjy.north.rukuapp.MyApp;
import com.zjy.north.rukuapp.R;
import com.zjy.north.rukuapp.activity.base.BaseMActivity;
import com.zjy.north.rukuapp.task.TaskManager;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Hashtable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import utils.common.MyFileUtils;
import utils.common.UpdateClient;
import utils.framwork.MyToast;

public class AboutActivity extends BaseMActivity {

    private Handler mHandler = new Handler();
    private ProgressDialog downPd;
    private TextView tvNewVersion;
    final String updateUrl = UpdateClient.downUrl;
    final String localName = UpdateClient.saveName;
    final String checkUrl = UpdateClient.checkUpdateURL;
    private ImageView updateIv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        tvNewVersion = (TextView) findViewById(R.id.activity_about_tv_newversion);
        TextView tvVersion = (TextView) findViewById(R.id.activity_about_tv_version);
        Button btnDonloadNew = (Button) findViewById(R.id.activity_about_btn_downloadnew);
         updateIv = (ImageView) findViewById(R.id.iv_about_update_qr);

        Button btnCheck = (Button) findViewById(R.id.activity_about_btn_check);
        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downPd.show();
                getNewVersion();
            }
        });
        downPd = new ProgressDialog(this);
        downPd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        downPd.setTitle("更新");
        downPd.setMax(100);
        downPd.setMessage("下载中");
        downPd.setProgress(0);
        downPd.show();

        getNewVersion();

        btnDonloadNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //必须设定进图条样式
                downPd.show();
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        String errMsg = "";
                        try {
                            updateAPK(mContext, mHandler, updateUrl);
                        } catch (final IOException e) {
                            errMsg = "下载更新失败," + e.getMessage();
                            e.printStackTrace();
                        }
                        final String finalErrMsg = errMsg;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (!"".equals(finalErrMsg)) {
                                    showMsgDialog(finalErrMsg);
                                }

                                downPd.cancel();
                            }
                        });
                    }
                }.start();
            }
        });
        PackageManager pm = getPackageManager();

        PackageInfo info = null;
        try {
            info = pm.getPackageInfo(getPackageName(), PackageManager.GET_ACTIVITIES);
            if (info != null) {
                tvVersion.setText("版本:v" + info.versionCode);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void init() {

    }

    @Override
    public void setListeners() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateIv.measure(0, 0);
        int w = updateIv.getMeasuredWidth();
        int h = updateIv.getMeasuredHeight();
        if (w == 0 || h == 0) {
            MyToast.showToast(this, "获取Iv大小失败");
            return;
        }
        Runnable mkQr = new Runnable() {
            @Override
            public void run() {
                createQRcodeImage(updateUrl, updateIv);
            }
        };
        TaskManager.getInstance().execute(mkQr);
    }

    public void createQRcodeImage(String url, final ImageView im1) {
        int w = im1.getMeasuredWidth();
        int h = im1.getMeasuredHeight();
        try {
            //判断URL合法性
            if (url == null || "".equals(url)) {
                return;
            }
            Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            //图像数据转换，使用了矩阵转换
            BitMatrix bitMatrix = new QRCodeWriter().encode(url, BarcodeFormat.QR_CODE, w, h, hints);
            int[] pixels = new int[w * h];
            //下面这里按照二维码的算法，逐个生成二维码的图片，
            //两个for循环是图片横列扫描的结果
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * w + x] = 0xff000000;
                    } else {
                        pixels[y * w + x] = 0xffffffff;
                    }
                }
            }
            //生成二维码图片的格式，使用ARGB_8888
            final Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, w, 0, 0, w, h);
            //显示到我们的ImageView上面
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    im1.setImageBitmap(bitmap);
                }
            });
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    public void updateAPK(final Context context, Handler mHandler, String downUrl) throws IOException {
        URL url = new URL(downUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(3 * 1000);
        conn.setReadTimeout(30000);
        if (conn.getResponseCode() == 200) {
            InputStream is = conn.getInputStream();
            int size = conn.getContentLength();
            File targetDir = MyFileUtils.getFileParent();
            File file1 = new File(targetDir, localName);
            if(!file1.getParentFile().exists()){
                file1.getParentFile().mkdirs();
            }
            FileOutputStream fos = new FileOutputStream(file1);
            int len = 0;
            int hasRead = 0;
            int percent = 0;
            byte[] buf = new byte[1024];
            while ((len = is.read(buf)) != -1) {
                hasRead = hasRead + len;
                percent = (hasRead * 100) / size;
                final int tempPercent = percent;
                if (hasRead < 0) {
                    Log.e("zjy", "MainActivity.java->updateAPK(): hasRead==" + hasRead);
                }
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        int percent = tempPercent;
                        if (percent < 0) {
                            return;
                        }
                        downPd.setProgress(percent);
                        if (percent == 100) {
                            downPd.cancel();
                            MyToast.showToast(context, "下载完成");
                        }
                    }
                });
                fos.write(buf, 0, len);
            }
            fos.flush();
            is.close();
            fos.close();
            MyApp.myLogger.writeInfo("update download");
            Intent intent = new Intent(Intent.ACTION_VIEW);
            if (file1.exists()) {
                String mimeTypeFromExtension = MimeTypeMap.getSingleton().getMimeTypeFromExtension(".apk");

                Uri uri = Uri.fromFile(file1);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                    uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider",file1);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//增加读写权限
                }
                intent.setDataAndType(uri, "application/vnd.android.package-archive");
                context.startActivity(intent);
            } else {
                throw new FileNotFoundException();
            }
        }
    }

    public void getNewVersion() {
        Runnable mRun = new Runnable() {
            @Override
            public void run() {
                String errMsg = "";

                HashMap<String, String> updateInfo = null;
                try {
                    updateInfo = getUpdateXml(checkUrl);
                } catch (IOException e) {
                    e.printStackTrace();
                    errMsg = "连接失败," + e.getMessage();
                }
                final HashMap<String, String> finalUpdateInfo = updateInfo;
                final String finalErrMsg = errMsg;
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (finalUpdateInfo != null) {
                            final String sCode = finalUpdateInfo.get("code");
                            final String sContent = finalUpdateInfo.get("content");
                            final String sDate = finalUpdateInfo.get("date");
                            tvNewVersion.setText("v" + sCode);
                            MyToast.showToast(mContext, "已获取最新版本信息");
                        }else{
                            tvNewVersion.setText("获取最新版本信息失败," + finalErrMsg);
                        }
                        downPd.cancel();
                    }
                });

            }
        };
        TaskManager.getInstance().execute(mRun);
    }

    public static HashMap<String, String> getUpdateXml(String url) throws IOException {
        URL urll = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) urll.openConnection();
        conn.setConnectTimeout(30 * 1000);
        conn.setReadTimeout(30 * 1000);
        if (conn.getResponseCode() == 200) {
            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"));
            String len = reader.readLine();
            StringBuilder stringBuilder = new StringBuilder();
            while (len != null) {
                stringBuilder.append(len);
                len = reader.readLine();
            }
            String res = stringBuilder.toString();
            HashMap<String, String> result = new HashMap<>();
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            try {
                DocumentBuilder docBuilder = factory.newDocumentBuilder();
                ByteArrayInputStream bin = new ByteArrayInputStream(res.getBytes("utf-8"));
                Document xmlDoc = docBuilder.parse(bin);
                NodeList newVersion = xmlDoc.getElementsByTagName("latest-version");
                Node item = newVersion.item(0);
                NodeList childNodes = item.getChildNodes();
                for (int i = 0; i < childNodes.getLength(); i++) {
                    Node n = childNodes.item(i);
                    String nName = n.getNodeName();
                    if (nName.equals("code")) {
                        result.put("code", n.getTextContent());
                    } else if (nName.equals("content")) {
                        result.put("content", n.getTextContent());
                    } else if (nName.equals("date")) {
                        result.put("date", n.getTextContent());
                    }
                }
                return result;
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        } else {
            return null;
        }
    }
}
