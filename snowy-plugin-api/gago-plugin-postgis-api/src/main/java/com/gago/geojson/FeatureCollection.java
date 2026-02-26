//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.gago.geojson;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"type", "features"})
public class FeatureCollection extends GeoJSON {
    private final Feature[] features;

    @JsonCreator
    public FeatureCollection(@JsonProperty("features") Feature[] features) {
        this.features = features;
    }

    public Feature[] getFeatures() {
        return this.features;
    }
}
