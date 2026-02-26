//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.gago.mybatis.postgis.type;

import com.gago.geojson.Geometry;
import com.gago.geojson.jts2geojson.GeoJSONReader;
import com.gago.geojson.jts2geojson.GeoJSONWriter;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.postgis.PGgeometry;
import org.postgis.jts.JtsGeometry;

public abstract class AbstractGeometryTypeHandler<T extends Geometry> extends BaseTypeHandler<T> {
    public AbstractGeometryTypeHandler() {
    }

    public void setNonNullParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException {
        GeoJSONReader reader = new GeoJSONReader();
        org.locationtech.jts.geom.Geometry geometryTemp = reader.read(parameter);
        PGgeometry pGgeometry = new PGgeometry(geometryTemp.toString());
        ps.setObject(i, pGgeometry);
    }

    public T getNullableResult(ResultSet rs, String columnName) throws SQLException {
        PGgeometry pGgeometry = (PGgeometry)rs.getObject(columnName);
        if (pGgeometry == null) {
            return null;
        } else {
            org.locationtech.jts.geom.Geometry geometry = JtsGeometry.geomFromString(pGgeometry.getGeometry().toString());
            GeoJSONWriter writer = new GeoJSONWriter();
            return (T) writer.write(geometry);
        }
    }

    public T getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        PGgeometry pGgeometry = (PGgeometry)rs.getObject(columnIndex);
        if (pGgeometry == null) {
            return null;
        } else {
            org.locationtech.jts.geom.Geometry geometry = JtsGeometry.geomFromString(pGgeometry.getGeometry().toString());
            GeoJSONWriter writer = new GeoJSONWriter();
            return (T) writer.write(geometry);
        }
    }

    public T getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        PGgeometry pGgeometry = (PGgeometry)cs.getObject(columnIndex);
        if (pGgeometry == null) {
            return null;
        } else {
            org.locationtech.jts.geom.Geometry geometry = JtsGeometry.geomFromString(pGgeometry.getGeometry().toString());
            GeoJSONWriter writer = new GeoJSONWriter();
            return (T) writer.write(geometry);
        }
    }
}
