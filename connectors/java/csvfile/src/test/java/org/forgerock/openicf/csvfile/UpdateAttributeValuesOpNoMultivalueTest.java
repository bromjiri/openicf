/*
 *
 * Copyright (c) 2010 ForgeRock Inc. All Rights Reserved
 *
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at
 * http://www.opensource.org/licenses/cddl1.php or
 * OpenIDM/legal/CDDLv1.0.txt
 * See the License for the specific language governing
 * permission and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL
 * Header Notice in each file and include the License file
 * at OpenIDM/legal/CDDLv1.0.txt.
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted 2010 [name of copyright owner]"
 *
 * Portions Copyrighted 2011 Viliam Repan (lazyman)
 *
 * $Id$
 */
package org.forgerock.openicf.csvfile;

import org.forgerock.openicf.csvfile.util.TestUtils;
import org.forgerock.openicf.csvfile.util.Utils;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeBuilder;
import org.identityconnectors.framework.common.objects.ObjectClass;
import org.identityconnectors.framework.common.objects.Uid;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import static org.testng.Assert.*;

/**
 * @author Viliam Repan (lazyman)
 */
public class UpdateAttributeValuesOpNoMultivalueTest extends AbstractCsvTest {

    private static final Log LOG = Log.getLog(UpdateAttributeValuesOpNoMultivalueTest.class);

    private CSVFileConnector connector;

    public UpdateAttributeValuesOpNoMultivalueTest() {
        super(LOG);
    }

    @Override
    public void customBeforeMethod(Method method) throws Exception {
        File file = TestUtils.getTestFile("update-attribute.csv");
        File backup = TestUtils.getTestFile("update-attribute-backup.csv");
        Utils.copyAndReplace(backup, file);

        CSVFileConfiguration config = new CSVFileConfiguration();

        URL testFile = UtilsTest.class.getResource("/files/update-attribute.csv");
        config.setFilePath(new File(testFile.toURI()));
        config.setUniqueAttribute("uid");
        config.setPasswordAttribute("password");
        config.setUsingMultivalue(false);

        connector = new CSVFileConnector();
        connector.init(config);
    }

    @Override
    public void customAfterMethod(Method method) throws Exception {
        connector.dispose();
        connector = null;
    }

    @Override
    public void customAfterClass() throws Exception {
        File file = TestUtils.getTestFile("update-attribute.csv");
        file.delete();
    }

    @Test
    public void updateNonExistingAttributeAdd() throws Exception {
        Set<Attribute> attributes = new HashSet<Attribute>();
        attributes.add(AttributeBuilder.build("nonExisting", "repan"));
        Uid uid = connector.addAttributeValues(ObjectClass.ACCOUNT, new Uid("vilo"), attributes, null);
        assertNotNull(uid);
        assertEquals("vilo", uid.getUidValue());

        String result = TestUtils.compareFiles(TestUtils.getTestFile("update-attribute.csv"),
                TestUtils.getTestFile("update-attribute-result-non-existing.csv"));
        assertNull(result, "File updated incorrectly: " + result);
    }

    @Test
    public void updateAttributeAdd() throws Exception {
        Set<Attribute> attributes = new HashSet<Attribute>();
        attributes.add(AttributeBuilder.build("lastName", "repantest"));
        Uid uid = connector.addAttributeValues(ObjectClass.ACCOUNT, new Uid("vilo"), attributes, null);
        assertNotNull(uid);
        assertEquals("vilo", uid.getUidValue());

        String result = TestUtils.compareFiles(TestUtils.getTestFile("update-attribute.csv"),
                TestUtils.getTestFile("update-attribute-result-add-1.csv"));
        assertNull(result, "File updated incorrectly: " + result);
    }

    @Test
    public void updateNonExistingAttributeRemove() throws Exception {
        Set<Attribute> attributes = new HashSet<Attribute>();
        attributes.add(AttributeBuilder.build("nonExisting", "repan"));
        Uid uid = connector.removeAttributeValues(ObjectClass.ACCOUNT, new Uid("vilo"), attributes, null);
        assertNotNull(uid);
        assertEquals("vilo", uid.getUidValue());

        String result = TestUtils.compareFiles(TestUtils.getTestFile("update-attribute.csv"),
                TestUtils.getTestFile("update-attribute-result-non-existing.csv"));
        assertNull(result, "File updated incorrectly: " + result);
    }

    @Test
    public void updateAttributeDeleteRemove() throws Exception {
        Set<Attribute> attributes = new HashSet<Attribute>();
        attributes.add(AttributeBuilder.build("lastName", "repan2"));
        Uid uid = connector.removeAttributeValues(ObjectClass.ACCOUNT, new Uid("miso"), attributes, null);
        assertNotNull(uid);
        assertEquals("miso", uid.getUidValue());

        String result = TestUtils.compareFiles(TestUtils.getTestFile("update-attribute.csv"),
                TestUtils.getTestFile("update-attribute-result-remove-1.csv"));
        assertNull(result, "File updated incorrectly: " + result);
    }

    @Test
    public void updateMultivalueAttributeAdd() throws Exception {
        Set<Attribute> attributes = new HashSet<Attribute>();
        attributes.add(AttributeBuilder.build("lastName", "repan", "repan2", "repan3"));
        Uid uid = connector.addAttributeValues(ObjectClass.ACCOUNT, new Uid("vilo"), attributes, null);
        assertNotNull(uid);
        assertEquals("vilo", uid.getUidValue());

        String result = TestUtils.compareFiles(TestUtils.getTestFile("update-attribute.csv"),
                TestUtils.getTestFile("update-attribute-result-add-multi-1.csv"));
        assertNull(result, "File updated incorrectly: " + result);
    }

    @Test
    public void updateMultivalueAttributeRemove() throws Exception {
        Set<Attribute> attributes = new HashSet<Attribute>();
        attributes.add(AttributeBuilder.build("lastName", "repan", "repan2", "repan3"));
        Uid uid = connector.removeAttributeValues(ObjectClass.ACCOUNT, new Uid("miso"), attributes, null);
        assertNotNull(uid);
        assertEquals("miso", uid.getUidValue());

        String result = TestUtils.compareFiles(TestUtils.getTestFile("update-attribute.csv"),
                TestUtils.getTestFile("update-attribute-result-remove-multi-1.csv"));
        assertNull(result, "File updated incorrectly: " + result);
    }
}
