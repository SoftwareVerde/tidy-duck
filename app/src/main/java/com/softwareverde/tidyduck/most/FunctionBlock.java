package com.softwareverde.tidyduck.most;

import com.softwareverde.mostadapter.Modification;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FunctionBlock {
    private Long _id;
    private String _mostId;
    private String _kind = "Proprietary";
    private String _name;
    private String _description;
    private String _release;
    private Date _lastModifiedDate;
    private Author _author;
    private Company _company;
    private String _access;
    private boolean _isSink;
    private boolean _isSource;
    private boolean _isDeleted;
    private Date _deletedDate;
    private boolean _isPermanentlyDeleted;
    private Date _permanentlyDeletedDate;
    private boolean _isApproved;
    private Long _approvalReviewId;
    private boolean _isReleased;
    private Long _baseVersionId;
    private Long _priorVersionId;
    private Long _creatorAccountId;
    private List<Modification> _modifications = new ArrayList<>();
    private List<MostInterface> _mostInterfaces = new ArrayList<>();

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

    public String getKind() {
        return _kind;
    }

    public void setKind(String kind) {
        _kind = kind;
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

    public Date getLastModifiedDate() {
        return _lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        _lastModifiedDate = lastModifiedDate;
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

    public void setAccess(String access) {
        _access = access;
    }

    public String getAccess() {
        return _access;
    }

    public boolean isSink() {
        return _isSink;
    }

    public void setIsSink(final boolean sink) {
        _isSink = sink;
    }

    public boolean isSource() {
        return _isSource;
    }

    public void setIsSource(final boolean source) {
        _isSource = source;
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

    public Long getBaseVersionId() {
        return _baseVersionId;
    }

    public void setBaseVersionId(Long baseVersionId) {
        _baseVersionId = baseVersionId;
    }

    public Long getPriorVersionId() {
        return _priorVersionId;
    }

    public void setPriorVersionId(Long priorVersionId) {
        _priorVersionId = priorVersionId;
    }

    public Long getCreatorAccountId() { return _creatorAccountId; }

    public void setCreatorAccountId(final Long creatorAccountId) { _creatorAccountId = creatorAccountId; }

    public List<Modification> getModifications() {
        return _modifications;
    }

    public void addModification(final Modification modification) {
        _modifications.add(modification);
    }

    public List<MostInterface> getMostInterfaces() {
        return new ArrayList<>(_mostInterfaces);
    }

    public void addMostInterface(MostInterface mostInterface) {
        _mostInterfaces.add(mostInterface);
    }

    public void setMostInterfaces(final List<MostInterface> mostInterfaces) {
        _mostInterfaces = new ArrayList<>(mostInterfaces);
    }
}
