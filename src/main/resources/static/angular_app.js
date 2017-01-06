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

        $scope.game_view_obj = response.data;  // store the game_viewDTO into a variable
        console.log($scope.game_view_obj);
        // storing ships, players & salvoes objects into variables
        $scope.ships = $scope.game_view_obj.ships;
        $scope.enemy_ships =  $scope.game_view_obj.enemy_ships;
        // $scope.player1 = $scope.game_view_obj.gamePlayers[0].player
        $scope.players = $scope.game_view_obj.gamePlayers
        $scope.salvoes = $scope.game_view_obj.salvoes;
        $scope.enemy_salvoes = response.data.enemy_salvoes;
        console.log("salvoes"); console.log($scope.salvoes); console.log("enemy salvoes"); console.log($scope.enemy_salvoes);
        // getting ship locations array
        $scope.all_ship_locations = updateGameView.getShipLocations($scope.ships);
        console.log($scope.all_ship_locations);

        //Painting the Ships
        $scope.getStyle = function(cell_data){
                                //apply the style to the ship if it is in the all_ship_locations array
                                for(var i = 0; i < $scope.all_ship_locations.length; i++){
                                if(cell_data === $scope.all_ship_locations[i]){ return {'background-color': 'green'}}
                                }
        }
        // Painting the Turns and Hits
        $scope.getTurn = function(cell_data){
        for(turn in $scope.salvoes){
            var salvo_array = $scope.salvoes[turn];
            for(var i = 0; i < salvo_array.length; i++){
                            if(cell_data == salvo_array[i]){return turn;}
                            }

            }
        }
        $scope.enemies = updateGameView.getEnemies($scope.enemy_ships);
        $scope.all_salvoes = updateGameView.allUserSalvoes($scope.salvoes);

        $scope.getEnemyStyle = function (cell_data){
            var all_salvoes = $scope.all_salvoes;
            var enemies = $scope.enemies;
            var hits = [];

          hits = updateGameView.getHitsArray(all_salvoes, enemies);

          for(var i = 0; i < hits.length; i++) {
          if (cell_data == hits[i]) {return {'background-color': 'red'} }
          }

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

              $http.get("/api/scores")
                             .then(function(response){

                                 $scope.scores_obj = angular.fromJson(response.data);
                                 $scope.scores_keys = Object.keys($scope.scores_obj);
                                 console.log($scope.scores_obj);
                                 console.log("scores_obj_keys_array: ")
                                 console.log($scope.scores_keys);

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

  this.getEnemies = function (enemies){
            var all_enemies = [];
            for (var j= 0; j < enemies.length ; j++){
                    var enemy_ship = enemies[j].locations;
                    console.log("enemy_ship"); console.log(enemy_ship)
                    for(var i = 0; i < enemy_ship.length; i++ ){
                    all_enemies.push(enemy_ship[i])
                    }

            }
            return all_enemies;
        }

    this.allUserSalvoes = function(salvoes_obj) {
        var all_salvoes = [];
         for(turn in salvoes_obj){
                          var salvo_array = salvoes_obj[turn];
                          for(var i = 0; i < salvo_array.length; i++){
                            all_salvoes.push(salvo_array[i]);
                          }
         }
         return all_salvoes;
    }

    this.getHitsArray = function (all_salvoes, enemies) {
            var hits = [];
          //looping through all_salvoes array
              for(var i = 0; i < all_salvoes.length; i++) {
    //                console.log("salvo");
    //                console.log(all_salvoes[i]);
                for(var j = 0; j < enemies.length; j++){
    //                console.log("enemy")
    //                console.log(enemies[j]);
                    if(all_salvoes[i] == enemies[j]){ console.log("hit!"); hits.push(all_salvoes[i]) }
                }
              }
        return hits;
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