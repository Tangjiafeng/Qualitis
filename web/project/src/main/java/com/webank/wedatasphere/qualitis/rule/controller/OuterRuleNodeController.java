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

package com.webank.wedatasphere.qualitis.rule.controller;

import com.webank.wedatasphere.qualitis.constants.ResponseStatusConstants;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;
import com.webank.wedatasphere.qualitis.project.service.ProjectBatchService;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;
import com.webank.wedatasphere.qualitis.rule.request.CopyRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.DeleteRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.ModifyRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.RuleNodeRequests;
import com.webank.wedatasphere.qualitis.rule.request.TemplatePageRequest;
import com.webank.wedatasphere.qualitis.rule.response.RuleNodeResponses;
import com.webank.wedatasphere.qualitis.rule.response.RuleResponse;
import com.webank.wedatasphere.qualitis.rule.response.RuleTemplateResponse;
import com.webank.wedatasphere.qualitis.rule.service.RuleNodeService;
import com.webank.wedatasphere.qualitis.rule.service.RuleTemplateService;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author allenzhou
 */
@Path("outer/api/v1/projector/rule")
public class OuterRuleNodeController {
    @Autowired
    private RuleNodeService ruleNodeService;
    @Autowired
    private RuleTemplateService ruleTemplateService;

    private static final Logger LOGGER = LoggerFactory.getLogger(OuterRuleNodeController.class);

    @POST
    @Path("delete")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse deleteRule(DeleteRuleRequest request) throws UnExpectedRequestException {
        try {
            return ruleNodeService.deleteRule(request);
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to delete rule, rule id: {}, caused by: {}", request.getRuleGroupId(), e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_DELETE_RULE}", null);
        }
    }

    @POST
    @Path("modify")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse modifyRule(ModifyRuleRequest request) throws UnExpectedRequestException {
        try {
            return ruleNodeService.modifyRule(request);
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to modify rule, rule id: {}, caused by: {}", request.getRuleGroupId(), e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_MODIFY_RULE}", null);
        }
    }

    @GET
    @Path("export/{ruleGroupId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<RuleNodeResponses> exportRuleByGroupId(@PathParam("ruleGroupId") Long ruleGroupId) throws UnExpectedRequestException {
        try {
            return ruleNodeService.exportRuleByGroupId(ruleGroupId);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to get rule detail. ruleGroupId: {}, caused by: {}", ruleGroupId, e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_EXPORT_RULE}", null);
        }
    }

    @PUT
    @Path("import")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<RuleResponse> importGroupRule(RuleNodeRequests ruleNodeRequests) throws UnExpectedRequestException {
        CommonChecker.checkCollections(ruleNodeRequests.getRuleNodeRequests(), "RuleNodeRequests");
        try {
            ruleNodeService.handleExecutionParamObject(ruleNodeRequests);
            return ruleNodeService.importRuleGroup(ruleNodeRequests);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to import rule. Caused by: {}", e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_IMPORT_RULE}", null);
        }
    }

    @POST
    @Path("copy")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<RuleResponse> copyRuleByRuleGroupId(CopyRuleRequest request) throws UnExpectedRequestException {
        try {
            return ruleNodeService.copyRuleByRuleGroupId(request);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to copy rule group. Project ID: {}, group ID: {}, caused by: {}", request.getTargetProjectId(), request.getTargetRuleGroupId(), e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_COPY_RULE_DETAIL}", null);
        }
    }

    @POST
    @Path("default/all")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<GetAllResponse<RuleTemplateResponse>> getDefaultRuleTemplate(TemplatePageRequest request) throws UnExpectedRequestException {
        try {
            return ruleTemplateService.getDefaultRuleTemplate(request);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to find default rule_template, caused by: {}", e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_FIND_DEFAULT_RULE_TEMPLATE}", null);
        }
    }
}
