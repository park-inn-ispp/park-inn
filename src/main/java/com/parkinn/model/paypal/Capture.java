
package com.parkinn.model.paypal;

import java.util.HashMap;
import java.util.List;
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
    "id",
    "status",
    "amount",
    "final_capture",
    "seller_protection",
    "seller_receivable_breakdown",
    "links",
    "create_time",
    "update_time"
})
@Generated("jsonschema2pojo")
public class Capture {

    @JsonProperty("id")
    private String id;
    @JsonProperty("status")
    private String status;
    @JsonProperty("amount")
    private Amount__1 amount;
    @JsonProperty("final_capture")
    private Boolean finalCapture;
    @JsonProperty("seller_protection")
    private SellerProtection sellerProtection;
    @JsonProperty("seller_receivable_breakdown")
    private SellerReceivableBreakdown sellerReceivableBreakdown;
    @JsonProperty("links")
    private List<Link> links = null;
    @JsonProperty("create_time")
    private String createTime;
    @JsonProperty("update_time")
    private String updateTime;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
    }

    @JsonProperty("amount")
    public Amount__1 getAmount() {
        return amount;
    }

    @JsonProperty("amount")
    public void setAmount(Amount__1 amount) {
        this.amount = amount;
    }

    @JsonProperty("final_capture")
    public Boolean getFinalCapture() {
        return finalCapture;
    }

    @JsonProperty("final_capture")
    public void setFinalCapture(Boolean finalCapture) {
        this.finalCapture = finalCapture;
    }

    @JsonProperty("seller_protection")
    public SellerProtection getSellerProtection() {
        return sellerProtection;
    }

    @JsonProperty("seller_protection")
    public void setSellerProtection(SellerProtection sellerProtection) {
        this.sellerProtection = sellerProtection;
    }

    @JsonProperty("seller_receivable_breakdown")
    public SellerReceivableBreakdown getSellerReceivableBreakdown() {
        return sellerReceivableBreakdown;
    }

    @JsonProperty("seller_receivable_breakdown")
    public void setSellerReceivableBreakdown(SellerReceivableBreakdown sellerReceivableBreakdown) {
        this.sellerReceivableBreakdown = sellerReceivableBreakdown;
    }

    @JsonProperty("links")
    public List<Link> getLinks() {
        return links;
    }

    @JsonProperty("links")
    public void setLinks(List<Link> links) {
        this.links = links;
    }

    @JsonProperty("create_time")
    public String getCreateTime() {
        return createTime;
    }

    @JsonProperty("create_time")
    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    @JsonProperty("update_time")
    public String getUpdateTime() {
        return updateTime;
    }

    @JsonProperty("update_time")
    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
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
