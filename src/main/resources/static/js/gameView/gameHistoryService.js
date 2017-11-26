angular.module('PlayerViewModule').service('gameHistory', function($http){

    this.getHistoryDTO = function(gpId, callbackFunc){


    $http.get("api/history/" + gpId)
            .success(function(response){

                console.info("api/history", response);
                callbackFunc(response);
            }).error(function(response){
                console.info("api/history", response)
                alert("unable to get gameHistoryDTO");
            });

    }


    this.getEnemyHitsArray = function(enemyHistoryDTO){

            var enemyHits = [];

            console.info("getEnemyHitsArray", enemyHistoryDTO);


            for (var turn_key in enemyHistoryDTO){

                var hits_arr = enemyHistoryDTO[turn_key].hits

                for(var sh_obj = 0; sh_obj < hits_arr.length; sh_obj++){

                    for (var sh_key in hits_arr[sh_obj]){

                        var ship_hits_arr = hits_arr[sh_obj][sh_key];

                        for (var sh_hit = 0; sh_hit < ship_hits_arr.length; sh_hit++){


                            enemyHits.push(ship_hits_arr[sh_hit]);
                        }


                    }
                }



            }

            console.info("enemyHits===>", enemyHits)

            return enemyHits;
    }

    this.showShipTurnHits = function(history, playerOrEnemy, last_turn){

         var ship_names = ["AircraftCarrier", "Battleship", "Submarine", "Destroyer", "PatrolBoat"]
        var ships = document.querySelectorAll("#"+playerOrEnemy + " tr.ship");
        console.log(history)

        var countTurnHits = function(hits_arr, ships, s){
            //console.log(ships[s].id);
            if(playerOrEnemy == "player-history"){
                var hits_arr_key = ships[s].id;
            } else {
                var id = ships[s].id.slice(2,ships[s].id.length);
                var hits_arr_key = id;
            }

            var all_ships_hits_arr = [];
            //loop through hits_arr && return hits for all ships
            for(var h = 0; h < hits_arr.length; h++){
                var hit_loc_arr = hits_arr[h][hits_arr_key]
                //console.log("hit_loc_arr", hit_loc_arr);
                //console.info("S: ", s);
                all_ships_hits_arr.push(hit_loc_arr);
            }
            function definedHit(hit){
                return hit != undefined;
            }

            return all_ships_hits_arr.find(definedHit);

        }

        var showTurnHits = function(turn_hits, ship_name, turn_num){

            var ship_cells = [];
            var removeSpaces = function (string) {
                                return string.replace(/\s+/g, '');
                            }
            ship_name = removeSpaces(ship_name);

            //console.info("removeSpaces", ship_name);

            if(playerOrEnemy == "player-history"){
             ship_cells = document.querySelectorAll("#"+removeSpaces(ship_name)+" .mock-cell");
            }

            if (playerOrEnemy == "enemy-player-history"){
               ship_cells = document.querySelectorAll("#E-"+removeSpaces(ship_name)+" .mock-cell");
            }

            if(turn_hits != undefined){
                 //TODO first empty all cells

//                var  empty_cells = document.querySelectorAll("#"+playerOrEnemy +" .mock-cell");
//                empty_cells.forEach(function(empty_cell){
//                        empty_cell.innerHTML = "";
//                })

                 //then fill them with the hits

//                var reset_count = turn_hits.length
//                while( reset_count > 0){
//                    for(var sc = 0; sc < ship_cells.length; sc++){
//                        if(ship_cells[sc].textContent == last_turn){
//                        ship_cells[sc].innerHTML = "";
//
//                        }
//                    }
//
//                    reset_count--
//                }

              var count = turn_hits.length;
               while( count > 0 ){

                    for(var sc = 0; sc < ship_cells.length; sc++){

                        var cell_empty = (ship_cells[sc].textContent == "");

                        if(cell_empty){

//                           var span = document.createElement('span');
//                          var textnode = document.createTextNode(turn_num);         // Create a text node
//                          span.appendChild(textnode);
//                            ship_cells[sc].appendChild(span); //.innerHTML = turn_num;
                            ship_cells[sc].innerHTML = turn_num;
                            break;
                        }


                    }
                count--;
                }
            }

        }

        var showSunkenShips = function(sunk_ships_arr) {
            if(sunk_ships_arr.length > 0){
               for (var s_sh = 0; s_sh < sunk_ships_arr.length; s_sh++){

                //sunk_ships_arr.forEach(function(sunk_ship){

                        //console.info("SUNK SHIP", sunk_ship)
                    var sunk_ship = sunk_ships_arr[s_sh];
                    var removeSpaces = function (string) {
                            return string.replace(/\s+/g, '');
                        }

                    sunk_ship = removeSpaces(sunk_ship);

                            if(playerOrEnemy == "player-history"){
                                document.getElementById(sunk_ship).setAttribute("class", "sunk");
                            } else {
                                document.getElementById("E-"+sunk_ship).setAttribute("class", "sunk");
                            }

                }

            }
        }

        eraseAllHits = function(ship_name){

         var ship_cells = [];
                    var removeSpaces = function (string) {
                                        return string.replace(/\s+/g, '');
                                    }
                    ship_name = removeSpaces(ship_name);

                    //console.info("removeSpaces", ship_name);

                    if(playerOrEnemy == "player-history"){
                     ship_cells = document.querySelectorAll("#"+removeSpaces(ship_name)+" .mock-cell");
                    }

                    if (playerOrEnemy == "enemy-player-history"){
                       ship_cells = document.querySelectorAll("#E-"+removeSpaces(ship_name)+" .mock-cell");
                    }


              ship_cells.forEach(function(ship_cell){

                            ship_cell.textContent = "";

              })


        }


        //loop through each ship element
        for(var s = 0; s < ships.length; s++){

                var ship_name = ship_names[s];
                //eraseAllHits(ship_name)
            for( var turn_key in history){
                var turn_hits = countTurnHits(history[turn_key].hits, ships, s)

                        //console.info("==== history", history[turn_key]);
                        //console.info("TURN HITS =====>", turn_hits);

                showTurnHits(turn_hits, ship_name, turn_key );
                var sunk_ships_arr = history[turn_key].sinks
                showSunkenShips(sunk_ships_arr)
            }

//            var turn_index = last_turn.toString()
//            var last_turn_hits = countTurnHits(history[turn_index].hits);
//            showTurnHits(last_turn_hits, ship_name, last_turn);

        }


        //TODO: solve this turn issue;
        //be sure this function is called only thrice per page load;
//        if(historyCount > 2){
//        this.showShipTurnHits = function() { return false;}
//        }
    }


    //LEGACY
    this.paintTurnHits = function(history_hits_array, ship_types_obj, turn_num) {
            //         $scope.ship_types = [
            //                    {type: "Aircraft Carrier", cells: 5},
            //                    {type: "Battleship", cells: 4},
            //                    {type: "Submarine", cells: 3},
            //                    {type: "Destroyer", cells: 3},
            //                    {type: "Patrol Boat", cells: 2}
            //            ];

                    var cell_turn_num = turn_num;


                    var removeSpaces = function (string) {
                                return string.replace(/\s+/g, '');
                            }


                    //1. loop through the history_hits_array and paint the number of turn in the correct ship, as many times as necessary

                    //                    for(var ship in history_hits_array){
                    //
                                            console.info("History HITS ARRAY", history_hits_array, turn_num)
                    //
                    //                         console.alert(history_hits_array[ship])
                    ////                         var ship_type = removeSpaces(history_hits_array[ship]);
                    ////                        document.getElementById(ship_type).style.color = "red";
                    //
                    //
                    //
                    //                    }


                    //            {"1": {"hits": [{"Submarine": 2}, {"Destroyer": 1}], "sinks" : []}
                    // TODO encapsulate in smaller functions with fewer arguments, get ship name out of there for example
                    var paintTurn = function(index, cell_turn_num, cell_index){
                            var ship_obj = history_hits_array[index];

                            var ship_name = Object.keys(ship_obj)[0];

                            console.info("SHIP TURN NUM", turn_num)

                            //                            var ship_cells = document.querySelectorAll("#" + ship_name + " td")
                            //                            if( ship_cells[cell_index].innerHTML = "   "){
                            //                                ship_cells[cell_index].innerHTML = turn_num;
                            //                            }

                            var ship_hits_arr = [];
                            for(var j = 0; j < num_hits; j++){
                                ship_hits_arr.push(cell_turn_num);
                            }
                            console.log( ship_hits_arr)


                           var ship_cell = document.querySelector("#"+ship_name + " .s"+cell_index)
                            //                            if(ship_cell.innerHTML == "   "){
                            //                                ship_cell.innerHTML = cell_turn_num;
                            //                            }
                             for(var k = 0; k < ship_hits_arr.length; k++){

                                ship_cell.innerHTML = ship_hits_arr[k];
                             }



                    }
                    //looping through each turn's hits objects (the ships that are hit. {ShipName: #hits)
                    for(var i =0; i < history_hits_array.length; i++){

                            var cell_index = 0;
                            var ship_obj = history_hits_array[i];
                            var ship_name = Object.keys(ship_obj)[0];
                            var num_hits = history_hits_array[i][ship_name]
                            console.info("NUM OF HITS", num_hits)

                            while(num_hits >=0){
                            console.log(ship_obj);console.log(ship_name)
                            paintTurn(i, cell_turn_num, cell_index)
                            cell_index++;
                            num_hits--;
                            }


                    }




                }




});