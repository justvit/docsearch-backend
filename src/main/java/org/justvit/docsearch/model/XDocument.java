package org.justvit.docsearch.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * External Document
 * Represents the structure element of file 'data.json'
 */
public class XDocument {

    @JsonProperty("Name")
    private String name;

    @JsonProperty("Type")
    private String type;

    @JsonProperty("Designed by")
    private String designedBy;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDesignedBy() {
        return designedBy;
    }

    public void setDesignedBy(String designedBy) {
        this.designedBy = designedBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        XDocument xDocument = (XDocument) o;

        if (!name.equals(xDocument.name)) return false;
        if (!type.equals(xDocument.type)) return false;
        return designedBy.equals(xDocument.designedBy);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + type.hashCode();
        result = 31 * result + designedBy.hashCode();
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("XDocument{");
        sb.append("name='").append(name).append('\'');
        sb.append(", type='").append(type).append('\'');
        sb.append(", designedBy='").append(designedBy).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
