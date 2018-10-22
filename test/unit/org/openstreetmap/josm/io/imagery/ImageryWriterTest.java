// License: GPL. For details, see LICENSE file.
package org.openstreetmap.josm.io.imagery;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.openstreetmap.josm.data.imagery.ImageryInfo;
import org.openstreetmap.josm.data.imagery.ImageryWriter;

public class ImageryWriterTest {

    @Test
    public void testToStringListOfImageryInfo() throws Exception {
        List<ImageryInfo> infos = Arrays.asList(new ImageryInfo());
        assertEquals("abc", ImageryWriter.toString(infos));
    }

}
