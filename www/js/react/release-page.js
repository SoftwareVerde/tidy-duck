class ReleasePage extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            showLoadingReleaseItemsIcon:    true,
            showSaveAnimation:              false,
            releaseItems:                   [],
            saveButtonTitle:                'Save & Release'
        };

        this.populateReleaseItems = this.populateReleaseItems.bind(this);
        this.onSubmit = this.onSubmit.bind(this);
        this.renderReleaseItems = this.renderReleaseItems.bind(this);
        this.renderSaveButton = this.renderSaveButton.bind(this);

        this.onNewVersionChanged = this.onNewVersionChanged.bind(this);
        this.onCancel = this.onCancel.bind(this);

        this.populateReleaseItems();
    }

    componentWillReceiveProps(newProps) {
        // Calling setState and populate release items will reset any version numbers typed in by the user.
        /*
        this.setState({
            showLoadingReleaseItemsIcon:    true,
            showSaveAnimation:              false,
            releaseItems:                   [],
            saveButtonTitle:                'Save & Release'
        });

        this.populateReleaseItems();
        */
    }

    populateReleaseItems() {
        const thisPage = this;
        const functionCatalog = this.props.functionCatalog;
        getReleaseItemList(functionCatalog.getId(), function(data) {
            if (data.wasSuccess) {
                const releaseItemsJson = data.releaseItems;

                const releaseItems = [];
                for (let i in releaseItemsJson) {
                    const releaseItemJson = releaseItemsJson[i];
                    const releaseItem = ReleaseItem.fromJson(releaseItemJson);

                    releaseItem.setNewVersion(releaseItem.getPredictedNextVersion());

                    releaseItems.push(releaseItem);
                }

                thisPage.setState({
                    releaseItems:                   releaseItems,
                    showLoadingReleaseItemsIcon:    false
                });
            }
        });
    }

    onSubmit() {
        this.setState({
            shouldShowSaveAnimation: true
        });

        const functionCatalog = this.props.functionCatalog;
        const releaseItems = this.state.releaseItems;
        const releaseItemsJson = [];
        for (let i in releaseItems) {
            const releaseItem = releaseItems[i];
            const releaseItemJson = ReleaseItem.toJson(releaseItem);
            releaseItemsJson.push(releaseItemJson);
        }

        const thisPage = this;
        releaseFunctionCatalog(functionCatalog.getId(), releaseItemsJson, function(data) {
            if (data.wasSuccess) {
                if (typeof thisPage.props.onRelease == "function") {
                    thisPage.props.onRelease();
                }
            }
            else {
                app.App.alert("Release Function Catalog", data.errorMessage);
                thisPage.setState({
                    shouldShowSaveAnimation: false
                });
            }
        });
    }

    onNewVersionChanged(releaseItem, value) {
        releaseItem.setNewVersion(value);
    }


    onCancel(event) {
        event.preventDefault();

        if (typeof this.props.onCancel == "function") {
            this.props.onCancel();
        }
    }

    renderReleaseItems() {
        if (this.state.showLoadingReleaseItemsIcon) {
            return (
                <div className="center ">
                    <i className="fa fa-4x fa-spin fa-refresh"></i>
                </div>
            );
        }

        const releaseItems = this.state.releaseItems;
        const reactComponents = [];
        for (let i in releaseItems) {
            const releaseItem = releaseItems[i];

            const itemType = releaseItem.getItemType();
            const itemName = releaseItem.getItemName();
            const itemVersion = releaseItem.getItemVersion() + '-' + releaseItem.getItemId();
            const newVersion = releaseItem.getNewVersion();

            reactComponents.push(
                <tr key={i}>
                    <td key="type">{itemType}</td>
                    <td key="name">{itemName}</td>
                    <td key="oldv">{itemVersion}</td>
                    <td key="newv"><app.InputField label="" type="text" onChange={(value) => this.onNewVersionChanged(releaseItem, value)} value={newVersion}/></td>
                </tr>
            );
        }

        return (
            <table id="release-items-table">
                <thead>
                    <tr>
                        <th>Type</th>
                        <th>Name</th>
                        <th>Current Version</th>
                        <th>New Version</th>
                    </tr>
                </thead>
                <tbody>
                    {reactComponents}
                </tbody>
            </table>
        );
    }

    renderSaveButton() {
        if (this.state.showLoadingReleaseItemsIcon) {
            // loading, don't display save button
            return '';
        }

        if (this.state.shouldShowSaveAnimation)  {
            return(<div className="center"><div className="button submit-button" id="most-function-submit"><i className="fa fa-refresh fa-spin"></i></div></div>);
        }
        return(
            <div className="center">
                <button className="button submit-button" id="most-function-submit" onClick={this.onSubmit}>{this.state.saveButtonTitle}</button>
                <div className="cancel-button"><button className="button" onClick={this.onCancel}>Cancel</button></div>
            </div>
        );
    }

    render() {
        const functionCatalog = this.props.functionCatalog;

        return (
            <div className="release-area accent primary-bg">
                <div key="release-area-title" id="release-area-title" className="secondary-bg">Releasing: {functionCatalog.getName()}</div>
                <div key="release-area-content" id="release-area-content">
                    {this.renderReleaseItems()}
                </div>
                <div key="release-button-area" id="release-button-area">
                    {this.renderSaveButton()}
                </div>
            </div>
        );
    }
}

registerClassWithGlobalScope("ReleasePage", ReleasePage);
