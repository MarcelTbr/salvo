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
     $scope.gp = $scope.url_obj["gp"];
     console.log($scope.gp);

    $http.get("api/game_view/" + $scope.gp)
    .then(function(response){

        $scope.game_view_obj = response.data;
        console.log($scope.game_view_obj);
        $scope.ships = $scope.game_view_obj.ships;
        $scope.player1 = $scope.game_view_obj.gamePlayers[0].player

        $scope.all_ship_locations = updateGameView.getShipLocations($scope.ships);
        console.log($scope.all_ship_locations);

        updateGameView.paintShips($scope.all_ship_locations);

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

//          ship_locations.push("A1");
//          ship_locations.push("A2");
//          ship_locations.push("A3");

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

    this.paintShips = function(locations){

        var grid = document.querySelectorAll('[data-cell]');
        //control logs
        console.log(locations);
//        console.log(grid)
        console.log(grid[7].attributes[1].nodeValue)

        var cells_to_paint = [];
        //storing the elements to paint as ships into an array
        for(var i = 0; i < locations.length; i++){
            var ship_el = document.querySelectorAll('[data-cell='+locations[i]+']');
            cells_to_paint.push(ship_el);
        }
        console.log(cells_to_paint);
        //changing the elements style to represent a ship
        for(var i=0; i < cells_to_paint.length; i++){
            cells_to_paint[i][0].style.backgroundColor = "green";
        }
    } //end paintShips()
  });