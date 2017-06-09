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

class FunctionCatalogsMetadataForm extends React.Component {
    //Currently renders original, functioning table HTML.
    render() {
        return (
            <table>
                <tbody>
                    <tr>
                        <td><label htmlFor="name">Name:</label></td>
                        <td><input type="text" id="name"/></td>
                    </tr>
                    <tr>
                        <td><label htmlFor="release">Release:</label></td>
                        <td><input type="text" id="release"/></td>
                        <td><label htmlFor="date">Date:</label></td>
                        <td><input type="text" id="date"/></td>
                    </tr>
                    <tr>
                        <td><label htmlFor="author">Author:</label></td>
                        <td><input type="text" id="author"/></td>
                        <td><label htmlFor="company">Company:</label></td>
                        <td><input type="text" id="company"/></td>
                    </tr>
                    <tr>
                        <td colSpan="3"></td>
                        <td><div id="function-catalog-submit"></div></td>
                    </tr>
                </tbody>
            </table>
        );
    }
}

class FunctionCatalogMetadataForm extends React.Component {
    //Renders Function Catalog metadata table for currently selected Function Catalog.
    render() {
        return (
            <table>
                <tbody>
                <tr>
                    <td><label htmlFor="release">Release:</label></td>
                    <td><input type="text" id="release" readOnly={true} value={this.props.release}/></td>
                    <td><label htmlFor="date">Date:</label></td>
                    <td><input type="text" id="date" readOnly={true} value={this.props.date}/></td>
                </tr>
                <tr>
                    <td><label htmlFor="author">Author:</label></td>
                    <td><input type="text" id="author" readOnly={true} value={this.props.author}/></td>
                    <td><label htmlFor="company">Company:</label></td>
                    <td><input type="text" id="company" readOnly={true} value={this.props.company}/></td>
                </tr>
                <tr>
                    <td colSpan="3"></td>
                    <td><div id="function-catalog-submit"></div></td>
                </tr>
                </tbody>
            </table>
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
        //Render Function Catalog submit button.
        ReactDOM.render(<FunctionCatalogSubmitButton/>, document.getElementById('function-catalog-submit'));

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
            newName : document.getElementById("name").value,
            newRelease : document.getElementById("release").value,
            newAuthor : document.getElementById("author").value,
            newDate : document.getElementById("date").value,
            newCompany : document.getElementById("company").value,
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
        ReactDOM.render(<FunctionCatalogMetadataForm
            release = {this.state.newRelease}
            date = {this.state.newDate}
            author = {this.state.newAuthor}
            company = {this.state.newCompany}
        />, document.getElementsByClassName("metadata-form")[0]);
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

//Render Function Catalog submit button.
ReactDOM.render(<FunctionCatalogSubmitButton/>, document.getElementById('function-catalog-submit'));

//Render default navigation column entry: "Function Catalogs"
ReactDOM.render(<NavEntry name="Function Catalogs"/>, document.getElementsByClassName("navigation-column")[0]);

