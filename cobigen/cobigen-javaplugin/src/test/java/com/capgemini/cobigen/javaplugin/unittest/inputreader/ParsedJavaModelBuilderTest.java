package com.capgemini.cobigen.javaplugin.unittest.inputreader;

import static com.capgemini.cobigen.javaplugin.util.JavaModelUtil.getField;
import static com.capgemini.cobigen.javaplugin.util.JavaModelUtil.getJavaDocModel;
import static com.capgemini.cobigen.javaplugin.util.JavaModelUtil.getMethod;
import static com.capgemini.cobigen.javaplugin.util.JavaModelUtil.getRoot;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.capgemini.cobigen.javaplugin.inputreader.JavaInputReader;
import com.capgemini.cobigen.javaplugin.inputreader.ModelConstant;
import com.capgemini.cobigen.javaplugin.inputreader.to.PackageFolder;
import com.capgemini.cobigen.javaplugin.unittest.inputreader.testdata.RootClass;
import com.capgemini.cobigen.javaplugin.util.JavaModelUtil;
import com.capgemini.cobigen.javaplugin.util.JavaParserUtil;
import com.google.common.base.Charsets;

/**
 * Tests for Class {@link ParsedJavaModelBuilderTest}
 *
 * @author <a href="m_brunnl@cs.uni-kl.de">Malte Brunnlieb</a>
 * @version $Revision$
 */
public class ParsedJavaModelBuilderTest {
    /**
     * Root path to all resources used in this test case
     */
    private static String testFileRootPath = "src/test/resources/testdata/unittest/inputreader/";

    /**
     * TestAttribute for {@link #testCorrectlyExtractedGenericAttributeTypes()}
     */
    @SuppressWarnings("unused")
    private List<String> parametricTestAttribute;

    /**
     * Tests whether parametric attribute types will be extracted correctly to the model
     *
     * @throws FileNotFoundException
     *             test fails
     */
    @Test
    public void testCorrectlyExtractedGenericAttributeTypes() throws FileNotFoundException {

        File file = new File(testFileRootPath + "TestClass.java");

        JavaInputReader javaModelBuilder = new JavaInputReader();
        Map<String, Object> model =
            javaModelBuilder.createModel(JavaParserUtil.getFirstJavaClass(new FileReader(file)));
        Map<String, Object> customList = JavaModelUtil.getField(model, "customList");

        // "List<String>" is not possible to retrieve using reflection due to type erasure
        assertEquals("List<String>", customList.get(ModelConstant.TYPE));
        assertEquals("java.util.List<java.lang.String>", customList.get(ModelConstant.CANONICAL_TYPE));
    }

    /**
     * Tests whether supertypes (extended Type and implemented Types) will be extracted correctly to the model
     *
     * @throws FileNotFoundException
     *             test fails
     */
    @Test
    public void testCorrectlyExtractedImplementedTypes() throws FileNotFoundException {

        File classFile = new File(testFileRootPath + "TestClass.java");

        JavaInputReader javaModelBuilder = new JavaInputReader();
        Map<String, Object> model =
            javaModelBuilder.createModel(JavaParserUtil.getFirstJavaClass(new FileReader(classFile)));

        // check whether implemented Types (interfaces) meet expectations
        List<Map<String, Object>> interfaces = JavaModelUtil.getImplementedTypes(model);

        // interface1
        assertEquals("TestInterface1", interfaces.get(0).get(ModelConstant.NAME));
        assertEquals("com.capgemini.cobigen.javaplugin.unittest.inputreader.testdata.TestInterface1",
            interfaces.get(0).get(ModelConstant.CANONICAL_NAME));
        assertEquals("com.capgemini.cobigen.javaplugin.unittest.inputreader.testdata",
            interfaces.get(0).get(ModelConstant.PACKAGE));

        // interface2
        assertEquals("TestInterface2", interfaces.get(1).get(ModelConstant.NAME));
        assertEquals("com.capgemini.cobigen.javaplugin.unittest.inputreader.testdata.TestInterface2",
            interfaces.get(1).get(ModelConstant.CANONICAL_NAME));
        assertEquals("com.capgemini.cobigen.javaplugin.unittest.inputreader.testdata",
            interfaces.get(1).get(ModelConstant.PACKAGE));
    }

