var urlGameView = "api/GameView/" + getParameterByName("gp");
processData(urlGameView);

function processData(urlGameView){
    $.get(urlGameView)
        .done(function(data){
            console.log("gameplay given!");
            document.getElementById("role").innerHTML = "Your role is: " + data.role;

            var HTML = "";
            data.opponents.forEach( function(opponent){
                HTML += "<tr>" + PlayerView(opponent) + "</tr>";
//                document.getElementById(opponentID).innerHTML = PlayerView(opponent);
            });
            HTML += "<tr>" + PlayerView(data.me) + "</tr>";
            document.getElementById("player-table-game").innerHTML = HTML;
        })
        .fail(function(){
            console.log("couldn't retrieve gameplay");
        });
}


function getParameterByName(name) {
    var match = RegExp('[?&]' + name + '=([^&]*)').exec(window.location.search);
    return match && decodeURIComponent(match[1].replace(/\+/g, ' '));
}

function PlayerView(data){
    var HTML = "";
    HTML += "<td><center><h2>" + data.username + "</h2></center></td>";
    data.cards.forEach(function(card){
        HTML += "<td>" + card.face + "</td>";
    });
    HTML += "<td>" + ((data.current == true) ? "PLAYS" : "") + "</td>";
    return HTML;
}