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

package com.webank.wedatasphere.qualitis.rule.adapter;

import com.google.common.collect.ImmutableMap;
import com.webank.wedatasphere.qualitis.constant.SpecCharEnum;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.rule.constant.ContrastTypeEnum;
import com.webank.wedatasphere.qualitis.rule.constant.MappingOperationEnum;
import com.webank.wedatasphere.qualitis.rule.constant.TemplateInputTypeEnum;
import com.webank.wedatasphere.qualitis.rule.constant.TemplateRegexpTypeEnum;
import com.webank.wedatasphere.qualitis.rule.dao.repository.RegexpExprMapperRepository;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateMidTableInputMeta;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateRegexpExpr;
import com.webank.wedatasphere.qualitis.rule.request.DataSourceColumnRequest;
import com.webank.wedatasphere.qualitis.rule.request.DataSourceRequest;
import com.webank.wedatasphere.qualitis.rule.request.TemplateArgumentRequest;
import com.webank.wedatasphere.qualitis.rule.request.multi.MultiDataSourceConfigRequest;
import com.webank.wedatasphere.qualitis.rule.request.multi.MultiDataSourceJoinColumnRequest;
import com.webank.wedatasphere.qualitis.rule.request.multi.MultiDataSourceJoinConfigRequest;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author howeye
 */
@Component
public class AutoArgumentAdapter {

    private static final String[] SPECIAL_WORD_LIST = {"\\", "$", "(", ")", "*", "+", ".", "[", "]", "?", "^", "{", "}", "|"};
    private static final List<String> EXCEL_TYPE = Arrays.asList(".xlsx", ".xls");
    private static final String DIFF_COUNT = "qualitis_mul_db_accuracy_num";

    @Autowired
    private RegexpExprMapperRepository regexpExprMapperRepository;

    /**
     * Get auto adapt value from db
     * 1.Get input type from template
     * 2.If Type equals to fields, database, tables, list, condition, fixed_value, return values in request
     * 3.If Type equals to field_concat, return string that connected with comma
     * 4.If Type equals to regex, return values in request if replaceByRequest = true, else return value in database
     * @param templateMidTableInputMeta
     * @return
     */
    public Map<String, String> getAdaptValue(TemplateMidTableInputMeta templateMidTableInputMeta, DataSourceRequest dataSourceRequest,
                                             List<TemplateArgumentRequest> templateArgumentRequests) throws UnExpectedRequestException {
        Integer inputType = templateMidTableInputMeta.getInputType();
        if (inputType.equals(TemplateInputTypeEnum.LIST.getCode()) || inputType.equals(TemplateInputTypeEnum.FIXED_VALUE.getCode()) || inputType.equals(TemplateInputTypeEnum.INTERMEDIATE_EXPRESSION.getCode())
                || inputType.equals(TemplateInputTypeEnum.VALUE_RANGE.getCode()) || inputType.equals(TemplateInputTypeEnum.MAXIMUM.getCode()) || inputType.equals(TemplateInputTypeEnum.MINIMUM.getCode())
                || inputType.equals(TemplateInputTypeEnum.FRONT_CONDITION.getCode()) || inputType.equals(TemplateInputTypeEnum.BEHIND_CONDITION.getCode())
                || inputType.equals(TemplateInputTypeEnum.STANDARD_VALUE_EXPRESSION.getCode()) || inputType.equals(TemplateInputTypeEnum.EXPRESSION.getCode())) {
            // Get value from request
            return ImmutableMap.of("value", findRequestById(templateMidTableInputMeta.getId(), templateArgumentRequests).getArgumentValue());
        } else if (inputType.equals(TemplateInputTypeEnum.REGEXP.getCode())) {
            // Get value from request if replaceByRequest = true, else return value in database
            return getRegexpValue(templateMidTableInputMeta, templateArgumentRequests);
        } else if (inputType.equals(TemplateInputTypeEnum.FIELD_REPLACE_NULL_CONCAT.getCode())) {
            // Get string that connected with comma
            return getFieldConcat(dataSourceRequest);
        } else if (inputType.equals(TemplateInputTypeEnum.FIELD.getCode()) && Boolean.TRUE.equals(templateMidTableInputMeta.getFieldMultipleChoice())) {
            // Get string that connected with comma
            return getFieldConcat(dataSourceRequest);
        } else {
            // Get value from request
            return findValueFromDataSourceRequest(inputType, dataSourceRequest);
        }
    }

