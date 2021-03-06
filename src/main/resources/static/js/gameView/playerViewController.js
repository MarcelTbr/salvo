
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
'$window', '$interval', '$timeout', '$q', 'updateGameView', 'placingShips', 'salvosLogic', 'gameHistory','myFactoryTest',   //'salvosLogic',
  function($scope, $http, $location, $window, $interval, $timeout, $q, updateGameView, placingShips, salvosLogic, gameHistory, myFactoryTest) { //salvosLogic,

    //  [1] Initialize variables

    $scope.nums = [1,2,3,4,5,6,7,8,9,10];
    $scope.abc = ["A", "B", "C", "D", "E", "F", "G", "H", "I", "J"];
      //getting the url to get the gp id.
    $scope.url = $location
    $scope.abs_url = $location.absUrl();
     // URL LOGS: console.log("Url:"); console.log($scope.url); console.log("Abs Url:"); console.log($scope.abs_url);
    $scope.url_obj = paramObj($scope.abs_url); //console.log($scope.url_obj);
    $scope.gp = $scope.url_obj["gp"]; //store the url_obj's value for the key "gp" == gamePlayer's id

    $scope.prov_salvo_array = [];
     // ==== important to initialize $scope.ship_placing_obj
    $scope.ship_placing_obj = {};

    $scope.ship_types = [
            {type: "Aircraft Carrier", cells: 5},
            {type: "Battleship", cells: 4},
            {type: "Submarine", cells: 3},
            {type: "Destroyer", cells: 3},
            {type: "Patrol Boat", cells: 2}
    ];
    console.info("Ship Types: ", $scope.ship_types);
    $scope.ship_align = "Vertical";

    $scope.user_info; $scope.user_info_ships;
    $scope.enemy_salvos_obj;
    $scope.ship_placing_obj[$scope.gp] = [];



    // LEGEND CLASS LOGIC

       //getLegendNameClass($index)

        $scope.getLegendNameClass = function(index) {
                var ship_name = $scope.ship_types[index].type;
                var placed_ships_array = $scope.ship_placing_obj[$scope.gp];
                var noShipRepeated = placingShips.noShipRepeated(ship_name, placed_ships_array );
                if(!noShipRepeated){ return "placed-legend";} else { return "";}
        }

         $scope.getLegendShipClass = function(index) {
                        var ship_name = $scope.ship_types[index].type;
                        var placed_ships_array = $scope.ship_placing_obj[$scope.gp];
                        var noShipRepeated = placingShips.noShipRepeated(ship_name, placed_ships_array );
                        if(!noShipRepeated){ return "placed-ship";} else { return "";}
                }

        //    [2] Load Back-end Objects

        // ******** getGameViewDTO ******* //
        $scope.game_view_obj = null;
        updateGameView.getGameViewDTO($scope.gp, function(response){
                     console.info("updateGameView.getGameViewDTO", response);
                    //console.info("SALVOES==>", response.salvoes); comes from the Salvoes object from loadSalvos();

                    // 1) store the game_viewDTO from backend response into a variable
                    $scope.game_view_obj = response;
                   // then update all the objects that use this response

                    //$interval(function(){updateGameViewObjects();}, 3500);

                    $scope.gameState = $scope.game_view_obj.gameState;
                    $scope.enemyGameState = $scope.game_view_obj.enemyGameState
                    console.info("gameState ====>>", $scope.game_view_obj.gameState);
                    console.info("enemyGameState ====>>", $scope.game_view_obj.enemyGameState);
                    loadSalvos();
                    updateGameViewObjects();

                     // *** USER INFO ** //
                     $scope.user_info = getUserInfo($scope.game_view_obj.enemyGameState, $scope.game_view_obj.gameState);


                })

        $interval(function(){ updateGameView.getGameViewDTO($scope.gp, function(response){
             console.info("updateGameView.getGameViewDTO", response);
            //console.info("SALVOES==>", response.salvoes); comes from the Salvoes object from loadSalvos();

            // 1) store the game_viewDTO from backend response into a variable
            $scope.game_view_obj = response;
           // then update all the objects that use this response

            //$interval(function(){updateGameViewObjects();}, 3500);


            $scope.gameState = $scope.game_view_obj.gameState;
            $scope.enemyGameState = $scope.game_view_obj.enemyGameState
            console.info("gameState ====>>", $scope.game_view_obj.gameState);
            console.info("enemyGameState ====>>", $scope.game_view_obj.enemyGameState);
            loadSalvos();
            updateGameViewObjects(response);

            // *** USER INFO ** //
             $scope.user_info = getUserInfo($scope.game_view_obj.enemyGameState, $scope.game_view_obj.gameState);

            console.info("historyDTO =====>>", $scope.game_view_obj.gameHistoryDTO);


        })

        }, 3500);



        var updateGameViewObjects = function(game_view_obj){
                        console.info("loadGameView() Response:", $scope.game_view_obj);
                        console.info("interval $scope.game_view_obj", $scope.game_view_obj)
//                        // 1) store the game_viewDTO from backend response into a variable
//                        $scope.game_view_obj = response.data;
                        console.info("$scope.game_view_obj", $scope.game_view_obj);
                        // 2) storing ships, players & salvoes objects into variables
                        $scope.ships = $scope.game_view_obj.ships;
                        $scope.enemy_ships =  $scope.game_view_obj.enemy_ships;
                        $scope.players = $scope.game_view_obj.gamePlayers
                        $scope.salvoes = $scope.game_view_obj.salvoes;
                        $scope.enemy_salvoes = $scope.game_view_obj.enemy_salvoes;
                            // console.log("salvoes"); console.log($scope.salvoes);
                            // console.log("enemy salvoes"); console.log($scope.enemy_salvoes);
                        // 3) getting ship locations array
                        $scope.all_ship_locations = updateGameView.getShipLocations($scope.ships);
                                // console.log("$scope.all_ship_locations: "); console.log($scope.all_ship_locations);

                        //4) Show Enemy Grid only if the User ships are placed;
                        $scope.ships_placed = function(){
                            //  number of user ships:  $scope.ships.length
                            if($scope.ships.length < 5 ) {
                                //$scope.user_info_ships = "Place your fleet ships in order to play."
                                return false;
                            } else {
                                //$scope.user_info_ships = "Waiting for an enemy to join the game/place ships ."
                                return true;
                            }
                        }
                        //5) Painting the Ships
                        $scope.getStyle = function(cell_data){
                            // 5.1) see if there are ships stored in the backend
                            if($scope.all_ship_locations.length > 0){
                                if($scope.enemy_hits_array != null){
                                    for (var i = 0; i < $scope.enemy_hits_array.length; i++){
                                        if(cell_data === $scope.enemy_hits_array[i]){ return {'background-color': 'orange'}}
                                    }
                                }
                                    // paint the cell green if it's a saved ship cell
                                    for(var i = 0; i < $scope.all_ship_locations.length; i++){
                                        if(cell_data === $scope.all_ship_locations[i]){ return {'background-color': 'green'}}
                                    }

                            } else {
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
                                } else {
                                    alert("be sure to place all ships before saving")
                                }
                         }


                        // ================Painting the Turns and Hits=============

                        $scope.targetCell = function(cell_data){

                            function findRepeated(cell){
                                    return cell == cell_data;
                            }
                            var repeated_cells = $scope.prov_salvo_array.filter(findRepeated, cell_data);
                            //check if salvo location is already in back-end
                            function repeatedSalvo(cell_data){

                                 for(turn in $scope.salvos_obj){
                                // find repeated cell in hits array
                                for(var i = 0; i < $scope.salvos_obj[turn]["hits"].length; i++) {
                                    if(cell_data == $scope.salvos_obj[turn]["hits"][i]){return true;}

                                }
                                // find repeated cell in the misses array
                                for(var i = 0; i < $scope.salvos_obj[turn]["misses"].length; i++) {
                                           if(cell_data == $scope.salvos_obj[turn]["misses"][i]){return true;}

                                }

                             }
                                // if cell not repeated
                                return false;
                            }
                            if ($scope.gameState > 3){

                           if($scope.enemyGameState == 5){
                                $.post("api/game_state/"+$scope.gp, {'gameState': 6})
                           }

                            if($scope.prov_salvo_array.length < 5 ){
                                    if (repeated_cells.length > 0 || repeatedSalvo(cell_data)) { alert("Choose another cell.")}
                                    else {

                                            if( $scope.gameState  > 3 &&  $scope.gameState != 6 ){
                                                $scope.prov_salvo_array.push(cell_data);
                                                //update game state to "shooting salvos"
                                               $.post("api/game_state/"+$scope.gp, {'gameState': 5})
                                               //$.post("api/enemy_game_state/"+$scope.gp, {'gameState': 6})
                                           } else {
                                                alert("Please wait for your turn to fire salvos")
                                           }
                                           // alert($scope.prov_salvo_array);
                                           }
                            } else {
                                alert("Salvo limit reached");
                            }
                            } else {  alert("Wait for the other player to join")}

                        }
                        $scope.isHitByEnemy = function (cell_data){

                            for(turn in $scope.enemy_salvos_obj){

                                for(var i = 0; i < $scope.enemy_salvos_obj[turn]["hits"].length; i++) {
                                    if(cell_data == $scope.enemy_salvos_obj[turn]["hits"][i]){return turn;}

                                }
                            }
                        }

                        $scope.getTurn = function(cell_data){

                            for(turn in $scope.salvos_obj){
                                // paint turn number of the hits array
                                for(var i = 0; i < $scope.salvos_obj[turn]["hits"].length; i++) {
                                    if(cell_data == $scope.salvos_obj[turn]["hits"][i]){return turn;}

                                }
                                // paint turn number of the misses array
                                for(var i = 0; i < $scope.salvos_obj[turn]["misses"].length; i++) {
                                           if(cell_data == $scope.salvos_obj[turn]["misses"][i]){return turn;}

                                }

                            }

                        }

                        //LEGACY: returns the turn number in order to print it in the corresponding cell
                        $scope.getTurnOld = function(cell_data){
                        for(turn in $scope.salvoes){
                            var salvo_array = $scope.salvoes[turn];
                            for(var i = 0; i < salvo_array.length; i++){
                                            if(cell_data == salvo_array[i]){return turn;}
                                            }

                            }
                        }

                        $scope.all_salvoes = updateGameView.allUserSalvoes($scope.salvoes);

                        $scope.hits_array = gameHistory.getEnemyHitsArray($scope.game_view_obj.gameHistoryDTO.enemyHistoryDTO);

                        //paintEnemyHits($scope.hits_array);

                        $scope.getEnemyStyle = function (cell_data){
                            //var all_salvoes = $scope.all_salvoes;
                            //var enemies = $scope.enemies;
                            var hits = [];

                            // LEGACY: hits = updateGameView.getHitsArray(all_salvoes, enemies);
                            // use the new Backend object
                            hits = $scope.hits_array;
                            if (hits != null){
                              //paint the ship cell red it it is found in the hits array.,

                              for(var i = 0; i < hits.length; i++) {
                                if (cell_data == hits[i]) {return {'background-color': 'red'} }
                              }

                              if ($scope.prov_salvo_array.length > 0){
                                 for(var i = 0; i < $scope.prov_salvo_array.length; i++){

                                    if ( cell_data == $scope.prov_salvo_array[i]) { return {'background-color': 'yellow'}}
                                 }

                              }
                            }

                        }

                        /* Getting User and Enemy Information*/
                        $scope.userGamePlayer = updateGameView.getUserPlayer($scope.gp, $scope.players);

                        /*  show Game History UI */
                        $scope.showGameHistoryUI = function(){

                                if($scope.ships.length == 5 && $scope.gameState > 3){

                                $scope.show_history_UI = true;

                                } else {

                                    $scope.show_history_UI = false;
                                }

                            }

                        $scope.showGameHistoryUI();

                        /* fill game History UI */
                         if($scope.user_turns != $scope.enemy_turns || $scope.enemyGameState >= 6) {

                            $scope.historyUICount == 1;
                         }

                        if($scope.historyUICount > 0){

                            //reload the tables before filling them
                            gameHistory.reloadPlayerTables();
                             fillGameHistoryUIPlayer($scope.game_view_obj.gameHistoryDTO, $scope.historyUICount);
                        }

                        if($scope.historyUICount > 0){

                             //reload the tables before filling them
                            gameHistory.reloadEnemyTables()
                            fillGameHistoryUIEnemy($scope.game_view_obj.gameHistoryDTO, $scope.historyUICount);

                         }
                          $scope.historyUICount++;

                        console.info("historyUICount", $scope.historyUICount);

                        //**** GAME OVER LOGIC **** ///

                        if ($scope.gameState > 4 ){ //$scope.gameState > 4

                            if($scope.isGameOver){

                                switch($scope.gameScore){

                                   case 1: alert("Game Over, you won!"); break;
                                   case 0.5: alert("Game Over, it's a tie!"); break;
                                   case 0: alert("Game Over, you lost, better luck next time!"); break;

                                }

                                $window.location.href = "http://" + $window.location.host + "/games.html";
                            }



                           $scope.isGameOVer = "";

                           if($scope.gameScore == undefined){
                               $http.get("api/game_over/" + $scope.gp)
                                .then(function successCallback(response){
                                         console.info("api/game_over", response);
                                        $scope.isGameOver = response.data.isGameOver;
                                        $scope.gameScore = response.data.gameScore;
                                },
                                function errorCallback(error){
                                     console.info("api/game_over", error);

                                })
                            }
                            //console.info( "GAME OVER ===>" , $scope.isGameOver);
                            //console.info("GAME SCORE", $scope.gameScore);



                         }

                        /// **** END OF GAME OVER LOGIC ****** //

                        // get the enemy player to display above the enemy game player grid
                        $scope.enemyGamePlayer = updateGameView.getEnemyPlayer($scope.gp, $scope.players);
                            console.info("$scope.userGamePlayer", $scope.userGamePlayer);
                            console.info("$scope.enemyGamePlayer", $scope.enemyGamePlayer);


        }

        //******* END OF getGameViewDTO *********//

        function loadSalvos(){


                    $scope.enemy_hits_array = salvosLogic.getHitsArray($scope.game_view_obj.salvos.enemySalvosDTO);
                    $scope.salvos_obj = $scope.game_view_obj.salvos.salvosDTO
                    $scope.enemy_salvos_obj = $scope.game_view_obj.salvos.enemySalvosDTO;
                           // console.info("====== ALL SALVOS DTO ====", response.data);

                    if($scope.salvos_obj != null){
                        $scope.user_turns = Object.keys($scope.salvos_obj).length;
                    }

                    if ($scope.enemy_salvos_obj != null) {
                         $scope.enemy_turns = Object.keys($scope.enemy_salvos_obj).length;
                    }

                    if ( $scope.salvos_obj == null){
                        $scope.user_turns = 1;
                    }

                    if ( $scope.enemy_salvos_obj == null){
                        $scope.enemy_turns = 1;
                    }

                    if ($scope.salvos_obj == null && $scope.enemy_salvos_obj == null) {
                         $scope.user_turns = 1;
                         $scope.enemy_turns = 1;
                    }


                    if ($scope.salvos_obj != null && $scope.enemy_salvos_obj != null) {
                        $timeout(function(){getCurrentTurn()}, 100);
                    }





        }


    $scope.showTurnInfo = function(){
        /// console.warn("$scope.user_turns", $scope.user_turns)
        //LEGACY
        //        if( $scope.current_turn && $scope.ships_placed()){
        //
        //            return true;
        //        }else {
        //            return false;
        //        }
        return true;

    }

    $scope.salvosReady = function(){
        if ( $scope.prov_salvo_array.length ==  5){

            return true;
        } else {

            return false;
        }
    }

    $scope.selectShip = function(ship_index){

        $scope.selected_ship = $scope.ship_types[ship_index];
                ///alert("Ship Selected: " + $scope.ship_types[ship_index].type);
        console.info("ship Selected", $scope.ship_types[ship_index]);
        $scope.ship_name = $scope.selected_ship["type"];
        console.info("ship_name", $scope.ship_name);

    }

    $scope.showShip = function(row, col) {
    // inputs: cell data, an array of predefined ship types, and the orientation or alignment of the ship
    // outputs: save a provisional array of ship locations, to paint on the frontend
        var placed_ships_array = $scope.ship_placing_obj[$scope.gp];

        //1- Check if the ship is repeated in the placed ships array
        var noShipRepeated = placingShips.noShipRepeated($scope.ship_name, placed_ships_array );
                                //console.info("NO SHIP REPEATED", noShipRepeated);
                                //console.info("PLACED SHIPS ARRAY LENGTH", placed_ships_array.length)
        //2- If selected_ship is in placed_ships array, then assign to undefined, to avoid painting it on the board
        if( !noShipRepeated && (typeof $scope.selected_ship != "undefined")  ){  $scope.selected_ship = "undefined"; }

        // 3- When Ship selected, get provisional ship location
        if( $scope.selected_ship != "undefined"){
            $scope.prov_ship_loc = placingShips.getProvShipLoc(row,col, $scope.selected_ship, $scope.ship_align);
                                //console.info("PLACED SHIPS ARR", $scope.ship_placing_obj[$scope.gp])


        }
     }

     // [3] get the current Turn

     function getCurrentTurn(){
        var user_turns = $scope.user_turns;
        var enemy_turns = $scope.enemy_turns;
            console.info("user_turns", user_turns);
            console.info("enemy_turns", enemy_turns);

            if (user_turns == 0 ){
                $scope.current_turn = 1;
                    // console.info( "$scope.ships", $scope.ships)
                if ($scope.ships.length == 0){
                     //$scope.user_info = "You can now place your ships"
                }
                else if( $scope.enemy_ships == undefined){
                    //$scope.user_info = "Enemy player is placing ships..."
                } else {
                    // console.warn("$scope.enemy_ships", $scope.enemy_ships);
                    if ($scope.enemy_ships.length == 0){
                        // $scope.user_info = "Enemy player is placing ships..."
                    }else if ($scope.ships.length == 0){
                       // $scope.user_info = "Enemy fleet is ready. Place ships in order to start the battle";
                    } else{
                       // $scope.user_info = "You are ready to play. Click 5 cells of the enemy's grid and submit them to start."
                    }
                }
            }
            else if(user_turns == enemy_turns && user_turns != 0){
                console.info("user_turns", user_turns);
                $scope.current_turn = user_turns + 1;
               // $scope.user_info = "Turn #"+ user_turns +" ended. You can submit more salvos";
            }
            else if( user_turns > enemy_turns) {
                $scope.current_turn = user_turns;
                //$scope.user_info = "Waiting for enemy salvos...";
            }
            else {
                //$scope.user_info = "It's your turn to fire salvos"
                $scope.current_turn = enemy_turns;

            }

     }

     $scope.submitSalvos = function(){

        // [A] what turn are we in?
        var current_turn = Object.keys($scope.salvos_obj).length + 1;
        // [B] create the obj to submit
        var submit_salvos_obj = {};
        submit_salvos_obj[current_turn] = $scope.prov_salvo_array;

        // [C] make sure it's your turn to submit salvos and it's not game over

        if(!$scope.isGameOver){
            if( $scope.gameState == 4 || $scope.gameState == 5 ){ // $scope.user_turns <= $scope.enemy_turns

                // [D] post it to the backend
                 $http.post("api/games/players/"+$scope.gp+"/salvos", submit_salvos_obj)
                 .then(function(response){
                    console.log("salvos submitted!"); console.log(response);
                 }, function(response){
                    console.log("something went wrong..."); console.log(response);
                 });
                $scope.salvosSent = true;
                $scope.historyUICount = 0;
                //update Game State to "waiting for enemy salvos"
                if($scope.game_view_obj.enemyGameState != 7){

                $.post("api/game_state/"+$scope.gp, {'gameState': 6});
                $.post("api/enemy_game_state/"+$scope.gp, {'gameState': 7});

                } else {

                $.post("api/game_state/"+$scope.gp, {'gameState': 7})

                }

            // [E] reset prov_salvo_array after posting salvos
             $scope.prov_salvo_array = [];
             $scope.historyCount = 0;
             $scope.historyUICount = 0;

            //LEGACY
            //$window.location.href = $window.location.href.slice(0, -3);

            } else {

             alert("Please wait for enemy's turn to submit salvos")
            $window.location.href = $window.location.href
            }



        } else {

              $window.location.href = "http://" + $window.location.host + "/games.html";

        }



      }

     $scope.placeShip = function (row,col) {
        //  var is_legal = placingShips.insideGridTwo($scope.prov_ship_loc, $scope.ship_align);
        var placed_ships = $scope.ship_placing_obj[$scope.gp];

        if(typeof placed_ships == 'undefined') {
                    // console.log("placed ships is undefined")
            $scope.ship_placing_obj[$scope.gp] = [];
        }
                   //  console.log("placed_ships"); console.log($scope.ship_placing_obj);

        var inside_grid = placingShips.insideGrid($scope.prov_ship_loc);
                    console.log("$scope.prov_ship_loc: ");console.log($scope.prov_ship_loc)
                    console.log("inside_grid is: " + inside_grid);
        if(inside_grid){
            var placed_ships_loc_array = placingShips.getPlacedShipLocArray($scope.ship_placing_obj[$scope.gp])
                        //console.log(placed_ships_loc_array);
            var ship_overlapping = placingShips.shipOverlapping($scope.prov_ship_loc, placed_ships_loc_array)
                        //alert("ship_overlapping is: " + ship_overlapping);

            placingShips.noShipRepeated($scope.ship_name, $scope.ship_placing_obj[$scope.gp]);

        }

        if(inside_grid && !ship_overlapping){

            var new_ship = placingShips.makeShip($scope.prov_ship_loc, $scope.ship_name);
                    console.log(new_ship);
             // 1) check if there's place in $scope.ship_placing_obj[gp_id]
             // 2) if there's place push the ship, if not alert user
            if($scope.ship_placing_obj[$scope.gp].length < 5){
            $scope.ship_placing_obj[$scope.gp].push(new_ship); console.log("Placed Ships: "); console.log(placed_ships);

            $.post("api/game_state/"+$scope.gp, {'gameState': 2 })
            } else { alert("Sorry, you can only place 5 ships")}

        }
       }

    $scope.paintPlacedShips = function(gp_placed_ships_obj, prov_ship_loc, cell_data){
            // var grid_cell_legal = placingShips.legalCell(placingShips.legalRow, placingShips.legalCol);
        var mouse_over_grid = typeof prov_ship_loc != 'undefined';
            // console.log(cell_data);
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

                             if (placingShips.legalPosition(prov_ship_loc[i], ship_list) && ($scope.selected_ship != 'undefined')){
                              return {'background-color': 'lightyellow'}
                              } else {

                                //paint the first non-placed ship lightyellow as well
                                $scope.is_first_ship = ($scope.ship_placing_obj[$scope.gp].length == 0);

                                    if( !$scope.is_first_ship){
                                    $scope.is_first_ship = false;
                                     return {'background-color': 'red'}
                                     } else {
                                       return {'background-color': 'lightyellow'}
                                     }
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


    /// ******* [Game History UI Logic] ******** \\\

//    [1] Get the historyDTO from the backend
    $scope.historyDTO = null; //!important
    $scope.hits_array = null;
    $scope.historyUICount = 0;

    var fillGameHistoryUI = function(historyDTO, historyUICount){

        if (historyDTO != null) {
            var player_history = historyDTO.historyDTO;
            var enemy_history = historyDTO.enemyHistoryDTO;
            var last_player_turn = Object.keys(historyDTO.historyDTO).length;
            var last_enemy_turn = Object.keys(historyDTO.enemyHistoryDTO).length;

                if(last_player_turn > 0){
                    gameHistory.showShipTurnHits(player_history, "player-history", last_player_turn);
                    $scope.historyCount++;
                }

                if ( last_enemy_turn > 0){
                    gameHistory.showShipTurnHits(enemy_history, "enemy-player-history", last_enemy_turn);
                    $scope.historyCount++;
                }
        }



    }

     var fillGameHistoryUIPlayer = function(historyDTO, historyUICount){

            if (historyDTO != null) {
                var player_history = historyDTO.historyDTO;
                var last_player_turn = Object.keys(historyDTO.historyDTO).length;
                   // console.info("last_player_turn", last_player_turn);
                    if(last_player_turn > 0){
                        gameHistory.showShipTurnHits(player_history, "player-history", last_player_turn);
                        $scope.historyCount++;
                    }

            }


        }
         var fillGameHistoryUIEnemy = function(historyDTO, historyUICount){

                if (historyDTO != null) {
                    var enemy_history = historyDTO.enemyHistoryDTO;
                    var last_enemy_turn = Object.keys(historyDTO.enemyHistoryDTO).length;
                        //console.info("enemyHistoryDTO*", historyDTO.enemyHistoryDTO)
                        //console.info("last_enemy_turn", last_enemy_turn)

                    if ( last_enemy_turn > 0){
                        gameHistory.showShipTurnHits(enemy_history, "enemy-player-history", last_enemy_turn);
                        $scope.historyCount++;
                    }
                }



            }

    //***** END OF GAME HISTORY UI ******** //


    // **** USER INFO LOGIC ****** //

//    $scope.user_info = getUserInfo();


    function getUserInfo(enemyGameState, gameState){

        var userInfo;

        if (enemyGameState > 0){
        switch(enemyGameState){


            case 0: userInfo = "Game Created"; break;
            case 1: userInfo = "Player Joined"; break;
            case 2: userInfo =  "Enemy is placing ships"; break;
            case 3: userInfo =  "Enemy is waiting for you to place ships"; break;
            case 4: userInfo = "All ships placed. Game can begin. Start submitting salvos"; break;
            case 5: userInfo = "Enemy is shooting salvos"; break;
            case 6: userInfo = "Enemy is waiting for you to shoot salvos"; break;
            case 7: userInfo = "Enemy received your salvos. Wait for him to shoot back"; break;
            case 8: userInfo =  "Game Over"; break;

        }

        } else {
            switch(gameState){
                case 0: userInfo = "Game Created"; break;
                case 1: userInfo = "You can start placing ships"; break;
                case 2: userInfo = "Placing ships..."; break;
                case 3: userInfo = "Waiting for enemy ships..."; break;


            }

        }

        return userInfo;



    }


    // **** END OF USER INFO LOGIC **** //



//
//    function paintEnemyHits(hits_array){
//
//          for(var i = 0; i < hits_array.length; i++) {
//
//            document.querySelector("#enemy-grid [data-cell='"+hits_array[i]+"']").style.backgroundColor = "red";
//
//          }
//
//
//
//    }

  }]);