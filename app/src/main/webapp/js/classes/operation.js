class Operation {
    static fromJson(json) {
        const operation = new Operation();

        operation.setId(json.id);
        operation.setName(json.name);
        operation.setChannel(json.channel);

        return operation;
    }

    static toJson(operation) {
        return {
            id:         operation.getId(),
            name:       operation.getName(),
            channel:    operation.getChannel()
        };
    }

    constructor() {
        this._id        = null;
        this._name      = null;
        this._channel   = "Control";
    }

    setId(id) {
        this._id = id;
    }

    getId() {
        return this._id;
    }

    setName(name) {
        this._name = name;
    }

    getName() {
        return this._name;
    }

    setChannel(channel) {
        this._channel = channel;
    }

    getChannel() {
        return this._channel;
    }
}