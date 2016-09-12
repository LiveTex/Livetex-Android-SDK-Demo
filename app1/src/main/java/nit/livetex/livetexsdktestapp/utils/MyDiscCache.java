package nit.livetex.livetexsdktestapp.utils;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;

import java.io.File;

/**
 * Created by user on 7/26/16.
 */
public class MyDiscCache extends UnlimitedDiskCache {

    private boolean ignoreDiskCache;

    public MyDiscCache(File cacheDir) {
        super(cacheDir);
    }


    @Override
    public File get(String key) {
        if (ignoreDiskCache) {
            return new File("fakePath");
        } else {
            return super.get(key);
        }
    }

    public void setIgnoreDiskCache(boolean ignoreDiskCache) {
        this.ignoreDiskCache = ignoreDiskCache;
    }
}