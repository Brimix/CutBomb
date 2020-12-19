var urlGameView = "api/GameView/" + getParameterByName("gp");
processData(urlGameView);

function processData(urlGameView){
    $.get(urlGameView)
        .done(function(data){
            document.getElementById("me").innerHTML = OwnView(data);
            var q = 0;
            data.opponents.forEach( function(opponent){
                q = q + 1;
                var opponentID = "gp" + q;
                document.getElementById(opponentID).innerHTML = OpponentView(opponent);
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

function OwnView(data){
    var HTML = "";
    HTML += "<tr><center><h2>" + data.username + "</h2></center></tr> <tr>";
    data.cards.forEach(function(card){
        HTML += "<td>" + card + "</td>";
    });
    HTML += "</tr>";
    return HTML;
}

function OpponentView(data){
    var HTML = "";
    HTML += "<tr><center><h2>" + data.username + "</h2></center></tr> <tr>";
    data.cards.forEach(function(card){
        HTML += "<td>" + card + "</td>";
    });
    HTML += "</tr>";
    return HTML;
}