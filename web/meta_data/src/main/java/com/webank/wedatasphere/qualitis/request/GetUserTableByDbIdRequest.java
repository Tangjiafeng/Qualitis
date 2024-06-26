/*
 * Copyright 2019 WeBank
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.webank.wedatasphere.qualitis.request;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author howeye
 */
public class GetUserTableByDbIdRequest {

    private static final int DEFAULT_START_INDEX = 0;
    private static final int DEFAULT_PAGE_SIZE = 20;

    @JsonProperty("start_index")
    private Integer startIndex;
    @JsonProperty("page_size")
    private Integer pageSize;
    @JsonProperty("cluster_name")
    private String clusterName;
    @JsonProperty("db_name")
    private String dbName;
    @JsonProperty("proxy_user")
    private String proxyUser;

    public GetUserTableByDbIdRequest() {
        startIndex = DEFAULT_START_INDEX;
        pageSize = DEFAULT_PAGE_SIZE;
    }

    public GetUserTableByDbIdRequest(Integer startIndex, Integer pageSize, String clusterName, String dbName) {
        this.startIndex = startIndex;
        this.pageSize = pageSize;
        this.clusterName = clusterName;
        this.dbName = dbName;
    }

    public Integer getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(Integer startIndex) {
        this.startIndex = startIndex;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getProxyUser() {
        return proxyUser;
    }

    public void setProxyUser(String proxyUser) {
        this.proxyUser = proxyUser;
    }

    @Override
    public String toString() {
        return "GetUserTableByDbIdRequest{" +
                "startIndex=" + startIndex +
                ", pageSize=" + pageSize +
                ", clusterName='" + clusterName + '\'' +
                ", dbName='" + dbName + '\'' +
                ", proxyUser='" + proxyUser + '\'' +
                '}';
    }
}
