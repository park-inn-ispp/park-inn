
package com.parkinn.model.paypal;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "given_name",
    "surname"
})
@Generated("jsonschema2pojo")
public class Name__1 {

    @JsonProperty("given_name")
    private String givenName;
    @JsonProperty("surname")
    private String surname;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("given_name")
    public String getGivenName() {
        return givenName;
    }

    @JsonProperty("given_name")
    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    @JsonProperty("surname")
    public String getSurname() {
        return surname;
    }

    @JsonProperty("surname")
    public void setSurname(String surname) {
        this.surname = surname;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
