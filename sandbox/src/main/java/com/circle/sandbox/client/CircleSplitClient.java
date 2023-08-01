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

package com.circle.sandbox.client;

import io.split.client.SplitClient;
import io.split.client.SplitClientConfig;
import io.split.client.SplitFactoryBuilder;

public class CircleSplitClient {

    private static final CircleSplitClient INSTANCE = new CircleSplitClient();
    private static final SplitClientConfig CONFIG = SplitClientConfig.builder()
        .setBlockUntilReadyTimeout(10000)
        .numThreadsForSegmentFetch(6)
        .enableDebug()
        .build();

    //Smokebox
    private static final String SDK_KEY = "sd66ensueb8rqd5nrkfs65lca4kadjaknjhn";

    //Prod
    //private static final String SDK_KEY = "9r1ju4iulq2m839aolun8cq9fnsspf3db30i";

    private SplitClient client;

    public static CircleSplitClient getInstance() {
        return INSTANCE;
    }

    public SplitClient getSplitClient() {

        if (client != null) {
            return client;
        }

        if (SDK_KEY.isEmpty()) {
            throw new IllegalArgumentException("Please specify a SDK Key!");
        }

        //Instantiates the client and waits until it's ready to be used
        try {
            client = SplitFactoryBuilder.build(SDK_KEY, CONFIG).client();
            client.blockUntilReady();
            System.out.println("*************After blockUntilReady*********");
        } catch (Exception e) {
            System.out.print("Exception: " + e.getMessage());
        }
        return client;
    }
}
