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

        this.onSubmit = this.onSubmit.bind(this);
        this.onInputChanged = this.onInputChanged.bind(this);
    }

    onInputChanged(newValue, inputName) {
        this.state[inputName] = newValue;
    }

    onSubmit() {
        var versionId = 1; // TODO
        var functionCatalogJson = {
            name:           this.state.name,
            release:        this.state.releaseVersion,
            releaseDate:    this.state.date,
            authorId:       this.state.author,
            companyId:      this.state.company
        };

        insertFunctionCatalog(versionId, functionCatalogJson, new function(data) {
            console.log(data);
        });

        var newDisplayAreaChild = document.createElement("div");
        newDisplayAreaChild.className = "function-catalog";
        document.getElementById("child-display-area").appendChild(newDisplayAreaChild);

        // Render function catalog in new display area slot.
        ReactDOM.render(<app.FunctionCatalog />, newDisplayAreaChild);
    }

    render() {
        return (
            <div>
                <app.InputField id="function-catalog-name" name="name" type="text" label="Name" value={this.state.name} readOnly={this.props.readOnly} onChange={this.onInputChanged} />
                <app.InputField id="function-catalog-release-version" name="releaseVersion" type="text" label="Release" value={this.state.releaseVersion} readOnly={this.props.readOnly} onChange={this.onInputChanged} />
                <app.InputField id="function-catalog-date" name="date" type="text" label="Date" value={this.state.date} readOnly={this.props.readOnly} onChange={this.onInputChanged} />
                <app.InputField id="function-catalog-author" name="author" type="text" label="Author" value={this.state.author} readOnly={this.props.readOnly} onChange={this.onInputChanged} />
                <app.InputField id="function-catalog-company" name="company" type="text" label="Company" value={this.state.company} readOnly={this.props.readOnly} onChange={this.onInputChanged} />
                <div className="center"><div className="submit-button" id="function-catalog-submit" onClick={this.onSubmit}>Submit</div></div>
            </div>
        );
    }
}

(function (app) {
    app.FunctionCatalogForm = FunctionCatalogForm;
})(window.app || (window.app = { }))
