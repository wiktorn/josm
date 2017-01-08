// License: GPL. For details, see LICENSE file.
package org.openstreetmap.josm.data.cache;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * @author user
 *
 * @param <T>
 */
public class TileDownloadTask<T> extends FutureTask<T> {

    private JCSCachedTileLoaderJob job;

    /**
     * @param callable
     */
    public TileDownloadTask(Callable<T> callable) {
        super(callable);
    }

    /**
     * @param r
     * @param t
     */
    public TileDownloadTask(Runnable r, T t) {
        super(r, t);
        if (r instanceof JCSCachedTileLoaderJob) {
            this.job = (JCSCachedTileLoaderJob) r;
        }
    }

    /**
     * @return
     * @throws IOException
     */
    public URL getUrl() throws IOException {
        return job.getUrl();
    }

    public JCSCachedTileLoaderJob<?, ?> getTileLoaderJob() {
        return job;
    }

}
