$('#login-form').on('submit', function (event) {
    event.preventDefault();
    $.post("/api/login",
        { user: $("#Username").val(),
          pass: $("#Password").val() })
        .done(function() {
            console.log("login ok");
//            $('#loginSuccess').show( "slow" ).delay(2000).hide( "slow" );
            $("#Username").val("");
            $("#Password").val("");
//            updateJson();
//            $("#createGameForm").show();
//            $("#registerForm").hide();

        })
        .fail(function() {
            console.log("login failed");
//            $('#loginFailed').show( "slow" ).delay(2000).hide( "slow" );
            $("#Username").val("");
            $("#Password").val("");
            $("#Username").focus();
            // $('#loginFailed').hide( "slow" );
        })
        .always(function() {
        });
});

$('#logout-form').on('submit', function (event) {
    event.preventDefault();
    $.post("/api/logout")
        .done(function () {
            console.log("logout ok");
//            $('#logoutSuccess').show("slow").delay(2000).hide("slow");
//            updateJson();
        })
        .fail(function () {
            console.log("logout fails");
        })
        .always(function () {

        });
});