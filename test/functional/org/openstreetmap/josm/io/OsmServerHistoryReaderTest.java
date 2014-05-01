// License: GPL. For details, see LICENSE file.
package org.openstreetmap.josm.io;

import org.junit.BeforeClass;
import org.junit.Test;
import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.data.osm.OsmPrimitiveType;
import org.openstreetmap.josm.data.osm.history.History;
import org.openstreetmap.josm.data.osm.history.HistoryDataSet;
import org.openstreetmap.josm.gui.progress.NullProgressMonitor;

public class OsmServerHistoryReaderTest {

    @BeforeClass
    public static void init() {
        System.setProperty("josm.home", "C:\\data\\projekte\\osm\\tag-editor-plugin");
        Main.pref.init(false);
    }

    @Test
    public void testNode()  throws OsmTransferException {
        OsmServerHistoryReader reader = new OsmServerHistoryReader(OsmPrimitiveType.NODE,266187);
        HistoryDataSet ds = reader.parseHistory(NullProgressMonitor.INSTANCE);
        History h = ds.getHistory(266187, OsmPrimitiveType.NODE);
        System.out.println("num versions: " + h.getNumVersions());
    }

    @Test
    public void testWay()  throws OsmTransferException {
        OsmServerHistoryReader reader = new OsmServerHistoryReader(OsmPrimitiveType.WAY,3058844);
        HistoryDataSet ds = reader.parseHistory(NullProgressMonitor.INSTANCE);
        History h = ds.getHistory(3058844, OsmPrimitiveType.WAY);
        System.out.println("num versions: " + h.getNumVersions());
    }

    @Test
    public void testRelation()  throws OsmTransferException {
        OsmServerHistoryReader reader = new OsmServerHistoryReader(OsmPrimitiveType.RELATION,49);
        HistoryDataSet ds = reader.parseHistory(NullProgressMonitor.INSTANCE);
        History h = ds.getHistory(49, OsmPrimitiveType.RELATION);
        System.out.println("num versions: " + h.getNumVersions());
    }
}
