/*
 * Copyright (c) 2023, Circle Internet Financial Trading Company Limited.
 * All rights reserved.
 *
 * Circle Internet Financial Trading Company Limited CONFIDENTIAL
 * This file includes unpublished proprietary source code of Circle Internet
 * Financial Trading Company Limited, Inc. The copyright notice above does not
 * evidence any actual or intended publication of such source code. Disclosure
 * of this source code or any related proprietary information is strictly
 * prohibited without the express written permission of Circle Internet Financial
 * Trading Company Limited.
 */

package com.circle.sandbox.config;

import com.circle.config.AbstractApplicationConfig;
import com.circle.config.resilience.FaultTolerantSecureRESTServiceConfig;
import com.circle.exceptions.SecretsException;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

public class ApplicationConfig extends AbstractApplicationConfig {

    private static final String AUTH_SERVER_PREFIX = "authServer";
    private static final String AUTH_SERVER_CLIENT_ID = "clientId";
    private static final String AUTH_SERVER_CLIENT_SECRET = "clientSecret";

    @Valid
    @JsonProperty("authServer")
    private FaultTolerantSecureRESTServiceConfig authServerConfig;


    public FaultTolerantSecureRESTServiceConfig getAuthServerConfig() {
        return authServerConfig;
    }

    public void setAuthServerConfig(FaultTolerantSecureRESTServiceConfig authServerConfig) throws SecretsException {
        this.authServerConfig = authServerConfig;

        // Load secrets as appropriate
        if (authServerConfig != null && authServerConfig.getPassword() == null) {
            List<String> secretsToLoad = ImmutableList.of(AUTH_SERVER_CLIENT_ID, AUTH_SERVER_CLIENT_SECRET);
            Map<String, String> secrets = loadSecrets(AUTH_SERVER_PREFIX, secretsToLoad);
            this.authServerConfig.setUser(secrets.get(AUTH_SERVER_CLIENT_ID));
            this.authServerConfig.setPassword(secrets.get(AUTH_SERVER_CLIENT_SECRET));
        }
    }

    private Map<String, String> loadSecrets(String param, List<String> properties) throws SecretsException {
        try {
            return getSecretService().getSecretsMap(param, properties);
        } catch (Exception e) {
            getLogger().error(e.getLocalizedMessage(), e);
            throw new SecretsException(String.format("Failed to load '%s' secrets", param));
        }
    }
}
