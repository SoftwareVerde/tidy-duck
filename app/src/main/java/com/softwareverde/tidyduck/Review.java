package com.softwareverde.tidyduck;

import com.softwareverde.tidyduck.most.FunctionBlock;
import com.softwareverde.tidyduck.most.FunctionCatalog;
import com.softwareverde.tidyduck.most.MostFunction;
import com.softwareverde.tidyduck.most.MostInterface;

import java.util.Date;

public class Review {
    private Long _id;
    private FunctionCatalog _functionCatalog = null;
    private FunctionBlock _functionBlock = null;
    private MostInterface _mostInterface = null;
    private MostFunction _mostFunction = null;
    private Account _account;
    private Date _createdDate;

    public Long getId() {
        return _id;
    }

    public void setId(final Long id) {
        _id = id;
    }

    public FunctionCatalog getFunctionCatalog() {
        return _functionCatalog;
    }

    public void setFunctionCatalog(final FunctionCatalog functionCatalog) {
        _functionCatalog = functionCatalog;
    }

    public FunctionBlock getFunctionBlock() {
        return _functionBlock;
    }

    public void setFunctionBlock(final FunctionBlock functionBlock) {
        _functionBlock = functionBlock;
    }

    public MostInterface getMostInterface() {
        return _mostInterface;
    }

    public void setMostInterface(final MostInterface mostInterface) {
        _mostInterface = mostInterface;
    }

    public MostFunction getMostFunction() {
        return _mostFunction;
    }

    public void setMostFunction(final MostFunction mostFunction) {
        _mostFunction = mostFunction;
    }

    public Account getAccount() {
        return _account;
    }

    public void setAccount(final Account account) {
        _account = account;
    }

    public Date getCreatedDate() {
        return _createdDate;
    }

    public void setCreatedDate(final Date createdDate) {
        _createdDate = createdDate;
    }
}
