// License: GPL. For details, see LICENSE file.
package org.openstreetmap.josm.data.imagery;

import static org.openstreetmap.josm.tools.I18n.tr;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.openstreetmap.josm.data.imagery.ImageryInfo.ImageryType;

/**
 *
 * Simple class representing default layer that might be set in imagery information
 *
 * This simple class is needed - as for WMS there is different information needed to specify layer than for WMTS
 *
 * @author Wiktor Niesiobedzki
 *
 */
public class DefaultLayer {
    private final String layerName;
    private final String tileMatrixSet;
    private final String style;

    /**
     * Constructor
     * @param layerName that is the DefaultLayer
     */
    public DefaultLayer(ImageryType imageryType, String layerName, String style, String tileMatrixSet) {
        this.layerName = layerName == null ? "" : layerName;
        this.style = style == null ? "" : style;
        if (!imageryType.equals(ImageryType.WMTS) && !(tileMatrixSet == null || "".equals(tileMatrixSet))) {
            throw new IllegalArgumentException(tr("{0} imagery has tileMatrixSet defined to: {1}", imageryType, tileMatrixSet));
        }
        this.tileMatrixSet = tileMatrixSet == null ? "" : tileMatrixSet;
    }

    /**
     * @return layer name of the default layer
     */
    public String getLayerName() {
        return layerName;
    }

    public String getTileMatrixSet() {
        return tileMatrixSet;
    }

    public String getStyle() {
        return style;
    }

    public JsonObject toJson() {
        JsonObjectBuilder ret = Json.createObjectBuilder();
        ret.add("layerName", layerName);
        ret.add("style", style);
        ret.add("tileMatrixSet", tileMatrixSet);
        return ret.build();
    }

    public static DefaultLayer fromJson(JsonObject o, ImageryType type) {
        return new DefaultLayer(type, o.getString("layerName"), o.getString("style"), o.getString("tileMatrixSet"));
    }
}
