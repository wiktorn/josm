// License: GPL. For details, see LICENSE file.
package org.openstreetmap.gui.jmapviewer.tilesources;

import org.junit.BeforeClass;
import org.junit.Test;
import org.openstreetmap.josm.JOSMFixture;
import org.openstreetmap.josm.PerformanceTestUtils;
import org.openstreetmap.josm.PerformanceTestUtils.PerformanceTestTimer;
import org.openstreetmap.josm.data.imagery.ImageryInfo;
import org.openstreetmap.josm.data.imagery.TemplatedWMSTileSource;
import org.openstreetmap.josm.data.projection.Projections;

public class TemplatedTMSTileSourcePerformanceTest {

    private static final int TEST_RUNS = 1;
    private final static int TIMES = 10_000;

    /**
     * Prepare the test.
     */
    @BeforeClass
    public static void createJOSMFixture() {
        JOSMFixture.createPerformanceTestFixture().init(true);
    }

    @Test
    public void testGetUrl() {
        ImageryInfo testImageryTMS = new ImageryInfo("test imagery",
                "https://maps{switch:1,2,3,4}.wien.gv.at/basemap/geolandbasemap/normal/google3857/{zoom}/{y}/{x}.png",
                "tms", null, null);
        TemplatedTMSTileSource tmsTs = new TemplatedTMSTileSource(testImageryTMS);

        for (int testRun = 0; testRun < TEST_RUNS; testRun++) {
            PerformanceTestTimer tmsTimer = PerformanceTestUtils.startTimer("TemplatedTMSTileSource#getUrl(String)");
            for (int i = 0; i < TIMES ; i++) {
                tmsTs.getTileUrl(i % 20, i, i);
            }
            tmsTimer.done();
        }

        ImageryInfo testImageryWMS = new ImageryInfo("test imagery", "http://localhost/{w}/{s}/{e}", "wms", null, null);
        TemplatedWMSTileSource wmsTs = new TemplatedWMSTileSource(testImageryWMS, Projections.getProjectionByCode("EPSG:3857"));

        for (int testRun = 0; testRun < TEST_RUNS; testRun++) {
            PerformanceTestTimer wmsTimer = PerformanceTestUtils.startTimer("TemplatedWMSTileSource#getUrl(String)");
            for (int i = 0; i < TIMES ; i++) {
                wmsTs.getTileUrl(i % 20, i, i);
            }
            wmsTimer.done();
        }

        System.exit(0);

    }

    public static void main(String args[]) {
        createJOSMFixture();
        new TemplatedTMSTileSourcePerformanceTest().testGetUrl();
    }
}
