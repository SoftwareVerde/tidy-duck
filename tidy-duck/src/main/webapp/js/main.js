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

/* TODO: create React classes for metadata form at top of page.
 *  Selected Function Catalog metadata form
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
        if (! this.props.readOnly) {
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

class FunctionCatalogForm extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            name:           this.props.name,
            releaseVersion: this.props.releaseVersion,
            date:           this.props.date,
            author:         this.props.author,
            company:        this.props.company
        };
    }

    render() {
        return (
            <div className="center" >
                <FormInput id="function-catalog-name" name="name" type="text" label="Name" value={this.state.name} readOnly={this.props.readOnly} />
                <FormInput id="function-catalog-release-version" name="release_version" type="text" label="Release" value={this.state.releaseVersion} readOnly={this.props.readOnly} />
                <FormInput id="function-catalog-date" name="date" type="text" label="Date" value={this.state.date} readOnly={this.props.readOnly} />
                <FormInput id="function-catalog-author" name="author" type="text" label="Author" value={this.state.author} readOnly={this.props.readOnly} />
                <FormInput id="function-catalog-company" name="company" type="text" label="Company" value={this.state.company} readOnly={this.props.readOnly} />
                <FunctionCatalogSubmitButton id="function-catalog-submit" />
            </div>
        );
    }
}

class FunctionCatalogSubmitButton extends React.Component {
    constructor(props) {
        super(props);

        this.handleClick = this.handleClick.bind(this);
    }

    handleClick() {
        // Create new div to place new function catalog element. This is needed to prevent existing elements in the display area from being erased.

        var newDisplayAreaChild = document.createElement("div");
        newDisplayAreaChild.className = "function-catalog";
        document.getElementById("child-display-area").appendChild(newDisplayAreaChild);

        // Render function catalog in new display area slot.
        ReactDOM.render(<FunctionCatalog />, newDisplayAreaChild);
    }

    render() {
        return ( <div className="submit-button" onClick={this.handleClick}>Submit</div> );
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
        // Get Metadata Form element so that it can be revealed.
        // var metaDataForm = document.getElementsByClassName("metadata-form")[0];
        // metaDataForm.style.display = "flex";
        var navigationColumn = document.getElementsByClassName("navigation-column")[0];

        // Render default metadata form at top of display area.
        ReactDOM.render(<FunctionCatalogForm />, document.getElementsByClassName("metadata-form")[0]);

        // TODO: This loop does not delete the correct amount of child elements in the navigation column,
        // TODO: assuming you have multiple navigation entries listed.
        // Starting i at 1 because we do not want to remove the default navigation entry.
        for(var i = 1; i < navigationColumn.childElementCount; i++){
            navigationColumn.removeChild(navigationColumn.childNodes[i]);
        }

        // Reveal existing function catalogs.
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
    constructor(props) {
        super(props);

        // TODO: These are the values that should be posted/retrieved from the database.
        //  They are currently pulling from entries in the metadata form at the top of the display area.
        this.state = {
            newName : document.getElementById("function-catalog-name").value,
            newRelease : document.getElementById("function-catalog-release-version").value,
            newAuthor : document.getElementById("function-catalog-author").value,
            newDate : document.getElementById("function-catalog-date").value,
            newCompany : document.getElementById("function-catalog-company").value,
        };

        this.clickOnFunctionCatalog = this.clickOnFunctionCatalog.bind(this);
    }

    clickOnFunctionCatalog() {
        // Get Metadata Form element so that it can be hidden.
        // var metaDataForm = document.getElementsByClassName("metadata-form")[0];
        // metaDataForm.style.display = "none";

        // Create new Navigation Entry DIV element.
        // TODO: New navigation entries are made using JS and are NOT rendering new React classes.
        var newNavigationEntry = document.createElement("div");
        newNavigationEntry.innerText = this.state.newName;
        newNavigationEntry.className = "navigation-entry";

        // Add navigation entry to Navigation Column.
        var navigationColumn = document.getElementsByClassName("navigation-column")[0];
        navigationColumn.appendChild(newNavigationEntry);

        /*
        // Hide existing function catalogs.
        var functionCatalogs = document.getElementsByClassName("function-catalog");
        for(var i = 0; i < functionCatalogs.length; i++) {
            functionCatalogs[i].style.display = 'none';
        }
        */

        // Render default metadata form at top of display area.
        // The props that are sent here are displayed as read-only inputs.
        ReactDOM.render(
            <FunctionCatalogForm releaseVersion={this.state.newRelease} date={this.state.newDate} author={this.state.newAuthor} company={this.state.newCompany} readOnly={true} />,
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

// Render default metadata form at top of display area.
ReactDOM.render(<FunctionCatalogForm />, document.getElementsByClassName("metadata-form")[0]);

// Render default navigation column entry: "Function Catalogs"
ReactDOM.render(<NavEntry name="Function Catalogs"/>, document.getElementsByClassName("navigation-column")[0]);

