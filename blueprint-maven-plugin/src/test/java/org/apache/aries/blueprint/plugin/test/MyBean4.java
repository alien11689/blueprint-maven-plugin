/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.aries.blueprint.plugin.test;

import org.springframework.beans.factory.annotation.Value;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
public class MyBean4 {

    ServiceA serviceA1;

    ServiceA serviceA2;

    ServiceB serviceB;

    int bla;

    @Inject
    public MyBean4(@Named("my1") ServiceA serviceA1, @Named("my2") ServiceA serviceA2, ServiceB serviceB, @Value("100") int bla) {
        this.serviceA1 = serviceA1;
        this.serviceA2 = serviceA2;
        this.serviceB = serviceB;
    }
}
