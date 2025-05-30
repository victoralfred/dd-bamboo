AJS.toInit (() => {
    const PLUGIN_BASE_URL = AJS.contextPath();
    const SAVE_SERVER_PAGE = PLUGIN_BASE_URL + "/plugins/servlet/configure";

    AJS.$("#add-new-server-button").on("click", (event) => {
        event.preventDefault();
        window.location.href = SAVE_SERVER_PAGE;
    });
})