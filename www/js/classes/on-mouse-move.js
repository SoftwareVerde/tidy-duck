class OnMouseMove {
    static _assignGlobalEventFunction() {
        if (this._callbacks.length > 0) {
            document.onmousemove = OnMouseMove._onMouseMove;
        }
        else {
            document.onmousemove = null;
        }
    }

    static _callbackExists(callback) {
        const callbacks = OnMouseMove._callbacks;
        for (var i=0; i<callbacks.length; i+=1) {
            const existingCallback = callbacks[i];
            if (existingCallback._callbackId == callback._callbackId) {
                return true;
            }
        }

        return false;
    }

    static addCallback(callback) {
        if (callback == null) { return; }
        if (OnMouseMove._callbackExists(callback)) { return; }

        const callbackId = OnMouseMove._nextCallbackId;
        OnMouseMove._nextCallbackId += 1;

        callback._callbackId = callbackId;
        OnMouseMove._callbacks.push(callback);

        OnMouseMove._assignGlobalEventFunction();
    }

    static removeCallback(callbackToRemove) {
        if (callbackToRemove == null) { return; }

        if (! callbackToRemove._callbackId) {
            console.log("WARNING: Cannot remove callback that has not been registered via OnMouseMove.addCallback");
            return;
        }

        const callbacks = OnMouseMove._callbacks;
        for (var i=0; i<callbacks.length; i+=1) {
            const callback = callbacks[i];
            if (callback._callbackId == callbackToRemove._callbackId) {
                callbacks.splice(i, 1);
                break;
            }
        }

        OnMouseMove._assignGlobalEventFunction();
    }
}

OnMouseMove._callbacks = [];
OnMouseMove._nextCallbackId = 1;

OnMouseMove._onMouseMove = function(event) {
    const callbacks = OnMouseMove._callbacks;
    for (var i=0; i<callbacks.length; i+=1) {
        const callback = callbacks[i];
        callback(event);
    }
};

window.OnMouseMove = OnMouseMove;