    public Map<String, String> getMultiSourceAdaptValue(Rule rule, TemplateMidTableInputMeta templateMidTableInputMeta, String clusterName, MultiDataSourceConfigRequest firstDataSource
        , MultiDataSourceConfigRequest secondDataSource, List<MultiDataSourceJoinConfigRequest> mappings, List<DataSourceColumnRequest> colNames, String filter, String contrastType, String connFieldOriginValue, String compFieldOriginValue
        , String leftMetricSql, String rightMetricSql) {

        Integer inputType = templateMidTableInputMeta.getInputType();
        if (inputType.equals(TemplateInputTypeEnum.CONTRAST_TYPE.getCode())) {
            // Contrast type
            rule.setContrastType(Integer.parseInt(StringUtils.isEmpty(contrastType) ? "2" : contrastType));
            return ImmutableMap.of("value", ContrastTypeEnum.getJoinType(Integer.parseInt(StringUtils.isEmpty(contrastType) ? "2" : contrastType)));
        } else if (inputType.equals(TemplateInputTypeEnum.COMPARISON_RESULTS_FOR_FILTER.getCode())) {
            // Comparison results for filter
            filter = StringUtils.isEmpty(filter) ? "" : filter;
            return ImmutableMap.of("value", filter);
        } else if (inputType.equals(TemplateInputTypeEnum.CONNECT_FIELDS.getCode())) {
            // Connect fields
            return ImmutableMap.of("value", generateAndConcatStatement(mappings), "originValue", StringUtils.isNotBlank(connFieldOriginValue)?connFieldOriginValue:"");
        } else if (inputType.equals(TemplateInputTypeEnum.COMPARISON_FIELD_SETTINGS.getCode())) {
            // Comparison field settings
            return ImmutableMap.of("value", generateAndConcatStatement(mappings), "originValue", StringUtils.isNotBlank(compFieldOriginValue)?compFieldOriginValue:"");
        } else if (inputType.equals(TemplateInputTypeEnum.LEFT_COLLECT_SQL.getCode())) {
            Map<String, String> map = new HashMap<>(4);
            map.put("value", leftMetricSql);
            map.put("clusterName", firstDataSource.getClusterName());
            return map;
        } else if (inputType.equals(TemplateInputTypeEnum.RIGHT_COLLECT_SQL.getCode())) {
            Map<String, String> map = new HashMap<>(4);
            map.put("value", rightMetricSql);
            map.put("clusterName", secondDataSource.getClusterName());
            return map;
        } else {
            // Database, tables, fields
            return findValueFromMultiDataSourceConfig(templateMidTableInputMeta, clusterName, firstDataSource, secondDataSource, mappings, colNames);
        }

    }

