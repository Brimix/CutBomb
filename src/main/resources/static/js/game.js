var gameViewData = {};
updateView();

function updateView(){
    loadData();
    setTimeout( function(){ processData(); }, 300);
    setTimeout( function(){ updateView(); }, 4000);
}

function loadData(){
    var urlGameView = "api/GameView/" + getParameterByName("gp");
    $.get(urlGameView)
        .done(function(data){
            console.log("gameplay given!");
//            console.log(gameViewData);
            gameViewData = data;
        })
        .fail(function(){
            console.log("couldn't retrieve gameplay");
//            console.log(data.error);
        });
}

function processData(){
    document.getElementById("role").innerHTML = "Your role is: " + gameViewData.role;
    document.getElementById("game-log").innerHTML = gameViewData.state;

    var HTML = "";
    HTML += "<th> Player </th>"
            + "<th colspan = " + gameViewData.numberOfCards + "> Cards </th>"
            + "<th> Spade </th>";
    document.getElementById("player-table-header").innerHTML = HTML;

    HTML = "";
    gameViewData.opponents.forEach( function(opponent){
        HTML += "<tr>" + PlayerView(opponent) + "</tr>";
    });
    HTML += "<tr>" + PlayerView(gameViewData.me) + "</tr>";
//    console.log(HTML);
    document.getElementById("player-table-game").innerHTML = HTML;
}

function getParameterByName(name) {
    var match = RegExp('[?&]' + name + '=([^&]*)').exec(window.location.search);
    return match && decodeURIComponent(match[1].replace(/\+/g, ' '));
}

function PlayerView(player){
    var HTML = "";
    HTML += "<td><center><h2>" + player.username + "</h2></center></td>";
    player.cards.forEach(function(card){
        if(canFlip(player.id, card.face)){
            HTML += "<td><button type=\"submit\""
                    + "class=\"form-control\""
                    + "form=\"flip-form\""
                    + "onclick=\"selectCard(" + card.id + ")\""
                    + ">" + card.face + "</button></td>";
        }
        else {
            HTML += "<td>" + card.face + "</td>";
        }
    });
    HTML += "<td>" + ((player.current == true) ? "PLAYS" : "") + "</td>";
    return HTML;
}
function canFlip(gpid, face){
    if(gameViewData.current == false)
        return false;
//    console.log("Current is you!");
    if(gpid == gameViewData.id)
        return false;
//    console.log("Your card?");
    if(face != "hidden")
        return false;
    return true;
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
            console.log(data.OK);
            updateView();
            if(data.OK == "Deal again"){
                setTimeout(
                    function(){
                        dealAgain();
                    }
                , 6000);
            }
        })
        .fail(function(data){
            console.log("couldn't flip card");
//            console.log(data);
            console.log(data.responseJSON.error);
        });
});

function dealAgain(){
    url = "../api/game/" + + getParameterByName("gp") + "/deal/";
    $.post(url)
        .done(function(data){
            console.log("Finished dealing cards!");
            console.log(data.OK);
            updateView();
        })
        .fail(function(data){
            console.log("Failed to deal cards");
            console.log(data.responseJSON.error);
        });
}