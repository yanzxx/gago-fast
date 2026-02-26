//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.gago.geojson;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

public class Point extends Geometry {
    @ApiModelProperty(
            value = "coordinates",
            example = "[\n          -73.98612856864929,\n          40.6954525131084\n        ]"
    )
    private final double[] coordinates;
    private final double[] bbox;

    @JsonCreator
    public Point(@JsonProperty("coordinates") double[] coordinates) {
        this.coordinates = coordinates;
        this.bbox = null;
    }

    public double[] getCoordinates() {
        return this.coordinates;
    }

    public double[] getBbox() {
        return this.bbox;
    }
}
