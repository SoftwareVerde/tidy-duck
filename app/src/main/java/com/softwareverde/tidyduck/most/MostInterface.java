package com.softwareverde.tidyduck.most;

import com.softwareverde.mostadapter.Modification;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MostInterface {
    private Long _id;
    private String _mostId;
    private String _name;
    private String _description;
    private String _version;
    private Date _lastModifiedDate;
    private boolean _isReleased;
    private Long _baseVersionId;
    private Long _priorVersionId;
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

    public boolean isReleased() {
        return _isReleased;
    }

    public void setReleased(boolean released) {
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

    public void setMostFunctions(List<MostFunction> mostFunctions) {
        _mostFunctions = new ArrayList<>(mostFunctions);
    }
}
