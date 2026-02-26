//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.gago.geojson.jts2geojson;

import com.gago.geojson.GeoJSON;
import com.gago.geojson.GeoJSONFactory;
import com.gago.geojson.GeometryCollection;
import com.gago.geojson.LineString;
import com.gago.geojson.MultiLineString;
import com.gago.geojson.MultiPoint;
import com.gago.geojson.MultiPolygon;
import com.gago.geojson.Point;
import com.gago.geojson.Polygon;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.PrecisionModel;

public class GeoJSONReader {
    static final GeometryFactory FACTORY;

    public GeoJSONReader() {
    }

    public Geometry read(String json) {
        return this.read((String)json, (GeometryFactory)null);
    }

    public Geometry read(String json, GeometryFactory geomFactory) {
        GeoJSON geoJSON = GeoJSONFactory.create(json);
        return this.read(geoJSON, geomFactory);
    }

    public Geometry read(GeoJSON geoJSON) {
        return this.read((GeoJSON)geoJSON, (GeometryFactory)null);
    }

    public Geometry read(GeoJSON geoJSON, GeometryFactory geomFactory) {
        GeometryFactory factory;
        if (geomFactory != null) {
            factory = geomFactory;
        } else {
            factory = FACTORY;
        }

        if (geoJSON instanceof Point) {
            return this.convert((Point)geoJSON, factory);
        } else if (geoJSON instanceof LineString) {
            return this.convert((LineString)geoJSON, factory);
        } else if (geoJSON instanceof Polygon) {
            return this.convert((Polygon)geoJSON, factory);
        } else if (geoJSON instanceof MultiPoint) {
            return this.convert((MultiPoint)geoJSON, factory);
        } else if (geoJSON instanceof MultiLineString) {
            return this.convert((MultiLineString)geoJSON, factory);
        } else if (geoJSON instanceof MultiPolygon) {
            return this.convert((MultiPolygon)geoJSON, factory);
        } else if (geoJSON instanceof GeometryCollection) {
            return this.convert((GeometryCollection)geoJSON, factory);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    Geometry convert(Point point, GeometryFactory factory) {
        return factory.createPoint(this.convert(point.getCoordinates()));
    }

    Geometry convert(MultiPoint multiPoint, GeometryFactory factory) {
        return factory.createMultiPointFromCoords(this.convert(multiPoint.getCoordinates()));
    }

    Geometry convert(LineString lineString, GeometryFactory factory) {
        return factory.createLineString(this.convert(lineString.getCoordinates()));
    }

    Geometry convert(MultiLineString multiLineString, GeometryFactory factory) {
        int size = multiLineString.getCoordinates().length;
        org.locationtech.jts.geom.LineString[] lineStrings = new org.locationtech.jts.geom.LineString[size];

        for(int i = 0; i < size; ++i) {
            lineStrings[i] = factory.createLineString(this.convert(multiLineString.getCoordinates()[i]));
        }

        return factory.createMultiLineString(lineStrings);
    }

    Geometry convert(Polygon polygon, GeometryFactory factory) {
        return this.convertToPolygon(polygon.getCoordinates(), factory);
    }

    org.locationtech.jts.geom.Polygon convertToPolygon(double[][][] coordinates, GeometryFactory factory) {
        LinearRing shell = factory.createLinearRing(this.convert(coordinates[0]));
        if (coordinates.length <= 1) {
            return factory.createPolygon(shell);
        } else {
            int size = coordinates.length - 1;
            LinearRing[] holes = new LinearRing[size];

            for(int i = 0; i < size; ++i) {
                holes[i] = factory.createLinearRing(this.convert(coordinates[i + 1]));
            }

            return factory.createPolygon(shell, holes);
        }
    }

    Geometry convert(MultiPolygon multiPolygon, GeometryFactory factory) {
        int size = multiPolygon.getCoordinates().length;
        org.locationtech.jts.geom.Polygon[] polygons = new org.locationtech.jts.geom.Polygon[size];

        for(int i = 0; i < size; ++i) {
            polygons[i] = this.convertToPolygon(multiPolygon.getCoordinates()[i], factory);
        }

        return factory.createMultiPolygon(polygons);
    }

    Geometry convert(GeometryCollection gc, GeometryFactory factory) {
        int size = gc.getGeometries().length;
        Geometry[] geometries = new Geometry[size];

        for(int i = 0; i < size; ++i) {
            geometries[i] = this.read((GeoJSON)gc.getGeometries()[i], factory);
        }

        return factory.createGeometryCollection(geometries);
    }

    Coordinate convert(double[] c) {
        return c.length == 2 ? new Coordinate(c[0], c[1]) : new Coordinate(c[0], c[1], c[2]);
    }

    Coordinate[] convert(double[][] ca) {
        Coordinate[] coordinates = new Coordinate[ca.length];

        for(int i = 0; i < ca.length; ++i) {
            coordinates[i] = this.convert(ca[i]);
        }

        return coordinates;
    }

    static {
        FACTORY = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING));
    }
}
