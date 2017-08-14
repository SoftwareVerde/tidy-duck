package com.softwareverde.tidyduck.most;

import com.softwareverde.mostadapter.Modification;
import com.softwareverde.util.Util;

import java.util.ArrayList;
import java.util.List;

public class FunctionCatalog {
    private Long _id;
    private String _name;
    private String _release;
    private Author _author;
    private Company _company;
    private boolean _isReleased;
    private Long _baseVersionId;
    private Long _priorVersionId;
    private final List<FunctionBlock> _functionBlocks = new ArrayList<>();
    private final List<Modification> _modifications = new ArrayList<>();
    private final List<ClassDefinition> _classDefinitions = new ArrayList<>();

    public Long getId() {
        return _id;
    }

    public void setId(final long id) {
        _id = id;
    }

    public String getName() {
        return _name;
    }

    public void setName(final String name) {
        _name = name;
    }

    public String getRelease() {
        return _release;
    }

    public void setRelease(final String release) {
        _release = release;
    }

    public Author getAuthor() {
        return _author;
    }

    public void setAuthor(final Author author) {
        _author = author;
    }

    public Company getCompany() {
        return _company;
    }

    public void setCompany(final Company company) {
        _company = company;
    }

    public boolean isReleased() {
        return _isReleased;
    }

    public void setReleased(final boolean released) {
        _isReleased = released;
    }

    public Long getBaseVersionId() {
        return _baseVersionId;
    }

    public void setBaseVersionId(final Long baseVersionId) {
        _baseVersionId = baseVersionId;
    }

    public Long getPriorVersionId() {
        return _priorVersionId;
    }

    public void setPriorVersionId(final Long priorVersionId) {
        _priorVersionId = priorVersionId;
    }

    public List<FunctionBlock> getFunctionBlocks() {
        return Util.copyList(_functionBlocks);
    }

    public List<ClassDefinition> getClassDefinitions() {
        return Util.copyList(_classDefinitions);
    }

    public void addClassDefinition(final ClassDefinition classDefinition) {
        _classDefinitions.add(classDefinition);
    }

    public void setClassDefinitions(final List<ClassDefinition> classDefinitions) {
        _classDefinitions.clear();
        _classDefinitions.addAll(classDefinitions);
    }

    public void addFunctionBlock(final FunctionBlock functionBlock) {
        _functionBlocks.add(functionBlock);
    }

    public void setFunctionBlocks(final List<FunctionBlock> functionBlocks) {
        _functionBlocks.clear();
        _functionBlocks.addAll(functionBlocks);
    }

    public List<Modification> getModifications() {
        return Util.copyList(_modifications);
    }

    public void addModification(final Modification modification) {
        _modifications.add(modification);
    }

    public void setModifications(final List<Modification> modifications) {
        _modifications.clear();
        _modifications.addAll(modifications);
    }
}
