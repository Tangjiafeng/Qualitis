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

package com.webank.wedatasphere.qualitis.rule.constant;

import org.apache.commons.lang3.StringUtils;

/**
 * @author howeye
 */
public enum TemplateDataSourceTypeEnum {
    /**
     * hive, mysql, tdsql, kafka, fps
     */
    HIVE(1, "hive"),
    MYSQL(2, "mysql"),
    TDSQL(3, "tdsql"),
    KAFKA(4, "kafka"),
    FPS(5, "fps");

    private Integer code;
    private String message;

    TemplateDataSourceTypeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static String getMessage(Integer code) {
        for (TemplateDataSourceTypeEnum templateDataSourceTypeEnum : TemplateDataSourceTypeEnum.values()) {
            if (templateDataSourceTypeEnum.getCode().equals(code)) {
                return templateDataSourceTypeEnum.getMessage();
            }
        }
        return "";
    }

    public static Integer getCode(String message) {
        String dataSourceTypeName = null;
        if (StringUtils.isNotBlank(message)) {
            dataSourceTypeName = message.toLowerCase();
        }
        for (TemplateDataSourceTypeEnum templateDataSourceTypeEnum : TemplateDataSourceTypeEnum.values()) {
            if (templateDataSourceTypeEnum.getMessage().toLowerCase().equals(dataSourceTypeName)) {
                return templateDataSourceTypeEnum.getCode();
            }
        }
        return null;
    }
}
