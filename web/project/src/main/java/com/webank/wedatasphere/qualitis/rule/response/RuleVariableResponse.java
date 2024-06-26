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

package com.webank.wedatasphere.qualitis.rule.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.constant.TaskStatusEnum;
import com.webank.wedatasphere.qualitis.rule.constant.InputActionStepEnum;
import com.webank.wedatasphere.qualitis.rule.constant.TemplateInputTypeEnum;
import com.webank.wedatasphere.qualitis.rule.entity.RuleVariable;
import com.webank.wedatasphere.qualitis.rule.constant.InputActionStepEnum;
import com.webank.wedatasphere.qualitis.rule.entity.RuleVariable;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang.StringEscapeUtils;

/**
 * @author howeye
 */
public class RuleVariableResponse {

    @JsonProperty("input_meta_id")
    private Long inputMetaId;
    @JsonProperty("action_step")
    private Integer actionStep;
    @JsonProperty("statistic_arg_id")
    private Long statisticArgId;
    private String value;
    private String alias;
    private Integer type;

    private static final List<String> ENUM_VALUE = Arrays
        .asList("{&ENUM_VALUE}","Enum Value","枚举值");

    public RuleVariableResponse() {
    }

    public RuleVariableResponse(RuleVariable ruleVariable, Long standardValueVersionId) {
        if (ruleVariable != null) {
            this.actionStep = ruleVariable.getInputActionStep();
            if (this.actionStep.equals(InputActionStepEnum.TEMPLATE_INPUT_META.getCode())) {
                this.inputMetaId = ruleVariable.getTemplateMidTableInputMeta().getId();
                this.alias = ruleVariable.getTemplateMidTableInputMeta().getPlaceholder();
                this.type = ruleVariable.getTemplateMidTableInputMeta().getInputType();
            } else {
                this.statisticArgId = ruleVariable.getTemplateStatisticsInputMeta().getId();
            }
            if (null != standardValueVersionId && TemplateInputTypeEnum.STANDARD_VALUE_EXPRESSION.getCode().equals(ruleVariable.getTemplateMidTableInputMeta().getInputType())) {
                this.value = standardValueVersionId.toString();
            } else {
                if (TemplateInputTypeEnum.CONNECT_FIELDS.equals(ruleVariable.getTemplateMidTableInputMeta().getInputType()) || TemplateInputTypeEnum.COMPARISON_FIELD_SETTINGS.equals(ruleVariable.getTemplateMidTableInputMeta().getInputType())) {
                    this.value = ruleVariable.getOriginValue();
                } else {
                    this.value = StringEscapeUtils.unescapeJava(ruleVariable.getValue());
                }
            }
        }

    }

    public Long getInputMetaId() {
        return inputMetaId;
    }

    public void setInputMetaId(Long inputMetaId) {
        this.inputMetaId = inputMetaId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getActionStep() {
        return actionStep;
    }

    public void setActionStep(Integer actionStep) {
        this.actionStep = actionStep;
    }

    public Long getStatisticArgId() {
        return statisticArgId;
    }

    public void setStatisticArgId(Long statisticArgId) {
        this.statisticArgId = statisticArgId;
    }
}
