class TrashPage extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            deletedFunctionCatalogs: [],
            deletedFunctionBlocks: [],
            deletedMostInterfaces: [],
            isLoadingDeletedFunctionCatalogs: true,
            isLoadingDeletedFunctionBlocks: true,
            isLoadingDeletedMostInterfaces: true
        }

        let thisPage = this;
        getFunctionCatalogsMarkedAsDeleted(function (functionCatalogs) {
            if (!functionCatalogs) {
                app.Alert.alert("Deleted Function Catalogs", "Unable to load deleted function catalogs.");
                thisPage.setState({
                    isLoadingDeletedFunctionCatalogs: false
                });
            } else {
                let objects = thisPage.getFlattenedObjectList(FunctionCatalog, functionCatalogs);
                thisPage.setState({
                    deletedFunctionCatalogs:            objects,
                    isLoadingDeletedFunctionCatalogs:   false
                });
            }
        });
        getFunctionBlocksMarkedAsDeleted(function (functionBlocks) {
            if (!functionBlocks) {
                app.Alert.alert("Deleted Function Blocks", "Unable to load deleted function blocks.");
                thisPage.setState({
                    isLoadingDeletedFunctionBlocks: false
                });
            } else {
                let objects = thisPage.getFlattenedObjectList(FunctionBlock, functionBlocks);
                thisPage.setState({
                    deletedFunctionBlocks:          objects,
                    isLoadingDeletedFunctionBlocks: false
                });
            }
        });
        getMostInterfacesMarkedAsDeleted(function (mostInterfaces) {
            if (!mostInterfaces) {
                app.Alert.alert("Deleted Interfaces", "Unable to load deleted interfaces.");
                thisPage.setState({
                    isLoadingDeletedMostInterfaces: false
                });
            } else {
                let objects = thisPage.getFlattenedObjectList(MostInterface, mostInterfaces);
                thisPage.setState({
                    deletedMostInterfaces:          objects,
                    isLoadingDeletedMostInterfaces: false
                });
            }
        });

        this.getFlattenedObjectList = this.getFlattenedObjectList.bind(this);
        this.renderItems = this.renderItems.bind(this);
        this.renderFunctionCatalogs = this.renderFunctionCatalogs.bind(this);
        this.renderFunctionBlocks = this.renderFunctionBlocks.bind(this);
        this.renderMostInterfaces = this.renderMostInterfaces.bind(this);
    }

    componentWillReceiveProps(newProps) {
    }

    getFlattenedObjectList(clazz, versionSeries) {
        let objects = [];
        for (let versionsIndex in versionSeries) {
            let versions = versionSeries[versionsIndex].versions;
            for (let itemIndex in versions) {
                let version = versions[itemIndex];
                let object = clazz.fromJson(version);
                objects.push(object);
            }
        }
        return objects;
    }

    renderItems(isLoading, items, renderItemFunction) {
        if (isLoading) {
            return <i className="fa fa-2x fa-spin fa-refresh"/>
        }
        if (items.length == 0) {
            return "None."
        } else {
            let reactComponents = [];
            for (let index in items) {
                let item = items[index];
                reactComponents.push(renderItemFunction(index, item));
            }
            return reactComponents;
        }
    }

    renderFunctionCatalogs() {
        return this.renderItems(this.state.isLoadingDeletedFunctionCatalogs, this.state.deletedFunctionCatalogs, function(index, item) {
            return <app.FunctionCatalog key={index} functionCatalog={item} displayVersionsList={false}/>
        });
    }

    renderFunctionBlocks() {
        return this.renderItems(this.state.isLoadingDeletedFunctionBlocks, this.state.deletedFunctionBlocks, function(index, item) {
            return <app.FunctionBlock key={index} functionBlock={item} displayVersionsList={false}/>
        });
    }

    renderMostInterfaces() {
        return this.renderItems(this.state.isLoadingDeletedMostInterfaces, this.state.deletedMostInterfaces, function(index, item) {
            return <app.MostInterface key={index} mostInterface={item} displayVersionsList={false}/>
        });
    }

    render() {
        return (
            <div id="trash-container" className="large-container">
                <div key="function-catalogs">
                    <h3>Deleted Function Catalogs</h3>
                    {this.renderFunctionCatalogs()}
                </div>
                <hr key="sep1"/>
                <div key="function-blocks">
                    <h3>Deleted Function Blocks</h3>
                    {this.renderFunctionBlocks()}
                </div>
                <hr key="sep2"/>
                <div key="interfaces">
                    <h3>Deleted Interfaces</h3>
                    {this.renderMostInterfaces()}
                </div>
            </div>
        );
    }
}

registerClassWithGlobalScope("TrashPage", TrashPage);