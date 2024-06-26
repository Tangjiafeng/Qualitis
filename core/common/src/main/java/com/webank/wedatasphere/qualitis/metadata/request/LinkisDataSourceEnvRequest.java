package com.webank.wedatasphere.qualitis.metadata.request;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashMap;
import java.util.Map;

/**
 * @author v_minminghe@webank.com
 * @date 2023-05-12 11:00
 * @description
 */
public class LinkisDataSourceEnvRequest {

    private Long id;
    private String envName;
    private String envDesc;
    private Long dataSourceTypeId;
    private String databaseInstance;
    private String dcnNum;
    private String logicArea;
    private Map<String, Object> connectParams = new HashMap<>();
    @JsonIgnore
    private LinkisConnectParamsRequest connectParamsRequest;

    public String getDcnNum() {
        return dcnNum;
    }

    public void setDcnNum(String dcnNum) {
        this.dcnNum = dcnNum;
    }

    public String getLogicArea() {
        return logicArea;
    }

    public void setLogicArea(String logicArea) {
        this.logicArea = logicArea;
    }

    public String getDatabaseInstance() {
        return databaseInstance;
    }

    public void setDatabaseInstance(String databaseInstance) {
        this.databaseInstance = databaseInstance;
    }

    public Long getDataSourceTypeId() {
        return dataSourceTypeId;
    }

    public void setDataSourceTypeId(Long dataSourceTypeId) {
        this.dataSourceTypeId = dataSourceTypeId;
    }

    public String getEnvName() {
        return envName;
    }

    public void setEnvName(String envName) {
        this.envName = envName;
    }

    public String getEnvDesc() {
        return envDesc;
    }

    public void setEnvDesc(String envDesc) {
        this.envDesc = envDesc;
    }

    public Map<String, Object> getConnectParams() {
        return connectParams;
    }

    public void setConnectParams(Map<String, Object> connectParams) {
        this.connectParams = connectParams;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LinkisConnectParamsRequest getConnectParamsRequest() {
        return connectParamsRequest;
    }

    public void setConnectParamsRequest(LinkisConnectParamsRequest connectParamsRequest) {
        this.connectParamsRequest = connectParamsRequest;
    }
}
