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
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;

/**
 * @author howeye
 */
public class DataSourceExecutionRequest {

    private String database;
    private String cluster;
    private String table;

    private String executionUser;
    private Boolean crossTable;
    private String createUser;

    @JsonProperty("execution_param")
    private String executionParam;


    @JsonProperty("fps_hash")
    private String fpsHashValue;
    @JsonProperty("fps_file_id")
    private String fpsFileId;
    @JsonProperty("env_names")
    private String envNames;

    @JsonProperty("cluster_name")
    private String clusterName;
    @JsonProperty("startup_param_name")
    private String startupParamName;
    @JsonProperty("set_flag")
    private String setFlag;

    @JsonProperty("dynamic_partition_bool")
    private boolean dyNamicPartition;
    @JsonProperty("dynamic_partition_prefix")
    private String dyNamicPartitionPrefix;
    @JsonProperty("bool_async")
    private boolean async;

    @JsonProperty("job_id")
    private String jobId;

    @JsonProperty("start_time")
    private String startTime;
    @JsonProperty("end_time")
    private String endTime;

    @JsonProperty("split_by")
    private String splitBy;

    @JsonProperty("engine_reuse")
    private Boolean engineReuse;

    public DataSourceExecutionRequest() {
        crossTable = false;
    }

    public String getCluster() {
        return cluster;
    }

    public void setCluster(String cluster) {
        this.cluster = cluster;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getExecutionParam() {
        return executionParam;
    }

    public void setExecutionParam(String executionParam) {
        this.executionParam = executionParam;
    }

    public String getExecutionUser() {
        return executionUser;
    }

    public void setExecutionUser(String executionUser) {
        this.executionUser = executionUser;
    }

    public Boolean getCrossTable() {
        return crossTable;
    }

    public void setCrossTable(Boolean crossTable) {
        this.crossTable = crossTable;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getFpsFileId() {
        return fpsFileId;
    }

    public void setFpsFileId(String fpsFileId) {
        this.fpsFileId = fpsFileId;
    }

    public String getFpsHashValue() {
        return fpsHashValue;
    }

    public void setFpsHashValue(String fpsHashValue) {
        this.fpsHashValue = fpsHashValue;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getStartupParamName() {
        return startupParamName;
    }

    public void setStartupParamName(String startupParamName) {
        this.startupParamName = startupParamName;
    }

    public String getSetFlag() {
        return setFlag;
    }

    public void setSetFlag(String setFlag) {
        this.setFlag = setFlag;
    }

    public boolean getDyNamicPartition() {
        return dyNamicPartition;
    }

    public void setDyNamicPartition(boolean dyNamicPartition) {
        this.dyNamicPartition = dyNamicPartition;
    }

    public String getDyNamicPartitionPrefix() {
        return dyNamicPartitionPrefix;
    }

    public void setDyNamicPartitionPrefix(String dyNamicPartitionPrefix) {
        this.dyNamicPartitionPrefix = dyNamicPartitionPrefix;
    }

    public String getEnvNames() {
        return envNames;
    }

    public void setEnvNames(String envNames) {
        this.envNames = envNames;
    }

    public boolean getAsync() {
        return async;
    }

    public void setAsync(boolean async) {
        this.async = async;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getSplitBy() {
        return splitBy;
    }

    public void setSplitBy(String splitBy) {
        this.splitBy = splitBy;
    }

    public Boolean getEngineReuse() {
        return engineReuse;
    }

    public void setEngineReuse(Boolean engineReuse) {
        this.engineReuse = engineReuse;
    }

    public static void checkRequest(DataSourceExecutionRequest request) throws UnExpectedRequestException {
        CommonChecker.checkObject(request, "Request");
        CommonChecker.checkString(request.getCluster(), "Cluster");
        CommonChecker.checkString(request.getDatabase(), "Database");
        CommonChecker.checkString(request.getTable(), "Table");
        CommonChecker.checkString(request.getExecutionUser(), "Execution_user");
        CommonChecker.checkString(request.getCreateUser(), "Create_user");

        RuleListExecutionRequest.sameParameterVerificationMethod(request.getExecutionParam(), "{&EXECUTION_VARIABLES_HAVE_THE_SAME_VARIABLE_NAME}: ");
        RuleListExecutionRequest.sameParameterVerificationMethod(request.getStartupParamName(), "{&DYNAMIC_ENGINE_HAVE_THE_SAME_VARIABLE_NAME}: ");
    }
}
