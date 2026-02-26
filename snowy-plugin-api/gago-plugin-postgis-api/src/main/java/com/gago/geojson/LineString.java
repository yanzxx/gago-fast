//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.gago.geojson;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

public class LineString extends Geometry {
    @ApiModelProperty(
            value = "coordinates",
            example = "[\n          [\n            -73.97994875907898,\n            40.69785620097987\n          ],\n          [\n            -73.97994875907898,\n            40.69764674601067\n          ],\n          [\n            -73.97958666086197,\n            40.69765894728909\n          ],\n          [\n            -73.97954642772675,\n            40.69757353829332\n          ]\n        ]"
    )
    private final double[][] coordinates;
    private final double[] bbox;

    @JsonCreator
    public LineString(@JsonProperty("coordinates") double[][] coordinates) {
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
