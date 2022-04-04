
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
    "address_line_1",
    "admin_area_2",
    "admin_area_1",
    "postal_code",
    "country_code"
})
@Generated("jsonschema2pojo")
public class Address {

    @JsonProperty("address_line_1")
    private String addressLine1;
    @JsonProperty("admin_area_2")
    private String adminArea2;
    @JsonProperty("admin_area_1")
    private String adminArea1;
    @JsonProperty("postal_code")
    private String postalCode;
    @JsonProperty("country_code")
    private String countryCode;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("address_line_1")
    public String getAddressLine1() {
        return addressLine1;
    }

    @JsonProperty("address_line_1")
    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    @JsonProperty("admin_area_2")
    public String getAdminArea2() {
        return adminArea2;
    }

    @JsonProperty("admin_area_2")
    public void setAdminArea2(String adminArea2) {
        this.adminArea2 = adminArea2;
    }

    @JsonProperty("admin_area_1")
    public String getAdminArea1() {
        return adminArea1;
    }

    @JsonProperty("admin_area_1")
    public void setAdminArea1(String adminArea1) {
        this.adminArea1 = adminArea1;
    }

    @JsonProperty("postal_code")
    public String getPostalCode() {
        return postalCode;
    }

    @JsonProperty("postal_code")
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    @JsonProperty("country_code")
    public String getCountryCode() {
        return countryCode;
    }

    @JsonProperty("country_code")
    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
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