    /**
     * Tests whether no {@link NullPointerException} will be thrown if the extended type is in the default
     * package
     * @throws FileNotFoundException
     *             test fails
     * @author mbrunnli (30.09.2014)
     */
    @Test
    public void testCorrectlyExtractedInhertedType_extendedTypeWithoutPackageDeclaration()
        throws FileNotFoundException {

        File noPackageFile = new File(testFileRootPath + "NoPackageClass.java");

        JavaInputReader javaModelBuilder = new JavaInputReader();

        // debug nullPointerException in case of superclass without package
        Map<String, Object> model =
            javaModelBuilder.createModel(JavaParserUtil.getFirstJavaClass(new FileReader(noPackageFile)));
        assertEquals(JavaModelUtil.getExtendedType(model).get(ModelConstant.PACKAGE), "");
    }

    /**
     * Tests whether the inherited type will be correctly extracted and put into the model
     * @throws FileNotFoundException
     *             test fails
     * @author mbrunnli (30.09.2014)
     */
    @Test
    public void testCorrectlyExtractedInheritedType() throws FileNotFoundException {
        File classFile = new File(testFileRootPath + "TestClass.java");

        JavaInputReader javaModelBuilder = new JavaInputReader();
        Map<String, Object> model =
            javaModelBuilder.createModel(JavaParserUtil.getFirstJavaClass(new FileReader(classFile)));

        Assert
            .assertEquals("AbstractTestClass", JavaModelUtil.getExtendedType(model).get(ModelConstant.NAME));
        assertEquals("com.capgemini.cobigen.javaplugin.unittest.inputreader.testdata.AbstractTestClass",
            JavaModelUtil.getExtendedType(model).get(ModelConstant.CANONICAL_NAME));
        assertEquals("com.capgemini.cobigen.javaplugin.unittest.inputreader.testdata", JavaModelUtil
            .getExtendedType(model).get(ModelConstant.PACKAGE));
    }

    /**
     * Tests whether the type and the canonical type of a field will be extracted correctly
     *
     * @throws FileNotFoundException
     *             test fails
     */
    @Test
    public void testCorrectlyResolvedFieldTypes() throws FileNotFoundException {

        File file = new File(testFileRootPath + "Pojo.java");

        JavaInputReader javaModelBuilder = new JavaInputReader();
        Map<String, Object> model =
            javaModelBuilder.createModel(JavaParserUtil.getFirstJavaClass(new FileReader(file)));
        Map<String, Object> customTypeField = JavaModelUtil.getField(model, "customTypeField");

        // "List<String>" is not possible to retrieve using reflection due to type erasure
        assertEquals("AnyOtherType", customTypeField.get(ModelConstant.TYPE));
        assertEquals("com.capgemini.cobigen.javaplugin.unittest.inputreader.AnyOtherType",
            customTypeField.get(ModelConstant.CANONICAL_TYPE));
    }

