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

    this.getGameOver = function(gpId, callbackFunc){

    $http.get("api/game_over/" + gpId)
        .success(function(response){
            console.info("api/game_over", response);

            callbackFunc(response);

        }).error(function(response){
            console.info("api/game_over", response);
            alert("unable to get GameOver response")
        })



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

            //console.info("enemyHits===>", enemyHits)

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

              var count = turn_hits.length;
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


        //loop through each ship element
        for(var s = 0; s < ships.length; s++){

                var ship_name = ship_names[s];

            for( var turn_key in history){
                var turn_hits = countTurnHits(history[turn_key].hits, ships, s)

                        //console.info("==== history", history[turn_key]);
                        //console.info("TURN HITS =====>", turn_hits);

                showTurnHits(turn_hits, ship_name, turn_key );
                var sunk_ships_arr = history[turn_key].sinks
                showSunkenShips(sunk_ships_arr)
            }



        }

    }



    this.reloadPlayerTables = function(){

            var ship_tables = document.querySelectorAll("#player-history table")

            ship_tables.forEach(function(ship_table){ ship_table.remove()});
            document.getElementById("s1").insertAdjacentHTML('afterend', '<table><tr id="AircraftCarrier" class="ship"><td  class="mock-cell s0"></td><td  class="mock-cell s1"></td><td  class="mock-cell s2"></td><td  class="mock-cell s3"></td><td  class="mock-cell s4"></td></tr></table>')
            document.getElementById("s2").insertAdjacentHTML('afterend', '<table><tr id="Battleship" class="ship"><td  class="mock-cell s0"></td><td  class="mock-cell s1"></td><td  class="mock-cell s2"></td><td  class="mock-cell s3"></td></tr></table>')
            document.getElementById("s3").insertAdjacentHTML('afterend', '<table><tr id="Submarine" class="ship"><td  class="mock-cell s0"></td><td  class="mock-cell s1"></td><td  class="mock-cell s2"></td></tr></table>')
            document.getElementById("s4").insertAdjacentHTML('afterend', '<table><tr id="Destroyer" class="ship"><td  class="mock-cell s0"></td><td  class="mock-cell s1"></td><td  class="mock-cell s2"></td></tr></table>')
            document.getElementById("s5").insertAdjacentHTML('afterend', '<table><tr id="PatrolBoat" class="ship"><td  class="mock-cell s0"></td><td  class="mock-cell s1"></td></tr></table>')

    }

    this.reloadEnemyTables = function(){


    var ship_tables = document.querySelectorAll("#enemy-player-history table")


    ship_tables.forEach(function(ship_table){ ship_table.remove()});
    document.getElementById("E-s1").insertAdjacentHTML('afterend', '<table><tr id="E-AircraftCarrier" class="ship"><td  class="mock-cell s0"></td><td  class="mock-cell s1"></td><td  class="mock-cell s2"></td><td  class="mock-cell s3"></td><td  class="mock-cell s4"></td></tr></table>')
    document.getElementById("E-s2").insertAdjacentHTML('afterend', '<table><tr id="E-Battleship" class="ship"><td  class="mock-cell s0"></td><td  class="mock-cell s1"></td><td  class="mock-cell s2"></td><td  class="mock-cell s3"></td></tr></table>')
    document.getElementById("E-s3").insertAdjacentHTML('afterend', '<table><tr id="E-Submarine" class="ship"><td  class="mock-cell s0"></td><td  class="mock-cell s1"></td><td  class="mock-cell s2"></td></tr></table>')
    document.getElementById("E-s4").insertAdjacentHTML('afterend', '<table><tr id="E-Destroyer" class="ship"><td  class="mock-cell s0"></td><td  class="mock-cell s1"></td><td  class="mock-cell s2"></td></tr></table>')
    document.getElementById("E-s5").insertAdjacentHTML('afterend', '<table><tr id="E-PatrolBoat" class="ship"><td  class="mock-cell s0"></td><td  class="mock-cell s1"></td></tr></table>')




    }


});