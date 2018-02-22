class RoleToggle extends React.Component{
    constructor(props) {
        super(props);

        this.state = {
            roleItems:              (this.props.roleItems || []),
            activeRole:             this.props.activeRole
        };

        this.onRoleClicked = this.onRoleClicked.bind(this);
    }

    componentWillReceiveProps(newProperties) {
        this.setState({
            roleItems:          (newProperties.roleItems || []),
            activeRole:         newProperties.activeRole
        });
    }

    onRoleClicked(roleItem) {
        this.setState({
            activeRole: roleItem
        });

        if (typeof this.props.handleClick == "function") {
            this.props.handleClick(roleItem);
        }
    }


    render() {
        const reactElements = [];
        const activeRole = this.state.activeRole;
        const roleItems = this.state.roleItems;

        for (let i in roleItems) {
            const roleKey = "roleItem" + i;
            const isActiveRole = roleItems[i] === activeRole;

            let roleName = roleItems[i];
            var displayName = roleName;
            if (this.props.displayMappings && this.props.displayMappings[roleName]) {
                displayName = this.props.displayMappings[roleName];
            }
            reactElements.push(<app.RoleItem key={roleKey} roleName={roleName} displayName={displayName} isActiveRoleItem={isActiveRole} onClick={this.onRoleClicked}/>);
        }

        return (
            <div className="role-toggle">
                {reactElements}
            </div>
        );
    }

}


registerClassWithGlobalScope("RoleToggle", RoleToggle);