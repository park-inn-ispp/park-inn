
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
    "gross_amount",
    "paypal_fee",
    "net_amount"
})
@Generated("jsonschema2pojo")
public class SellerReceivableBreakdown {

    @JsonProperty("gross_amount")
    private GrossAmount grossAmount;
    @JsonProperty("paypal_fee")
    private PaypalFee paypalFee;
    @JsonProperty("net_amount")
    private NetAmount netAmount;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("gross_amount")
    public GrossAmount getGrossAmount() {
        return grossAmount;
    }

    @JsonProperty("gross_amount")
    public void setGrossAmount(GrossAmount grossAmount) {
        this.grossAmount = grossAmount;
    }

    @JsonProperty("paypal_fee")
    public PaypalFee getPaypalFee() {
        return paypalFee;
    }

    @JsonProperty("paypal_fee")
    public void setPaypalFee(PaypalFee paypalFee) {
        this.paypalFee = paypalFee;
    }

    @JsonProperty("net_amount")
    public NetAmount getNetAmount() {
        return netAmount;
    }

    @JsonProperty("net_amount")
    public void setNetAmount(NetAmount netAmount) {
        this.netAmount = netAmount;
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
