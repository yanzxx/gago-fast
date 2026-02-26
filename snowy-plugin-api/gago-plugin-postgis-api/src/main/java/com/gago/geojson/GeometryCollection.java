//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.gago.geojson;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"type", "geometries"})
public class GeometryCollection extends Geometry {
    private final Geometry[] geometries;

    @JsonCreator
    public GeometryCollection(@JsonProperty("geometries") Geometry[] geometries) {
        this.geometries = geometries;
    }

    public Geometry[] getGeometries() {
        return this.geometries;
    }
}
