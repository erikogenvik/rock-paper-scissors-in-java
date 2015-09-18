package com.jayway.rps.infra.graphql;

import org.hibernate.validator.constraints.NotEmpty;

import java.util.Map;

/**
 * Created by erik on 2015-09-18.
 */
public class RelaySpec {
    @NotEmpty
    public String query;

    public Map<String, Object> variables;
}
