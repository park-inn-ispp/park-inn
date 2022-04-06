
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
    "item_total",
    "shipping",
    "handling",
    "insurance",
    "shipping_discount",
    "discount"
})
@Generated("jsonschema2pojo")
public class Breakdown {

    @JsonProperty("item_total")
    private ItemTotal itemTotal;
    @JsonProperty("shipping")
    private Shipping shipping;
    @JsonProperty("handling")
    private Handling handling;
    @JsonProperty("insurance")
    private Insurance insurance;
    @JsonProperty("shipping_discount")
    private ShippingDiscount shippingDiscount;
    @JsonProperty("discount")
    private Discount discount;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("item_total")
    public ItemTotal getItemTotal() {
        return itemTotal;
    }

    @JsonProperty("item_total")
    public void setItemTotal(ItemTotal itemTotal) {
        this.itemTotal = itemTotal;
    }

    @JsonProperty("shipping")
    public Shipping getShipping() {
        return shipping;
    }

    @JsonProperty("shipping")
    public void setShipping(Shipping shipping) {
        this.shipping = shipping;
    }

    @JsonProperty("handling")
    public Handling getHandling() {
        return handling;
    }

    @JsonProperty("handling")
    public void setHandling(Handling handling) {
        this.handling = handling;
    }

    @JsonProperty("insurance")
    public Insurance getInsurance() {
        return insurance;
    }

    @JsonProperty("insurance")
    public void setInsurance(Insurance insurance) {
        this.insurance = insurance;
    }

    @JsonProperty("shipping_discount")
    public ShippingDiscount getShippingDiscount() {
        return shippingDiscount;
    }

    @JsonProperty("shipping_discount")
    public void setShippingDiscount(ShippingDiscount shippingDiscount) {
        this.shippingDiscount = shippingDiscount;
    }

    @JsonProperty("discount")
    public Discount getDiscount() {
        return discount;
    }

    @JsonProperty("discount")
    public void setDiscount(Discount discount) {
        this.discount = discount;
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
