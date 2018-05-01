// License: GPL. For details, see LICENSE file.
package org.openstreetmap.josm.data.imagery;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.openstreetmap.gui.jmapviewer.interfaces.TemplatedTileSource;
import org.openstreetmap.josm.data.projection.Projection;
import org.openstreetmap.josm.gui.layer.WMSLayer;
import org.openstreetmap.josm.io.imagery.WMSImagery;
import org.openstreetmap.josm.io.imagery.WMSImagery.WMSGetCapabilitiesException;

public class WMSEndpointTileSource extends AbstractWMSTileSource implements TemplatedTileSource {

    private final WMSImagery wmsi;
    private String rootUrl;
    private List<DefaultLayer> layers;
    private String urlPattern;
    private static final Pattern PATTERN_PARAM  = Pattern.compile("\\{([^}]+)\\}");
    private final Map<String, String> headers = new ConcurrentHashMap<>();

    public WMSEndpointTileSource(ImageryInfo info, Projection tileProjection) {
        super(info, tileProjection);
        try {
            wmsi = new WMSImagery(info.getUrl());
        } catch (IOException | WMSGetCapabilitiesException e) {
            throw new IllegalArgumentException(e);
        }
        layers = info.getDefaultLayers();
        initProjection();
        urlPattern = wmsi.buildGetMapUrl(layers, info.isTransparent());
        this.headers.putAll(info.getCustomHttpHeaders());
    }

    @Override
    public int getDefaultTileSize() {
        return WMSLayer.PROP_IMAGE_SIZE.get();
    }

    @Override
    public String getTileUrl(int zoom, int tilex, int tiley) {
        String bbox = getBbox(zoom, tilex, tiley, wmsi.belowWMS130() ? false : getTileProjection().switchXY());

        // Using StringBuffer and generic PATTERN_PARAM matcher gives 2x performance improvement over replaceAll
        StringBuffer url = new StringBuffer(urlPattern.length());
        Matcher matcher = PATTERN_PARAM.matcher(urlPattern);
        while (matcher.find()) {
            String replacement;
            switch (matcher.group(1)) {
            case "proj":
                replacement = getServerCRS();
                break;
            case "bbox":
                replacement = bbox;
                break;
            case "width":
            case "height":
                replacement = String.valueOf(getTileSize());
                break;
            default:
                replacement = '{' + matcher.group(1) + '}';
            }
            matcher.appendReplacement(url, replacement);
        }
        matcher.appendTail(url);
        return url.toString();
    }

    public List<String> getServerProjections() {
        return wmsi.getLayers(layers).stream().flatMap(x -> x.getCrs().stream()).distinct().collect(Collectors.toList());
    }

    @Override
    public Map<String, String> getHeaders() {
        return headers;
    }
}
