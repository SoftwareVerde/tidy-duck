package com.softwareverde.tidyduck.mostadapter;

import com.softwareverde.tidyduck.Account;
import com.softwareverde.tidyduck.Company;
import com.softwareverde.tidyduck.FunctionCatalog;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import static org.junit.Assert.assertEquals;

public class MostAdapterTest {

    private static final String SAMPLE_XML_RESOURCE_PATH = "/function_catalog_without_function_blocks.xml";

    @Test
    public void testSampleXml() throws MostAdapterException, IOException {
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

        assertEquals(getResourceAsString(SAMPLE_XML_RESOURCE_PATH), mostXml);
    }

    private String getResourceAsString(String resourcePath) throws IOException {
        InputStream inputStream = getClass().getResource(resourcePath).openStream();
        java.util.Scanner s = new java.util.Scanner(inputStream).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}
