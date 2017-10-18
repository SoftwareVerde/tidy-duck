class Alert extends React.Component {
    constructor(props) {
        super(props);

        const width = (this.props.width || 400);

        this.state = {
            shouldShow: (this.props.shouldShow || false),
            title:      (this.props.title || ""),
            content:    (this.props.content || ""),
            onConfirm:  (this.props.onConfirm || null),
            width:      width,
            x:          (this.props.x || ((window.innerWidth - width) / 2) ),
            y:          (this.props.y || ((window.innerHeight - width) / 2) ),
            isMoving:   false,
            lastMoveTime: 0,
            hasEverMoved: false
        };

        this.onMouseDown = this.onMouseDown.bind(this);
        this.onMouseMove = this.onMouseMove.bind(this);
        this.onMouseUp = this.onMouseUp.bind(this);
        this.onMouseOut = this.onMouseOut.bind(this);
        this.onButtonClick = this.onButtonClick.bind(this);
    }

    onMouseDown(event) {
        event.preventDefault();

        this.setState({
            isMoving:   true
        });

        return true;
    }

    onMouseMove(event) {
        if (! this.state.isMoving) { return; }
        this.state.lastMoveTime = (new Date()).getTime();

        const width = this.refs.alertContainer.offsetWidth;
        const height = this.refs.alertContainer.offsetHeight;

        this.setState({
            x:  event.clientX - (width / 2),
            y:  event.clientY - (25),
            hasEverMoved: true
        });
    }

    onMouseUp(event) {
        event.preventDefault();

        this.setState({
            isMoving:   false
        });

        return true;
    }

    onMouseOut(event) {
        event.preventDefault();

        if (this.state.isMoving) {
            const eventTime = (new Date()).getTime();
            event.persist();
            const _this = this;
            setTimeout(function() {
                if (_this.state.lastMoveTime >= eventTime) { return; }

                if (_this.state.isMoving) {
                    _this.onMouseMove(event);
                }
            }, 10);
        }

        /*
            this.setState({
                isMoving:   false
            });
        */

        return true;
    }

    onButtonClick(isConfirmClicked) {
        if (isConfirmClicked) {
            if (typeof this.props.onConfirm == "function") {
                this.props.onConfirm();
            }
        }
        else if (typeof this.props.onCancel == "function") {
            this.props.onCancel();
        }

        this.setState({
            shouldShow: false
        });
    }

    componentWillReceiveProps(newProperties) {
        const width = (newProperties.width || 400);

        this.setState({
            shouldShow: (newProperties.shouldShow || false),
            title:      (newProperties.title || ""),
            content:    (newProperties.content || ""),
            onConfirm:  (newProperties.onConfirm || null),
            width:      width,
            x:          (newProperties.x || ((window.innerWidth - width) / 2) ),
            y:          (newProperties.y || ((window.innerHeight - width) / 2) )
        });
    }

    render() {
        const divStyle = {
            "top":          this.state.y,
            "left":         this.state.x,
            "width":        this.state.width,
            "visibility":   this.state.shouldShow ? 'visible' : 'hidden',
            "opacity":      this.state.shouldShow ? 1 : 0
        };

        const hasMoved = (this.state.lastMoveTime > 0);

        let cancelButton = "";
        let confirmButtonText = "Got it!";
        const isConfirmAlert = this.props.isConfirmAlert;
        if (isConfirmAlert) {
            confirmButtonText = "OK";
            cancelButton = <div className="alert-button" onClick={() => this.onButtonClick(false)}>Cancel</div>
        }

        if (this.state.isMoving) {
            OnMouseMove.addCallback(this.onMouseMove);
        }
        else if (hasMoved) {
            OnMouseMove.removeCallback(this.onMouseMove);
        }

        return (
            <div ref="alertContainer" className="alert" onMouseOut={this.onMouseOut} style={divStyle} >
                <div className="alert-title" onMouseUp={this.onMouseUp} onMouseDown={this.onMouseDown}>{this.state.title}</div>
                <div className="alert-content">{this.state.content}</div>
                <div className="alert-button" onClick={() => this.onButtonClick(true)}>{confirmButtonText}</div>
                {cancelButton}
            </div>
        );
    }
}

registerClassWithGlobalScope("Alert", Alert);