    private Map<String, String> findValueFromMultiDataSourceConfig(TemplateMidTableInputMeta templateMidTableInputMeta, String clusterName, MultiDataSourceConfigRequest firstDataSource, MultiDataSourceConfigRequest secondDataSource, List<MultiDataSourceJoinConfigRequest> mappings, List<DataSourceColumnRequest> colNames) {
        Map<String, String> map = new HashMap<>(4);
        String firstFileType = firstDataSource.getFileType();
        String firstDbName = firstDataSource.getDbName();
        if (StringUtils.isNotBlank(firstFileType)  && ! EXCEL_TYPE.contains(firstFileType)) {
            firstDbName = "";
        }
        String secondFileType = secondDataSource.getFileType();
        String secondDbName = secondDataSource.getDbName();
        if (StringUtils.isNotBlank(secondFileType) && ! EXCEL_TYPE.contains(secondFileType)) {
            secondDbName = "";
        }
        Integer inputType = templateMidTableInputMeta.getInputType();
        if (inputType.equals(TemplateInputTypeEnum.SOURCE_DB.getCode())) {
            map.put("clusterName", clusterName);
            map.put("dbName", firstDbName);
            map.put("value", firstDbName);
        } else if (inputType.equals(TemplateInputTypeEnum.SOURCE_TABLE.getCode())) {
            map.put("clusterName", clusterName);
            map.put("dbName", firstDbName);
            map.put("tableName", firstDataSource.getTableName());
            map.put("value", firstDataSource.getTableName());
        } else if (inputType.equals(TemplateInputTypeEnum.SOURCE_FIELDS.getCode())) {
            List<String> fieldName = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(mappings)) {
                for (MultiDataSourceJoinConfigRequest request : mappings) {
                    for (MultiDataSourceJoinColumnRequest columnRequest : request.getLeft()) {
                        if (columnRequest.getColumnName().equals(DIFF_COUNT)) {
                            continue;
                        }
                        fieldName.add(columnRequest.getColumnName().replace("tmp1.", ""));
                    }
                }
            } else if (CollectionUtils.isNotEmpty(colNames)) {
                for (DataSourceColumnRequest dataSourceColumnRequest : colNames) {
                    fieldName.add(dataSourceColumnRequest.getColumnName());
                }
            }
            map.put("value", String.join(", ", fieldName));
        } else if (inputType.equals(TemplateInputTypeEnum.TARGET_DB.getCode())) {
            map.put("clusterName", clusterName);
            map.put("dbName", secondDbName);
            map.put("value", secondDbName);
        } else if (inputType.equals(TemplateInputTypeEnum.TARGET_TABLE.getCode())) {
            map.put("clusterName", clusterName);
            map.put("dbName", secondDbName);
            map.put("tableName", secondDataSource.getTableName());
            map.put("value", secondDataSource.getTableName());
        } else if (inputType.equals(TemplateInputTypeEnum.TARGET_FIELDS.getCode())) {
            List<String> fieldName = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(mappings)) {
                for (MultiDataSourceJoinConfigRequest request : mappings) {
                    for (MultiDataSourceJoinColumnRequest columnRequest : request.getRight()) {
                        if (columnRequest.getColumnName().equals(DIFF_COUNT)) {
                            continue;
                        }
                        fieldName.add(columnRequest.getColumnName().replace("tmp2.", ""));
                    }
                }
            } else if (CollectionUtils.isNotEmpty(colNames)) {
                for (DataSourceColumnRequest dataSourceColumnRequest : colNames) {
                    fieldName.add(dataSourceColumnRequest.getColumnName());
                }
            }
            map.put("value", String.join(", ", fieldName));
        }
        return map;
    }

    private String generateAndConcatStatement(List<MultiDataSourceJoinConfigRequest> mappings) {
        List<String> originStatement = new ArrayList<>();
        for (MultiDataSourceJoinConfigRequest mapping : mappings) {
            StringBuilder fullStatement = new StringBuilder();
            String leftStatement = StringUtils.isNotBlank(mapping.getLeftStatement())?mapping.getLeftStatement():appendColumn(mapping.getLeft());
            String rightStatement = StringUtils.isNotBlank(mapping.getRightStatement())?mapping.getRightStatement():appendColumn(mapping.getRight());
            fullStatement.append(leftStatement).append(MappingOperationEnum.getByCode(mapping.getOperation()).getSymbol()).append(rightStatement);
            originStatement.add(fullStatement.toString());
        }

        return generateStatementByConcatStr(originStatement, "AND");
    }

    private String appendColumn(List<MultiDataSourceJoinColumnRequest> columnList){
        List<String> columnStrList = new ArrayList<>();
        for (MultiDataSourceJoinColumnRequest request: columnList) {
            columnStrList.add(request.getColumnName());
        }
        return StringUtils.join(columnStrList, SpecCharEnum.EMPTY.getValue());
    }

    private String getPlaceHolder(String str) {
        return "${" + str + "}";
    }

    private String generateStatementByConcatStr(List<String> statement, String concatStr) {
        return String.join(" " + concatStr + " ", statement);
    }

    /**
     * 1.Get input type
     * 2.Auto adapt according to type
     */
    private Map<String, String> findValueFromDataSourceRequest(Integer inputType, DataSourceRequest request) throws UnExpectedRequestException {
        Map<String, String> map = new HashMap<>(4);
        String fileType = request.getFileType();
        String dbName = request.getDbName();
        if (StringUtils.isNotBlank(fileType) && ! EXCEL_TYPE.contains(fileType)) {
            dbName = "";
        }
        if (inputType.equals(TemplateInputTypeEnum.FIELD.getCode())) {
            // Filed Type
            if (request.getColNames().size() != 1) {
                throw new UnExpectedRequestException("Can not find field from dataSource");
            }
            map.put("clusterName", request.getClusterName());
            map.put("dbName", dbName);
            map.put("tableName", request.getTableName());
            map.put("value", request.getColNames().get(0).getColumnName());
        } else if (inputType.equals(TemplateInputTypeEnum.TABLE.getCode())) {
            // Table Type
            map.put("clusterName", request.getClusterName());
            map.put("dbName", dbName);
            map.put("tableName", request.getTableName());
            map.put("value", request.getTableName());
        } else if (inputType.equals(TemplateInputTypeEnum.DATABASE.getCode())) {
            // Database Type
            map.put("clusterName", request.getClusterName());
            map.put("dbName", dbName);
            map.put("value", dbName);
        }

        return Collections.unmodifiableMap(map);
    }

    private Map<String, String> getFieldConcat(DataSourceRequest dataSourceRequest) {
        String fileType = dataSourceRequest.getFileType();
        String dbName = dataSourceRequest.getDbName();
        if (fileType != null && ! EXCEL_TYPE.contains(fileType)) {
            dbName = "";
        }
        return ImmutableMap.of("value", String.join(",", dataSourceRequest.getColNames().stream().map(DataSourceColumnRequest::getColumnName).collect(Collectors.toList())),
                "clusterName", dataSourceRequest.getClusterName(),
                "dbName", dbName,
                "tableName", dataSourceRequest.getTableName());
    }

    /**
     * If replaceByRequest = true, return value in request
     * Else return value in database
     * @param templateMidTableInputMeta
     * @param templateArgumentRequests
     * @return
     */
    private Map<String, String> getRegexpValue(TemplateMidTableInputMeta templateMidTableInputMeta, List<TemplateArgumentRequest> templateArgumentRequests) throws UnExpectedRequestException {
        if (templateMidTableInputMeta.getReplaceByRequest()) {
            // Get value in request
            String regexp = findRequestById(templateMidTableInputMeta.getId(), templateArgumentRequests).getArgumentValue();
            String value = escapeExprSpecialWord(regexp);
            return ImmutableMap.of("value", value);
        } else {
            String key = null;
            // Get value in database
            if (templateMidTableInputMeta.getRegexpType().equals(TemplateRegexpTypeEnum.DATE.getCode())) {
                key = findRequestById(templateMidTableInputMeta.getId(), templateArgumentRequests).getArgumentValue();
            }
            TemplateRegexpExpr templateRegexpExpr = regexpExprMapperRepository.findByRegexpTypeAndKeyName(templateMidTableInputMeta.getRegexpType(), key);
            if (templateRegexpExpr == null) {
                throw new UnExpectedRequestException("KeyName: [" + key + "] is not supported");
            }

            String value = escapeExprSpecialWord(templateRegexpExpr.getRegexpValue());
            Map<String, String> map = new HashMap<>(2);
            map.put("value", value);
            map.put("originValue", key);
            return Collections.unmodifiableMap(map);
        }
    }

    private TemplateArgumentRequest findRequestById(Long id, List<TemplateArgumentRequest> templateArgumentRequests) throws UnExpectedRequestException {
        List<TemplateArgumentRequest> request = templateArgumentRequests.stream().filter(r -> r.getArgumentId().equals(id)).collect(Collectors.toList());
        if (request.size() != 1) {
            throw new UnExpectedRequestException("Can not find suitable request");
        }
        TemplateArgumentRequest.checkRequest(request.get(0));

        return request.get(0);
    }

    private String escapeExprSpecialWord(String str) {
        if (StringUtils.isNotBlank(str)) {
            for (String specialWord : SPECIAL_WORD_LIST) {
                if (str.contains(specialWord)) {
                    str = str.replace(specialWord, "\\" + specialWord);
                }
            }
        }
        return str;
    }

}
