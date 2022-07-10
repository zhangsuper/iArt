package com.gsq.iart.app.image;

import android.content.Context;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.engine.cache.DiskCache;
import com.bumptech.glide.load.engine.cache.ExternalPreferredCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.AppGlideModule;
import com.gsq.iart.data.Constant;

import java.io.InputStream;

import okhttp3.Call;
import okhttp3.OkHttpClient;

@GlideModule
public class MyAppGlideModule extends AppGlideModule {

    public static LruResourceCache lruResourceCache;

    public static DiskCache diskCache;

    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
        super.registerComponents(context, glide, registry);
        // 替换底层网络框架为okhttp3
        // registry.replace(GlideUrl.class, InputStream.class,
        //         new OkHttpUrlLoader.Factory(ProgressManager.getOkHttpClient()));

        OkHttpClient mHttpClient = new OkHttpClient().newBuilder()
                .sslSocketFactory(SSLSocketClient.getSSLSocketFactory(), SSLSocketClient.geX509tTrustManager())
                .hostnameVerifier(SSLSocketClient.getHostnameVerifier())
                .build();

        registry.replace(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory((Call.Factory) mHttpClient));

        // 增加解密图片的方法
//        Downsampler downsampler =
//                new Downsampler(
//                        registry.getImageHeaderParsers(), App.instance.getResources().getDisplayMetrics(), glide.getBitmapPool(), glide.getArrayPool());
//        registry.append(InputStream.class, Bitmap.class, new AesDecryptStreamBitmapDecoder(downsampler, glide.getArrayPool()));
//        registry.append(InputStream.class, Bitmap.class, new DecryptStreamBitmapDecoder(downsampler, glide.getArrayPool()));

    }

    @Override
    public void applyOptions(@NonNull Context context, @NonNull GlideBuilder builder) {
        MemorySizeCalculator calculator = new MemorySizeCalculator.Builder(context)
                .setMemoryCacheScreens(2)
                .build();
        lruResourceCache = new LruResourceCache(calculator.getMemoryCacheSize());
        builder.setMemoryCache(lruResourceCache);
        ExternalPreferredCacheDiskCacheFactory externalPreferredCacheDiskCacheFactory = new ExternalPreferredCacheDiskCacheFactory(context.getApplicationContext(), Constant.GLIDE_CACHE_DIR, Constant.GLIDE_CACHE_SIZE);
        builder.setDiskCache(externalPreferredCacheDiskCacheFactory);
        diskCache = externalPreferredCacheDiskCacheFactory.build();
    }
}
