class ReleasePage extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            showLoadingReleaseItemsIcon:    true,
            showSaveAnimation:              false,
            releaseItems:                   [],
            saveButtonTitle:                'Save & Release'
        };

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

        this.onSubmit = this.onSubmit.bind(this);
        this.renderReleaseItems = this.renderReleaseItems.bind(this);
        this.renderSaveButton = this.renderSaveButton.bind(this);

        this.onNewVersionChanged = this.onNewVersionChanged.bind(this);
    }

    componentWillReceiveProps(newProps) {

    }

    onSubmit() {
        this.setState({
            shouldShowSaveAnimation: true
        });

        if (typeof this.props.onRelease == "function") {
            // TODO: provide arguments
            this.props.onRelease();
        }
    }

    onNewVersionChanged(releaseItem, value) {
        releaseItem.setNewVersion(value);
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
        return(<div className="center"><button className="button submit-button" id="most-function-submit" onClick={this.onSubmit}>{this.state.saveButtonTitle}</button></div>);
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
