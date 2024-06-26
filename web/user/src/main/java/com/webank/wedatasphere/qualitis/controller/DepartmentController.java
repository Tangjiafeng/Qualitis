package com.webank.wedatasphere.qualitis.controller;

import com.webank.wedatasphere.qualitis.constants.ResponseStatusConstants;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.metadata.response.DepartmentSubResponse;
import com.webank.wedatasphere.qualitis.request.DepartmentAddRequest;
import com.webank.wedatasphere.qualitis.request.DepartmentModifyRequest;
import com.webank.wedatasphere.qualitis.request.QueryDepartmentRequest;
import com.webank.wedatasphere.qualitis.response.DepartmentResponse;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;
import com.webank.wedatasphere.qualitis.service.DepartmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * @author allenzhou
 */
@Path("api/v1/admin/department")
public class DepartmentController {
    @Autowired
    private DepartmentService departmentService;

    private static final Logger LOGGER = LoggerFactory.getLogger(DepartmentController.class);

    @POST
    @Path("add")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<DepartmentResponse> addDepartment(DepartmentAddRequest request) throws UnExpectedRequestException{
        try {
            return departmentService.addDepartment(request);
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to add department, department: {}, caused by: {}", request.getDepartmentName(), e.getMessage());
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_ADD_DEPARTMENT}", null);
        }
    }

    @POST
    @Path("modify")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse modifyDepartment(DepartmentModifyRequest request) throws UnExpectedRequestException{
        try {
            return departmentService.modifyDepartment(request);
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to modify department, department: {}, caused by: {}", request.getDepartmentName(), e.getMessage());
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_MODIFY_DEPARTMENT}", null);
        }
    }

    @POST
    @Path("delete/{department_id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse deleteDepartment(@PathParam("department_id") Long departmentId) throws UnExpectedRequestException{
        try {
            return departmentService.deleteDepartment(departmentId);
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to modify department, department id: {}, caused by: {}", departmentId, e.getMessage());
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_DELETE_DEPARTMENT}", null);
        }
    }

    @POST
    @Path("all")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<GetAllResponse<DepartmentResponse>> getAllDepartment(QueryDepartmentRequest queryDepartmentRequest) throws UnExpectedRequestException{
        try {
            return departmentService.findAllDepartment(queryDepartmentRequest);
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to get department, caused by: {}", e.getMessage());
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_GET_DEPARTMENT}", null);
        }
    }

    @GET
    @Path("tenant_user/list")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<GetAllResponse<DepartmentResponse>> findFromTenantUser() throws UnExpectedRequestException{
        try {
            return departmentService.findFromTenantUser();
        } catch (Exception e) {
            LOGGER.error("Failed to get department, caused by: {}", e.getMessage());
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_GET_DEPARTMENT}", null);
        }
    }

}
