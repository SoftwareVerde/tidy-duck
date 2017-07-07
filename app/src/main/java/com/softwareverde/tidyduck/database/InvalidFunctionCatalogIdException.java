package com.softwareverde.tidyduck.database;

public class InvalidFunctionCatalogIdException extends RuntimeException {
    InvalidFunctionCatalogIdException() {
        super("Invalid Function Catalog ID");
    }
}
