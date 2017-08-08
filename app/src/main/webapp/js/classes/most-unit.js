class MostUnit {
    static fromJson(json) {
        if (json == null) {
            return null;
        }
        const mostUnit = new MostUnit();

        mostUnit.setId(json.id);
        mostUnit.setReferenceName(json.referenceName);
        mostUnit.setDefinitionName(json.definitionName);
        mostUnit.setDefinitionCode(json.definitionCode);
        mostUnit.setDefinitionGroup(json.definitionGroup);

        return mostUnit;
    }

    static toJson(mostUnit) {
        if (mostUnit == null) {
            return null;
        }
        return {
            id:                 mostUnit.getId(),
            referenceName:      mostUnit.getReferenceName(),
            definitionName:     mostUnit.getDefinitionName(),
            definitionCode:     mostUnit.getDefinitionCode(),
            definitionGroup:    mostUnit.getDefinitionGroup(),
        };
    }

    constructor() {
        this._id                = null;
        this._referenceName     = null;
        this._definitionName    = null;
        this._definitionCode    = null;
        this._definitionGroup   = null;
    };

    setId(id) {
        this._id = id;
    }

    getId() {
        return this._id;
    }

    setReferenceName(referenceName) {
        this._referenceName = referenceName;
    }

    getReferenceName() {
        return this._referenceName;
    }

    setDefinitionName(definitionName) {
        this._definitionName = definitionName;
    }

    getDefinitionName() {
        return this._definitionName;
    }

    setDefinitionCode(definitionCode) {
        this._definitionCode = definitionCode;
    }

    getDefinitionCode() {
        return this._definitionCode;
    }

    setDefinitionGroup(definitionGroup) {
        this._definitionGroup = definitionGroup;
    }

    getDefinitionGroup() {
        return this._definitionGroup;
    }

}