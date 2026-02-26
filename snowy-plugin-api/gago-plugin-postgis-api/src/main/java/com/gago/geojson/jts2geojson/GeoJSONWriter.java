//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.gago.geojson.jts2geojson;

import com.gago.geojson.Feature;
import com.gago.geojson.FeatureCollection;
import com.gago.geojson.Geometry;
import java.util.List;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

public class GeoJSONWriter {
    static final GeoJSONReader reader = new GeoJSONReader();

    public GeoJSONWriter() {
    }

    public Geometry write(org.locationtech.jts.geom.Geometry geometry) {
        Class<? extends org.locationtech.jts.geom.Geometry> c = geometry.getClass();
        if (c.equals(Point.class)) {
            return this.convert((Point)geometry);
        } else if (c.equals(LineString.class)) {
            return this.convert((LineString)geometry);
        } else if (c.equals(Polygon.class)) {
            return this.convert((Polygon)geometry);
        } else if (c.equals(MultiPoint.class)) {
            return this.convert((MultiPoint)geometry);
        } else if (c.equals(MultiLineString.class)) {
            return this.convert((MultiLineString)geometry);
        } else if (c.equals(MultiPolygon.class)) {
            return this.convert((MultiPolygon)geometry);
        } else if (c.equals(GeometryCollection.class)) {
            return this.convert((GeometryCollection)geometry);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public FeatureCollection write(List<Feature> features) {
        int size = features.size();
        Feature[] featuresJson = new Feature[size];

        for(int i = 0; i < size; ++i) {
            featuresJson[i] = (Feature)features.get(i);
        }

        return new FeatureCollection(featuresJson);
    }

    com.gago.geojson.Point convert(Point point) {
        com.gago.geojson.Point json = new com.gago.geojson.Point(this.convert(point.getCoordinate()));
        return json;
    }

    com.gago.geojson.MultiPoint convert(MultiPoint multiPoint) {
        return new com.gago.geojson.MultiPoint(this.convert(multiPoint.getCoordinates()));
    }

    com.gago.geojson.LineString convert(LineString lineString) {
        return new com.gago.geojson.LineString(this.convert(lineString.getCoordinates()));
    }

    com.gago.geojson.MultiLineString convert(MultiLineString multiLineString) {
        int size = multiLineString.getNumGeometries();
        double[][][] lineStrings = new double[size][][];

        for(int i = 0; i < size; ++i) {
            lineStrings[i] = this.convert(multiLineString.getGeometryN(i).getCoordinates());
        }

        return new com.gago.geojson.MultiLineString(lineStrings);
    }

    com.gago.geojson.Polygon convert(Polygon polygon) {
        int size = polygon.getNumInteriorRing() + 1;
        double[][][] rings = new double[size][][];
        rings[0] = this.convert(polygon.getExteriorRing().getCoordinates());

        for(int i = 0; i < size - 1; ++i) {
            rings[i + 1] = this.convert(polygon.getInteriorRingN(i).getCoordinates());
        }

        return new com.gago.geojson.Polygon(rings);
    }

    com.gago.geojson.MultiPolygon convert(MultiPolygon multiPolygon) {
        int size = multiPolygon.getNumGeometries();
        double[][][][] polygons = new double[size][][][];

        for(int i = 0; i < size; ++i) {
            polygons[i] = this.convert((Polygon)multiPolygon.getGeometryN(i)).getCoordinates();
        }

        return new com.gago.geojson.MultiPolygon(polygons);
    }

    com.gago.geojson.GeometryCollection convert(GeometryCollection gc) {
        int size = gc.getNumGeometries();
        Geometry[] geometries = new Geometry[size];

        for(int i = 0; i < size; ++i) {
            geometries[i] = this.write(gc.getGeometryN(i));
        }

        return new com.gago.geojson.GeometryCollection(geometries);
    }

    double[] convert(Coordinate coordinate) {
        return !Double.isNaN(coordinate.getZ()) && (new Double("0.0")).compareTo(coordinate.getZ()) != 0 ? new double[]{coordinate.x, coordinate.y, coordinate.getZ()} : new double[]{coordinate.x, coordinate.y};
    }

    double[][] convert(Coordinate[] coordinates) {
        double[][] array = new double[coordinates.length][];

        for(int i = 0; i < coordinates.length; ++i) {
            array[i] = this.convert(coordinates[i]);
        }

        return array;
    }
}
