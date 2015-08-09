// License: GPL. For details, see Readme.txt file.
package org.openstreetmap.gui.jmapviewer;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Logger;

import org.openstreetmap.gui.jmapviewer.interfaces.TileCache;
import org.openstreetmap.gui.jmapviewer.interfaces.TileSource;

/**
 * {@link TileCache} implementation that stores all {@link Tile} objects in
 * memory up to a certain limit ({@link #getCacheSize()}). If the limit is
 * exceeded the least recently used {@link Tile} objects will be deleted.
 *
 * @author Jan Peter Stotz
 */
public class MemoryTileCache implements TileCache {

    protected static final Logger log = Logger.getLogger(MemoryTileCache.class.getName());

    /**
     * Default cache size
     */
    protected int cacheSize;

    protected final Map<String, CacheEntry> hash;

    /**
     * List of all tiles in their last recently used order
     */
    protected final SortedSet<CacheEntry> lruTiles;

    /**
     * Constructs a new {@code MemoryTileCache}.
     */
    public MemoryTileCache() {
        this(200);
    }

    /**
     * Constructs a new {@code MemoryTileCache}.
     * @param cacheSize size of the cache
     */
    public MemoryTileCache(int cacheSize) {
        this.cacheSize = cacheSize;
        hash = new HashMap<>(cacheSize);
        lruTiles = new TreeSet<>(new Comparator<CacheEntry>() {
            @Override
            public int compare(CacheEntry o1, CacheEntry o2) {
                if (o1.tile.loaded == true && o2.tile.loaded == true) {
                    return Long.compare(o1.lastAccessTime, o2.lastAccessTime);
                }
                return Boolean.compare(o1.tile.loaded, o2.tile.loaded);
            }

        });
    }


    @Override
    public synchronized void addTile(Tile tile) {
        CacheEntry entry = createCacheEntry(tile);
        hash.put(tile.getKey(), entry);
        lruTiles.add(entry);
        if (hash.size() > cacheSize) {
            removeOldEntries();
        }
    }

    @Override
    public synchronized Tile getTile(TileSource source, int x, int y, int z) {
        CacheEntry entry = hash.get(Tile.getTileKey(source, x, y, z));
        if (entry == null)
            return null;
        entry.lastAccessTime = System.currentTimeMillis();
        return entry.tile;
    }

    /**
     * Removes the least recently used tiles
     */
    protected synchronized void removeOldEntries() {
        try {
            while (lruTiles.size() > cacheSize) {
                removeEntry(lruTiles.last());
            }
        } catch (Exception e) {
            log.warning(e.getMessage());
        }
    }

    protected synchronized void removeEntry(CacheEntry entry) {
        hash.remove(entry.tile.getKey());
        lruTiles.remove(entry);
    }

    protected CacheEntry createCacheEntry(Tile tile) {
        return new CacheEntry(tile);
    }

    @Override
    public synchronized void clear() {
        hash.clear();
        lruTiles.clear();
    }

    @Override
    public synchronized int getTileCount() {
        return hash.size();
    }

    public synchronized int getCacheSize() {
        return cacheSize;
    }

    /**
     * Changes the maximum number of {@link Tile} objects that this cache holds.
     *
     * @param cacheSize
     *            new maximum number of tiles
     */
    public synchronized void setCacheSize(int cacheSize) {
        this.cacheSize = cacheSize;
        if (hash.size() > cacheSize)
            removeOldEntries();
    }

    /**
     * Linked list element holding the {@link Tile} and links to the
     * {@link #next} and {@link #prev} item in the list.
     */
    protected static class CacheEntry {
        private Tile tile;
        private long lastAccessTime;

        protected CacheEntry(Tile tile) {
            this.tile = tile;
            lastAccessTime = System.currentTimeMillis();
        }
    }

 }
