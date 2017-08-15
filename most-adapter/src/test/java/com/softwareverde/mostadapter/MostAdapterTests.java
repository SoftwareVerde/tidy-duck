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
            "private"
        );
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

    private FunctionCatalog createTestFunctionCatalog(final String release, final String author, final String company) {
        final FunctionCatalog functionCatalog = new FunctionCatalog();
        functionCatalog.setRelease(release);

        functionCatalog.setAuthor(author);
        functionCatalog.setCompany(company);

        functionCatalog.addClassDefinition(new ClassDefinition("class_trigger", "Trigger", null));
        functionCatalog.addClassDefinition(new ClassDefinition("class_switch", "Switch", null));

        functionCatalog.addPropertyCommandDefinition(new PropertyCommandDefinition("PCmdSet", "0x0", "Set", null));
        functionCatalog.addPropertyCommandDefinition(new PropertyCommandDefinition("PCmdGet", "0x1", "Get", null));

        functionCatalog.addMethodCommandDefinition(new MethodCommandDefinition("MCmdStart", "0x0", "Start", null));
        functionCatalog.addMethodCommandDefinition(new MethodCommandDefinition("MCmdAbort", "0x1", "Abort", null));

        functionCatalog.addPropertyReportDefinition(new PropertyReportDefinition("PReportStatus", "0xC", "Status", null));
        functionCatalog.addPropertyReportDefinition(new PropertyReportDefinition("PReportInterface", "0xE", "Interface", null));

        functionCatalog.addMethodReportDefinition(new MethodReportDefinition("MReportErrorAck", "0x9", "ErrorAck", null));
        functionCatalog.addMethodReportDefinition(new MethodReportDefinition("MReportProcessingAck", "0xA", "ProcessingAck", null));

        functionCatalog.addTypeDefinition(new TypeDefinition("type_record", "Record", 255, null));
        functionCatalog.addTypeDefinition(new TypeDefinition("type_array", "Array", 255, null));

        functionCatalog.addUnitDefinition(new UnitDefinition("unit_1_min", "1/min", "0x20", "Frequency"));
        functionCatalog.addUnitDefinition(new UnitDefinition("unit_360_degree_2pow32", "360_deg/2pow32", "0xA3", "Angle"));

        functionCatalog.addErrorDefinition(new ErrorDefinition("error_general_0x01", "0x01", " = FBlockID not available", "0x00", null));
        functionCatalog.addErrorDefinition(new ErrorDefinition("error_general_0x02", "0x02", " = InstID not available", "0x00", null));

        return functionCatalog;
    }

    private FunctionBlock createTestFunctionBlock(final String mostId, final String name, final String kind, final String description, final String release, final Date lastModifiedDate, final String author, final String company, final String access) {
        final FunctionBlock functionBlock = new FunctionBlock();
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

    private String getResourceAsString(final String resourcePath) throws IOException {
        final InputStream inputStream = getClass().getResource(resourcePath).openStream();
        java.util.Scanner s = new java.util.Scanner(inputStream).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    private String getCorrectXml(final String resourcePath) throws IOException {
        final String xml = getResourceAsString(resourcePath);
        final String correctedXml = xml.replace("%CURRENT_DATE%", getCurrentDate());
        return correctedXml;
    }

    private String getCurrentDate() {
        final SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
        return formatter.format(new Date()).toUpperCase();
    }
}
