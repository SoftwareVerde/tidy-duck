package com.softwareverde.tidyduck.most;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>Represents a MOST function.</p>
 *
 * <p>This class is abstract as it does not implement the
 * XmlNode generateXmlElement method.  This is left for
 * concrete implementations (e.g. Property and Method) to
 * implements since the XML export may change depending
 * on the function type.</p>
 */
public abstract class MostFunction {
    private Long _id;
    private String _mostId;
    private String _name;
    private String _description;
    private String _release;
    private boolean _isDeleted;
    private Date _deletedDate;
    private boolean _isPermanentlyDeleted;
    private Date _permanentlyDeletedDate;
    private boolean _isApproved;
    private boolean _isReleased;
    private Long _approvalReviewId;
    private MostFunctionStereotype _functionStereotype;
    private Author _author;
    private Company _company;
    private String _returnParameterName;
    private String _returnParameterDescription;
    private MostType _returnType;
    private List<Operation> _operations = new ArrayList<>();

    public Long getId() {
        return _id;
    }

    public void setId(Long id) {
        _id = id;
    }

    public String getMostId() {
        return _mostId;
    }

    public void setMostId(String mostId) {
        _mostId = mostId;
    }

    public String getName() {
        return _name;
    }

    public void setName(String name) {
        _name = name;
    }

    public String getDescription() {
        return _description;
    }

    public void setDescription(String description) {
        _description = description;
    }

    public String getRelease() {
        return _release;
    }

    public void setRelease(String release) {
        _release = release;
    }

    public boolean isDeleted() {
        return _isDeleted;
    }

    public void setIsDeleted(final boolean deleted) {
        _isDeleted = deleted;
    }

    public Date getDeletedDate() {
        return _deletedDate;
    }

    public void setDeletedDate(final Date deletedDate) {
        _deletedDate = deletedDate;
    }

    public boolean isPermanentlyDeleted() {
        return _isPermanentlyDeleted;
    }

    public void setIsPermanentlyDeleted(final boolean permanentlyDeleted) {
        _isPermanentlyDeleted = permanentlyDeleted;
    }

    public Date getPermanentlyDeletedDate() {
        return _permanentlyDeletedDate;
    }

    public void setPermanentlyDeletedDate(final Date permanentlyDeletedDate) {
        _permanentlyDeletedDate = permanentlyDeletedDate;
    }

    public boolean isApproved() {
        return _isApproved;
    }

    public void setIsApproved(final boolean approved) {
        _isApproved = approved;
    }

    public Long getApprovalReviewId() { return _approvalReviewId; }

    public void setApprovalReviewId(final Long approvalReviewId) { _approvalReviewId = approvalReviewId; }

    public boolean isReleased() {
        return _isReleased;
    }

    public void setIsReleased(boolean released) {
        _isReleased = released;
    }

    public MostFunctionStereotype getFunctionStereotype() {
        return _functionStereotype;
    }

    public void setFunctionStereotype(MostFunctionStereotype functionStereotype) {
        _functionStereotype = functionStereotype;
    }

    public Author getAuthor() {
        return _author;
    }

    public void setAuthor(Author author) {
        _author = author;
    }

    public Company getCompany() {
        return _company;
    }

    public void setCompany(Company company) {
        _company = company;
    }

    public String getReturnParameterName() {
        return _returnParameterName;
    }

    public void setReturnParameterName(final String returnParameterName) {
        _returnParameterName = returnParameterName;
    }

    public String getReturnParameterDescription() {
        return _returnParameterDescription;
    }

    public void setReturnParameterDescription(final String returnParameterDescription) {
        _returnParameterDescription = returnParameterDescription;
    }

    public MostType getReturnType() {
        return _returnType;
    }

    public void setReturnType(MostType returnType) {
        _returnType = returnType;
    }

    public List<Operation> getOperations() {
        return new ArrayList<>(_operations);
    }

    public void addOperation(final Operation operation) {
        _operations.add(operation);
    }

    public void setOperations(final List<Operation> operations) {
        _operations = new ArrayList<>(operations);
    }

    public abstract String getFunctionType();
}
