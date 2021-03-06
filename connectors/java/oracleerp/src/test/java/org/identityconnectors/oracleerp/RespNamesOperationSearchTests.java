/*
 * ====================
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008-2009 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License("CDDL") (the "License").  You may not use this file
 * except in compliance with the License.
 *
 * You can obtain a copy of the License at
 * http://opensource.org/licenses/cddl1.php
 * See the License for the specific language governing permissions and limitations
 * under the License.
 *
 * When distributing the Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://opensource.org/licenses/cddl1.php.
 * If applicable, add the following below this CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 * ====================
 */
package org.identityconnectors.oracleerp;

import static org.identityconnectors.oracleerp.OracleERPUtil.NAME;
import static org.identityconnectors.oracleerp.OracleERPUtil.RESP_NAMES_OC;

import java.util.List;

import org.identityconnectors.framework.common.objects.AttributeBuilder;
import org.identityconnectors.framework.common.objects.ConnectorObject;
import org.identityconnectors.framework.common.objects.ObjectClass;
import org.identityconnectors.framework.common.objects.OperationOptions;
import org.identityconnectors.framework.common.objects.OperationOptionsBuilder;
import org.identityconnectors.framework.common.objects.filter.Filter;
import org.identityconnectors.framework.common.objects.filter.FilterBuilder;
import org.identityconnectors.test.common.TestHelpers;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;

/**
 * Attempts to test the {@link AuditorOperationSearch}.
 *
 * @author petr
 * @since 1.0
 */
@Test(groups = { "integration" })
public class RespNamesOperationSearchTests extends OracleERPTestsBase {

    /**
     * Test method for
     * {@link ResponsibilitiesOperationSearch#executeQuery(ObjectClass, org.identityconnectors.dbcommon.FilterWhereBuilder, org.identityconnectors.framework.common.objects.ResultsHandler, OperationOptions)}
     * .
     */
    @Test
    public void testResponsibilitiesSearchAll() {
        final OracleERPConnector c = getConnector(CONFIG_SYSADM);

        OperationOptionsBuilder oob = new OperationOptionsBuilder();
        oob.setAttributesToGet(NAME);

        final Filter noFilter = new Filter() {
            public boolean accept(ConnectorObject obj) {
                return false;
            }
        };

        List<ConnectorObject> results =
                TestHelpers.searchToList(c, RESP_NAMES_OC, noFilter, oob.build());
        System.out.println(results);
        AssertJUnit.assertEquals("connector object size", 1253, results.size());
    }

    /**
     * Test method for
     * {@link ResponsibilitiesOperationSearch#executeQuery(ObjectClass, org.identityconnectors.dbcommon.FilterWhereBuilder, org.identityconnectors.framework.common.objects.ResultsHandler, OperationOptions)}
     * .
     */
    @Test
    public void testResponsibilitiesSearchOne() {
        final OracleERPConnector c = getConnector(CONFIG_SYSADM);

        OperationOptionsBuilder oob = new OperationOptionsBuilder();
        oob.setAttributesToGet(NAME);

        final Filter filter =
                FilterBuilder.equalTo(AttributeBuilder.build(NAME, "System Administrator"));
        List<ConnectorObject> results =
                TestHelpers.searchToList(c, RESP_NAMES_OC, filter, oob.build());
        System.out.println(results);
        AssertJUnit.assertEquals("connector object size", 1, results.size());
    }

}
