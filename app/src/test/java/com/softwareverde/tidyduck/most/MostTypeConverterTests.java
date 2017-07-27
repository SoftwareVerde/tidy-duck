package com.softwareverde.tidyduck.most;

import org.junit.Assert;
import org.junit.Test;

import java.util.Date;
import java.util.List;

public class MostTypeConverterTests {

    private Company createCompany() {
        Company company = new Company();
        company.setId(3L);
        company.setName("Company Name");
        return company;
    }

    private Author createAuthor(Company company) {
        Author author = new Author();
        author.setId(2L);
        author.setName("Test Author");
        author.setCompany(company);
        return author;
    }

    private FunctionCatalog createFunctionCatalog(long id, String name, String release, Author author, Company company) {
        FunctionCatalog functionCatalog = new FunctionCatalog();
        functionCatalog.setId(id);
        functionCatalog.setName(name);
        functionCatalog.setRelease(release);
        functionCatalog.setReleased(true);
        functionCatalog.setAuthor(author);
        functionCatalog.setCompany(company);
        return functionCatalog;
    }

    private FunctionBlock createFunctionBlock(String mostId, String name, String access, String release, Author author, Company company) {
        FunctionBlock functionBlock = new FunctionBlock();
        functionBlock.setMostId(mostId);
        functionBlock.setName(name);
        functionBlock.setAccess(access);
        functionBlock.setDescription("This is a function block description.");
        functionBlock.setRelease(release);
        functionBlock.setKind("Proprietary");
        functionBlock.setLastModifiedDate(new Date());
        functionBlock.setAuthor(author);
        functionBlock.setCompany(company);

        return functionBlock;
    }

    @Test
    public void convertFunctionCatalog_convert_large_function_catalog() {
        // Setup
        Company company = createCompany();
        Author author = createAuthor(company);
        FunctionCatalog functionCatalog = createFunctionCatalog(1L, "Test Function Catalog", "Release", author, company);

        FunctionBlock functionBlock1 = createFunctionBlock("0x01", "Test FBlock 1", "public", "1", author, company);
        functionCatalog.addFunctionBlock(functionBlock1);

        FunctionBlock functionBlock2 = createFunctionBlock("0x02", "Test FBlock 2", "private", "2", author, company);
        functionCatalog.addFunctionBlock(functionBlock2);

        // Action
        MostTypeConverter mostTypeConverter = new MostTypeConverter();
        com.softwareverde.mostadapter.FunctionCatalog convertedFunctionCatalog = mostTypeConverter.convertFunctionCatalog(functionCatalog);

        // Assert
        // function catalog
        Assert.assertEquals("Test Function Catalog", convertedFunctionCatalog.getName());
        Assert.assertEquals("Release", convertedFunctionCatalog.getRelease());
        Assert.assertEquals("Test Author", convertedFunctionCatalog.getAuthor());
        Assert.assertEquals("Company Name", convertedFunctionCatalog.getCompany());

        List<com.softwareverde.mostadapter.FunctionBlock> convertedFunctionBlocks = convertedFunctionCatalog.getFunctionBlocks();
        Assert.assertEquals(2, convertedFunctionBlocks.size());
        // function block 1
        Assert.assertEquals("0x01", convertedFunctionBlocks.get(0).getMostId());
        Assert.assertEquals("Test FBlock 1", convertedFunctionBlocks.get(0).getName());
        Assert.assertEquals("public", convertedFunctionBlocks.get(0).getAccess());
        Assert.assertEquals("Proprietary", convertedFunctionBlocks.get(0).getKind());
        Assert.assertEquals("This is a function block description.", convertedFunctionBlocks.get(0).getDescription());
        // function block 2
        Assert.assertEquals("0x02", convertedFunctionBlocks.get(1).getMostId());
        Assert.assertEquals("Test FBlock 2", convertedFunctionBlocks.get(1).getName());
        Assert.assertEquals("private", convertedFunctionBlocks.get(1).getAccess());
        Assert.assertEquals("Proprietary", convertedFunctionBlocks.get(1).getKind());
        Assert.assertEquals("This is a function block description.", convertedFunctionBlocks.get(1).getDescription());

    }
}
