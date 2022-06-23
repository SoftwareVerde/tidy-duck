// calls callbackFunction with the setting value
function getApplicationSettingValue(settingName, callbackFunction) {
    const request = new Request(
        API_PREFIX + "application-settings/" + settingName,
        {
            method: "GET",
            credentials: "include"
        }
    );

    tidyFetch(request, function(data) {
        let settingValue = null;

        if (data.wasSuccess) {
            settingValue = data.settingValue;
        } else {
            console.error("Unable to get setting value: " + data.errorMessage);
        }

        if (typeof callbackFunction == "function") {
            callbackFunction(settingValue);
        }
    });
}

// calls callbackFunction with wasSuccess
function setApplicationSettingValue(settingName, settingValue, callbackFunction) {
    const request = new Request(
        API_PREFIX + "application-settings/" + settingName,
        {
            method: "POST",
            credentials: "include",
            body: JSON.stringify({
                settingValue: settingValue
            })
        }
    );

    tidyFetch(request, function(data) {
        if (typeof callbackFunction == "function") {
            callbackFunction(data.wasSuccess);
        }
    });
}