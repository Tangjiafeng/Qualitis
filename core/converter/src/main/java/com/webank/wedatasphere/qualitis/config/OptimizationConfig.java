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

package com.webank.wedatasphere.qualitis.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author allenzhou
 */
@Configuration
public class OptimizationConfig {
    @Value("${linkis.lightweight_query}")
    private Boolean lightweightQuery;

    public Boolean getLightweightQuery() {
        return lightweightQuery;
    }

    public void setLightweightQuery(Boolean lightweightQuery) {
        this.lightweightQuery = lightweightQuery;
    }
}
