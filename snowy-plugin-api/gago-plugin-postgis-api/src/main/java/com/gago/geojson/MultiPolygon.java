//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.gago.geojson;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

public class MultiPolygon extends Geometry {
    @ApiModelProperty(
            value = "coordinates",
            example = "[[\n          [\n            [\n              -73.99173974990845,\n              40.69676214737228\n            ],\n            [\n              -73.98869276046753,\n              40.69511899964887\n            ],\n            [\n              -73.98590326309204,\n              40.696664535768484\n            ],\n            [\n              -73.98613929748535,\n              40.6984052545694\n            ],\n            [\n              -73.99036645889282,\n              40.69851913178431\n            ],\n            [\n              -73.99173974990845,\n              40.69676214737228\n            ]\n          ]\n        ]]"
    )
    private final double[][][][] coordinates;
    private final double[] bbox;

    @JsonCreator
    public MultiPolygon(@JsonProperty("coordinates") double[][][][] coordinates) {
        this.coordinates = coordinates;
        this.bbox = null;
    }

    public double[][][][] getCoordinates() {
        return this.coordinates;
    }

    public double[] getBbox() {
        return this.bbox;
    }
}
