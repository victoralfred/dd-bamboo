AJS.toInit (() => {
    const PLUGIN_BASE_URL = AJS.contextPath();
    const SAVE_SERVER_PAGE = PLUGIN_BASE_URL + "/plugins/servlet/configure";
    const OAUTH_API_ENDPOINT = PLUGIN_BASE_URL + "/rest/metrics/1.0/authorize";

    const addNewServerButton = AJS.$("#add-new-server-button");
    const oauthConnectionActionButton = AJS.$("#connect-metrics-server-oauth");
    const oauthScreenDialog = AJS.dialog2("#oauth-screen");
    const proceedOauthConnection = AJS.$("#authentication-proceed-button");
    const serverType = AJS.$("#server-type");
    const close = AJS.$("#close-dialog");
    addNewServerButton.on("click", (event) => {
        event.preventDefault();
        window.location.href = SAVE_SERVER_PAGE;
    });
    // Open the modal for proceeding/accepting to continue with the OAuth2 process
    oauthConnectionActionButton.click((e) => {
        e.preventDefault();
        // capture the server_type before opening the modal
        const serverId = AJS.$(e.currentTarget).data("server-id");
        if (serverId) {
            serverType.val(serverId);
        }
        oauthScreenDialog.show();
    })
    close.on("click", () => {
        oauthScreenDialog.hide();
    })
    proceedOauthConnection.click( (e) => {
            e.preventDefault();
            AJS.$.ajax(({
                url: OAUTH_API_ENDPOINT+ "/"+serverType.val(),
                type: "GET",
                success: (data) => {
                    if (data) {
                        window.location.href = data;
                    } else {
                        messageFlag = AJS.flag({
                            type: "error",
                            body: 'Error when submitting form',
                            close: "auto"
                        })
                    }
                },
                error: (data) => {
                    AJS.dialog2("#oauth-screen").hide();
                    if (data.status === 400) {
                        messageFlag = AJS.flag({
                            type: "error",
                            body: 'Invalid URL or parameters',
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
        });
})