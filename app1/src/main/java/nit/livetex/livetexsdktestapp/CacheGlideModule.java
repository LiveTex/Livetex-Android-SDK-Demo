package nit.livetex.livetexsdktestapp;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Keep;

import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;

@Keep
@GlideModule
public class CacheGlideModule extends AppGlideModule {

    @Override
    public void applyOptions(@androidx.annotation.NonNull Context context, @androidx.annotation.NonNull GlideBuilder builder) {
        long diskCacheSizeBytes = 50L * 1024L * 1024L; // 50 MB
        builder.setDiskCache(new InternalCacheDiskCacheFactory(context, diskCacheSizeBytes));
        builder.setLogLevel(Log.ERROR);
        builder.setDefaultRequestOptions(
                new RequestOptions()
                        //.placeholder(R.drawable.placeholder)
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .format(DecodeFormat.PREFER_RGB_565));
    }
}