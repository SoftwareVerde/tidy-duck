function generateClassNames(reactObject) {
    var classes = "";
    for (var className in reactObject.state.classes) {
        var isEnabled = reactObject.state.classes[className];
        if (isEnabled) {
            classes += className +" ";
        }
    }
    return classes;
}

/*
TODO: create React classes for metadata form at top of page.
* Selected Function Catalog metadata form
 */

class FormInput extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            value: ""
        };

        this.handleChange = this.handleChange.bind(this);
    }

    handleChange(event) {
        if (! this.prop.readOnly) {
            this.setState({value: event.target.value});
        }
    }

    render() {
        return (
            <div className="input-container">
                <label htmlFor={this.props.id}>{this.props.label}:</label>
                <input type={this.props.type} id={this.props.id} name={this.props.name} value={this.state.value} onChange={this.handleChange} readOnly={this.props.readOnly} />
            </div>
        );
    }
}

class FunctionCatalogsMetadataForm extends React.Component {
    //Currently renders original, functioning table HTML.
    render() {
        return (
            <div className="center" >
                <FormInput id="new-function-catalog-name" name="name" type="text" label="Name"/>
                <FormInput id="new-function-catalog-release-version" name="release_version" type="text" label="Release"/>
                <FormInput id="new-function-catalog-date" name="date" type="text" label="Date"/>
                <FormInput id="new-function-catalog-author" name="author" type="text" label="Author"/>
                <FormInput id="new-function-catalog-company" name="company" type="text" label="Company"/>
                <FunctionCatalogSubmitButton className="submit-button" id="new-function-catalog-submit" />
            </div>
        );
    }
}

class FunctionCatalogMetadataForm extends React.Component {
    //Renders Function Catalog metadata table for currently selected Function Catalog.
    render() {
        return (
            <div className="center" >
                <FormInput id="function-catalog-name" name="name" type="text" label="Name" readOnly={true} />
                <FormInput id="function-catalog-release-version" name="release_version" type="text" label="Release" readOnly={true} />
                <FormInput id="function-catalog-date" name="date" type="text" label="Date" readOnly={true} />
                <FormInput id="function-catalog-author" name="author" type="text" label="Author" readOnly={true} />
                <FormInput id="function-catalog-company" name="company" type="text" label="Company" readOnly={true} />
                <FunctionCatalogSubmitButton className="submit-button" id="function-catalog-submit" readOnly={true} />
            </div>
        );
    }
}

class FunctionCatalogSubmitButton extends React.Component {
    handleClick() {
        /*
        Create new div to place new function catalog element. This is needed to prevent existing
        elements in the display area from being erased.
         */
        var newDisplayAreaChild = document.createElement("DIV");
        newDisplayAreaChild.className = "function-catalog";
        document.getElementById("child-display-area").appendChild(newDisplayAreaChild);

        //Render function catalog in new display area slot.
        ReactDOM.render(<FunctionCatalog/>, newDisplayAreaChild);
    }

    render() {
        return ( <button onClick={this.handleClick}>Submit</button> );
    }
}

class NavEntry extends React.Component {

    /*
     Change display area to show parent's metadata form and its children.
     TODO: This is currently hardcoded to return to the top level "Function Catalogs" navigation entry.
     TODO: It needs to be able to find its parent, display the React elements associated with it in the
     TODO: display area, and hide any unrelated elements.
     */


    constructor(props) {
        super(props);

        this.state = {
            classes: {
                "navigation-entry": true,
                "hovered": false
            },
            className: ""
        };

        this.state.className = generateClassNames(this);
    }

    returnToParentNavigationEntry() {
        //Get Metadata Form element so that it can be revealed.
        //var metaDataForm = document.getElementsByClassName("metadata-form")[0];
        //metaDataForm.style.display = "flex";
        var navigationColumn = document.getElementsByClassName("navigation-column")[0];

        //Render default metadata form at top of display area.
        ReactDOM.render(<FunctionCatalogsMetadataForm/>, document.getElementsByClassName("metadata-form")[0]);

        //TODO: This loop does not delete the correct amount of child elements in the navigation column,
        //TODO: assuming you have multiple navigation entries listed.
        //Starting i at 1 because we do not want to remove the default navigation entry.
        for(var i = 1; i < navigationColumn.childElementCount; i++){
            navigationColumn.removeChild(navigationColumn.childNodes[i]);
        }

        //Reveal existing function catalogs.
        var functionCatalogs = document.getElementsByClassName("function-catalog");
        for(var i = 0; i < functionCatalogs.length; i++) {
            functionCatalogs[i].style.display = 'inherit';
        }
    }

    toggleHover() {
        var shouldBeHovered = (! this.state.classes['hovered']);
        this.state.classes['hovered'] = shouldBeHovered
        this.setState({className: generateClassNames(this)});
    }

    render() {
        return <div
                className={this.state.className}
                onClick={() => this.returnToParentNavigationEntry()}
                onMouseEnter={() => this.toggleHover()}
                onMouseLeave={() => this.toggleHover()}
            >
            {this.props.name}
        </div>;
    }
}

class FunctionCatalog extends React.Component {
    constructor() {
        super();
        //TODO: These are the values that should be posted/retrieved from the database.
        //TODO: They are currently pulling from entries in the metadata form at the top of the display area.
        this.state = {
            newName : document.getElementById("new-function-catalog-name").value,
            newRelease : document.getElementById("new-function-catalog-release-version").value,
            newAuthor : document.getElementById("new-function-catalog-author").value,
            newDate : document.getElementById("new-function-catalog-date").value,
            newCompany : document.getElementById("new-function-catalog-company").value,
        };
        /*
        Need to bind properties to functions before they can be used.
        This prevents function catalogs from creating navigation entries with whatever "name"
        is currently in the metadata form.
        */
        this.clickOnFunctionCatalog = this.clickOnFunctionCatalog.bind(this);
    }

    clickOnFunctionCatalog() {
        //Get Metadata Form element so that it can be hidden.
        //var metaDataForm = document.getElementsByClassName("metadata-form")[0];
        //metaDataForm.style.display = "none";

        //Create new Navigation Entry DIV element.
        //TODO: New navigation entries are made using JS and are NOT rendering new React classes.
        var newNavigationEntry = document.createElement("DIV");
        newNavigationEntry.innerText = this.state.newName;
        newNavigationEntry.className = "navigation-entry";

        //Add navigation entry to Navigation Column.
        var navigationColumn = document.getElementsByClassName("navigation-column")[0];
        navigationColumn.appendChild(newNavigationEntry);

        //Hide existing function catalogs.
        var functionCatalogs = document.getElementsByClassName("function-catalog");
        for(var i = 0; i < functionCatalogs.length; i++) {
            functionCatalogs[i].style.display = 'none';
        }

        //Render default metadata form at top of display area.
        //The props that are sent here are displayed as read-only inputs.
        ReactDOM.render(
            <FunctionCatalogMetadataForm release= {this.state.newRelease} date={this.state.newDate} author={this.state.newAuthor} company={this.state.newCompany} />,
            document.getElementsByClassName("metadata-form")[0]
        );
    }

    render() {
        return (
            <div onClick={this.clickOnFunctionCatalog}>
                 {this.state.newName}
            </div>
        );
    }
}

//Render default metadata form at top of display area.
ReactDOM.render(<FunctionCatalogsMetadataForm/>, document.getElementsByClassName("metadata-form")[0]);

//Render default navigation column entry: "Function Catalogs"
ReactDOM.render(<NavEntry name="Function Catalogs"/>, document.getElementsByClassName("navigation-column")[0]);

