// License: GPL. For details, see LICENSE file.
package org.openstreetmap.josm.gui.layer;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.openstreetmap.gui.jmapviewer.Tile;
import org.openstreetmap.gui.jmapviewer.TileXY;
import org.openstreetmap.gui.jmapviewer.tilesources.TMSTileSource;
import org.openstreetmap.josm.JOSMFixture;
import org.openstreetmap.josm.PerformanceTestUtils;
import org.openstreetmap.josm.PerformanceTestUtils.PerformanceTestTimer;
import org.openstreetmap.josm.data.imagery.ImageryInfo;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.testutils.JOSMTestRules;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public class AbstractTileSourceLayerPerformanceTest {

    /**
     * Setup test.
     */
    @Rule
    @SuppressFBWarnings(value = "URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD")
    public JOSMTestRules test = new JOSMTestRules().main().projection();

    @BeforeClass
    public static void createJOSMFixture() {
        JOSMFixture.createPerformanceTestFixture().init(true);
        JOSMFixture.initMainPanel();
    }
    ;

    @Test
    public void testTileSet() {
        ImageryInfo info = new ImageryInfo("test imagery", "http://localhost/{z}/{x}/{y}", "tms", null, null);
        TMSLayer layer = new TMSLayer(info);
        MainApplication.getLayerManager().addLayer(layer);

        layer.initTileSource(new TMSTileSource(info));
        PerformanceTestTimer tmsTimer = PerformanceTestUtils.startTimer("AbstractTileSourceLayer.TileSet#cośtam(...)");

        int zoom = 10;
        TileXY minTileXY = new TileXY(10, 10);
        TileXY maxTileXY = new TileXY(25, 25);

        for (Tile tile: layer.new TileSet(minTileXY, maxTileXY, zoom).allTilesCreate()) {
                AbstractTileSourceLayer<TMSTileSource>.TileSet ts = layer.new TileSet(minTileXY, maxTileXY, zoom);
                ts.hasAllLoadedTiles();
                tile.finishLoading();
        }
        for (int i = 0; i < 1000; i++) {
            AbstractTileSourceLayer<TMSTileSource>.TileSet ts = layer.new TileSet(minTileXY, maxTileXY, zoom);
            ts.hasAllLoadedTiles();
        }
        tmsTimer.done();
    }

    public static void main(String[] args) {
        createJOSMFixture();
        for (int i = 0; i < 20; i++) {
            new AbstractTileSourceLayerPerformanceTest().testTileSet();
        }
        System.exit(0);
    }

}
