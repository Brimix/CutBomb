var gameViewData = "";
var gameViewDataNew = "";
updateView();

function updateView(){
    loadData();
    if(gameViewData.state != gameViewDataNew.state){
        console.log(gameViewDataNew);
        gameViewData = gameViewDataNew;
        processData();
    }
//    setTimeout( function(){ processData(); }, 300);
    setTimeout( function(){ updateView(); }, 1000);
}

function loadData(){
    var urlGameView = "api/GameView/" + getParameterByName("gp");
    $.get(urlGameView)
        .done(function(data){
            console.log("gameplay given!");
//            console.log(gameViewData);
            gameViewDataNew = data;
        })
        .fail(function(data){
            console.log("couldn't retrieve gameplay");
//            console.log(data.error);
        });
}

function processData(){
    document.getElementById("role").classList.add((gameViewData.role == "Savior") ? "role-good" : "role-bad");
    document.getElementById("role").innerHTML = "<h2>Your role is: " + gameViewData.role + "</h2>";
    document.getElementById("game-log").innerHTML = "<h2>" + gameViewData.state + "</h2>";

    var HTML = "";
    HTML += "<th width=\"30%\" class=\"left-row\"> Player </th>"
            + "<th width=\"60%\" class=\"right-row\" colspan = " + gameViewData.numberOfCards + "><center> Cards </center></th>";
    document.getElementById("player-table-header").innerHTML = HTML;

    HTML = "";
    gameViewData.opponents.forEach( function(opponent){
        HTML += "<tr>" + PlayerView(opponent) + "</tr>";
    });
    HTML += "<tr>" + PlayerView(gameViewData.me) + "</tr>";
    document.getElementById("player-table-game").innerHTML = HTML;
//    document.getElementById("blanks").innerHTML = gameViewData.blanks;
//    document.getElementById("wires").innerHTML = gameViewData.wires;
//    document.getElementById("bombs").innerHTML = gameViewData.bombs;
    document.getElementById("blanks2").innerHTML = createCardBox("blank", gameViewData.blanks);
    document.getElementById("wires2").innerHTML = createCardBox("wire", gameViewData.wires);
    document.getElementById("bombs2").innerHTML = createCardBox("bomb", gameViewData.bombs);
}

function getParameterByName(name) {
    var match = RegExp('[?&]' + name + '=([^&]*)').exec(window.location.search);
    return match && decodeURIComponent(match[1].replace(/\+/g, ' '));
}

function PlayerView(player){
    var HTML = "";
    HTML += "<td class=\"left-row\"><h3>" + player.username + "</h2></td>";
    var count = 0;
    player.cards.forEach(function(card){
        count++;
        HTML += "<td class=\"cell";
        if(count == gameViewData.numberOfCards){
            HTML += " right-row";
        }
        HTML += "\">";
        if(canFlip(player.id, card.face)) HTML += clickableButton(card.id, card.face);
        else HTML += notClickableButton(card.face);
        HTML += "</td>";
    });
    if(player.current == true){
        HTML += "<td class=\"cell-spade\"><img class=\"card\" src=\"img/pliers.png\" alt=\"pliers.png\"></td>";
    }
    return HTML;
}
function canFlip(gpid, face){
    if(gameViewData.current == false)
        return false;
    if(gpid == gameViewData.id)
        return false;
    if(face != "hidden")
        return false;
    return true;
}
function clickableButton(id, face){
    return  "<input type=\"image\" "
            + "class=\"card\" "
            + "src=\"img/" + face + ".png\" "
            + "alt=\"" + face + ".png\" "
            + "form=\"flip-form\" "
            + "onclick=\"selectCard(" + id + ")\">";
}

function notClickableButton(face){
    return "<img class=\"card\" src=\"img/" + face + ".png\" alt=\"" + face + ".png\">";
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

function createCardBox(face, cnt){
    var HTML = "";
    HTML += "<img class=\"card-box-img\" "
            + "src=\"img/" + face + ".png\" "
            + "alt=\"" + face + ".png\">";
    HTML += "<div class=\"card-box-num\">" + cnt + "</div>";
    return HTML;
}