showMessage("", "result-default");

$('#login-form').on('submit', function (event) {
    event.preventDefault();
    $.post("/api/login",
        { user: $("#logUser").val(),
          pass: $("#logPass").val() })
        .done(function() {
            console.log("login ok");
            showMessage("Logged in as " + $("#logUser").val(), "result-ok");
            $("#logUser").val("");
            $("#logPass").val("");
            setTimeout( function(){ location.href = "main.html"; }, 3000);
        })
        .fail(function(data) {
            console.log("login failed");
            console.log(data);
            showMessage("Login failed", "result-error");
            $("#logUser").val("");
            $("#logPass").val("");
            $("#logUser").focus();
        })
        .always(function() {
        });
});

$('#register-form').on('submit', function(event){
    event.preventDefault();
    $.post("api/register",
        { user: $("#regUser").val(),
          pass: $("#regPass").val() })
        .done(function(){
            console.log("register ok");
            showMessage("Registered as a new player!", "result-ok");
            setTimeout( function(){
                showMessage("Logging in as " + $("#regUser").val(), "result-ok");
                $.post("/api/login",
                    { user: $("#regUser").val(),
                      pass: $("#regPass").val() })
                    .done(function() {
                        console.log("login ok");
                        showMessage("Logged in as " + $("#regUser").val(), "result-ok");
                        $("#regUser").val("");
                        $("#regPass").val("");
                        setTimeout( function(){ location.href = "main.html"; }, 3000);
                    })
                    .fail(function(data) {
                        console.log("Fatal error. Contact admin.");
                        showMessage("Fatal error. Try logging from the \"login form\" or contact admin.", "result-error");
                    });
            }, 1000);

        })
        .fail(function(data){
            console.log("register fail");
            showMessage(data.responseJSON.error);
            $("#regUser").val("");
            $("#regPass").val("");
            $("#regUser").focus();
        });
});


$('#logout-form').on('submit', function (event) {
    event.preventDefault();
    $.post("/api/logout")
        .done(function () {
            console.log("logout ok");
        })
        .fail(function () {
            console.log("logout fails");
        })
        .always(function () {

        });
});

function showMessage(msg, classType){
    var HTML = "";
    HTML += "<center><h2 class=\"message-box " + classType + "\">"
            + msg
            + "</h2></center>";
    document.getElementById("logInfo").innerHTML = HTML;
    if(msg != "")
        setTimeout( function(){ showMessage("", "result-default"); }, 3000);
}
