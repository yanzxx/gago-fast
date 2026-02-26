//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.gago.geojson;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class GeoJSONFactory {
    private static final ObjectMapper mapper = new ObjectMapper();

    public GeoJSONFactory() {
    }

    public static GeoJSON create(String json) {
        try {
            JsonNode node = mapper.readTree(json);
            String type = node.get("type").asText();
            if (type.equals("FeatureCollection")) {
                return readFeatureCollection(node);
            } else {
                return (GeoJSON)(type.equals("Feature") ? readFeature(node) : readGeometry(node, type));
            }
        } catch (Exception var3) {
            throw new RuntimeException(var3);
        }
    }

    private static FeatureCollection readFeatureCollection(JsonNode node) throws JsonParseException, JsonMappingException, IOException, ClassNotFoundException {
        Iterator<JsonNode> it = node.get("features").iterator();
        List<Feature> features = new ArrayList();

        while(it.hasNext()) {
            JsonNode jFeature = (JsonNode)it.next();
            features.add(readFeature(jFeature));
        }

        return new FeatureCollection((Feature[])features.toArray(new Feature[features.size()]));
    }

    private static Feature readFeature(JsonNode node) throws JsonParseException, JsonMappingException, IOException, ClassNotFoundException {
        JsonNode geometryNode = node.get("geometry");
        JavaType javaType = mapper.getTypeFactory().constructMapType(Map.class, String.class, Object.class);
        Object id = node.get("id");
        Map<String, Object> properties = (Map)mapper.readValue(node.get("properties").traverse(), javaType);
        Geometry geometry = readGeometry(geometryNode);
        return new Feature(id, geometry, properties);
    }

    private static Geometry readGeometry(JsonNode node) throws JsonParseException, JsonMappingException, IOException, ClassNotFoundException {
        if (!node.isNull()) {
            String type = node.get("type").asText();
            return readGeometry(node, type);
        } else {
            return null;
        }
    }

    private static Geometry readGeometry(JsonNode node, String type) throws JsonParseException, JsonMappingException, IOException, ClassNotFoundException {
        return (Geometry)mapper.readValue(node.traverse(), Class.forName("com.gago.geojson." + type));
    }
}
