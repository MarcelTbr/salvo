angular.module('PlayerViewModule').service('gameHistory', function($http){

    this.showShipTurnHits = function(history, playerOrEnemy){

         var ship_names = ["AircraftCarrier", "Battleship", "Submarine", "Destroyer", "PatrolBoat"]
        var ships = document.querySelectorAll("#"+playerOrEnemy + " tr.ship");
        console.log(history)

        var countTurnHits = function(hits_arr){
            console.log(ships[s].id);
            if(playerOrEnemy == "player-history"){
                var hits_arr_key = ships[s].id;
            } else {
                var id = ships[s].id.slice(2,ships[s].id.length);
                var hits_arr_key = id;
            }
            //if(playerOrEnemy =="player-history"){
                var all_ships_hits_arr = [];
                //loop through hits_arr && return hits for all ships
                for(var h = 0; h < hits_arr.length; h++){
                    //console.log(hits_arr[h][hits_arr_key])
                    //console.info("S: ", s);
                    all_ships_hits_arr.push(hits_arr[h][hits_arr_key]);
                }
                function definedHit(hit){
                    return hit != undefined;
                }

                return all_ships_hits_arr.find(definedHit);

        }

        var showTurnHits = function(turn_hits, ship_name, turn_num){

            var ship_cells = [];
            if(playerOrEnemy == "player-history"){
             ship_cells = document.querySelectorAll("#"+ship_name+" .mock-cell");
            }

            if (playerOrEnemy == "enemy-player-history"){
               ship_cells = document.querySelectorAll("#E-"+ship_name+" .mock-cell");
            }


                var count = turn_hits
               while( count > 0 ){

                    for(var sc = 0; sc < ship_cells.length; sc++){

                        var cell_empty = (ship_cells[sc].textContent == "");

                        if(cell_empty){
                            ship_cells[sc].innerHTML = turn_num;

                            break;
                        }


                    }
                count--;
                }


            }



        //loop through each ship element
        for(var s = 0; s < ships.length; s++){

            for( var turn_key in history){
                var turn_hits = countTurnHits(history[turn_key].hits)
                var ship_name = ship_names[s];
                        console.info("==== history", history[turn_key]);
                        console.info("TURN HITS =====>", turn_hits);

                showTurnHits(turn_hits, ship_name, turn_key );

            }





        }


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

                       // TODO
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