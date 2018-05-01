// License: GPL. For details, see LICENSE file.
package org.openstreetmap.josm.data.imagery;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import org.openstreetmap.josm.data.Bounds;

/**
 * The details of a layer of this WMS server.
 */
public class LayerDetails {
    private Map<String, String> styles = new ConcurrentHashMap<>(); // name -> title
    private Collection<String> crs = new ArrayList<>();
    /**
     * The layer name (WMS {@code Title})
     */
    private String title;
    /**
     * The layer name (WMS {@code Name})
     */
    private String name;
    /**
     * The layer abstract (WMS {@code Abstract})
     * @since 13199
     */
    private String abstr;
    private LayerDetails parentLayer;
    private Bounds bounds;
    private List<LayerDetails> children = new ArrayList<>();

    public LayerDetails(LayerDetails parentLayer) {
        this.parentLayer = parentLayer;
    }

    public Collection<String> getCrs() {
        Collection<String> ret = new ArrayList<>();
        if (parentLayer != null) {
            ret.addAll(parentLayer.getCrs());
        }
        ret.addAll(crs);
        return crs;
    }

    public Map<String, String> getStyles() {
        Map<String, String> ret = new ConcurrentHashMap<>();
        if (parentLayer != null) {
            ret.putAll(parentLayer.getStyles());
        }
        ret.putAll(styles);
        return ret;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addStyle(String name, String title) {
        this.styles.put(name, title);
    }

    public void addCrs(String crs) {
        this.crs.add(crs);
    }

    public Bounds getBounds() {
        return bounds;
    }

    public void setBounds(Bounds bounds) {
        this.bounds = bounds;
    }

    @Override
    public String toString() {
        String baseName = (title == null || title.isEmpty()) ? name : title;
        return abstr == null || abstr.equalsIgnoreCase(baseName) ? baseName : baseName + " (" + abstr + ')';
    }

    public LayerDetails getParent() {
        return parentLayer;
    }

    public void setChildren(List<LayerDetails> children) {
        this.children = children;

    }

    public List<LayerDetails> getChildren() {
        return children;
    }

    public boolean isSelectable() {
        return !(name == null || name.isEmpty());
    }

    public String getAbstract() {
        return abstr;
    }

    public void setAbstract(String abstr) {
        this.abstr = abstr;
    }

    public Stream<LayerDetails> flattened() {
        return Stream.concat(
                Stream.of(this),
                getChildren().stream().flatMap(LayerDetails::flattened)
                );
    }
}
