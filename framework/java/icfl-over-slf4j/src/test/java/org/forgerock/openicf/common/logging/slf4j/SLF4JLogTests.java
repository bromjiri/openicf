/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright © 2011 ForgeRock AS. All rights reserved.
 *
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at
 * http://forgerock.org/license/CDDLv1.0.html
 * See the License for the specific language governing
 * permission and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL
 * Header Notice in each file and include the License file
 * at http://forgerock.org/license/CDDLv1.0.html
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * $Id$
 */
package org.forgerock.openicf.common.logging.slf4j;

import org.testng.annotations.Test;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.CyclicBarrier;

import static org.testng.Assert.*;

public class SLF4JLogTests {

    @Test
    public void testMultithreaded() {
        final SLF4JLog logger = new SLF4JLog();
        int size = 100;
        CyclicBarrier barier = new CyclicBarrier(size);
        List<Thread> threads = new ArrayList<Thread>(size);
        Set<String> keys = new HashSet<String>();
        for (int i = 0; i < 100; i++) {
            String key = "" + i % 10;
            keys.add(key);
            Thread thread = new Thread(new CreateLogger(barier, logger, key));
            threads.add(thread);
            thread.start();
        }
        for (Thread thread : threads) {
            try {
                thread.join();
            }
            catch (InterruptedException e) {
            }
        }
        assertEquals(keys, logger.getMap().keySet());
    }

    private static class CreateLogger implements Runnable {

        final CyclicBarrier barier;
        final SLF4JLog logger;
        final String key;

        private CreateLogger(CyclicBarrier barier, SLF4JLog logger, String key) {
            this.barier = barier;
            this.logger = logger;
            this.key = key;
        }

        public void run() {
            try {
                barier.await();
            }
            catch (Exception e1) {
            }
            logger.getSLF4JLogger(key);
        }
    }
}