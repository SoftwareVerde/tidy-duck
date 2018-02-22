class TrashPage extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            deletedFunctionCatalogs: [],
            deletedFunctionBlocks: [],
            deletedMostInterfaces: [],
            deletedMostFunctions: [],
            isLoadingDeletedFunctionCatalogs: true,
            isLoadingDeletedFunctionBlocks: true,
            isLoadingDeletedMostInterfaces: true,
            isLoadingDeletedMostFunctions: true
        }

        // TODO: load delete items

        this.renderFunctionCatalogs = this.renderFunctionCatalogs.bind(this);
        this.renderFunctionBlocks = this.renderFunctionBlocks.bind(this);
        this.renderMostInterfaces = this.renderMostInterfaces.bind(this);
        this.renderMostFunctions = this.renderMostFunctions.bind(this);
    }

    componentWillReceiveProps(newProps) {
    }

    renderFunctionCatalogs() {
        if (this.state.isLoadingDeletedFunctionCatalogs) {
            return <i className="fa fa-2x fa-spin fa-refresh"/>
        }
    }

    renderFunctionBlocks() {
        if (this.state.isLoadingDeletedFunctionCatalogs) {
            return <i className="fa fa-2x fa-spin fa-refresh"/>
        }
    }

    renderMostInterfaces() {
        if (this.state.isLoadingDeletedFunctionCatalogs) {
            return <i className="fa fa-2x fa-spin fa-refresh"/>
        }
    }

    renderMostFunctions() {
        if (this.state.isLoadingDeletedFunctionCatalogs) {
            return <i className="fa fa-2x fa-spin fa-refresh"/>
        }
    }

    render() {
        return (
            <div id="trash-container" className="large-container">
                <div key="function-catalogs">
                    <h3>Function Catalogs</h3>
                    {this.renderFunctionCatalogs()}
                </div>
                <hr key="sep1"/>
                <div key="function-blocks">
                    <h3>Function Blocks</h3>
                    {this.renderFunctionBlocks()}
                </div>
                <hr key="sep2"/>
                <div key="interfaces">
                    <h3>Interfaces</h3>
                    {this.renderMostInterfaces()}
                </div>
                <hr key="sep3"/>
                <div key="functions">
                    <h3>Functions</h3>
                    {this.renderMostFunctions()}
                </div>
            </div>
        );
    }
}

registerClassWithGlobalScope("TrashPage", TrashPage);