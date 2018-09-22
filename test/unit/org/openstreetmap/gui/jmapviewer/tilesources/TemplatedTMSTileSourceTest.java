// License: GPL. For details, see LICENSE file.
package org.openstreetmap.gui.jmapviewer.tilesources;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openstreetmap.josm.data.imagery.ImageryInfo;

public class TemplatedTMSTileSourceTest {

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


}
