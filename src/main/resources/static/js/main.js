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
                HTML += "<td>" + "NOT YET IMP" + "</td>";
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
            { capacity: $("#Capacity").val()})
        .done(function(){
            console.log("game created");
        })
        .fail(function(){
            console.log("game creation failed");
        });
});