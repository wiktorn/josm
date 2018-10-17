// License: GPL. For details, see LICENSE file.
package org.openstreetmap.josm.gui.layer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.awt.Graphics2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.Projected;
import org.openstreetmap.gui.jmapviewer.Tile;
import org.openstreetmap.gui.jmapviewer.TileXY;
import org.openstreetmap.gui.jmapviewer.interfaces.ICoordinate;
import org.openstreetmap.gui.jmapviewer.interfaces.IProjected;
import org.openstreetmap.gui.jmapviewer.interfaces.TileCache;
import org.openstreetmap.gui.jmapviewer.interfaces.TileJob;
import org.openstreetmap.gui.jmapviewer.interfaces.TileLoader;
import org.openstreetmap.gui.jmapviewer.interfaces.TileLoaderListener;
import org.openstreetmap.gui.jmapviewer.tilesources.AbstractTMSTileSource;
import org.openstreetmap.gui.jmapviewer.tilesources.TMSTileSource;
import org.openstreetmap.gui.jmapviewer.tilesources.TileSourceInfo;
import org.openstreetmap.josm.data.ProjectionBounds;
import org.openstreetmap.josm.data.imagery.ImageryInfo;
import org.openstreetmap.josm.data.imagery.TileLoaderFactory;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.projection.Projection;
import org.openstreetmap.josm.data.projection.ProjectionRegistry;
import org.openstreetmap.josm.data.projection.Projections;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.MapView;
import org.openstreetmap.josm.gui.layer.imagery.ImageryFilterSettings;
import org.openstreetmap.josm.gui.layer.imagery.TilePosition;
import org.openstreetmap.josm.testutils.JOSMTestRules;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import mockit.Expectations;
import mockit.Mocked;

/**
 * Test of the base {@link AbstractTileSourceLayer} class
 */
public class AbstractTileSourceLayerTest {

    /**
     * Setup test
     */
    @Rule
    @SuppressFBWarnings(value = "URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD")
    public JOSMTestRules test = new JOSMTestRules().projection().main();

    private static final class TMSTileStubSource extends TMSTileSource {
        private TMSTileStubSource() {
            super(new TileSourceInfo());
        }

        @Override
        public TileXY latLonToTileXY(double lat, double lon, int zoom) {
            return new TileXY(lon / 2, lat / 2);
        }

        @Override
        public ICoordinate tileXYToLatLon(int x, int y, int zoom) {
            return new Coordinate(2*y, 2*x);
        }

        @Override
        public IProjected tileXYtoProjected(int x, int y, int zoom) {
            return new Projected(2*x, 2*y);
        }

        @Override
        public TileXY projectedToTileXY(IProjected p, int zoom) {
            return new TileXY(p.getEast() / 2, p.getNorth() / 2);
        }

    }

    private static class TileSourceStubLayer extends AbstractTileSourceLayer<AbstractTMSTileSource> {

        TileSourceStubLayer() {
            super(new ImageryInfo());
            hookUpMapView();
        }

        @Override
        protected TileLoaderFactory getTileLoaderFactory() {
            return new TileLoaderFactory() {
                @Override
                public TileLoader makeTileLoader(TileLoaderListener listener, Map<String, String> headers,
                        long minimumExpiryTime) {
                    return new TileLoader() {
                        @Override
                        public TileJob createTileLoaderJob(Tile tile) {
                            return new TileJob() {
                                boolean loaded = false;
                                @Override
                                public void run() {
                                    // TODO Auto-generated method stub

                                }

                                @Override
                                public void submit() {
                                    // TODO Auto-generated method stub
                                    submit(false);
                                }

                                @Override
                                public void submit(boolean force) {
                                    // TODO Auto-generated method stub
                                    if (loaded && !force) {
                                        return;
                                    }
                                    try {
                                        tile.getUrl();
                                        tile.finishLoading();
                                    } catch (IOException e) {
                                    }
                                }

                            };
                        }

                        @Override
                        public void cancelOutstandingTasks() {
                        }

                    };
                }
            };
        }

        @Override
        public Collection<String> getNativeProjections() {
            return null;
        }

        @Override
        protected AbstractTMSTileSource getTileSource() {
            return new TMSTileStubSource();
        }

        TileCache getTileCache() {
            return tileCache;
        }
    }

    private TileSourceStubLayer testLayer;
    AtomicBoolean invalidated = new AtomicBoolean();



    /**
     * Create test layer
     */
    @Before
    public void setUp() {
        MainApplication.getLayerManager().addLayer(new OsmDataLayer(new DataSet(), "", null));
        testLayer = new TileSourceStubLayer();
        testLayer.addInvalidationListener(l -> invalidated.set(true));
    }

    /**
     * Test {@link AbstractTileSourceLayer#filterChanged}
     */
    @Test
    public void testFilterChanged() {
        try {
            ImageryFilterSettings filterSettings = new ImageryFilterSettings();
            filterSettings.addFilterChangeListener(testLayer);
            assertFalse(invalidated.get());
            filterSettings.setGamma(0.5);
            assertTrue(invalidated.get());
        } finally {
            invalidated.set(false);
        }
    }

    /**
     * Test {@link AbstractTileSourceLayer#clearTileCache}
     */
    @Test
    public void testClearTileCache() {
        testLayer.loadAllTiles(true);
        assertTrue(testLayer.getTileCache().getTileCount() > 0);
        testLayer.clearTileCache();
        assertEquals(0, testLayer.getTileCache().getTileCount());
    }

    /**
     * Test {@link AbstractTileSourceLayer#getAdjustAction}
     */
    @Test
    public void testGetAdjustAction() {
        assertNotNull(testLayer.getAdjustAction());
    }

