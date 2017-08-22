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
    private boolean _isApproved;
    private boolean _isReleased;
    private Long _baseVersionId;
    private Long _priorVersionId;
    private final List<FunctionBlock> _functionBlocks = new ArrayList<>();
    private final List<Modification> _modifications = new ArrayList<>();
    private final List<ClassDefinition> _classDefinitions = new ArrayList<>();
    private final List<PropertyCommandDefinition> _propertyCommandDefinitions = new ArrayList<>();
    private final List<MethodCommandDefinition> _methodCommandDefinitions = new ArrayList<>();
    private final List<PropertyReportDefinition> _propertyReportDefinitions = new ArrayList<>();
    private final List<MethodReportDefinition> _methodReportDefinitions = new ArrayList<>();
    private final List<TypeDefinition> _typeDefinitions = new ArrayList<>();
    private final List<UnitDefinition> _unitDefinitions = new ArrayList<>();
    private final List<ErrorDefinition> _errorDefinitions = new ArrayList<>();

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

    public boolean isApproved() {
        return _isApproved;
    }

    public void setIsApproved(final boolean approved) {
        _isApproved = approved;
    }

    public boolean isReleased() {
        return _isReleased;
    }

    public void setIsReleased(final boolean released) {
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

    public List<PropertyCommandDefinition> getPropertyCommandDefinitions() {
        return Util.copyList(_propertyCommandDefinitions);
    }

    public List<MethodCommandDefinition> getMethodCommandDefinitions() {
        return Util.copyList(_methodCommandDefinitions);
    }

    public List<PropertyReportDefinition> getPropertyReportDefinitions() {
        return Util.copyList(_propertyReportDefinitions);
    }

    public List<MethodReportDefinition> getMethodReportDefinitions() {
        return Util.copyList(_methodReportDefinitions);
    }

    public List<TypeDefinition> getTypeDefinitions() {
        return Util.copyList(_typeDefinitions);
    }

    public List<UnitDefinition> getUnitDefinitions() {
        return Util.copyList(_unitDefinitions);
    }

    public List<ErrorDefinition> getErrorDefinitions() {
        return Util.copyList(_errorDefinitions);
    }

    public void addClassDefinition(final ClassDefinition classDefinition) {
        _classDefinitions.add(classDefinition);
    }

    public void setClassDefinitions(final List<ClassDefinition> classDefinitions) {
        _classDefinitions.clear();
        _classDefinitions.addAll(classDefinitions);
    }

    public void addPropertyCommandDefinition(final PropertyCommandDefinition commandDefinition) {
        _propertyCommandDefinitions.add(commandDefinition);
    }

    public void setPropertyCommandDefinitions(final List<PropertyCommandDefinition> commandDefinitions) {
        _propertyCommandDefinitions.clear();
        _propertyCommandDefinitions.addAll(commandDefinitions);
    }

    public void addMethodCommandDefinition(final MethodCommandDefinition commandDefinition) {
        _methodCommandDefinitions.add(commandDefinition);
    }

    public void setMethodCommandDefinitions(final List<MethodCommandDefinition> commandDefinitions) {
        _methodCommandDefinitions.clear();
        _methodCommandDefinitions.addAll(commandDefinitions);
    }

    public void addPropertyReportDefinition(final PropertyReportDefinition reportDefinition) {
        _propertyReportDefinitions.add(reportDefinition);
    }

    public void setPropertyReportDefinitions(final List<PropertyReportDefinition> reportDefinitions) {
        _propertyReportDefinitions.clear();
        _propertyReportDefinitions.addAll(reportDefinitions);
    }

    public void addMethodReportDefinition(final MethodReportDefinition reportDefinition) {
        _methodReportDefinitions.add(reportDefinition);
    }

    public void setMethodReportDefinitions(final List<MethodReportDefinition> reportDefinitions) {
        _methodReportDefinitions.clear();
        _methodReportDefinitions.addAll(reportDefinitions);
    }

    public void addTypeDefinition(final TypeDefinition typeDefinition) {
        _typeDefinitions.add(typeDefinition);
    }

    public void setTypeDefinitions(final List<TypeDefinition> typeDefinitions) {
        _typeDefinitions.clear();
        _typeDefinitions.addAll(typeDefinitions);
    }

    public void addUnitDefinition(final UnitDefinition unitDefinition) {
        _unitDefinitions.add(unitDefinition);
    }

    public void setUnitDefinitions(final List<UnitDefinition> unitDefinitions) {
        _unitDefinitions.clear();
        _unitDefinitions.addAll(unitDefinitions);
    }

    public void addErrorDefinition(final ErrorDefinition errorDefinition) {
        _errorDefinitions.add(errorDefinition);
    }

    public void setErrorDefinitions(final List<ErrorDefinition> errorDefinitions) {
        _errorDefinitions.clear();
        _errorDefinitions.addAll(errorDefinitions);
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