    /**
     * Tests the correct extraction of 'methodAccessibleFields' for {@link PackageFolder} as input.
     * @author mbrunnli (25.01.2015)
     */
    @Test
    public void testCorrectExtractionOfInheritedFields_input_packageFolder() {
        File packageFolderFile = new File(testFileRootPath + "packageFolder");
        PackageFolder packageFolder =
            new PackageFolder(packageFolderFile.toURI(), RootClass.class.getPackage().getName());

        JavaInputReader javaInputReader = new JavaInputReader();
        List<Object> objects = javaInputReader.getInputObjects(packageFolder, Charsets.UTF_8);

        assertNotNull("The package folder does not contain any java sources!", objects);
        assertEquals(2, objects.size());

        boolean found = false;
        for (Object o : objects) {
            Map<String, Object> model = javaInputReader.createModel(o);
            assertNotNull("No model has been created!", model);
            if (RootClass.class.getSimpleName().equals(JavaModelUtil.getName(model))) {
                List<Map<String, Object>> methodAccessibleFields =
                    JavaModelUtil.getMethodAccessibleFields(model);
                assertNotNull(methodAccessibleFields);
                assertEquals(3, methodAccessibleFields.size());

                Map<String, Object> field = JavaModelUtil.getMethodAccessibleField(model, "value");
                assertNotNull("Field 'value' not found!", field);
                assertEquals("value", field.get(ModelConstant.NAME));
                assertEquals("String", field.get(ModelConstant.TYPE));
                assertEquals("java.lang.String", field.get(ModelConstant.CANONICAL_TYPE));

                field = JavaModelUtil.getMethodAccessibleField(model, "setterVisibleByte");
                assertNotNull("Field 'setterVisibleByte' not found!", field);
                assertEquals("setterVisibleByte", field.get(ModelConstant.NAME));
                assertEquals("byte", field.get(ModelConstant.TYPE));
                assertEquals("byte", field.get(ModelConstant.CANONICAL_TYPE));

                field = JavaModelUtil.getMethodAccessibleField(model, "genericAccessible");
                assertNotNull("Field 'genericAccessible' not found!", field);
                assertEquals("genericAccessible", field.get(ModelConstant.NAME));
                // TODO: Known Issue, this is not possible as the SuperClass2 is not in the same folder and
                // thus not parsed. Thus, due to type erasure the parametric type will be lost.
                // assertEquals("List<RootClass>", field.get(ModelConstant.TYPE));
                // assertEquals("java.util.List<RootClass>", field.get(ModelConstant.CANONICAL_TYPE));
                assertEquals("java.util.List", field.get(ModelConstant.TYPE));
                assertEquals("java.util.List", field.get(ModelConstant.CANONICAL_TYPE));

                found = true;
            }
        }
        assertTrue("Class " + RootClass.class.getName()
            + "could not be found as child of the package folder.", found);
    }

    /**
     * Tests the correct extraction of the JavaDoc properties.
     * @throws Exception
     *             test fails
     * @author mbrunnli (30.01.2015)
     */
    @Test
    public void testCorrectExtractionOfJavaDoc() throws Exception {
        File classFile = new File(testFileRootPath + "DocumentedClass.java");

        JavaInputReader javaModelBuilder = new JavaInputReader();
        Map<String, Object> model =
            javaModelBuilder.createModel(JavaParserUtil.getFirstJavaClass(new FileReader(classFile)));

        Map<String, String> javaDocModel = getJavaDocModel(getRoot(model));
        assertEquals("Class Doc.", javaDocModel.get(ModelConstant.COMMENT));
        assertEquals("mbrunnli (30.01.2015)", javaDocModel.get("author"));

        javaDocModel = getJavaDocModel(getField(model, "field"));
        assertEquals("Field Doc.", javaDocModel.get(ModelConstant.COMMENT));

        javaDocModel = getJavaDocModel(getMethod(model, "getField"));
        assertEquals("Returns the field 'field'.", javaDocModel.get(ModelConstant.COMMENT));
        assertEquals("value of field", javaDocModel.get("return"));
        assertEquals("mbrunnli (30.01.2015)", javaDocModel.get("author"));

        javaDocModel = getJavaDocModel(getMethod(model, "setField"));
        assertEquals("Sets the field 'field'.", javaDocModel.get(ModelConstant.COMMENT));
        assertEquals("field" + System.getProperty("line.separator") + "           new value of field",
            javaDocModel.get("param"));
        assertEquals("mbrunnli (30.01.2015)", javaDocModel.get("author"));
    }

}
