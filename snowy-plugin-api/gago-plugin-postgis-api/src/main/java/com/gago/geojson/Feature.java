//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.gago.geojson;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.Map;

@JsonPropertyOrder({"type", "id", "geometry", "properties"})
public class Feature extends GeoJSON {
    @JsonInclude(Include.NON_EMPTY)
    private final Object id;
    private final Geometry geometry;
    private final Map<String, Object> properties;

    public Feature(@JsonProperty("geometry") Geometry geometry, @JsonProperty("properties") Map<String, Object> properties) {
        this((Object)null, geometry, properties);
    }

    @JsonCreator
    public Feature(@JsonProperty("id") Object id, @JsonProperty("geometry") Geometry geometry, @JsonProperty("properties") Map<String, Object> properties) {
        this.id = id;
        this.geometry = geometry;
        this.properties = properties;
    }

    public Object getId() {
        return this.id;
    }

    public Geometry getGeometry() {
        return this.geometry;
    }

    public Map<String, Object> getProperties() {
        return this.properties;
    }
}
