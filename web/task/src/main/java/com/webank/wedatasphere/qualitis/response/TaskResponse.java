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

package com.webank.wedatasphere.qualitis.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.entity.Task;
import com.webank.wedatasphere.qualitis.entity.TaskDataSource;
import com.webank.wedatasphere.qualitis.entity.TaskRuleSimple;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author howeye
 */
public class TaskResponse {
    @JsonProperty("task_id")
    private Long taskId;
    @JsonProperty("task_remote_id")
    private Long taskRemoteId;
    @JsonProperty("start_time")
    private String startTime;
    @JsonProperty("end_time")
    private String endTime;
    private Integer status;
    @JsonProperty("task_rules")
    private List<TaskRuleResponse> taskRules;
    @JsonProperty("cluster_id")
    private String clusterId;

    public TaskResponse(Task task) {
        this.taskId = task.getId();
        this.taskRemoteId = task.getTaskRemoteId();
        this.startTime = task.getBeginTime();
        this.endTime = task.getEndTime();
        this.status = task.getStatus();
        this.clusterId = task.getClusterName();
        this.taskRules = new ArrayList<>();
        for (TaskRuleSimple taskRuleSimple : task.getTaskRuleSimples()) {
            taskRules.add(new TaskRuleResponse(taskRuleSimple, task.getTaskDataSources()));
        }
    }

    public TaskResponse(Task task, StringBuilder ruleDataSource) {
        this.taskId = task.getId();
        this.taskRemoteId = task.getTaskRemoteId();
        this.startTime = task.getBeginTime();
        this.endTime = task.getEndTime();
        this.status = task.getStatus();
        this.clusterId = task.getClusterName();
        this.taskRules = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(task.getTaskRuleSimples())) {
            for (TaskRuleSimple taskRuleSimple : task.getTaskRuleSimples()) {
                Set<TaskDataSource> taskDataSourceSet = task.getTaskDataSources();
                List<TaskDataSource> taskDataSourceList = new ArrayList<>(taskDataSourceSet);

//            Sorting by datasource_index
                if (taskDataSourceList.size() > 1) {
                    Comparator<TaskDataSource> indexComparator = Comparator.comparing(TaskDataSource::getDatasourceIndex, Comparator.nullsFirst(Integer::compareTo));
                    Collections.sort(taskDataSourceList, indexComparator);
                }

                for (TaskDataSource taskDataSource : taskDataSourceList) {
                    if (taskDataSource != null) {
                        String databaseName = StringUtils.isNotBlank(taskDataSource.getDatabaseName()) ? taskDataSource.getDatabaseName() : "";
                        String tableName = StringUtils.isNotBlank(taskDataSource.getTableName()) ? taskDataSource.getTableName() : "";
                        if (StringUtils.isNotBlank(databaseName)) {
                            ruleDataSource.append(databaseName).append(".").append(tableName);
                        } else {
                            ruleDataSource.append(databaseName).append(tableName);
                        }
                        ruleDataSource.append(";");
                    }
                }
                if (ruleDataSource.toString().length() > 1) {
                    ruleDataSource.deleteCharAt(ruleDataSource.length() - 1);
                }
                taskRules.add(new TaskRuleResponse(taskRuleSimple, task.getTaskDataSources()));
            }
        }
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Long getTaskRemoteId() {
        return taskRemoteId;
    }

    public void setTaskRemoteId(Long taskRemoteId) {
        this.taskRemoteId = taskRemoteId;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<TaskRuleResponse> getTaskRules() {
        return taskRules;
    }

    public void setTaskRules(List<TaskRuleResponse> taskRules) {
        this.taskRules = taskRules;
    }

    public String getClusterId() {
        return clusterId;
    }

    public void setClusterId(String clusterId) {
        this.clusterId = clusterId;
    }
}
