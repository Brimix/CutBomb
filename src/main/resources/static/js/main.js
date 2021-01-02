var gameSelected = -1;
function selectGame(gameid){
    gameSelected = gameid;
}

$(function() {
    $('.join-button').click(function () {
        gameSelected = $(this).attr('name')
    });
});

getGameList();
function getGameList(){
    $.get("api/games")
        .done(function(data){
            var HTML = "";
            data.forEach(function(game){
                HTML += "<tr>";
                HTML += "<td>" + game.id + "</td>";
                HTML += "<td>" + game.created + "</td>";
                HTML += "<td>" + game.occupancy + "/" + game.capacity + "</td>";
                HTML += "<td>" + action(game) + "</td>";
                HTML += "</tr>";
            });
            document.getElementById("game-table").innerHTML = HTML;
            console.log("games retrieved!");
        })
        .fail(function(){
            console.log("couldn't retrieve games");
        });
}

$('#create-form').on('submit', function (event) {
    $.post("/api/CreateGame",
            { capacity: $("#capacity").val()})
        .done(function(data){
            console.log("game created");
            url = "waiting'-'room.html?gp=" + data.gpid;
            location.href = url;
        })
        .fail(function(data){
            console.log("game creation failed");
            console.log(data);
            console.log(data.responseJSON.error);
        });
});

$('#join-form').on('submit', function (event){
    console.log("submitted!");
    url = "api/JoinGame/" + gameSelected;
    console.log("accessing: " + url);
    $.post(url)
        .done(function(data){
            console.log("game joined");
            url = "waiting-room.html/?gp=" + data.gpid;
            location.href = url;
        })
        .fail(function(data){
            console.log("game joining failed");
            console.log(data);
            console.log(data.responseJSON.error);
        })
});

function action(game){
    if((game.state == "waiting") && (game.occupancy < game.capacity))
        return "<button type=\"submit\""
               + "class=\"form-control join-button btn btn-warning\""
               + "form=\"join-form\""
               + "onclick=\"selectGame( " + game.id + ")\""
               + ">Join</button>";
    if(game.alreadyIn)
        return "<button onclick=\"location.href='" + getGamePlayURL(game) + "'\""
               + "class=\"form-control btn btn-info\""
               + ">Enter</button>";
    return "---";
}

function getGamePlayURL(game){
    var url = "/api/game/" + game.id + "/gamePlay";
    $.get(url)
        .done(function(data){
            console.log("gamePlay retrieved");
            var gpurl = "waiting-room.html?gp=";
            gpurl += data.gpid;
            return gpurl;
        })
        .fail(function(data){
            console.log("gamePlay retrieving failed");
        })
        .always(function(data){
           console.log(data);
        });
}