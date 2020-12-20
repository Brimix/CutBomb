var urlGameView = "api/GameView/" + getParameterByName("gp");
processData(urlGameView);

function processData(urlGameView){
    $.get(urlGameView)
        .done(function(data){
            document.getElementById("me").innerHTML = PlayerView(data.me);
            var q = 0;
            data.opponents.forEach( function(opponent){
                q = q + 1;
                var opponentID = "gp" + q;
                document.getElementById(opponentID).innerHTML = PlayerView(opponent);
            });
            console.log("gameplay given!");
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