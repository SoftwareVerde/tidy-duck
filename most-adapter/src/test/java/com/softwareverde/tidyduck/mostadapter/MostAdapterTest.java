package com.softwareverde.tidyduck.mostadapter;

import com.softwareverde.tidyduck.Account;
import com.softwareverde.tidyduck.Company;
import com.softwareverde.tidyduck.FunctionBlock;
import com.softwareverde.tidyduck.FunctionCatalog;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import static org.junit.Assert.assertEquals;

public class MostAdapterTest {

    private static final String FUNCTION_CATALOG_WITHOUT_FUNCTION_BLOCKS_XML = "/function_catalog_without_function_blocks.xml";
    private static final String FUNCTION_BLOCK_WITHOUT_INTERFACES_XML = "/function_block_without_functions_indented.xml";

    @Test
    public void testFunctionCatalogWithoutFunctionBlocks() throws MostAdapterException, IOException {
        FunctionCatalog functionCatalog = new FunctionCatalog();
        functionCatalog.setRelease("3.0.3.2");
        Date releaseDate = new Date(1475251200000L); // date -d "30-SEP-2016 12:00:00" +%s (converted to ms)
        functionCatalog.setReleaseDate(releaseDate);
        Account account = new Account();
        account.setName("WG DA");
        functionCatalog.setAccount(account);
        Company company = new Company();
        company.setName("MOST Cooperation");
        functionCatalog.setCompany(company);

        MostAdapter adapter = new MostAdapter();
        String mostXml = adapter.getMostXml(functionCatalog);

        assertEquals(getResourceAsString(FUNCTION_CATALOG_WITHOUT_FUNCTION_BLOCKS_XML), mostXml);
    }

    @Test
    public void testFunctionBlockWithoutFunctions_Indented() throws MostAdapterException, IOException {
        FunctionCatalog functionCatalog = new FunctionCatalog();
        functionCatalog.setRelease("3.0.2.2");
        Date releaseDate = new Date(1461600000000L); // date -d "25-APR-2016 12:00:00" +%s (converted to ms)
        functionCatalog.setReleaseDate(releaseDate);
        Account account = new Account();
        account.setName("Specification Support");
        functionCatalog.setAccount(account);
        Company company = new Company();
        company.setName("MOST Cooperation");
        functionCatalog.setCompany(company);

        FunctionBlock functionBlock = new FunctionBlock();
        functionBlock.setMostId("0x0F");
        functionBlock.setName("EnhancedTestability");
        functionBlock.setDescription("This is an FBlock description.");
        functionBlock.setRelease("3.0.2");
        Date lastModifiedDate = new Date(1297702800000L); // date -d "2011-02-14 12:00:00" +%s (converted to ms)
        functionBlock.setLastModifiedDate(lastModifiedDate);
        functionBlock.setAccount(account);
        functionBlock.setCompany(company);
        functionCatalog.addFunctionBlock(functionBlock);

        MostAdapter adapter = new MostAdapter();
        adapter.setIndented(true);
        String mostXml = adapter.getMostXml(functionCatalog);

        assertEquals(getResourceAsString(FUNCTION_BLOCK_WITHOUT_INTERFACES_XML), mostXml);
    }

    private String getResourceAsString(String resourcePath) throws IOException {
        InputStream inputStream = getClass().getResource(resourcePath).openStream();
        java.util.Scanner s = new java.util.Scanner(inputStream).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}
