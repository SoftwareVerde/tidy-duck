package com.softwareverde.mostadapter;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertEquals;

public class MostAdapterTests {

    // function catalog tests
    private static final String FUNCTION_CATALOG_WITHOUT_FUNCTION_BLOCKS_XML = "/function_catalog_without_function_blocks.xml";
    // function block tests
    private static final String FUNCTION_BLOCK_WITHOUT_INTERFACES_XML = "/function_block_without_functions_indented.xml";
    private static final String MULTIPLE_FUNCTION_BLOCKS_WITHOUT_INTERFACES_XML = "/multiple_function_blocks_without_functions_indented.xml";

    @Test
    public void should_generate_xml_for_functionCatalog_without_functionBlocks() throws Exception {
        final FunctionCatalog functionCatalog = createTestFunctionCatalog("3.0.3.2", "WG DA", "MOST Cooperation");

        final MostAdapter adapter = new MostAdapter();
        final String mostXml = adapter.getMostXml(functionCatalog);

        assertEquals(getCorrectXml(FUNCTION_CATALOG_WITHOUT_FUNCTION_BLOCKS_XML), mostXml);
    }

    @Test
    public void should_generate_xml_for_functionCatalog_without_functionBlocks_with_indented_output() throws Exception {
        final FunctionCatalog functionCatalog = createDefaultTestFunctionCatalog();

        final Date lastModifiedDate = new Date(1297702800000L); // date -d "2011-02-14 12:00:00" +%s (converted to ms)
        final FunctionBlock functionBlock = createTestFunctionBlock(
                "0x0F",
                "EnhancedTestability",
                "Proprietary",
                "This is an FBlock description.",
                "3.0.2",
                lastModifiedDate,
                "Specification Support",
                "MOST Cooperation",
                "public");
        functionCatalog.addFunctionBlock(functionBlock);

        final MostAdapter adapter = new MostAdapter();
        adapter.setIndented(true);
        final String mostXml = adapter.getMostXml(functionCatalog);

        assertEquals(getCorrectXml(FUNCTION_BLOCK_WITHOUT_INTERFACES_XML), mostXml);
    }

    @Test
    public void should_generate_xml_for_functionCatalog_with_multiple_functionBlocks_with_indented_output() throws Exception {
        final FunctionCatalog functionCatalog = createDefaultTestFunctionCatalog();

        final Date lastModifiedDate1 = new Date(1297702800000L); // date -d "14-FEB-2011 12:00:00" +%s (converted to ms)
        final FunctionBlock functionBlock1 = createTestFunctionBlock(
            "0x0F",
            "EnhancedTestability",
            "Proprietary",
            "This is an FBlock description.",
            "3.0.2",
            lastModifiedDate1,
            "Specification Support",
            "MOST Cooperation",
            "public"
        );
        functionCatalog.addFunctionBlock(functionBlock1);

        final Date lastModifiedDate2 = new Date(1489852800000L); // date -d "18-MAR-2017 12:00:00" +%s (converted to ms)
        final FunctionBlock functionBlock2 = createTestFunctionBlock(
                "0x10",
                "SecondFunctionBlock",
                "Test",
                "This is a second FBlock description.",
                "3.0.3",
                lastModifiedDate2,
                "Test User",
                "Software Verde",
                "private");
        functionCatalog.addFunctionBlock(functionBlock2);

        final MostAdapter adapter = new MostAdapter();
        adapter.setIndented(true);
        final String mostXml = adapter.getMostXml(functionCatalog);

        assertEquals(getCorrectXml(MULTIPLE_FUNCTION_BLOCKS_WITHOUT_INTERFACES_XML), mostXml);
    }

    private FunctionCatalog createDefaultTestFunctionCatalog() {
        return createTestFunctionCatalog(
                "3.0.2.2",
                "Specification Support",
                "MOST Cooperation"
        );
    }

    private FunctionCatalog createTestFunctionCatalog(String release, String author, String company) {
        FunctionCatalog functionCatalog = new FunctionCatalog();
        functionCatalog.setRelease(release);

        functionCatalog.setAuthor(author);
        functionCatalog.setCompany(company);

        functionCatalog.addClassDefinition(new ClassDefinition("class_trigger", "Trigger", null));
        functionCatalog.addClassDefinition(new ClassDefinition("class_switch", "Switch", null));

        return functionCatalog;
    }

    private FunctionBlock createTestFunctionBlock(String mostId, String name, String kind, String description, String release, Date lastModifiedDate, String author, String company, String access) {
        FunctionBlock functionBlock = new FunctionBlock();
        functionBlock.setMostId(mostId);
        functionBlock.setName(name);
        functionBlock.setKind(kind);
        functionBlock.setDescription(description);
        functionBlock.setRelease(release);
        functionBlock.setLastModifiedDate(lastModifiedDate);
        functionBlock.setAuthor(author);
        functionBlock.setCompany(company);
        functionBlock.setAccess(access);

        return functionBlock;
    }

    private String getResourceAsString(String resourcePath) throws IOException {
        InputStream inputStream = getClass().getResource(resourcePath).openStream();
        java.util.Scanner s = new java.util.Scanner(inputStream).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    private String getCorrectXml(String resourcePath) throws IOException {
        String xml = getResourceAsString(resourcePath);
        String correctedXml = xml.replace("%CURRENT_DATE%", getCurrentDate());
        return correctedXml;
    }

    private String getCurrentDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
        return formatter.format(new Date()).toUpperCase();
    }
}
