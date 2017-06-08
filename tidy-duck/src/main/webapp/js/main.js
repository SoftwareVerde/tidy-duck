
class FunctionCatalogSubmitButton extends React.Component {
    handleClick() {
        var newDisplayAreaChild = document.createElement("DIV");
        newDisplayAreaChild.className = "function-catalog";
        document.getElementById("child-display-area").appendChild(newDisplayAreaChild);

        ReactDOM.render(<FunctionCatalog/>, newDisplayAreaChild); //document.getElementById('child-display-area'));
    }

    render() {
        return ( <button onClick={this.handleClick}>Submit</button> );
    }
}

class NavEntry extends React.Component {

    /*
     Change display area to show parent object any siblings.
     TODO: This is currently hardcoded to return to the top level "Function Catalogs" navigation entry.
     TODO: It needs to be able to find its parent, display the React elements associated with it in the
     TODO: display area, and hide any unrelated elements.
     */

    returnToParentNavigationEntry() {
        //Get Metadata Form element so that it can be revealed.
        var metaDataForm = document.getElementsByClassName("metadata-form")[0];
        metaDataForm.style.display = "flex";
        var navigationColumn = document.getElementsByClassName("navigation-column")[0];;

        //TODO: This loop does not delete the correct amount of child elements in the navigation column,
        //assuming you have multiple navigation entries listed
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

    render() {
        return <div
            className="navigation-entry"
            onClick={() => this.returnToParentNavigationEntry()}>
            {this.props.name}
        </div>;
    }
}

class FunctionCatalog extends React.Component {
    constructor() {
        super();
        this.state = {
            newName : document.getElementById("name").value,
        };
        //Need to bind certain properties to functions before they can be used.
        this.clickOnFunctionCatalog = this.clickOnFunctionCatalog.bind(this);
    }

    clickOnFunctionCatalog() {
        //const newName = this.state.newName;
        //Get Metadata Form element so that it can be hidden.
        var metaDataForm = document.getElementsByClassName("metadata-form")[0];
        metaDataForm.style.display = "none";

        //TODO: Get Function Catalog that was clicked on so that it can be hidden.

        //Create new Navigation Entry DIV element.
        //This is currently using JS and is NOT rendering new React elements.
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
    }

    render() {
        /*
        //Could move these consts to a constructor for reuse in other functions?
        const newName = document.getElementById("name").value;
        const newRelease = document.getElementById("release").value;
        const newAuthor = document.getElementById("author").value;
        const newDate = document.getElementById("date").value;
        const newCompany = document.getElementById("company").value;
        */
        return (
            /*
            <div className="function-catalog" onClick={this.clickOnFunctionCatalog}>
                {this.state.newName}
            </div>
            */
            <div onClick={this.clickOnFunctionCatalog}>
                 {this.state.newName}
            </div>
        );
    }
}

//Render default navigation column entry: "Function Catalogs"
ReactDOM.render(<NavEntry name="Function Catalogs"/>, document.getElementsByClassName("navigation-column")[0]);

//Render Function Catalog submit button.
ReactDOM.render(<FunctionCatalogSubmitButton/>, document.getElementById('function-catalog-submit'));