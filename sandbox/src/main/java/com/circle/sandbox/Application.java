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

package com.circle.sandbox;

import com.circle.application.AbstractApplication;
import com.circle.config.resilience.FaultTolerantSecureRESTServiceConfig;
import com.circle.logging.CircleLogger;
import com.circle.sandbox.config.ApplicationConfig;
import com.circle.sandbox.resources.v1.TestResource;
import com.circle.services.auth.AuthPrincipal;
import com.circle.services.auth.RolesAllowedDynamicFeature;
import com.circle.services.auth.UnauthedV2Handler;
import com.circle.services.auth.token.jwt.JWTAuthFilter;
import com.circle.services.auth.token.jwt.JWTAuthProvider;
import com.circle.services.auth.token.jwt.JWTAuthenticator;
import com.circle.services.auth.token.jwt.JWTAuthorizer;
import com.circle.services.auth.token.jwt.RSAPublicKeyResolver;
import com.circle.services.resources.PingResource;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthFilter;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.slf4j.Logger;

import javax.ws.rs.client.Client;

public class Application extends AbstractApplication<ApplicationConfig> {
    private static final CircleLogger LOGGER = new CircleLogger("sandbox");

    public static void main(String[] args) throws Exception {
        new Application().run(args);
    }

    @Override
    public String getName() {
        return "SANDBOX";
    }

    @Override
    protected Logger getLogger() {
        return LOGGER.getLogger();
    }

    @Override
    public void initialize(Bootstrap<ApplicationConfig> bootstrap) {
        super.initialize(bootstrap);
    }

    @Override
    public void run(ApplicationConfig configuration, Environment environment) throws Exception {
        super.run(configuration, environment);

        ////////////////////
        // Authentication //
        ////////////////////
        setupAuthFilter(configuration, environment);

        ///////////////
        // Resources //
        ///////////////
        environment.jersey().register(PingResource.class);
        environment.jersey().register(TestResource.class);
    }

    private void setupAuthFilter(ApplicationConfig configuration, Environment environment) {

        FaultTolerantSecureRESTServiceConfig authConfig = configuration.getAuthServerConfig();
        Client authHttpClient = new JerseyClientBuilder(environment)
                .using(authConfig.getJerseyClientConfig())
                .build("TEST JKW API");

        AuthFilter serviceFilter = new JWTAuthFilter.Builder<AuthPrincipal>()
                .setAuthenticator(new JWTAuthenticator(new JWTAuthProvider(new RSAPublicKeyResolver(
                        authHttpClient, authConfig, environment.metrics()))))
                .setAuthorizer(new JWTAuthorizer())
                .setRealm("Circle")
                .setPrefix("Bearer")
                .buildAuthFilter();

        environment.jersey().register(new AuthDynamicFeature(serviceFilter));
        environment.jersey().register(new RolesAllowedDynamicFeature(new UnauthedV2Handler()));
        environment.jersey().register(new AuthValueFactoryProvider.Binder<>(AuthPrincipal.class));
    }
}
