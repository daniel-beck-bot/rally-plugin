package com.jenkins.plugins.rally.utils;

import com.jenkins.plugins.rally.RallyAssetNotFoundException;
import com.jenkins.plugins.rally.RallyException;
import com.rallydev.rest.RallyRestApi;
import com.rallydev.rest.request.QueryRequest;
import com.rallydev.rest.response.QueryResponse;
import com.rallydev.rest.util.Fetch;
import com.rallydev.rest.util.QueryFilter;

import java.io.IOException;

public class RallyQueryBuilder {
    private QueryRequest query;
    private RallyRestApi rallyRestApi;

    public static RallyQueryBuilder createQueryFrom(RallyRestApi restApi) {
        RallyQueryBuilder rallyQueryBuilder = new RallyQueryBuilder();
        rallyQueryBuilder.rallyRestApi = restApi;
        return rallyQueryBuilder;
    }

    public RallyQueryBuilder ofType(String type) {
        this.query = new QueryRequest(type);
        return this;
    }

    public RallyQueryBuilder withFetchValues(String... values) {
        this.query.setFetch(new Fetch(values));
        return this;
    }

    public RallyQueryBuilder inWorkspace(String workspace) {
        this.query.setWorkspace(workspace);
        return this;
    }

    public RallyQueryBuilder withQueryFilter(String field, String operator, String value) {
        this.query.setQueryFilter(new QueryFilter(field, operator, value));
        return this;
    }

    public String andExecuteReturningRef() throws RallyException {
        try {
            QueryResponse scmQueryResponse = this.rallyRestApi.query(this.query);

            if (scmQueryResponse.getTotalResultCount() == 0) {
                throw new RallyAssetNotFoundException();
            }

            return scmQueryResponse.getResults().get(0).getAsJsonObject().get("_ref").getAsString();
        } catch (IOException exception) {
            throw new RallyException(exception);
        }
    }
}