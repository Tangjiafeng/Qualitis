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

package com.webank.wedatasphere.qualitis.entity;

import org.springframework.boot.json.JacksonJsonParser;

import javax.persistence.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author howeye
 */
@Entity
@Table(name = "qualitis_auth_proxy_user")
public class ProxyUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "proxy_user_name", length = 20)
    private String proxyUserName;
    @Column(name = "user_config_json", columnDefinition = "MEDIUMTEXT")
    private String userConfigJson;
    @Transient
    private Map<String, Object> userConfigMap = new HashMap<>();
    @ManyToOne(fetch = FetchType.EAGER)
    private Department department;
    @OneToMany(mappedBy = "proxyUser", cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    private List<ProxyUserDepartment> proxyUserDepartment;
    @OneToMany(mappedBy = "proxyUser", cascade = CascadeType.REMOVE)
    private List<UserProxyUser> userProxyUsers;

    @Column(name = "create_user")
    private String createUser;

    @Column(name = "create_time")
    private String createTime;

    @Column(name = "modify_user")
    private String modifyUser;

    @Column(name = "modify_time")
    private String modifyTime;

    public String getUserConfigJson() {
        return userConfigJson;
    }

    public void setUserConfigJson(String userConfigJson) {
        this.userConfigJson = userConfigJson;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getModifyUser() {
        return modifyUser;
    }

    public void setModifyUser(String modifyUser) {
        this.modifyUser = modifyUser;
    }

    public String getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(String modifyTime) {
        this.modifyTime = modifyTime;
    }
    public ProxyUser() {
        // Default Constructor
    }

    public Department getDepartment() {
        return department;
    }

    public List<ProxyUserDepartment> getProxyUserDepartment() {
        return proxyUserDepartment;
    }

    public void setProxyUserDepartment(List<ProxyUserDepartment> proxyUserDepartment) {
        this.proxyUserDepartment = proxyUserDepartment;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public ProxyUser(String proxyUserName) {
        this.proxyUserName = proxyUserName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProxyUserName() {
        return proxyUserName;
    }

    public void setProxyUserName(String proxyUserName) {
        this.proxyUserName = proxyUserName;
    }

    public List<UserProxyUser> getUserProxyUsers() {
        return userProxyUsers;
    }

    public void setUserProxyUsers(List<UserProxyUser> userProxyUsers) {
        this.userProxyUsers = userProxyUsers;
    }

    public Map<String, Object> getUserConfigMap() {
        if (this.userConfigJson != null) {
            this.userConfigMap = new JacksonJsonParser().parseMap(this.userConfigJson);
        }
        return this.userConfigMap;
    }
}
