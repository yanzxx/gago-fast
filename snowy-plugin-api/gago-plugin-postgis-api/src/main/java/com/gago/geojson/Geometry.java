//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.gago.geojson;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

@JsonTypeInfo(
        use = Id.NAME,
        include = As.EXISTING_PROPERTY,
        property = "type"
)
@JsonSubTypes({@Type(
        value = Point.class,
        name = "Point"
), @Type(
        value = LineString.class,
        name = "LineString"
), @Type(
        value = Polygon.class,
        name = "Polygon"
), @Type(
        value = MultiPoint.class,
        name = "MultiPoint"
), @Type(
        value = MultiLineString.class,
        name = "MultiLineString"
), @Type(
        value = MultiPolygon.class,
        name = "MultiPolygon"
), @Type(
        value = Feature.class,
        name = "Feature"
), @Type(
        value = FeatureCollection.class,
        name = "FeatureCollection"
), @Type(
        value = GeometryCollection.class,
        name = "GeometryCollection"
)})
@JsonPropertyOrder({"type", "coordinates", "bbox"})
public abstract class Geometry extends GeoJSON {
    @JsonCreator
    public Geometry() {
    }
}
