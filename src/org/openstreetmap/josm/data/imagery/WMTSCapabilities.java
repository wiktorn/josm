// License: GPL. For details, see LICENSE file.
package org.openstreetmap.josm.data.imagery;

import java.util.Collection;

import org.openstreetmap.josm.data.imagery.GetCapabilitiesParseHelper.TransferMode;
import org.openstreetmap.josm.data.imagery.WMTSTileSource.Layer;

public class WMTSCapabilities {
    private String baseUrl;
    private TransferMode transferMode;
    private Collection<Layer> layers;


    public WMTSCapabilities(String baseUrl, TransferMode transferMode) {
        this.baseUrl = baseUrl;
        this.transferMode = transferMode;
    }

    public void addLayers(Collection<Layer> layers) {
        this.layers = layers;

    }

    public Collection<Layer> getLayers() {
        return layers;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public TransferMode getTransferMode() {
        return transferMode;
    }
}
