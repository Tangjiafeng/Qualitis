package com.webank.wedatasphere.qualitis.rule.util;

import com.webank.wedatasphere.qualitis.constant.SpecCharEnum;
import com.webank.wedatasphere.qualitis.constants.QualitisConstants;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.metadata.client.MetaDataClient;
import com.webank.wedatasphere.qualitis.metadata.response.column.ColumnInfoDetail;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.rule.request.DataSourceEnvRequest;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

/**
 * @author allenzhou@webank.com
 * @date 2022/8/25 16:15
 */
public class DatasourceEnvUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatasourceEnvUtil.class);

    public static void constructDatasourceAndEnv(StringBuilder datasourceId, StringBuilder datasourceName, StringBuilder datasourceType
        , List<DataSourceEnvRequest> ruleDataSourceEnvs, StringBuilder envsStringBuffer, String userName, String clusterName
            , MetaDataClient metaDataClient, String database, String table, List<ColumnInfoDetail> cols) throws Exception {
        if (StringUtils.isNotBlank(datasourceId.toString())) {
            LOGGER.info("Find data source connect. Data source ID: " + datasourceId.toString());
            GeneralResponse<Map<String, Object>> response = metaDataClient.getDataSourceInfoDetail(clusterName, userName, Long.parseLong(datasourceId.toString()), null);

            Map<String, Object> dataSourceInfo = ((Map) response.getData().get("info"));
            String dataSourceInfoName = (String) dataSourceInfo.get("dataSourceName");
            String dataSourceInfoType = (String) ((Map) dataSourceInfo.get("dataSourceType")).get("name");
            datasourceName.append(dataSourceInfoName);
            datasourceType.append(dataSourceInfoType);
            if (StringUtils.isNotEmpty(database) && StringUtils.isNotEmpty(table)) {
                cols.addAll(metaDataClient.getColumnsByDataSourceName(clusterName, userName, dataSourceInfoName, database, table, null).getContent());
            }
            if (StringUtils.isNotEmpty(envsStringBuffer.toString())) {
                List<String> envIds = Arrays.stream(envsStringBuffer.toString().split(SpecCharEnum.COMMA.getValue())).collect(Collectors.toList());
                // Check envs existence from dataource and set to request.
                List<String> realEnvIds = (List<String>) ((Map) dataSourceInfo.get("connectParams")).get("envIdArray");

                if (! realEnvIds.containsAll(envIds)) {
                    throw new UnExpectedRequestException("Datasource env parameters {&DOES_NOT_EXIST}");
                }

                for (String currentEnvId : envIds) {
                    DataSourceEnvRequest dataSourceEnvRequest = new DataSourceEnvRequest();
                    GeneralResponse<Map<String, Object>> envResMap = metaDataClient.getDatasourceEnvById(clusterName, userName, Long.parseLong(currentEnvId));
                    Map<String, Object> envMap = (Map) envResMap.getData().get("env");
                    String envName = removePrefixOfEnvName(Integer.valueOf(datasourceId.toString()), (String) envMap.get("envName"));
                    dataSourceEnvRequest.setEnvName(envName);

                    dataSourceEnvRequest.setEnvId(Long.parseLong(currentEnvId));
                    ruleDataSourceEnvs.add(dataSourceEnvRequest);
                }
            } else {
                LOGGER.warn("Missing datasource env parameters");
            }
        } else if (StringUtils.isNotBlank(datasourceName.toString())) {
            LOGGER.info("Find data source connect. Data source name: " + datasourceName.toString());
            GeneralResponse<Map<String, Object>> response = metaDataClient.getDataSourceInfoDetailByName(clusterName, userName, datasourceName.toString());
            Map<String, Object> dataSourceInfo = ((Map) response.getData().get("info"));
            String dataSourceInfoName = (String) dataSourceInfo.get("dataSourceName");
            String dataSourceInfoType = (String) ((Map) dataSourceInfo.get("dataSourceType")).get("name");
            Integer currentDataSourceId = (Integer) dataSourceInfo.get("id");
            datasourceType.append(dataSourceInfoType);
            datasourceId.append(currentDataSourceId);

            if (StringUtils.isNotEmpty(database) && StringUtils.isNotEmpty(table)) {
                cols.addAll(metaDataClient.getColumnsByDataSourceName(clusterName, userName, dataSourceInfoName, database, table, null).getContent());
            }

            if (StringUtils.isNotEmpty(envsStringBuffer.toString())) {
                List<String> envNames = Arrays.stream(envsStringBuffer.toString().split(SpecCharEnum.COMMA.getValue())).collect(Collectors.toList());
                // Check envs existence from dataource and set to request.
                List<String> realEnvIds = (List<String>) ((Map) dataSourceInfo.get("connectParams")).get("envIdArray");
                for (String currentEnvId : realEnvIds) {
                    DataSourceEnvRequest dataSourceEnvRequest = new DataSourceEnvRequest();
                    GeneralResponse<Map<String, Object>> envResMap = metaDataClient.getDatasourceEnvById(clusterName, userName, Long.parseLong(currentEnvId));
                    Map<String, Object> envMap = (Map) envResMap.getData().get("env");
                    String envName = removePrefixOfEnvName(currentDataSourceId, (String) envMap.get("envName"));
//                    判断用户指定的环境名称是否匹配实际保存落库的环境名称
                    if (! envNames.contains(envName)) {
                        continue;
                    }
                    dataSourceEnvRequest.setEnvName(envName);
                    dataSourceEnvRequest.setEnvId(Long.parseLong(currentEnvId));

                    ruleDataSourceEnvs.add(dataSourceEnvRequest);
                }
                if (envNames.size() != ruleDataSourceEnvs.size()) {
                    throw new UnExpectedRequestException("Datasource env parameters {&DOES_NOT_EXIST}");
                }
            } else {
                LOGGER.warn("Missing datasource env parameters");
            }
        }

    }

    private static String removePrefixOfEnvName(Integer dataSourceId, String linkisEnvName) {
        return linkisEnvName.replace(dataSourceId + SpecCharEnum.BOTTOM_BAR.getValue(), "");
    }

    public static void getDatasourceIdOrName(StringBuilder datasourceId, StringBuilder datasourceName, StringBuilder envsStringBuffer, StringBuilder dbAndTableOrCluster) {
        Matcher matcherId = QualitisConstants.DATA_SOURCE_ID.matcher(dbAndTableOrCluster.toString().toUpperCase());
        Matcher matcherName = QualitisConstants.DATA_SOURCE_NAME.matcher(dbAndTableOrCluster.toString().toUpperCase());
        while (matcherId.find()) {
            String group = matcherId.group();
            datasourceId.append(group.replace(".(", "").replace(")", "").split("=")[1]);

            int startIndex = dbAndTableOrCluster.toString().toUpperCase().indexOf(group);
            dbAndTableOrCluster.delete(startIndex, startIndex + group.length());
            handleDatasourceEnvs(envsStringBuffer, datasourceId);
        }
        if (StringUtils.isBlank(datasourceId.toString())) {
            while (matcherName.find()) {
                String group = matcherName.group();
                int startIndex = dbAndTableOrCluster.toString().toUpperCase().indexOf(group);
                String replaceStr = dbAndTableOrCluster.substring(startIndex, startIndex + group.length());
                replaceStr = replaceStr.replace(".(", "");
                replaceStr = replaceStr.substring(0, replaceStr.lastIndexOf(")"));
                datasourceName.append(replaceStr.split("=")[1]);
                dbAndTableOrCluster.delete(startIndex, startIndex + group.length());
                handleDatasourceEnvs(envsStringBuffer, datasourceName);
            }
        }
    }

    public static void main(String[] args) {
        StringBuilder datasourceId = new StringBuilder();
        StringBuilder datasourceName = new StringBuilder();
        StringBuilder envsStringBuffer = new StringBuilder();
        getDataSourceAndEnv(datasourceId, datasourceName, envsStringBuffer, "NAME=testDcnNo{asdasd}");
        System.out.println("datasourceName: " + datasourceName);
        System.out.println("envsStringBuffer: " + envsStringBuffer);
    }

    /**
     * Extract envs and (dataSourceId or dataSourceName) from datasourceExpress
     * @param datasourceId
     * @param datasourceName
     * @param envsStringBuffer
     * @param datasourceExpress
     */
    public static void getDataSourceAndEnv(StringBuilder datasourceId, StringBuilder datasourceName, StringBuilder envsStringBuffer, String datasourceExpress) {
        Matcher datasourceNameMatcher = QualitisConstants.DATASOURCE_NAME_ENV_REGEX.matcher(datasourceExpress.toUpperCase());
        Matcher datasourceIdMatcher = QualitisConstants.DATASOURCE_ID_ENV_REGEX.matcher(datasourceExpress.toUpperCase());
        if (datasourceNameMatcher.find()) {
            // 提取"data_source"和"env"字段
            String dataSource = datasourceNameMatcher.group();
            dataSource = dataSource.replace("NAME=", "");
            if (dataSource.indexOf(SpecCharEnum.LEFT_BIG_BRACKET.getValue()) != -1) {
                datasourceName.append(StringUtils.substring(dataSource, 0, dataSource.indexOf(SpecCharEnum.LEFT_BIG_BRACKET.getValue())));
                envsStringBuffer.append(StringUtils.substring(dataSource, dataSource.indexOf(SpecCharEnum.LEFT_BIG_BRACKET.getValue()) + 1, dataSource.indexOf(SpecCharEnum.RIGHT_BIG_BRACKET.getValue())));
            } else  {
                datasourceName.append(dataSource);
            }
        }
        if (datasourceIdMatcher.find()) {
            // 提取"data_source"和"env"字段
            String dataSource = datasourceIdMatcher.group();
            dataSource = dataSource.replace("ID=", "");

            datasourceId.append(StringUtils.substring(dataSource, 0, dataSource.indexOf(SpecCharEnum.LEFT_BIG_BRACKET.getValue())));
            envsStringBuffer.append(StringUtils.substring(dataSource, dataSource.indexOf(SpecCharEnum.LEFT_BIG_BRACKET.getValue()) + 1, dataSource.indexOf(SpecCharEnum.RIGHT_BIG_BRACKET.getValue())));
        }
    }

    /**
     *
     * @param envsStringBuffer
     * @param datasourceIdOrName {env_name}
     * @return
     */
    private static String handleDatasourceEnvs(StringBuilder envsStringBuffer, StringBuilder datasourceIdOrName) {
        if (datasourceIdOrName.toString().contains(SpecCharEnum.LEFT_BIG_BRACKET.getValue()) && datasourceIdOrName.toString().contains(SpecCharEnum.RIGHT_BIG_BRACKET.getValue())) {
            int lastIndex = datasourceIdOrName.indexOf(SpecCharEnum.RIGHT_BIG_BRACKET.getValue());
            int index = datasourceIdOrName.indexOf(SpecCharEnum.LEFT_BIG_BRACKET.getValue());
            String envsStr = datasourceIdOrName.substring(index + 1, lastIndex);
            datasourceIdOrName.delete(index, lastIndex + 1);
            envsStringBuffer.append(envsStr);
        }
        return "";
    }
}
