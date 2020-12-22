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
//        HTML += "<td>" + card.face + "</td>";
        HTML += "<td><button type=\"submit\""
                + "class=\"form-control\""
                + "form=\"flip-form\""
                + "onclick=\"selectCard(" + card.id + ")\""
                + ">" + card.face + "</button></td>";
    });
    HTML += "<td>" + ((data.current == true) ? "PLAYS" : "") + "</td>";
    return HTML;
}

var cardSelected = -1;
function selectCard(cardid){
    cardSelected = cardid;
}

$('#flip-form').on('submit', function (event){
    console.log("submitted!");
    url = "../api/game/" + getParameterByName("gp") + "/card/" + cardSelected;
    $.post(url)
        .done(function(data){
            console.log("card flipped!");
        })
        .fail(function(data){
            console.log("couldn't flip card");
        });
});