    /**
     * Test {@link AbstractTileSourceLayer#getInfoComponent}
     */
    @Test
    public void testGetInfoComponent() {
        assertNotNull(testLayer.getInfoComponent());
    }

    /**
     * Test {@link AbstractTileSourceLayer.TileSourceLayerPopup}
     */
    @Test
    public void testTileSourceLayerPopup() {
        assertNotNull(testLayer.new TileSourceLayerPopup(100, 100));
    }

    @Test
    public void testDrawInViewArea(@Mocked Graphics2D graphics2d, @Mocked MapView mapView, @Mocked TMSTileStubSource tileSource) throws Exception {
        Projection proj = Projections.getProjectionByCode("EPSG:3857");
        ProjectionBounds bounds = proj.getWorldBoundsBoxEastNorth();
        /// 0/0/0 is whole world as we need 4 tiles (1024/256) to cover screen - we will be working on zoom level 2
        new Expectations(MapView.class) {{
            mapView.getWidth(); result = 1024;
            mapView.getHeight(); result = 1024;
            mapView.getEastNorth(0, 0); result = bounds.getMin();
            mapView.getEastNorth(1024, 1024); result = bounds.getMax();
        }};
        new Expectations(TMSTileStubSource.class){{
            tileSource.getTileUrl(1, anyInt, anyInt); times=0;
            tileSource.getTileUrl(2, anyInt, anyInt); times=16;
            tileSource.getTileUrl(3, anyInt, anyInt); times=0;
            tileSource.getTileUrl(4, anyInt, anyInt); times=0;
        }};
        testLayer.drawInViewArea(graphics2d, mapView, bounds);
    }

    @Test
    public void testDrawInViewArea_allTilesLoaded(@Mocked Graphics2D graphics2d, @Mocked MapView mapView, @Mocked TMSTileStubSource tileSource) throws Exception {
        Projection proj = Projections.getProjectionByCode("EPSG:3857");
        ProjectionRegistry.setProjection(proj);
        ProjectionBounds bounds = proj.getWorldBoundsBoxEastNorth();
        /// 0/0/0 is whole world as we need 4 tiles (1024/256) to cover screen - we will be working on zoom level 2
        // mark all tiles as already loaded
        new Expectations(MapView.class) {{
            mapView.getWidth(); result = 1024;
            mapView.getHeight(); result = 1024;
            mapView.getEastNorth(0, 0); result = bounds.getMin();
            mapView.getEastNorth(1024, 1024); result = bounds.getMax();
        }};
        new Expectations(TMSTileStubSource.class){{
            tileSource.getServerCRS(); result = "EPSG:3857";
            tileSource.getTileUrl(1, anyInt, anyInt); times=0;
            tileSource.getTileUrl(2, anyInt, anyInt); times=16;
            tileSource.getTileUrl(3, anyInt, anyInt); times=0;
            tileSource.getTileUrl(4, anyInt, anyInt); times=0;
        }};
        testLayer.getTileSet(bounds, 2).loadAllTiles(true);
        new Expectations(TMSTileStubSource.class){{
            tileSource.getTileUrl(1, anyInt, anyInt); times=0;
            tileSource.getTileUrl(2, anyInt, anyInt); times=0;
            tileSource.getTileUrl(3, anyInt, anyInt); times=0;
            tileSource.getTileUrl(4, anyInt, anyInt); times=0;
        }};
        testLayer.drawInViewArea(graphics2d, mapView, bounds);
    }

    @Test
    public void testDrawInViewArea_missingAllTiles(@Mocked Graphics2D graphics2d, @Mocked MapView mapView, @Mocked TMSTileStubSource tileSource) throws Exception {
        Projection proj = Projections.getProjectionByCode("EPSG:3857");
        ProjectionRegistry.setProjection(proj);
        ProjectionBounds bounds = proj.getWorldBoundsBoxEastNorth();
        /// 0/0/0 is whole world as we need 4 tiles (1024/256) to cover screen - we will be working on zoom level 2
        // mark all tiles as already loaded
        new Expectations(MapView.class) {{
            mapView.getWidth(); result = 1024;
            mapView.getHeight(); result = 1024;
            mapView.getEastNorth(0, 0); result = bounds.getMin();
            mapView.getEastNorth(1024, 1024); result = bounds.getMax();
        }};
        new Expectations(TMSTileStubSource.class){{
            tileSource.getServerCRS(); result = "EPSG:3857";
            tileSource.getTileUrl(1, anyInt, anyInt); times=0;
            tileSource.getTileUrl(2, anyInt, anyInt); times=16;
            tileSource.getTileUrl(3, anyInt, anyInt); times=0;
            tileSource.getTileUrl(4, anyInt, anyInt); times=0;
        }};
        AbstractTileSourceLayer<AbstractTMSTileSource>.TileSet ts = testLayer.getTileSet(bounds, 2);
        ts.loadAllTiles(true);
        ArrayList<TilePosition> missed = new ArrayList<>();
        ts.visitTiles(x -> {
            x.putValue("tile-info", "no-tile");
            x.setError("Failure to load");
        }, missed::add);

        new Expectations(TMSTileStubSource.class){{
            tileSource.getTileUrl(1, anyInt, anyInt); times=0;
            tileSource.getTileUrl(2, anyInt, anyInt); times=0;
            tileSource.getTileUrl(3, anyInt, anyInt); times=16<<2;
            tileSource.getTileUrl(4, anyInt, anyInt); times=0;
        }};
        testLayer.drawInViewArea(graphics2d, mapView, bounds);
    }

}

