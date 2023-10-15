package resource.data;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class Row {
    private String name;
    private Map<String, Object> fields;


    public Row() {
        this.fields = new HashMap<>();
    }

    public void addField(String fieldName, Object value) {
        this.fields.put(fieldName, value);
    }

    public void removeField(String fieldName) {
        this.fields.remove(fieldName);
    }

    public void setFields(String parametar, Object o) {
    }
}
