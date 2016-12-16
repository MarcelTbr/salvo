/* ===================== angular_app.js ====================== */

 $(document).ready(loadData)

var app = angular.module("GamesApp", []);

/*function loadData() {
    $.get("/api/games")
    .done(function(data) {

      angularGameList(data, app);
    })
    .fail(function( jqXHR, textStatus ) {
      showOutput( "Failed: " + textStatus );
    });
  }*/

  app.controller('GamesController', ['$scope', '$http',  function($scope, $http) {

              $http.get("/api/games")
                .then(function(response){

                    $scope.games_obj = angular.fromJson(response.data);
                    $scope.games =  $scope.games_obj; //angularGameList($scope.games_obj);
                    console.log($scope.games_obj);

                })


              //["Game1", "Game2", "Game3"];


    }]);

function angularGameList(json){


        var game_date1 = json[0].game_date;
        var game_date2 = json[1].game_date;
        var game_date3 = json[2].game_date;

        var player1 = json[0].game_players[0].player_username;
        var player2 = json[0].game_players[1].player_username;
        var player3 = json[1].game_players[0].player_username;
        var player4 = json[1].game_players[1].player_username;
        var player5 = json[2].game_players[0].player_username;
        var player6 = json[2].game_players[1].player_username;

        var inline_game_1 =
             '   <li> <b>Game:</b> ' + game_date1 +
             ' | <b>Blue Squadron:</b> ' + player1 +
             ' | <b>Red Squadron:</b> ' + player2 + '</li>';

        var inline_game_2 =
                 '   <li> <b>Game:</b> ' + game_date2 +
                 ' | <b>Blue Squadron:</b> ' + player3 +
                 ' | <b>Red Squadron:</b> ' + player4 + '</li>';

        var inline_game_3 =
                       '   <li> <b>Game:</b> ' + game_date3 +
                       ' | <b>Blue Squadron:</b> ' + player5 +
                       ' | <b>Red Squadron:</b> ' + player6 + '</li>';


       var games_list = [];

       games_list.push(inline_game_1);
       games_list.push(inline_game_2);
       games_list.push(inline_game_3);

       return games_list;



};