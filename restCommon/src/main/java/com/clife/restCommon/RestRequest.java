package com.clife.restCommon;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

public class RestRequest {
    public String controllerName;
    public RestController controller;
    public String objectId;
    public RestRequestType type;
    public Map<String, String> parentFilter;
    public Map<String, List<String>> queryParams;
    public Object requestBody;
    public HttpServletRequest originalServletRequest;

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
