AJS.toInit (() => {
    let messageFlag= undefined;
    const API_ENDPOINT = AJS.contextPath() + "/rest/metrics/1.0/authorize";
    const addServerSubmissionButton = AJS.$("#add-metrics-server");
    if (addServerSubmissionButton.length) {
        addServerSubmissionButton.click((event) => {
            const serverName = AJS.$("#server-name").val();
            const serverDescription = AJS.$("#server-description").val();
            const serverUrl = AJS.$("#server-url").val();
            event.preventDefault();
            if (validateFields(serverName.trim(),serverDescription.trim(), serverUrl.trim()) ){
                AJS.$.ajax(({
                    url:API_ENDPOINT,
                    type:"POST",
                    contentType:"application/json",
                    data: JSON.stringify({
                        serverName: serverName,
                        description: serverDescription,
                        serverUrl: serverUrl
                    }),
                    success: (data) => {
                        if(data.success){
                            messageFlag = AJS.flag({
                                type: "success",
                                body: 'Server added successfully',
                                close: "auto"
                            })
                            AJS.$("#server-name").val("");
                            AJS.$("#server-description").val("");
                            AJS.$("#server-url").val("");
                        }
                        window.location.assign(data)
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
const validateFields = (serverName, serverDescription, serverUrl) => {
    if (serverName.length<4 && serverName==="") {
        messageFlag = AJS.flag({
            type: "error",
            body: 'Server name is required',
            close: "auto"
        })
        return false;
    }
    if (serverDescription.length .length<4 && serverDescription ==="") {
        messageFlag = AJS.flag({
            type: "error",
            body: 'Server description is required',
            close: "auto"
        })
        return false;
    }
    // Validate URL - check if it's a valid URL with OAuth parameters
    const urlPattern = /^(https:\/\/)([a-zA-Z0-9-]+\.)+[a-zA-Z]{2,}(\/[a-zA-Z0-9\-._~:/?#[\]@!$&'()*+,;=%]*)*(\?client_id=[^&]+&redirect_uri=[^&]+&response_type=[^&]+.*)?$/;
    const httpsUrlPattern = /^https:\/\/([a-zA-Z0-9-]+\.)+[a-zA-Z]{2,6}(:\d+)?(\/[^\s]*)?$/;

    if (!urlPattern.test(serverUrl) || !httpsUrlPattern.test(serverUrl)) {
        messageFlag = AJS.flag({
            type: "error",
            body: 'Server URL is required',
            close: "auto"
        })
        return false;
    }
    return true;
}
