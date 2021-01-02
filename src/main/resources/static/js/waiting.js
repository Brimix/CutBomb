var waitingData = "";
var waitingDataNew = "";
updateView();

function getParameterByName(name) {
    var match = RegExp('[?&]' + name + '=([^&]*)').exec(window.location.search);
    return match && decodeURIComponent(match[1].replace(/\+/g, ' '));
}
function updateView(){
    loadData();
    if(waitingData.players != waitingDataNew.players){
        console.log(waitingDataNew);
        waitingData = waitingDataNew;
        processData();
    }
    setTimeout( function(){ updateView(); }, 1000);
}

function loadData(){
    var url = "api/waitingView/" + getParameterByName("gp");
    $.get(url)
        .done(function(data){
            console.log("players retrieved!");
            waitingDataNew = data;
        })
        .fail(function(data){
            console.log("couldn't retrieve players");
            console.log(data.responseJSON.error);
        });
}

function processData(){
    if(waitingData.started){
        gameurl = "../game.html?gp=" + getParameterByName("gp");
        location.href = gameurl;
        return;
    }

    var HTML = "";
    if(waitingData.host){
        if(waitingData.count > 2){
            HTML += "<center> <button "
                    + "onclick=\"startGame()\" "
                    + "class=\"btn btn-danger\" "
                    + "> Start! </button> </center>"
        }
        else{
            HTML += "<center><h3>Not enough players to start</h3></center>";
        }
    }
    else{
        HTML += "<center><h3>Waiting for game to start...</h3></center>"
    }
    document.getElementById("start-box").innerHTML = HTML;

    HTML = "";
    waitingData.players.forEach(function(player){
        HTML += "<tr>";
        HTML += "<td>" + player.id + "</td>";
        HTML += "<td>" + player.username + "</td>";
        HTML += "<td>" + player.joined + "</td>";
        if(player.host){
            HTML += "<td><center> HOST </center></td>";
        }
        else{
            HTML += "<td></td>";
        }
        HTML += "</tr>";
    });
    document.getElementById("players-table").innerHTML = HTML;
}

function startGame(){
    var url = "../api/StartGame/" + getParameterByName("gp");
    $.post(url)
        .done(function(){
            console.log("game starting...");
//            setTimeout(
//                function()
//                {
                    gameurl = "../game.html?gp=" + getParameterByName("gp");
                    location.href = gameurl;
//                }, 5000);
        })
        .fail(function(){
            console.log("can\'t start the game");
        });
}