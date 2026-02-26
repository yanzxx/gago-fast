//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.gago.geojson;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

public class Polygon extends Geometry {
    @ApiModelProperty(
            value = "coordinates",
            example = "[\n          [\n            [\n              -73.99446487426758,\n              40.69612766939108\n            ],\n            [\n              -73.99450778961182,\n              40.69274368477265\n            ],\n            [\n              -73.992018699646,\n              40.6907913077715\n            ],\n            [\n              -73.9865255355835,\n              40.693492080788715\n            ],\n            [\n              -73.9879846572876,\n              40.69645304346962\n            ],\n            [\n              -73.99446487426758,\n              40.69612766939108\n            ]\n          ]\n        ]"
    )
    private final double[][][] coordinates;
    private final double[] bbox;

    @JsonCreator
    public Polygon(@JsonProperty("coordinates") double[][][] coordinates) {
        this.coordinates = coordinates;
        this.bbox = null;
    }

    public double[][][] getCoordinates() {
        return this.coordinates;
    }

    public double[] getBbox() {
        return this.bbox;
    }
}
