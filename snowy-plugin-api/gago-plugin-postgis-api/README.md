# Gago PostGIS API 插件

这是一个用于处理PostGIS几何数据类型的MyBatis类型处理器插件，支持GeoJSON格式和JTS几何库的双向转换。

## 功能特性

- ✅ **完整的GeoJSON支持** - 支持所有标准GeoJSON几何类型
- ✅ **MyBatis集成** - 提供完整的PostGIS几何类型处理器
- ✅ **JTS集成** - 支持与JTS几何库的双向转换
- ✅ **类型安全** - 强类型的Java对象模型
- ✅ **PostGIS兼容** - 完全兼容PostGIS数据库

## 支持的几何类型

### 基础几何类型
- `Point` - 点
- `LineString` - 线
- `Polygon` - 多边形

### 多重几何类型
- `MultiPoint` - 多点
- `MultiLineString` - 多线
- `MultiPolygon` - 多多边形

### 集合类型
- `GeometryCollection` - 几何集合
- `Feature` - GeoJSON特征对象
- `FeatureCollection` - GeoJSON特征集合

## 依赖项

```xml
<dependencies>
    <!-- Jackson JSON processing -->
    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-core</artifactId>
    </dependency>
    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-databind</artifactId>
    </dependency>
    
    <!-- MyBatis -->
    <dependency>
        <groupId>org.mybatis</groupId>
        <artifactId>mybatis</artifactId>
    </dependency>
    
    <!-- JTS Topology Suite -->
    <dependency>
        <groupId>org.locationtech.jts</groupId>
        <artifactId>jts-core</artifactId>
        <version>1.19.0</version>
    </dependency>
</dependencies>
```

## 使用示例

### 1. 在实体类中使用类型处理器

```java
import com.gago.geojson.Point;
import com.gago.mybatis.postgis.type.PointTypeHandler;
import com.baomidou.mybatisplus.annotation.TableField;

@TableName("location_table")
public class Location {
    
    @TableId
    private String id;
    
    @TableField(typeHandler = PointTypeHandler.class)
    private Point coordinate;
    
    @TableField(typeHandler = PolygonTypeHandler.class)
    private Polygon boundary;
    
    // getter and setter...
}
```

### 2. 创建几何对象

```java
import com.gago.geojson.*;

// 创建点
Point point = new Point(new double[]{116.3974, 39.9093}); // 经度,纬度

// 创建线
double[][] lineCoords = {
    {116.3974, 39.9093},
    {116.3975, 39.9094},
    {116.3976, 39.9095}
};
LineString line = new LineString(lineCoords);

// 创建多边形
double[][][] polygonCoords = {{
    {116.3974, 39.9093},
    {116.3975, 39.9094},
    {116.3976, 39.9095},
    {116.3974, 39.9093}  // 闭合
}};
Polygon polygon = new Polygon(polygonCoords);
```

### 3. GeoJSON与Java对象转换

```java
import com.gago.geojson.GeoJSONFactory;

// 从GeoJSON字符串创建对象
String geoJsonStr = "{\"type\":\"Point\",\"coordinates\":[116.3974,39.9093]}";
Point point = (Point) GeoJSONFactory.create(geoJsonStr);

// 转换为GeoJSON字符串
String jsonStr = point.toString();
```

### 4. JTS几何库集成

```java
import com.gago.geojson.jts2geojson.*;
import org.locationtech.jts.geom.Geometry;

// GeoJSON转JTS
GeoJSONReader reader = new GeoJSONReader();
Geometry jtsGeometry = reader.read(point);

// JTS转GeoJSON
GeoJSONWriter writer = new GeoJSONWriter();
Point geoJsonPoint = writer.convert((org.locationtech.jts.geom.Point) jtsGeometry);
```

### 5. MyBatis映射配置

```xml
<!-- 在MyBatis配置中注册类型处理器 -->
<typeHandlers>
    <typeHandler handler="com.gago.mybatis.postgis.type.PointTypeHandler"/>
    <typeHandler handler="com.gago.mybatis.postgis.type.PolygonTypeHandler"/>
    <typeHandler handler="com.gago.mybatis.postgis.type.LineStringTypeHandler"/>
</typeHandlers>
```

或者使用注解方式：

```java
@MappedTypes(Point.class)
@Component
public class MyPointTypeHandler extends PointTypeHandler {
    // 可以扩展自定义逻辑
}
```

## 数据库表结构示例

```sql
-- PostgreSQL + PostGIS
CREATE TABLE location_table (
    id VARCHAR(20) PRIMARY KEY,
    name VARCHAR(100),
    coordinate GEOMETRY(POINT, 4326),
    boundary GEOMETRY(POLYGON, 4326),
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 插入测试数据
INSERT INTO location_table (id, name, coordinate, boundary) VALUES 
('1', '北京天安门', ST_GeomFromText('POINT(116.3974 39.9093)', 4326), 
 ST_GeomFromText('POLYGON((116.396 39.908, 116.398 39.908, 116.398 39.910, 116.396 39.910, 116.396 39.908))', 4326));
```

## 核心类说明

### 几何对象类

| 类名 | 说明 | 坐标结构 |
|------|------|----------|
| `GeoJSON` | 所有GeoJSON对象的基类 | - |
| `Geometry` | 所有几何对象的基类 | - |
| `Point` | 点几何 | `double[]` |
| `LineString` | 线几何 | `double[][]` |
| `Polygon` | 多边形几何 | `double[][][]` |
| `MultiPoint` | 多点几何 | `double[][]` |
| `MultiLineString` | 多线几何 | `double[][][]` |
| `MultiPolygon` | 多多边形几何 | `double[][][][]` |
| `GeometryCollection` | 几何集合 | `Geometry[]` |
| `Feature` | GeoJSON特征 | 包含几何+属性 |
| `FeatureCollection` | 特征集合 | `Feature[]` |

### 类型处理器类

| 类名 | 说明 |
|------|------|
| `AbstractGeometryTypeHandler<T>` | 抽象类型处理器基类 |
| `GeometryTypeHandler` | 通用几何类型处理器 |
| `PointTypeHandler` | Point类型处理器 |
| `LineStringTypeHandler` | LineString类型处理器 |
| `PolygonTypeHandler` | Polygon类型处理器 |
| `MultiPointTypeHandler` | MultiPoint类型处理器 |
| `MultiPolygonTypeHandler` | MultiPolygon类型处理器 |

### 转换工具类

| 类名 | 说明 |
|------|------|
| `GeoJSONFactory` | GeoJSON对象工厂，用于解析JSON字符串 |
| `GeoJSONReader` | GeoJSON到JTS几何对象转换器 |
| `GeoJSONWriter` | JTS到GeoJSON对象转换器 |

## 注意事项

1. **坐标系统**: 默认使用WGS84坐标系统 (SRID: 4326)
2. **坐标顺序**: 使用 `[经度, 纬度]` 的顺序
3. **多边形闭合**: 多边形的外环和内环都必须是闭合的（首尾坐标相同）
4. **类型安全**: 使用泛型确保类型安全，避免运行时类型错误
5. **性能考虑**: 大量几何数据操作时，建议使用批量处理

## 版本信息

- **版本**: 3.0-RELEASE
- **基于**: mybatis-typehandlers-postgis-json
- **JTS版本**: 1.19.0
- **Jackson版本**: 继承自父项目
- **MyBatis版本**: 继承自父项目

## 许可证

本项目遵循与主项目相同的许可证协议。
