
AJS.toInit (() => {
    let messageFlag= undefined;
    const PLUGIN_BASE_URL = AJS.contextPath();
    const SERVER_CONFIG = PLUGIN_BASE_URL+ "/rest/metrics/1.0/discover";
    const SAVE_SERVER = PLUGIN_BASE_URL + "/rest/metrics/1.0/save";
    const TEST_API_KEY_ENDPOINT = PLUGIN_BASE_URL + "/rest/metrics/1.0/test/api/validate";
    const addServerSubmissionButton = AJS.$("#add-metrics-server");
    const serverType = AJS.$("#server-type");
    const redirectUrl = AJS.$("#redirect-url");
    const tokenEndpoint = AJS.$("#token-endpoint");
    const oauthEndpoint = AJS.$("#oauth-endpoint");
    const apiEndpoint = AJS.$("#api-endpoint");
    const infoPanel = AJS.$(".aui-message-info");
    const clientKey = AJS.$("#clientKey")
    const clientSecret = AJS.$("#clientSecret")
    const clientIdDescription = AJS.$("#api-key-description,#api-key-description");
    const clientSecreteDescription = AJS.$("#client-secret-description,#client-secret-description");
    const urlPattern = /^(https:\/\/)([a-zA-Z0-9-]+\.)+[a-zA-Z]{2,}(\/[a-zA-Z0-9\-._~:/?#[\]@!$&'()*+,;=%]*)*(\?client_id=[^&]+&redirect_uri=[^&]+&response_type=[^&]+.*)?$/;
    const httpsUrlPattern = /^https:\/\/([a-zA-Z0-9-]+\.)+[a-zA-Z]{2,6}(:\d+)?(\/[^\s]*)?$/;
    const serverConfigurationForn = AJS.$("#metrics-server-config");
    const testApikeyButton = AJS.$("#test-api-key");
    // Hide the add server button initially
    addServerSubmissionButton.hide();
    testApikeyButton.hide();
    // Hide the info panel initially
    infoPanel.hide();
    serverConfigurationForn.change((event) => {
        event.preventDefault();
        const toggleButton = AJS.$("#use-api-key-pass-or-oauth");
        let checked  =  (toggleButton[0].checked);
        if(checked){
            infoPanel.show();
            AJS.$('#server-type-text').html('You API key and secret will be used to authenticate with the server.')
            // Show the add server button when the key type is selected
            testApikeyButton.show();
            addServerSubmissionButton.hide();

        }else{
            infoPanel.show();
            AJS.$("#server-type-text").html('After saving the server, you will need to authorize it by clicking the "Authorize" button.')
            // Show the add server button when the key type is selected
            addServerSubmissionButton.show();
            testApikeyButton.hide();

        }
        clientIdDescription.html(checked ? 'API Key':'Client ID' );
        clientSecreteDescription.html(checked ?  'App Key': 'Client Secret' );
    })
    serverType.change((event) => {
        const selected = serverType.val().trim()
        if(selected==="none" || selected==="") {
            redirectUrl.val("");
            tokenEndpoint.val("");
            oauthEndpoint.val("");
            apiEndpoint.val("");
            redirectUrl.prop("readonly", false);
            tokenEndpoint.prop("readonly", false);
            oauthEndpoint.prop("readonly", false);
            apiEndpoint.prop("readonly", false);
        }else{
            serverType.val(selected);
            AJS.$.ajax(({
                url: SERVER_CONFIG,
                type: "POST",
                contentType: "application/json",
                dataType: "json",
                data: JSON.stringify({
                    serverType: selected
                }),
                success: (data) => {
                    if (data) {
                        redirectUrl.val(data.pluginRedirectUrl);
                        tokenEndpoint.val(data.tokenEndpoint);
                        oauthEndpoint.val(data.authorizationEndpoint);
                        apiEndpoint.val(data.apiEndpoint);
                        // Make the fields read-only
                        redirectUrl.prop("readonly", true);
                        tokenEndpoint.prop("readonly", true);
                        oauthEndpoint.prop("readonly", true);
                        apiEndpoint.prop("readonly", true);
                    } else {
                        // make the fields empty and write-able
                        redirectUrl.prop("readonly", false);
                        tokenEndpoint.prop("readonly", false);
                        oauthEndpoint.prop("readonly", false);
                        apiEndpoint.prop("readonly", false);
                    }
                }
            }))
        }
    });
    testApikeyButton.click((e) => {
        e.preventDefault();
        AJS.$.ajax(({
            url: TEST_API_KEY_ENDPOINT,
            method: "POST",
            contentType: "application/json",
            dataType: "json",
            data: JSON.stringify({
                apiKey: clientKey.val(),appKey:clientSecret.val(), endpoint:apiEndpoint.val()
            }),
            success: (data) => {
                messageFlag = AJS.flag({
                    type: "success",
                    body: 'API Key is valid',
                    close: "auto"
                })
                const {valid} = data
                if(valid){
                    testApikeyButton.hide();
                    addServerSubmissionButton.show();
                }
            },
            error: (data) => {
                messageFlag = AJS.flag({
                    type: "error",
                    body: 'API Key is invalid',
                    close: "auto"
                })
            }
        }))
    })
    if (addServerSubmissionButton.length) {
        addServerSubmissionButton.click((event) => {
            const serverType = AJS.$("#server-type");
            const serverName = AJS.$("#server-name").val();
            const serverDescription = AJS.$("#server-description").val();
            const clientKey = AJS.$("#clientKey").val();
            const clientSecret = AJS.$("#clientSecret").val();
            const toggleButton = AJS.$("#use-api-key-pass-or-oauth");
            let checked  =  (toggleButton[0].checked);
            event.preventDefault();
            const validated = validateFields(serverName.trim(),
                serverType.val().trim(), clientKey, clientKey);
            if ( validated){
                AJS.$.ajax(({
                    url: SAVE_SERVER,
                    type:"POST",
                    contentType:"application/json",
                    data: JSON.stringify({
                        serverName: serverName,
                        description: serverDescription,
                        clientKey: clientKey,
                        clientSecret: clientSecret,
                        redirectUrl: redirectUrl.val(),
                        tokenEndpoint: tokenEndpoint.val(),
                        oauthEndpoint: oauthEndpoint.val(),
                        apiEndpoint: apiEndpoint.val(),
                        serverType: serverType.val(),
                        oauthOrApiKey: checked ? "API_KEY" : "OAUTH"
                    }),
                    success: (data) => {
                        if(data){
                            messageFlag = AJS.flag({
                                type: "success",
                                body: data,
                                close: "auto"
                            })
                            AJS.$("#server-name").val("");
                            AJS.$("#server-description").val("");
                            AJS.$("#server-url").val("");
                            AJS.$("#clientKey").val("");
                            AJS.$("#clientSecret").val("");
                            serverType.val("none");
                            redirectUrl.val("");
                            tokenEndpoint.val("");
                            oauthEndpoint.val("");
                            apiEndpoint.val("");
                            // Reset the fields to empty and make them write-able
                            redirectUrl.prop("readonly", false);
                            tokenEndpoint.prop("readonly", false);
                            oauthEndpoint.prop("readonly", false);
                            apiEndpoint.prop("readonly", false);
                            serverType.change();
                            // Redirect to the server list page
                            window.location.href = PLUGIN_BASE_URL + "/plugins/servlet/metrics";
                        }

                    },
                    error: (data) => {
                        if (data.status === 400) {
                            messageFlag = AJS.flag({
                                type: "error",
                                body: 'Server already exists',
                                close: "auto"
                            })
                        } else if (data.status === 500) {
                            messageFlag = AJS.flag({
                                type: "error",
                                body: 'Internal server error',
                                close: "auto"
                            })
                        }else {
                            messageFlag = AJS.flag({
                                type: "error",
                                body: 'Error when submitting form',
                                close: "auto"
                            })
                        }
                    }
                }))
            } else {
                messageFlag = AJS.flag({
                    type: "error",
                    body: 'Error when submitting form',
                    close: "auto"
                })
            }
        });
    }
})

const validateFields = (serverName, serverType,
                        clientSecret, clientKey ) => {
    let messageFlag;
    if (serverName.length<4 && serverName==="") {
        messageFlag = AJS.flag({
            type: "error",
            body: 'Server name is required',
            close: "auto"
        })
        return false;
    }
    if (serverType.length .length<4 && serverType ==="") {
        messageFlag = AJS.flag({
            type: "error",
            body: 'Server description is required',
            close: "auto"
        })
        return false;
    }
    if (clientSecret.length < 4 && clientSecret === "") {
        messageFlag = AJS.flag({
            type: "error",
            body: 'Client Secrete is required is required',
            close: "auto"
        })
        return false;
    }

    if (clientKey.length < 4 && clientKey === "") {
        messageFlag = AJS.flag({
            type: "error",
            body: 'Client Key is required',
            close: "auto"
        })
        return false;
    }
    return true;

}