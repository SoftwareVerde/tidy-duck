package com.softwareverde.tidyduck.database;

public class InvalidFunctionCatalogIdException extends RuntimeException {
    InvalidFunctionCatalogIdException(final Object catalogId, final Exception exception) {
        super("Invalid Function Catalog ID: "+ catalogId, exception);
    }
}
