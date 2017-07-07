class MostFunctionForm extends React.Component {
    constructor(props) {
        super(props);

        const isNewMostFunction = (! this.props.MostFunction);
        const mostFunction = MostFunction.fromJson(MostFunction.toJson(isNewMostFunction ? new MostFunction() : this.props.mostFunction));

        this.state = {
            showTitle:                  this.props.showTitle,
            shouldShowSaveAnimation:    this.props.shouldShowSaveAnimation,
            mostFunction:               mostFunction,
            buttonTitle:                (this.props.buttonTitle || "Submit"),
            defaultButtonTitle:         this.props.defaultButtonTitle
        };

        // TODO: Bind functions to this.
        this.onMostIdChanged = this.onMostIdChanged.bind(this);
        this.onNameChanged = this.onNameChanged.bind(this);
        this.onDescriptionChange = this.onDescriptionChange.bind(this);
        this.onReleaseVersionChanged = this.onReleaseVersionChanged.bind(this);
        this.onStereotypeChanged = this.onStereotypeChanged.bind(this);
        this.onReturnTypeChanged = this.onReturnTypeChanged.bind(this);

        this.onClick = this.onClick.bind(this);
        this.onSubmit = this.onSubmit.bind(this);

        this.renderFormTitle = this.renderFormTitle.bind(this);
    }

    componentWillReceiveProps(newProperties) {
        const isNewMostFunction = (! this.props.MostFunction);
        const mostFunction = MostFunction.fromJson(MostFunction.toJson(isNewMostFunction ? new MostFunction() : this.props.mostFunction));

        mostFunction.setId((newProperties.mostFunction || mostFunction).getId());
        this.setState({
            showTitle:                  newProperties.showTitle,
            shouldShowSaveAnimation:    newProperties.shouldShowSaveAnimation,
            mostFunction:              mostFunction,
            buttonTitle:                (newProperties.buttonTitle || "Submit"),
            defaultButtonTitle:         newProperties.defaultButtonTitle
        });
    }

    onMostIdChanged(newValue) {
        const mostFunction = this.state.mostFunction;
        mostFunction.setMostId(newValue);

        const defaultButtonTitle = this.state.defaultButtonTitle;
        this.setState({buttonTitle: defaultButtonTitle});

        if (typeof this.props.onUpdate == "function") {
            this.props.onUpdate();
        }
    }

    onNameChanged(newValue) {
        const mostFunction = this.state.mostFunction;
        mostFunction.setName(newValue);

        const defaultButtonTitle = this.state.defaultButtonTitle;
        this.setState({buttonTitle: defaultButtonTitle});

        if (typeof this.props.onUpdate == "function") {
            this.props.onUpdate();
        }
    }

    onDescriptionChange(newValue) {
        const mostFunction = this.state.mostFunction;
        mostFunction.setDescription(newValue);

        const defaultButtonTitle = this.state.defaultButtonTitle;
        this.setState({buttonTitle: defaultButtonTitle});

        if (typeof this.props.onUpdate == "function") {
            this.props.onUpdate();
        }
    }

    onReleaseVersionChanged(newValue) {
        const mostFunction = this.state.mostFunction;
        mostFunction.setReleaseVersion(newValue);

        const defaultButtonTitle = this.state.defaultButtonTitle;
        this.setState({buttonTitle: defaultButtonTitle});

        if (typeof this.props.onUpdate == "function") {
            this.props.onUpdate();
        }
    }

    onStereotypeChanged(newValue) {
        const mostFunction = this.state.mostFunction;
        mostFunction.setStereotype(newValue);

        const defaultButtonTitle = this.state.defaultButtonTitle;
        this.setState({buttonTitle: defaultButtonTitle});

        if (typeof this.props.onUpdate == "function") {
            this.props.onUpdate();
        }
    }

    onReturnTypeChanged(newValue) {
        const mostFunction = this.state.mostFunction;
        mostFunction.setReturnType(newValue);

        const defaultButtonTitle = this.state.defaultButtonTitle;
        this.setState({buttonTitle: defaultButtonTitle});

        if (typeof this.props.onUpdate == "function") {
            this.props.onUpdate();
        }
    }

    onClick(event) {
        event.stopPropagation();
    }

    onSubmit() {
        const createdMostFunction = this.state.mostFunction;
        if (typeof this.props.onSubmit == "function") {
            this.props.onSubmit(createdMostFunction);
        }
    }

    renderFormTitle() {
        if (! this.state.showTitle) {
            return null;
        }

        return (<div className="metadata-form-title">New Function</div>);
    }
}


registerClassWithGlobalScope("MostFunctionForm", MostFunctionForm);
