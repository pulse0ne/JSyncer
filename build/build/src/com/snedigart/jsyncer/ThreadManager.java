/*
 * Copyright 2016 Tyler Snedigar.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 3 for more details.
 */
package com.snedigart.jsyncer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class ThreadManager {
    private static ThreadManager INSTANCE = null;

    private ExecutorService threadExecutor;

    private ThreadManager() {
        threadExecutor = Executors.newSingleThreadExecutor();
    }

    public static ThreadManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ThreadManager();
        }
        return INSTANCE;
    }

    public void submitJob(Runnable r) {
        if (!threadExecutor.isShutdown()) {
            threadExecutor.submit(r);
        }
    }

    public void shutdown() {
        if (threadExecutor != null) {
            threadExecutor.shutdownNow();
        }
    }
}
