// License: GPL. For details, see LICENSE file.
package org.openstreetmap.josm.data.imagery;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.commons.jcs.access.behavior.ICacheAccess;
import org.openstreetmap.gui.jmapviewer.Tile;
import org.openstreetmap.gui.jmapviewer.interfaces.TileJob;
import org.openstreetmap.gui.jmapviewer.interfaces.TileLoaderListener;
import org.openstreetmap.josm.data.cache.BufferedImageCacheEntry;
import org.openstreetmap.josm.data.preferences.IntegerProperty;

/**
 * Tileloader for WMS based imagery. It is separate to use different ThreadPoolExecutor, as we want
 * to define number of simultaneous downloads for WMS separately
 *
 * @author Wiktor Niesiobędzki
 * @since 8526
 */
public class WMSCachedTileLoader extends TMSCachedTileLoader {

    public static final IntegerProperty THREAD_LIMIT = new IntegerProperty("imagery.wms.loader.maxjobs", 10);

    private static ThreadPoolExecutor DEFAULT_DOWNLOAD_JOB_DISPATCHER = getNewThreadPoolExecutor("WMS-downloader-%d", THREAD_LIMIT.get());


    /**
     * Creates a TileLoader with separate WMS downloader.
     *
     * @param listener that will be notified when tile is loaded
     * @param cache reference
     * @param connectTimeout to tile source
     * @param readTimeout from tile source
     * @param headers to be sent with requests
     * @throws IOException when there is a problem creating cache repository
     */
    public WMSCachedTileLoader(TileLoaderListener listener, ICacheAccess<String, BufferedImageCacheEntry> cache,
            int connectTimeout, int readTimeout, Map<String, String> headers) throws IOException {

        super(listener, cache, connectTimeout, readTimeout, headers);
        setDownloadExecutor(DEFAULT_DOWNLOAD_JOB_DISPATCHER);
    }

    @Override
    public TileJob createTileLoaderJob(Tile tile) {
        return new WMSCachedTileLoaderJob(listener, tile, cache, connectTimeout, readTimeout, headers, getDownloadExecutor());
    }
}
