// License: GPL. For details, see LICENSE file.
package org.openstreetmap.josm.data.imagery;

import org.junit.BeforeClass;
import org.junit.Test;
import org.openstreetmap.josm.JOSMFixture;
import org.openstreetmap.josm.PerformanceTestUtils;
import org.openstreetmap.josm.PerformanceTestUtils.PerformanceTestTimer;
import org.openstreetmap.josm.data.projection.Projections;

public class TemplatedWMSTileSourcePerformanceTest {

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
    public void testGetTileUrl() {
        ImageryInfo testImageryWMS = new ImageryInfo("test imagery",
                "https://services.slip.wa.gov.au/public/services/SLIP_Public_Services/Transport/MapServer/WMSServer?LAYERS=8&"
                + "TRANSPARENT=TRUE&SERVICE=WMS&VERSION=1.1.1&REQUEST=GetMap&STYLES=&FORMAT=image%2Fpng&SRS={proj}&BBOX={bbox}&"
                + "WIDTH={width}&HEIGHT={height}",
                "wms",
                null,
                null);
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
        new TemplatedWMSTileSourcePerformanceTest().testGetTileUrl();
    }


}
