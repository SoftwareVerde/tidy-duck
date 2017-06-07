
class FunctionCatalogSubmitButton extends React.Component {
    handleClick() {
        ReactDOM.render(<FunctionCatalog/>, document.getElementById('#child-display-area'));
    }

    render() {
        return ( <button onClick={this.handleClick}>Submit</button> );
    }
}

class FunctionCatalog extends React.Component {
    render() {
        const newName = document.getElementById("name").val();
        const newRelease = document.getElementById("release").val();
        const newAuthor = document.getElementById("author").val();
        const newDate = document.getElementById("date").val();
        const newCompany = document.getElementById("company").val();

        return (
            <div className="function-block">{newName}</div>
        )
    }
}

//$(document).load(function() {
    ReactDOM.render(<FunctionCatalogSubmitButton/>, document.getElementById('#function-catalog-submit'));
//})

