// License: GPL. For details, see LICENSE file.
package org.openstreetmap.gui.jmapviewer.tilesources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.Triple;
import org.junit.Test;
import org.openstreetmap.josm.data.imagery.ImageryInfo;


public class TemplatedTMSTileSourceTest {

    private final static Collection<String> TMS_IMAGERIES = Arrays.asList(new String[]{
            "http://imagico.de/map/osmim_tiles.php?layer=S2A_R136_N41_20150831T093006&z={zoom}&x={x}&y={-y}",
            /*
             *  generate for example with:
             *  $ curl https://josm.openstreetmap.de/maps | \
             *    xmlstarlet sel -N 'josm=http://josm.openstreetmap.de/maps-1.0' -t -v "//josm:entry[josm:type='tms']/josm:url" -n  | \
             *    sed -e 's/\&amp;/\&/g' -e 's/^/"/' -e 's/$/",/'
             */
    });

    /**
     * triple of:
     *  * baseUrl
     *  * expected tile url for zoom=1, x=2, y=3
     *  * expected tile url for zoom=3, x=2, y=1
     */
    @SuppressWarnings("unchecked")
    private Collection<Triple<String, String, String>> TEST_DATA = Arrays.asList(new Triple[] {
            Triple.of("http://imagico.de/map/osmim_tiles.php?layer=S2A_R136_N41_20150831T093006&z={zoom}&x={x}&y={-y}", "http://imagico.de/map/osmim_tiles.php?layer=S2A_R136_N41_20150831T093006&z=1&x=2&y=-2", "http://imagico.de/map/osmim_tiles.php?layer=S2A_R136_N41_20150831T093006&z=3&x=2&y=6"),
            /*
             * generate with main method below once TMS_IMAGERIES is filled in
             */
    });
    @Test
    public void testGetTileUrl() {
        ImageryInfo testImageryTMS = new ImageryInfo("test imagery", "http://localhost/{z}/{x}/{y}", "tms", null, null);
        TemplatedTMSTileSource ts = new TemplatedTMSTileSource(testImageryTMS);
        assertEquals("http://localhost/1/1/1", ts.getTileUrl(1, 1, 1));
        assertEquals("http://localhost/2/2/2", ts.getTileUrl(2, 2, 2));
        assertEquals("http://localhost/3/3/3", ts.getTileUrl(3, 3, 3));
        assertEquals("http://localhost/4/4/4", ts.getTileUrl(4, 4, 4));
    }

    @Test
    public void testGetTileUrl_positive_zoom() {
        ImageryInfo testImageryTMS = new ImageryInfo("test imagery", "http://localhost/{zoom+5}/{x}/{y}", "tms", null, null);
        TemplatedTMSTileSource ts = new TemplatedTMSTileSource(testImageryTMS);
        assertEquals("http://localhost/6/2/3", ts.getTileUrl(1, 2, 3));
        assertEquals("http://localhost/7/3/4", ts.getTileUrl(2, 3, 4));
        assertEquals("http://localhost/8/4/5", ts.getTileUrl(3, 4, 5));
        assertEquals("http://localhost/9/5/6", ts.getTileUrl(4, 5, 6));
    }

    @Test
    public void testGetTileUrl_negative_zoom() {
        ImageryInfo testImageryTMS = new ImageryInfo("test imagery", "http://localhost/{zoom-5}/{x}/{y}", "tms", null, null);
        TemplatedTMSTileSource ts = new TemplatedTMSTileSource(testImageryTMS);
        assertEquals("http://localhost/-4/2/3", ts.getTileUrl(1, 2, 3));
        assertEquals("http://localhost/-3/3/4", ts.getTileUrl(2, 3, 4));
        assertEquals("http://localhost/-2/4/5", ts.getTileUrl(3, 4, 5));
        assertEquals("http://localhost/-1/5/6", ts.getTileUrl(4, 5, 6));
    }

