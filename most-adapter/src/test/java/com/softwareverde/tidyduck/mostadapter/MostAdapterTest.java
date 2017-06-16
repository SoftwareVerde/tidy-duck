package com.softwareverde.tidyduck.mostadapter;

import com.softwareverde.tidyduck.Author;
import com.softwareverde.tidyduck.Company;
import com.softwareverde.tidyduck.FunctionBlock;
import com.softwareverde.tidyduck.FunctionCatalog;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import static org.junit.Assert.assertEquals;

public class MostAdapterTest {

    // function catalog tests
    private static final String FUNCTION_CATALOG_WITHOUT_FUNCTION_BLOCKS_XML = "/function_catalog_without_function_blocks.xml";
    // function block tests
    private static final String FUNCTION_BLOCK_WITHOUT_INTERFACES_XML = "/function_block_without_functions_indented.xml";
    private static final String MULTIPLE_FUNCTION_BLOCKS_WITHOUT_INTERFACES_XML = "/multiple_function_blocks_without_functions_indented.xml";

    @Test
    public void testFunctionCatalogWithoutFunctionBlocks() throws MostAdapterException, IOException {
        Date releaseDate = new Date(1475251200000L); // date -d "30-SEP-2016 12:00:00" +%s (converted to ms)
        FunctionCatalog functionCatalog = createTestFunctionCatalog("3.0.3.2", releaseDate, "WG DA", "MOST Cooperation");

        MostAdapter adapter = new MostAdapter();
        String mostXml = adapter.getMostXml(functionCatalog);

        assertEquals(getResourceAsString(FUNCTION_CATALOG_WITHOUT_FUNCTION_BLOCKS_XML), mostXml);
    }

    @Test
    public void testFunctionBlockWithoutFunctions_Indented() throws MostAdapterException, IOException {
        FunctionCatalog functionCatalog = createDefaultTestFunctionCatalog();

        Date lastModifiedDate = new Date(1297702800000L); // date -d "2011-02-14 12:00:00" +%s (converted to ms)
        FunctionBlock functionBlock = createTestFunctionBlock(
                "0x0F",
                "EnhancedTestability",
                "This is an FBlock description.",
                "3.0.2",
                lastModifiedDate,
                "Specification Support",
                "MOST Cooperation");
        functionCatalog.addFunctionBlock(functionBlock);

        MostAdapter adapter = new MostAdapter();
        adapter.setIndented(true);
        String mostXml = adapter.getMostXml(functionCatalog);

        assertEquals(getResourceAsString(FUNCTION_BLOCK_WITHOUT_INTERFACES_XML), mostXml);
    }

    @Test
    public void testMultipleFunctionBlocksWithoutFunctions_Indented() throws MostAdapterException, IOException {
        FunctionCatalog functionCatalog = createDefaultTestFunctionCatalog();

        Date lastModifiedDate1 = new Date(1297702800000L); // date -d "14-FEB-2011 12:00:00" +%s (converted to ms)
        FunctionBlock functionBlock1 = createTestFunctionBlock(
                "0x0F",
                "EnhancedTestability",
                "This is an FBlock description.",
                "3.0.2",
                lastModifiedDate1,
                "Specification Support",
                "MOST Cooperation");
        functionCatalog.addFunctionBlock(functionBlock1);

        Date lastModifiedDate2 = new Date(1489852800000L); // date -d "18-MAR-2017 12:00:00" +%s (converted to ms)
        FunctionBlock functionBlock2 = createTestFunctionBlock(
                "0x10",
                "SecondFunctionBlock",
                "This is a second FBlock description.",
                "3.0.3",
                lastModifiedDate2,
                "Test User",
                "Software Verde");
        functionCatalog.addFunctionBlock(functionBlock2);

        MostAdapter adapter = new MostAdapter();
        adapter.setIndented(true);
        String mostXml = adapter.getMostXml(functionCatalog);

        assertEquals(getResourceAsString(MULTIPLE_FUNCTION_BLOCKS_WITHOUT_INTERFACES_XML), mostXml);
    }

    private FunctionCatalog createDefaultTestFunctionCatalog() {
        Date releaseDate = new Date(1461600000000L); // date -d "25-APR-2016 12:00:00" +%s (converted to ms)
        return createTestFunctionCatalog("3.0.2.2", releaseDate, "Specification Support", "MOST Cooperation");
    }

    private FunctionCatalog createTestFunctionCatalog(String release, Date releaseDate, String account, String company) {
        FunctionCatalog functionCatalog = new FunctionCatalog();
        functionCatalog.setRelease(release);
        functionCatalog.setReleaseDate(releaseDate);

        functionCatalog.setAuthor(createTestAuthor(account));
        functionCatalog.setCompany(createTestCompany(company));
        return functionCatalog;
    }

    private FunctionBlock createTestFunctionBlock(String mostId, String name, String description, String release, Date lastModifiedDate, String account, String company) {
        FunctionBlock functionBlock = new FunctionBlock();
        functionBlock.setMostId(mostId);
        functionBlock.setName(name);
        functionBlock.setDescription(description);
        functionBlock.setRelease(release);
        functionBlock.setLastModifiedDate(lastModifiedDate);
        functionBlock.setAuthor(createTestAuthor(account));
        functionBlock.setCompany(createTestCompany(company));

        return functionBlock;
    }

    private Author createTestAuthor(String name) {
        Author author = new Author();
        author.setName(name);
        return author;
    }

    private Company createTestCompany(String name) {
        Company company = new Company();
        company.setName(name);
        return company;
    }

    private String getResourceAsString(String resourcePath) throws IOException {
        InputStream inputStream = getClass().getResource(resourcePath).openStream();
        java.util.Scanner s = new java.util.Scanner(inputStream).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}
