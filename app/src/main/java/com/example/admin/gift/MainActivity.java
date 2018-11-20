package com.example.admin.gift;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.opensource.svgaplayer.SVGADrawable;
import com.opensource.svgaplayer.SVGADynamicEntity;
import com.opensource.svgaplayer.SVGAImageView;
import com.opensource.svgaplayer.SVGAParser;
import com.opensource.svgaplayer.SVGAVideoEntity;

import org.jetbrains.annotations.NotNull;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button bt;
    Button bt1;
    SVGAImageView svg;
    String name = "good.svga";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bt = findViewById(R.id.bt);
        bt1 = findViewById(R.id.bt1);
        svg = findViewById(R.id.svg);
        bt.setOnClickListener(this);
        bt1.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt:
                setLocalResource();
                break;
            case R.id.bt1:
                setNetWorkSource();
                break;
        }
    }

    /**
     * @param filePath sd卡上的文件路径
     */
    private void setSdResource(String filePath) {
        try {
            svg.clearAnimation();
            SVGAParser parser = new SVGAParser(this);
            parser.parse(new FileInputStream(filePath), "key", new SVGAParser.ParseCompletion() {
                @Override
                public void onComplete(@NotNull SVGAVideoEntity videoItem) {
                    SVGADrawable drawable = new SVGADrawable(videoItem, new SVGADynamicEntity());
                    svg.setLoops(1);                //只播放一次，不循环
                    svg.setClearsAfterStop(true);   //播放停止后清除
                    svg.setImageDrawable(drawable);
                    svg.startAnimation();
                }

                @Override
                public void onError() {

                }
            }, true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置本地资源
     */
    private void setLocalResource() {
        try {
            svg.clearAnimation();

            if (name.equals("good.svga")) {
                name = "sample-2_0.svga";
            } else {
                name = "good.svga";
            }
            SVGAParser parser = new SVGAParser(this);
            parser.parse(name, new SVGAParser.ParseCompletion() {
                @Override
                public void onComplete(@NotNull SVGAVideoEntity videoItem) {
                    SVGADrawable drawable = new SVGADrawable(videoItem, new SVGADynamicEntity());
                    svg.setLoops(1);                //只播放一次，不循环
                    svg.setClearsAfterStop(true);   //播放停止后清除
                    svg.setImageDrawable(drawable);
                    svg.startAnimation();
                }

                @Override
                public void onError() {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置网络资源
     */
    private void setNetWorkSource() {
        try {
            svg.clearAnimation();

            SVGAParser parser = new SVGAParser(this);
            resetDownloader(parser);
            parser.parse(new URL("https://github.com/yyued/SVGA-Samples/blob/master/kingset.svga?raw=true"), new SVGAParser.ParseCompletion() {
                @Override
                public void onComplete(@NotNull SVGAVideoEntity videoItem) {
                    SVGADrawable drawable = new SVGADrawable(videoItem, new SVGADynamicEntity());
                    svg.setLoops(1);                //只播放一次，不循环
                    svg.setClearsAfterStop(true);   //播放停止后清除
                    svg.setImageDrawable(drawable);
                    svg.startAnimation();
                }

                @Override
                public void onError() {

                }
            });
        } catch (Exception e) {
            System.out.print(true);
        }
    }

    private void resetDownloader(SVGAParser parser) {
        parser.setFileDownloader(new SVGAParser.FileDownloader() {
            @Override
            public void resume(final URL url, final Function1<? super InputStream, Unit> complete, final Function1<? super Exception, Unit> failure) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        OkHttpClient client = new OkHttpClient();
                        Request request = new Request.Builder().url(url).get().build();
                        try {
                            Response response = client.newCall(request).execute();
                            complete.invoke(response.body().byteStream());
                        } catch (IOException e) {
                            e.printStackTrace();
                            failure.invoke(e);
                        }
                    }
                }).start();
            }
        });
    }
}
