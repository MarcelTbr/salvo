
//function to return an object from a query string i.e. location.search
function paramObj(search) {
  var obj = {};
  var reg = /(?:[?&]([^?&#=]+)(?:=([^&#]*))?)(?:#.*)?/g;

  search.replace(reg, function(match, param, val) {
    obj[decodeURIComponent(param)] = val === undefined ? "" : decodeURIComponent(val);
  });

  return obj;
}

angular.module('PlayerViewModule').controller('PlayerViewController', ['$scope', '$http', '$location',
'$window','updateGameView', 'placingShips', 'myFactoryTest',
  function($scope, $http, $location, $window, updateGameView, placingShips, myFactoryTest) {

    $scope.nums = [1,2,3,4,5,6,7,8,9,10];
    $scope.abc = ["A", "B", "C", "D", "E", "F", "G", "H", "I", "J"];
      //getting the url to get the gp id.
     $scope.url = $location
     $scope.abs_url = $location.absUrl();
     // URL LOGS: console.log("Url:"); console.log($scope.url); console.log("Abs Url:"); console.log($scope.abs_url);
     $scope.url_obj = paramObj($scope.abs_url); //console.log($scope.url_obj);
     $scope.gp = $scope.url_obj["gp"]; //store the url_obj's value for the key "gp" == gamePlayer's id

     // ==== important to initialize $scope.ship_placing_obj
     $scope.ship_placing_obj = {};

     $scope.ship_types = [
                {type: "Aircraft Carrier", cells: 5},
                {type: "Battleship", cells: 4},
                {type: "Submarine", cells: 3},
                {type: "Destroyer", cells: 3},
                {type: "Patrol Boat", cells: 2}
                ];
                        console.log("Ship Types:"); console.log($scope.ship_types);
            //Default selected ship
            $scope.selected_ship = $scope.ship_types[1];
            $scope.ship_align = "Vertical";
            $scope.ship_name = $scope.selected_ship["shipType"];

    $scope.selectShip = function(ship_index){
        $scope.selected_ship = $scope.ship_types[ship_index]; //alert("ship Selected: "+ ship_index);
    }

    $scope.showShip = function(row, col) {
    // inputs: cell data, an array of predefined ship types, and the orientation or alignment of the ship
    // outputs: save a provisional array of ship locations, to paint on the frontend
     $scope.prov_ship_loc = placingShips.getProvShipLoc(row,col, $scope.selected_ship, $scope.ship_align);
     }
     //TODO finish method
//    $scope.submitSalvos2 = function(){
//
//     $http.post("api/games/players/"+$scope.gp+"/salvos", 4, ["A9", "G9", "F5"] )
//         .then(function(response){
//            console.log("salvos submitted!"); console.log(response);
//         }, function(response){
//            console.log("something went wrong..."); console.log(response);
//         });
//         }





     $scope.submitSalvos = function(){

     $http.post("api/games/players/"+$scope.gp+"/salvos", { "4": ["A9", "G9", "F5"]})
     .then(function(response){
        console.log("salvos submitted!"); console.log(response);
     }, function(response){
        console.log("something went wrong..."); console.log(response);
     });
     }


    //TODO condition to place every ship type ONLY ONCE!
    $scope.placeShip = function (row,col) {
        //  var is_legal = placingShips.insideGridTwo($scope.prov_ship_loc, $scope.ship_align);
        var placed_ships = $scope.ship_placing_obj[$scope.gp];

        if(typeof placed_ships == 'undefined') {
                    console.log("placed ships is undefined")
            $scope.ship_placing_obj[$scope.gp] = [];
        }
                    console.log("placed_ships"); console.log($scope.ship_placing_obj);

        var inside_grid = placingShips.insideGrid($scope.prov_ship_loc);
                    console.log("$scope.prov_ship_loc: ");console.log($scope.prov_ship_loc)
                    console.log("inside_grid is: " + inside_grid);
        if(inside_grid){
            var placed_ships_loc_array = placingShips.getPlacedShipLocArray($scope.ship_placing_obj[$scope.gp])
                        //console.log(placed_ships_loc_array);
            var ship_overlapping = placingShips.shipOverlapping($scope.prov_ship_loc, placed_ships_loc_array)
                        //alert("ship_overlapping is: " + ship_overlapping);
        }

        if(inside_grid && !ship_overlapping){
            var new_ship = placingShips.makeShip($scope.prov_ship_loc, $scope.ship_name);
                    console.log(new_ship);
             // 1) check if there's place in $scope.ship_placing_obj[gp_id]
             // 2) if there's place push the ship, if not alert user
            if($scope.ship_placing_obj[$scope.gp].length < 5){
            $scope.ship_placing_obj[$scope.gp].push(new_ship); console.log("Placed Ships: "); console.log(placed_ships);
            } else { alert("Sorry, you can only place 5 ships")}

        }
       }

    //TODO finish method and ENCAPSULATE IT!
    $scope.paintPlacedShips = function(gp_placed_ships_obj, prov_ship_loc, cell_data){
//        var grid_cell_legal = placingShips.legalCell(placingShips.legalRow, placingShips.legalCol);
        var mouse_over_grid = typeof prov_ship_loc != 'undefined';
//        console.log(cell_data);
        if(gp_placed_ships_obj != null){
            // 1) get the correct ship list for this gp.
            var gp_id = $scope.gp.toString()
            var ship_list = gp_placed_ships_obj[gp_id];

            //2) paint the provisional ship to place, on mouse over (ONLY)

            if (mouse_over_grid){
                //when mouse is over grid, show selected ship in light_yellow

                for (var i = 0; i < prov_ship_loc.length; i++){ //iterating through the mouseover ship locations

                         var provisional_ship = typeof prov_ship_loc != 'undefined';
                         if(provisional_ship && prov_ship_loc[i] === cell_data){
                             if (placingShips.legalPosition(prov_ship_loc[i], ship_list)){
                              return {'background-color': 'lightyellow'}
                              }else {
                                  return {'background-color': 'red'}
                             }
                         }

                }
            }

            var list_full = typeof ship_list != 'undefined';
            if (list_full){
                // 3) paint the ships that are already placed if ship list exist, ONLY
                for(var i = 0; i < ship_list.length; i++){ //looping the ship list
                    var ship_loc_list = ship_list[i]["shipLocations"];

                    for(var j = 0; j < ship_loc_list.length; j++){ //iterating through each location of each ship
                        //     console.log(ship_loc_list[j]);
                        if(ship_loc_list[j] === cell_data){
                            //       console.log("match-found!")
                            return {'background-color': 'yellow'}
                        }
                    }

                }
            } //end if (list_full)

        }else {  console.log("gp_placed_ships is null!!");
            if (mouse_over_grid){

                return placingShips.paintFirstShip(prov_ship_loc, cell_data);

            }else { return "";}
        }




    } // end of paintPlaced Ships

    // get the game_view object of this game player
    $http.get("api/game_view/" + $scope.gp)
    .then(function(response){
        console.log("Response:"); console.log(response);

        $scope.game_view_obj = response.data;  // 1) store the game_viewDTO from backend response into a variable
        console.log($scope.game_view_obj);
        // 2) storing ships, players & salvoes objects into variables
        $scope.ships = $scope.game_view_obj.ships;
        $scope.enemy_ships =  $scope.game_view_obj.enemy_ships;
        $scope.players = $scope.game_view_obj.gamePlayers
        $scope.salvoes = $scope.game_view_obj.salvoes;
        $scope.enemy_salvoes = response.data.enemy_salvoes;
        console.log("salvoes"); console.log($scope.salvoes); console.log("enemy salvoes"); console.log($scope.enemy_salvoes);
        // 3) getting ship locations array
        $scope.all_ship_locations = updateGameView.getShipLocations($scope.ships);
        console.log("$scope.all_ship_locations: "); console.log($scope.all_ship_locations);

        //4) Show Enemy Grid only if the User ships are placed;
        $scope.ships_placed = function(){
            //  number of user ships:  $scope.ships.length
            if($scope.ships.length < 5 ) {return false;} else { return true; }
        }
        //5) Painting the Ships
        $scope.getStyle = function(cell_data){
            // 5.1) see if there are ships stored in the backend
            if($scope.all_ship_locations.length > 0){
                    // paint the cell green if it's a saved ship cell
                    for(var i = 0; i < $scope.all_ship_locations.length; i++){
                    if(cell_data === $scope.all_ship_locations[i]){ return {'background-color': 'green'}}
                    }
            } else {
            //            console.log("You have to place the ships");
            // TODO: finish function to paint placed ships
               return $scope.paintPlacedShips($scope.ship_placing_obj, $scope.prov_ship_loc, cell_data);
                    //======== list of placed ships (yellow)
                    //======== + locations on mouse over (light yellow) + current cell
          }
        } //======== end of getStyle(cell_data)

        // ======== save SHIPS =====
         $scope.saveShips = function(){
                var placed_ships_obj_array = $scope.ship_placing_obj[$scope.gp];
                if (placed_ships_obj_array.length == 5) {
                placingShips.saveShips(placed_ships_obj_array, $scope.gp);
                $window.location.href = "http://" + $window.location.host + "/game.html?gp="+$scope.gp;
                } else { alert("be sure to place all ships before saving")}
            }


        // ================Painting the Turns and Hits=============
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
        console.log("Enemy Game Player"); console.log($scope.enemyGamePlayer);
        //        TODO: later implement a method that orders the gamePlayers Array [0]: User [1]: Enemy
        //        updateGameView.orderGamePlayers($scope.gp, $scope.players);

    }, function(response){ //user feedback & redirection if he is not authorized
            console.log("Response:"); console.log(response);
            alert(response.data.unauthorize);
            $window.location.href = "http://" + $window.location.host + "/games.html";
    })

  }]);