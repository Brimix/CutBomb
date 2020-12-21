getPlayerList();

function getParameterByName(name) {
    var match = RegExp('[?&]' + name + '=([^&]*)').exec(window.location.search);
    return match && decodeURIComponent(match[1].replace(/\+/g, ' '));
}

function getPlayerList(){
    var url = "../api/PlayersInGame/" + getParameterByName("gp");
    $.get(url)
        .done(function(data){
            var HTML = "";
            data.forEach(function(player){
                HTML += "<tr>";
                HTML += "<td>" + player.id + "</td>";
                HTML += "<td>" + player.username + "</td>";
                HTML += "<td>" + player.joined + "</td>";
                if(player.host){
                    HTML += "<td> HOST </td>";
                }
//                HTML += "<td><button type=\"submit\""
//                        + "class=\"form-control join-button\""
//                        + "form=\"join-form\""
//                        + "onclick=\"selectGame( " + game.id + ")\""
//                        + ">Join</button></td>";
                HTML += "</tr>";
            });
            document.getElementById("players-table").innerHTML = HTML;
            console.log("players retrieved!");
        })
        .fail(function(){
            console.log("couldn't retrieve players");
        });
}

function startGame(){
}