package com.softwareverde.tidyduck;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MostInterface extends XmlNode {

    private Long _id;
    private String _mostId;
    private String _name;
    private String _description;
    private String _version;
    private Date _lastModifiedDate;
    private boolean _isCommitted;
    private List<Modification> _modifications = new ArrayList<>();
    private List<MostFunction> _mostFunctions = new ArrayList<>();

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

    public String getVersion() {
        return _version;
    }

    public void setVersion(String version) {
        _version = version;
    }

    public Date getLastModifiedDate() {
        return _lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        _lastModifiedDate = lastModifiedDate;
    }

    public boolean isCommitted() {
        return _isCommitted;
    }

    public void setCommitted(boolean committed) {
        _isCommitted = committed;
    }

    public List<Modification> getModifications() {
        return _modifications;
    }

    public void addModification(final Modification modification) {
        _modifications.add(modification);
    }

    public List<MostFunction> getMostFunctions() {
        return new ArrayList<>(_mostFunctions);
    }

    public void addMostFunction(MostFunction mostFunction) {
        _mostFunctions.add(mostFunction);
    }

    @Override
    public Element generateXmlElement(Document document) {
        throw new UnsupportedOperationException("generateXmlElement is invalid for MostInterface as there is not associated XML for interfaces.");
    }
}