    @Test
    public void testGetTileUrl_inverse_negative_zoom() {
        ImageryInfo testImageryTMS = new ImageryInfo("test imagery", "http://localhost/{5-zoom}/{x}/{y}", "tms", null, null);
        TemplatedTMSTileSource ts = new TemplatedTMSTileSource(testImageryTMS);
        assertEquals("http://localhost/4/2/3", ts.getTileUrl(1, 2, 3));
        assertEquals("http://localhost/3/3/4", ts.getTileUrl(2, 3, 4));
        assertEquals("http://localhost/2/4/5", ts.getTileUrl(3, 4, 5));
        assertEquals("http://localhost/1/5/6", ts.getTileUrl(4, 5, 6));
    }

    @Test
    public void testGetTileUrl_both_offsets() {
        ImageryInfo testImageryTMS = new ImageryInfo("test imagery", "http://localhost/{10-zoom-5}/{x}/{y}", "tms", null, null);
        TemplatedTMSTileSource ts = new TemplatedTMSTileSource(testImageryTMS);
        assertEquals("http://localhost/4/2/3", ts.getTileUrl(1, 2, 3));
        assertEquals("http://localhost/3/3/4", ts.getTileUrl(2, 3, 4));
        assertEquals("http://localhost/2/4/5", ts.getTileUrl(3, 4, 5));
        assertEquals("http://localhost/1/5/6", ts.getTileUrl(4, 5, 6));
    }

    @Test
    public void testGetTileUrl_switch() {
        ImageryInfo testImageryTMS = new ImageryInfo("test imagery", "http://{switch:a,b,c}.localhost/{10-zoom-5}/{x}/{y}", "tms", null, null);
        TemplatedTMSTileSource ts = new TemplatedTMSTileSource(testImageryTMS);
        assertTrue(
                Stream.of(
                        "http://a.localhost/4/2/3",
                        "http://b.localhost/4/2/3",
                        "http://c.localhost/4/2/3"
                        )
                .anyMatch(Predicate.isEqual(ts.getTileUrl(1, 2, 3)))
                );

        assertTrue(
                Stream.of(
                        "http://a.localhost/3/3/4",
                        "http://b.localhost/3/3/4",
                        "http://c.localhost/3/3/4"
                        )
                .anyMatch(Predicate.isEqual(ts.getTileUrl(2, 3, 4)))
                );
        assertTrue(
                Stream.of(
                        "http://a.localhost/2/4/5",
                        "http://b.localhost/2/4/5",
                        "http://c.localhost/2/4/5"
                        )
                .anyMatch(Predicate.isEqual(ts.getTileUrl(3, 4, 5)))
                );
        assertTrue(
                Stream.of(
                        "http://a.localhost/1/5/6",
                        "http://b.localhost/1/5/6",
                        "http://c.localhost/1/5/6"
                        )
                .anyMatch(Predicate.isEqual(ts.getTileUrl(4, 5, 6)))
                );
    }


    /**
     * Tests all entries in TEST_DATA. This test will fail if {switch:...} template is used
     */
    @Test
    public void testAllUrls() {
        for(Triple<String, String, String> test: TEST_DATA) {
            ImageryInfo testImageryTMS = new ImageryInfo("test imagery", test.getLeft(), "tms", null, null);
            TemplatedTMSTileSource ts = new TemplatedTMSTileSource(testImageryTMS);
            assertEquals(test.getMiddle(), ts.getTileUrl(1, 2, 3));
            assertEquals(test.getRight(), ts.getTileUrl(3, 2, 1));
        }
    }

    public static void main(String[] args) {
        for(String url: TMS_IMAGERIES) {
            ImageryInfo testImageryTMS = new ImageryInfo("test imagery", url, "tms", null, null);
            TemplatedTMSTileSource ts = new TemplatedTMSTileSource(testImageryTMS);
            System.out.println(MessageFormat.format("Triple.of(\"{0}\", \"{1}\", \"{2}\"),", url, ts.getTileUrl(1, 2, 3), ts.getTileUrl(3, 2, 1)));
        }
    }

}
