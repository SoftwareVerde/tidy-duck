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
        };

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
        this.onItemSelected = this.onItemSelected.bind(this);
        this.onRestoreFunctionCatalogFromTrash = this.onRestoreFunctionCatalogFromTrash.bind(this);
        this.onRestoreFunctionBlockFromTrash = this.onRestoreFunctionBlockFromTrash.bind(this);
        this.onRestoreMostInterfaceFromTrash = this.onRestoreMostInterfaceFromTrash.bind(this);
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

    onItemSelected(item) {
        if (typeof this.props.onItemSelected == "function") {
            this.props.onItemSelected(item);
        }
    }

    onRestoreFunctionCatalogFromTrash(functionCatalog) {
        const thisPage = this;
        this.props.onRestoreFunctionCatalog(functionCatalog, function(confirmedRestore) {
            if (confirmedRestore) {
                const functionCatalogId = functionCatalog.getId();
                const deletedFunctionCatalogs = thisPage.state.deletedFunctionCatalogs.filter(function(item) {
                    return item.getId() != functionCatalogId;
                });

                thisPage.setState({
                    deletedFunctionCatalogs: deletedFunctionCatalogs
                });
            }
        });
    }

    onRestoreFunctionBlockFromTrash(functionBlock) {
        const thisPage = this;
        this.props.onRestoreFunctionBlock(functionBlock, function(confirmedRestore) {
            if (confirmedRestore) {
                const functionBlockId = functionBlock.getId();
                const deletedFunctionBlocks = thisPage.state.deletedFunctionBlocks.filter(function(item) {
                    return item.getId() != functionBlockId;
                });

                thisPage.setState({
                    deletedFunctionBlocks: deletedFunctionBlocks
                });
            }
        });
    }

    onRestoreMostInterfaceFromTrash(mostInterface) {
        const thisPage = this;
        this.props.onRestoreMostInterface(mostInterface, function(confirmedRestore) {
            if (confirmedRestore) {
                const mostInterfaceId = mostInterface.getId();
                const deletedMostInterfaces = thisPage.state.deletedMostInterfaces.filter(function(item) {
                    return item.getId() != mostInterfaceId;
                });

                thisPage.setState({
                    deletedMostInterfaces: deletedMostInterfaces
                });
            }
        });
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
                let reactComponent = renderItemFunction(index, item);
                reactComponents.push(reactComponent);
            }
            return reactComponents;
        }
    }

    renderFunctionCatalogs() {
        let thisPage = this;
        return this.renderItems(this.state.isLoadingDeletedFunctionCatalogs, this.state.deletedFunctionCatalogs, function(index, item) {
            return <app.FunctionCatalog key={index} functionCatalog={item} displayVersionsList={false} onClick={thisPage.onItemSelected} showDeletedVersions={true} onRestoreFromTrash={() => thisPage.onRestoreFunctionCatalogFromTrash(item)}/>
        });
    }

    renderFunctionBlocks() {
        let thisPage = this;
        return this.renderItems(this.state.isLoadingDeletedFunctionBlocks, this.state.deletedFunctionBlocks, function(index, item) {
            return <app.FunctionBlock key={index} functionBlock={item} displayVersionsList={false} onClick={thisPage.onItemSelected} showDeletedVersions={true} onRestoreFromTrash={() => thisPage.onRestoreFunctionBlockFromTrash(item)}/>
        });
    }

    renderMostInterfaces() {
        let thisPage = this;
        return this.renderItems(this.state.isLoadingDeletedMostInterfaces, this.state.deletedMostInterfaces, function(index, item) {
            return <app.MostInterface key={index} mostInterface={item} displayVersionsList={false} onClick={thisPage.onItemSelected} showDeletedVersions={true} onRestoreFromTrash={() => thisPage.onRestoreMostInterfaceFromTrash(item)}/>
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