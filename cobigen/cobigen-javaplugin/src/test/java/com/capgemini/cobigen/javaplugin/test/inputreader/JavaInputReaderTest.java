package com.capgemini.cobigen.javaplugin.test.inputreader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.capgemini.cobigen.javaplugin.inputreader.JavaInputReader;
import com.capgemini.cobigen.javaplugin.inputreader.ModelConstant;
import com.capgemini.cobigen.javaplugin.test.inputreader.testdata.TestClass;
import com.capgemini.cobigen.javaplugin.test.inputreader.testdata.TestClassWithAnnotations;
import com.capgemini.cobigen.javaplugin.test.inputreader.testdata.TestClassWithAnnotationsContainingObjectArrays;
import com.capgemini.cobigen.javaplugin.test.inputreader.testdata.TestClassWithRecursiveAnnotations;
import com.capgemini.cobigen.javaplugin.util.JavaModelUtil;
import com.capgemini.cobigen.javaplugin.util.JavaParserUtil;
import com.capgemini.cobigen.javaplugin.util.freemarkerutil.IsAbstractMethod;
import com.capgemini.cobigen.javaplugin.util.freemarkerutil.IsSubtypeOfMethod;

/**
 * This class tests the {@link JavaInputReader}. More specific it should test the model extraction by using
 * reflection and java parsing in combination.
 *
 * @author <a href="m_brunnl@cs.uni-kl.de">Malte Brunnlieb</a>
 * @version $Revision$
 */
public class JavaInputReaderTest {

    /**
     * Root path to all resources used in this test case
     */
    private static String testFileRootPath =
        "src/test/resources/com/capgemini/cobigen/javaplugin/test/inputreader/";

    /**
     * Tests whether both features can be used when providing parsed and reflected inputs for one java class
     *
     * @throws FileNotFoundException
     *             test fails
     */
    @Test
    public void provideParsingAndReflectionModelFeatures() throws FileNotFoundException {

        File javaSourceFile = new File(testFileRootPath + "TestClass.java");
        Class<?> javaClass = TestClass.class;

        JavaInputReader javaInputReader = new JavaInputReader();
        Map<String, Object> model =
            javaInputReader.createModel(new Object[] {
                JavaParserUtil.getFirstJavaClass(new FileReader(javaSourceFile)), javaClass });
        Assert.assertNotNull("No model has been created!", model);

        // Check parser feature (resolving of parametric type variables)
        Map<String, Object> fieldAttributes = JavaModelUtil.getField(model, "customList");
        Assert.assertEquals("Parametric types are not be resolved correctly!", "List<String>",
            fieldAttributes.get(ModelConstant.TYPE));
    }

    /**
     * Tests whether both features can be used when providing parsed and reflected inputs for one java class,
     * whereas one model does not provide any fields and/or methods
     *
     * @throws FileNotFoundException
     *             test fails
     */
    @Test
    public void provideParsingAndReflectionModelFeatures_oneModelEmpty() throws FileNotFoundException {

        File javaSourceFile = new File(testFileRootPath + "TestClass_empty.java");
        Class<?> javaClass = TestClass.class;

        JavaInputReader javaInputReader = new JavaInputReader();
        Map<String, Object> model =
            javaInputReader.createModel(new Object[] {
                JavaParserUtil.getFirstJavaClass(new FileReader(javaSourceFile)), javaClass });
        Assert.assertNotNull("No model has been created!", model);

    }

    /**
     * Tests whether both features can be used when providing parsed and reflected inputs for one java class,
     * whereas the models are equal and contain boolean values within annotations
     *
     * @throws FileNotFoundException
     *             test fails
     */
    @Test
    public void provideParsingAndReflectionModelFeatures_withAnnotations() throws FileNotFoundException {

        File javaSourceFile = new File(testFileRootPath + "TestClassWithAnnotations.java");
        Class<?> javaClass = TestClassWithAnnotations.class;

        JavaInputReader javaInputReader = new JavaInputReader();
        Map<String, Object> model =
            javaInputReader.createModel(new Object[] {
                JavaParserUtil.getFirstJavaClass(new FileReader(javaSourceFile)), javaClass });
        Assert.assertNotNull("No model has been created!", model);

    }

    /**
     * Tests whether both features can be used when providing parsed and reflected inputs for one java class,
     * whereas the models are equal and contain recursive annotations
     *
     * @throws FileNotFoundException
     *             test fails
     */
    @Test
    public void provideParsingAndReflectionModelFeatures_withRecursiveAnnotations()
        throws FileNotFoundException {

        File javaSourceFile = new File(testFileRootPath + "TestClassWithRecursiveAnnotations.java");
        Class<?> javaClass = TestClassWithRecursiveAnnotations.class;

        JavaInputReader javaInputReader = new JavaInputReader();
        Map<String, Object> model =
            javaInputReader.createModel(new Object[] {
                JavaParserUtil.getFirstJavaClass(new FileReader(javaSourceFile)), javaClass });
        Assert.assertNotNull("No model has been created!", model);

    }

    /**
     * Tests whether both features can be used when providing parsed and reflected inputs for one java class,
     * whereas the models are equal and an annotation contains an object array as property value
     *
     * @throws FileNotFoundException
     *             test fails
     */
    @Test
    public void provideParsingAndReflectionModelFeatures_withAnnotationsContainingObjectArrays()
        throws FileNotFoundException {

        File javaSourceFile =
            new File(testFileRootPath + "TestClassWithAnnotationsContainingObjectArrays.java");
        Class<?> javaClass = TestClassWithAnnotationsContainingObjectArrays.class;

        JavaInputReader javaInputReader = new JavaInputReader();
        Map<String, Object> model =
            javaInputReader.createModel(new Object[] {
                JavaParserUtil.getFirstJavaClass(new FileReader(javaSourceFile)), javaClass });
        Assert.assertNotNull("No model has been created!", model);

    }

    /**
     * Test method for {@link JavaInputReader#getTemplateMethods(Object)}. Checks whether the returning Map
     * contains isSubtypeOf and isAstract as template methods.
     */
    @Test
    public void testgetTemplateMethods() {
        // create instance
        JavaInputReader reader = new JavaInputReader();

        // create test data
        Map<String, Object> methods = reader.getTemplateMethods(getClass());
        Set<String> keys = methods.keySet();

        // validate
        Assert.assertTrue(keys.contains("isSubtypeOf"));
        Assert.assertTrue(keys.contains("isAbstract"));
        Assert.assertTrue(methods.get("isSubtypeOf") instanceof IsSubtypeOfMethod);
        Assert.assertTrue(methods.get("isAbstract") instanceof IsAbstractMethod);
    }
}
