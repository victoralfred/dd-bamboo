AJS.toInit (() => {
    let messageFlag= undefined;
    const PLUGIN_BASE_URL = AJS.contextPath();
    const API_ENDPOINT = PLUGIN_BASE_URL + "/rest/metrics/1.0/authorize";
    const SERVER_CONFIG = PLUGIN_BASE_URL+ "/rest/metrics/1.0/discover";
    const SAVE_SERVER = PLUGIN_BASE_URL + "/rest/metrics/1.0/save";
    const addServerSubmissionButton = AJS.$("#add-metrics-server");
    const serverType = AJS.$("#server-type");
    const redirectUrl = AJS.$("#redirect-url");
    const tokenEndpoint = AJS.$("#token-endpoint");
    const oauthEndpoint = AJS.$("#oauth-endpoint");
    const apiEndpoint = AJS.$("#api-endpoint");
    AJS.$("#connect-metrics-server-oauth").click((e) => {

        e.preventDefault();
        AJS.dialog2("#oauth-screen").show();
        const serverId = AJS.$(e.currentTarget).data("server-id");
        if (serverId) {
            serverType.val(serverId);
        }
    })

    const urlPattern = /^(https:\/\/)([a-zA-Z0-9-]+\.)+[a-zA-Z]{2,}(\/[a-zA-Z0-9\-._~:/?#[\]@!$&'()*+,;=%]*)*(\?client_id=[^&]+&redirect_uri=[^&]+&response_type=[^&]+.*)?$/;
    const httpsUrlPattern = /^https:\/\/([a-zA-Z0-9-]+\.)+[a-zA-Z]{2,6}(:\d+)?(\/[^\s]*)?$/;
        AJS.$("#authentication-proceed-button").click( (e) => {
            const serverType = AJS.$("#server-type");
            e.preventDefault();
            AJS.$.ajax(({
                url: API_ENDPOINT+ "/"+serverType.val().trim(),
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

    if (addServerSubmissionButton.length) {
        addServerSubmissionButton.click((event) => {
            const serverType = AJS.$("#server-type");
            const serverName = AJS.$("#server-name").val();
            const serverDescription = AJS.$("#server-description").val();
            const clientKey = AJS.$("#clientKey").val();
            const clientSecret = AJS.$("#clientSecret").val();
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
                        serverType: serverTypeSelector.val()
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
                            serverTypeSelector.val("none");
                            redirectUrl.val("");
                            tokenEndpoint.val("");
                            oauthEndpoint.val("");
                            apiEndpoint.val("");
                            // Reset the fields to empty and make them write-able
                            redirectUrl.prop("readonly", false);
                            tokenEndpoint.prop("readonly", false);
                            oauthEndpoint.prop("readonly", false);
                            apiEndpoint.prop("readonly", false);
                            serverTypeSelector.change();
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
  AJS.$("#test-oauth").click((event) => {
        event.preventDefault();
      const serverId = AJS.$("#test-oauth").data("server-id");
      if (serverId) {
          const API_TEST = PLUGIN_BASE_URL + "/rest/metrics/1.0/kpi/" + serverId.trim();
          AJS.$.ajax({
              url: API_TEST,
              type: "GET",
              success: (data) => {
                  if (data) {
                      messageFlag = AJS.flag({
                          type: "success",
                          body: 'API is working',
                          close: "auto"
                      })
                        AJS.$("table#metrics-server-status-table").load(window.location.href + " table#metrics-server-status-table")
                  } else {
                      messageFlag = AJS.flag({
                          type: "error",
                          body: 'Error when testing API',
                          close: "auto"
                      })
                  }
              },
              error: (data) => {
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
                          body: 'Error when testing API',
                          close: "auto"
                      })
                  }
              }
          })

      }else{
          messageFlag = AJS.flag({
              type: "error",
              body: 'Failed to retrieve token',
              close: "auto"
          })
      }

    })
})

const validateFields = (serverName, serverTypeSelector,
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
    if (serverTypeSelector.length .length<4 && serverTypeSelector ==="") {
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
