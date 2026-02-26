//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.gago.geojson;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MultiPoint extends Geometry {
    private final double[][] coordinates;
    private final double[] bbox;

    @JsonCreator
    public MultiPoint(@JsonProperty("coordinates") double[][] coordinates) {
        this.coordinates = coordinates;
        this.bbox = null;
    }

    public double[][] getCoordinates() {
        return this.coordinates;
    }

    public double[] getBbox() {
        return this.bbox;
    }
}
