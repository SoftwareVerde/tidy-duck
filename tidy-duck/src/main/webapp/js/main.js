
class FunctionCatalogSubmitButton extends React.Component {
    handleClick() {
        ReactDOM.render(<FunctionCatalog/>, document.getElementById('child-display-area'));
    }

    render() {
        return ( <button onClick={this.handleClick}>Submit</button> );
    }
}

class FunctionCatalog extends React.Component {
    render() {
        const newName = document.getElementById("name").value;
        const newRelease = document.getElementById("release").value;
        const newAuthor = document.getElementById("author").value;
        const newDate = document.getElementById("date").value;
        const newCompany = document.getElementById("company").value;

        return (
            <div className="function-block">{newName}</div>
        )
    }
}

ReactDOM.render(<FunctionCatalogSubmitButton/>, document.getElementById('function-catalog-submit'));
