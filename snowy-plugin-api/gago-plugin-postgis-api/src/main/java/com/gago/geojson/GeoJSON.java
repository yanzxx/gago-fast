//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.gago.geojson;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;

public abstract class GeoJSON {
    private static final ObjectMapper mapper = new ObjectMapper();
    @JsonProperty("type")
    private String type;

    @JsonCreator
    public GeoJSON() {
        this.setType(this.getClass().getSimpleName());
    }

    public String toString() {
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonGenerationException var2) {
            return "Unhandled exception occured when serializing this instance";
        } catch (JsonMappingException var3) {
            return "Unhandled exception occured when serializing this instance";
        } catch (IOException var4) {
            return "Unhandled exception occured when serializing this instance";
        }
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
