/* ===================== angular_app.js ====================== */

//function to return an object from a query string i.e. location.search
function paramObj(search) {
  var obj = {};
  var reg = /(?:[?&]([^?&#=]+)(?:=([^&#]*))?)(?:#.*)?/g;

  search.replace(reg, function(match, param, val) {
    obj[decodeURIComponent(param)] = val === undefined ? "" : decodeURIComponent(val);
  });

  return obj;
}


//Angular Code
var app = angular.module('App', ['PlayerViewModule'], function($locationProvider){
    $locationProvider.html5Mode(true);
});

 var pl_view_mod = angular.module('PlayerViewModule', [])
  pl_view_mod.controller('PlayerViewController', ['$scope', '$http', '$location','updateGameView',
  function($scope, $http, $location, updateGameView){

    $scope.nums = [1,2,3,4,5,6,7,8,9,10];
    $scope.abc = ["A", "B", "C", "D", "E", "F", "G", "H", "I", "J"];

     $scope.url = $location
     $scope.abs_url = $location.absUrl();
     console.log("Url:");
     console.log($scope.url);
     console.log("Abs Url:")
     console.log($scope.abs_url);

     $scope.url_obj = paramObj($scope.abs_url);
     console.log($scope.url_obj);
     $scope.gp = $scope.url_obj["gp"]; //store the url_obj's value for the key "gp"
     console.log($scope.gp); // that is the gamePlayer's id

     $scope.gv_title = { 'text-align':'center'}; //styling game view's title

    $http.get("api/game_view/" + $scope.gp)
    .then(function(response){

        $scope.game_view_obj = response.data;  //store the game_viewDTO into a variable
        console.log($scope.game_view_obj);
        $scope.ships = $scope.game_view_obj.ships;
        $scope.player1 = $scope.game_view_obj.gamePlayers[0].player
        $scope.players = $scope.game_view_obj.gamePlayers
        $scope.all_ship_locations = updateGameView.getShipLocations($scope.ships);
        console.log($scope.all_ship_locations);
        //Painting the Ships
        $scope.getStyle = function(cell_data){
                                //apply the style to the ship if it is in the all_ship_locations array
                                for(var i = 0; i < $scope.all_ship_locations.length; i++){
                                if(cell_data === $scope.all_ship_locations[i]){ return {'background-color': 'green'}}
                                }
                                //later is going to apply another style for 'salvo-hits' or 'sunken-ships'.
                     }
        /* Getting User and Enemy Information*/
        $scope.userGamePlayer = updateGameView.getUserPlayer($scope.gp, $scope.players);
        $scope.enemyGamePlayer = updateGameView.getEnemyPlayer($scope.gp, $scope.players);
        console.log($scope.userGamePlayer);
        console.log("Enemy Game Player");
        console.log($scope.enemyGamePlayer);
//        TODO: later implement a method that orders the gamePlayers Array [0]: User [1]: Enemy
//        updateGameView.orderGamePlayers($scope.gp, $scope.players);
    })

  }]);

  app.controller('GamesController', ['$scope', '$http', function($scope, $http) {

              $http.get("/api/games")
                .then(function(response){

                    $scope.games_obj = angular.fromJson(response.data);
                    $scope.games =  $scope.games_obj;
                    console.log($scope.games_obj);

                })

    }]);



pl_view_mod.service('updateGameView', function(){

    this.getShipLocations = function(ships_array){

        var ship_locations = [];

    //looping through all the ships of the game player
        for(var i = 0; i < ships_array.length; i++){
                     var ship = ships_array[i].locations;
                     //looping through each ship's individual location
                     for(var j = 0; j < ship.length; j++){
                         ship_locations.push(ship[j]);
                     }
        }
        return ship_locations;
    }

    this.getUserPlayer = function (url_gp_id, players_array){
        var gp_id = players_array[0].gp_id;
        if(gp_id == url_gp_id){ return players_array[0]}
        else { return players_array[1]}
    }

    this.getEnemyPlayer = function (url_gp_id, players_array){
     var gp_id = players_array[0].gp_id;
     if(gp_id != url_gp_id){ return players_array[0]}
     else { return players_array[1]}
    }


  });