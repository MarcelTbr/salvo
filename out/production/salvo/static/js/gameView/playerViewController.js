//
//app.controller('PlayerViewController', ['$scope', '$http', '$location', '$window','updateGameView',
//  function($scope, $http, $location, $window, updateGameView) {
//
//    $scope.testService = function(){ myFactoryTest.factoryTest1()};
//
//    $scope.nums = [1,2,3,4,5,6,7,8,9,10];
//    $scope.abc = ["A", "B", "C", "D", "E", "F", "G", "H", "I", "J"];
//      //getting the url to get the gp id.
//     $scope.url = $location
//     $scope.abs_url = $location.absUrl();
//     console.log("Url:");
//     console.log($scope.url);
//     console.log("Abs Url:")
//     console.log($scope.abs_url);
//
//     $scope.url_obj = paramObj($scope.abs_url);
//     console.log($scope.url_obj);
//     $scope.gp = $scope.url_obj["gp"]; //store the url_obj's value for the key "gp"
//     console.log($scope.gp); // that is the gamePlayer's id
//
//     $scope.gv_title = { 'text-align':'center'}; //styling game view's title
//
//    $scope.ship_placing_list = [{ "shipType": "Submarine", "shipLocations": ["A7", "B7", "C7"] },
//                                                           { "shipType": "Destroyer", "shipLocations": ["A9", "B9", "C9"] }];
//    $scope.saveShips = function(){
//
//        $http.post( "/api/games/players/1/ships",
//                           [{ "shipType": "Submarine", "shipLocations": ["A7", "B7", "C7"] },
//                            { "shipType": "Destroyer", "shipLocations": ["A9", "B9", "C9"] }])
//
//
//    }
//
//     $scope.ship_types = [
//                {type: "Aircraft Carrier", cells: 5},
//                {type: "Battleship", cells: 4},
//                {type: "Submarine", cells: 3},
//                {type: "Destroyer", cells: 3},
//                {type: "Patrol Boat", cells: 2}
//                ];
//
//
//    $scope.selected_ship_loc = ["G4", "G5", "G6", "G7", "G8", "A6", "A7", "A8"];
////TODO: implement dynamic variables, this are constant
//    console.log("Ship Types:"); console.log($scope.ship_types);
//    $scope.selected_ship = $scope.ship_types[1];
//    $scope.ship_align = "H";
//
//    $scope.showShip = function(row, col){
//
//        console.log("Ship Types:")
//        console.log($scope.ship_types);
//
//        if($scope.grid_cell != row+col){
//            /*var start = $scope.prov_ship_loc.length - ($scope.selected_ship["cells"]);
//            var deleteCount =  $scope.selected_ship["cells"];
//            $scope.prov_ship_loc.splice(start, deleteCount);*/
//            $scope.prov_ship_loc = [];
//            $scope.prov_ship_loc.push(row+col);
//            if($scope.ship_align == "H"){
//
//                    for(var i = 1; i <= $scope.selected_ship["cells"]; i++){
//
//
//                       $scope.prov_ship_loc.push(row+(col++));
//
//                    }
//
//                $scope.grid_cell = row+col;
//            }
//
//        }
//    }
//
//    $scope.legalPosition = function(grid_cell, ship_list){
//                    console.log("legalPositon : " + grid_cell)
//                    var is_legal;
//             for(var i = 0; i < ship_list.length; i++){ //iterating through each ship
//                            var ship_loc_list = ship_list[i]["shipLocations"];
//
//                            for(var j = 0; j < ship_loc_list.length; j++){ //iterating through all ship locations
//
//                                if(ship_loc_list[j] == grid_cell){ //if the location overlaps it's illegal
//                                    console.log(grid_cell + " is an illegal position!")
//                                    console.log("ILLEGAL>>cell: " + grid_cell+ " == " + ship_loc_list[j] )
//                                    return false;
//                                }else { is_legal = true;}
//                            }
//
//                        }
//                        return is_legal;
//
//    }
//
//    $scope.paintPlacedShips = function(ship_list, selected_ship_loc, prov_ship_loc, cell_data){
////    console.log(ship_list)
//
// var mouse_over_grid = typeof prov_ship_loc != 'undefined';
//
//    if (mouse_over_grid){
//        for (var i = 0; i < prov_ship_loc.length; i++){ //iterating through the mouseover ship locations
//
//                 var provisional_ship = typeof prov_ship_loc != 'undefined';
//                 if(provisional_ship && prov_ship_loc[i] === cell_data){
//                     if ($scope.legalPosition(prov_ship_loc[i], ship_list)){
//                      return {'background-color': 'lightyellow'}
//                      }else {
//                          return {'background-color': 'red'}
//                     }
//                 }
//
//        }
//    }
//
//     /*for(var i = 0; i < selected_ship_loc.length; i++){ //iterating through the array of locations
//                //                    console.log(ship_loc_list[j]);
//                var ship_selected = typeof selected_ship_loc != 'undefined';
//                var cell_selected = selected_ship_loc[i] === cell_data;
//                                    if(ship_selected && cell_selected ){
//                                        if ($scope.legalPosition(selected_ship_loc[i], ship_list)){
//                                           return {'background-color': 'orange'}
//                                            }else {
//                                                return {'background-color': 'red'}
//                                        }
//
//                                    }
//
//     }*/ //end selected_ship_loc iteration
//
//
//            for(var i = 0; i < ship_list.length; i++){ //looping the ship list
//                var ship_loc_list = ship_list[i]["shipLocations"];
//
//                for(var j = 0; j < ship_loc_list.length; j++){ //iterating through each location of each ship
////                    console.log(ship_loc_list[j]);
//                    if(ship_loc_list[j] === cell_data){
////                        console.log("match-found!")
//                        return {'background-color': 'yellow'}
//                    }
//                }
//
//            }
//
//
//
//    }
//
//
//
//    $http.get("api/game_view/" + $scope.gp)
//    .then(function(response){
//        console.log("Response:"); console.log(response);
//
//        $scope.game_view_obj = response.data;  // store the game_viewDTO into a variable
//        console.log($scope.game_view_obj);
//        // storing ships, players & salvoes objects into variables
//        $scope.ships = $scope.game_view_obj.ships;
//        $scope.enemy_ships =  $scope.game_view_obj.enemy_ships;
//        // $scope.player1 = $scope.game_view_obj.gamePlayers[0].player
//        $scope.players = $scope.game_view_obj.gamePlayers
//        $scope.salvoes = $scope.game_view_obj.salvoes;
//        $scope.enemy_salvoes = response.data.enemy_salvoes;
//        console.log("salvoes"); console.log($scope.salvoes); console.log("enemy salvoes"); console.log($scope.enemy_salvoes);
//        // getting ship locations array
//        $scope.all_ship_locations = updateGameView.getShipLocations($scope.ships);
//        console.log($scope.all_ship_locations);
//
//        //Show Enemy Grid only if the User ships are placed;
//                    $scope.ships_placed = function(){
//        //            console.log("number of user ships: " + $scope.ships.length);
//                    if($scope.ships.length < 5 ) {return false;} else { return true; }
//                    }
//
//        //Painting the Ships
//        $scope.getStyle = function(cell_data){
//
//                    if($scope.all_ship_locations.length > 0){
//
//                                //apply the style to the ship if it is in the all_ship_locations array sent from backend
//                                for(var i = 0; i < $scope.all_ship_locations.length; i++){
//                                if(cell_data === $scope.all_ship_locations[i]){ return {'background-color': 'green'}}
//                                }
//          } else {
////            console.log("You have to place the ships");
//// TODO: finish function to pain placed ships
//               return $scope.paintPlacedShips($scope.ship_placing_list, $scope.selected_ship_loc ,$scope.prov_ship_loc, cell_data);
//
//          }
//        }
//        // Painting the Turns and Hits
//        $scope.getTurn = function(cell_data){
//        for(turn in $scope.salvoes){
//            var salvo_array = $scope.salvoes[turn];
//            for(var i = 0; i < salvo_array.length; i++){
//                            if(cell_data == salvo_array[i]){return turn;}
//                            }
//
//            }
//        }
//        $scope.enemies = updateGameView.getEnemies($scope.enemy_ships);
//        $scope.all_salvoes = updateGameView.allUserSalvoes($scope.salvoes);
//
//        $scope.getEnemyStyle = function (cell_data){
//            var all_salvoes = $scope.all_salvoes;
//            var enemies = $scope.enemies;
//            var hits = [];
//
//          hits = updateGameView.getHitsArray(all_salvoes, enemies);
//
//          for(var i = 0; i < hits.length; i++) {
//          if (cell_data == hits[i]) {return {'background-color': 'red'} }
//          }
//
//        }
//
//        /* Getting User and Enemy Information*/
//        $scope.userGamePlayer = updateGameView.getUserPlayer($scope.gp, $scope.players);
//        $scope.enemyGamePlayer = updateGameView.getEnemyPlayer($scope.gp, $scope.players);
//        console.log($scope.userGamePlayer);
//        console.log("Enemy Game Player");
//        console.log($scope.enemyGamePlayer);
////        TODO: later implement a method that orders the gamePlayers Array [0]: User [1]: Enemy
////        updateGameView.orderGamePlayers($scope.gp, $scope.players);
//
//    }, function(response){ //user feedback & redirection if he is not authorized
//            console.log("Response:"); console.log(response);
//            alert(response.data.unauthorize);
//            $window.location.href = "http://" + $window.location.host + "/games.html";
//    })
//
//  }]);