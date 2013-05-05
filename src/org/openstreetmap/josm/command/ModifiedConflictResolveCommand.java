// License: GPL. For details, see LICENSE file.
package org.openstreetmap.josm.command;

import static org.openstreetmap.josm.tools.I18n.marktr;
import static org.openstreetmap.josm.tools.I18n.tr;

import java.util.Collection;

import javax.swing.Icon;

import org.openstreetmap.josm.data.conflict.Conflict;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.OsmPrimitiveType;
import org.openstreetmap.josm.tools.ImageProvider;

/**
 * Represents a command for to set the modified flag {@link OsmPrimitive}
 *
 *
 */
public class ModifiedConflictResolveCommand extends ConflictResolveCommand {

    /** the conflict to resolve */
    private Conflict<? extends OsmPrimitive> conflict;

    /**
     * constructor
     * @param conflict the conflict data set
     */
    public ModifiedConflictResolveCommand(Conflict<? extends OsmPrimitive> conflict) {
        this.conflict = conflict;
    }

    @Override
    public String getDescriptionText() {
        String msg = "";
        switch(OsmPrimitiveType.from(conflict.getMy())) {
        case NODE: msg = marktr("Set the ''modified'' flag for node {0}"); break;
        case WAY: msg = marktr("Set the ''modified'' flag for way {0}"); break;
        case RELATION: msg = marktr("Set the ''modified'' flag for relation {0}"); break;
        }
        return tr(msg,conflict.getMy().getId());
    }

    @Override
    public Icon getDescriptionIcon() {
        return ImageProvider.get("data", "object");
    }

    @Override
    public boolean executeCommand() {
        super.executeCommand();
        if (!conflict.getMy().isNew() && conflict.getMy().hasEqualSemanticAttributes(conflict.getTheir())) {
            conflict.getMy().setModified(conflict.getTheir().isModified());
        }
        getLayer().getConflicts().remove(conflict);
        rememberConflict(conflict);
        return true;
    }

    @Override
    public void fillModifiedData(Collection<OsmPrimitive> modified, Collection<OsmPrimitive> deleted,
            Collection<OsmPrimitive> added) {
        modified.add(conflict.getMy());
    }
}
