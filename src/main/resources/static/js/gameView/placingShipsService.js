angular.module('PlayerViewModule').service('placingShips', function($http){

  this.makeShip = function(prov_ship_loc, ship_type){
        ship_obj = {}
        ship_obj["shipType"] = ship_type;
        ship_obj["shipLocations"] = prov_ship_loc;
        return ship_obj;
  }

  this.saveShips = function(ship_obj_array, gp_id){
    console.log("Ships Saved:"); console.log(ship_obj_array);
    $http.post( "/api/games/players/"+gp_id+"/ships", ship_obj_array)
  }
  //TODO: use it or remove it
  this.legalCol = function(cell_data){
           var cell_num = Number(cell_data.slice(1,3));
           console.log("cell_num: " + cell_num);
           var col_num = 10;
           if((cell_num <= col_num) && (cell_num > 0)){ return true;} else { return false;}
   }

  this.legalPosition = function(grid_cell, ship_list){
        //        console.log("legalPositon : " + grid_cell)
        var is_legal;
        var list_full = typeof ship_list != 'undefined'; //make sure ship_list is defined and has data
        if(list_full){
              for(var i = 0; i < ship_list.length; i++){ //iterating through each ship
                             var ship_loc_list = ship_list[i]["shipLocations"];

                             for(var j = 0; j < ship_loc_list.length; j++){ //iterating through all ship locations

                                 if(ship_loc_list[j] == grid_cell){ //if the location overlaps it's illegal
                                        //  console.log(grid_cell + " is an illegal position!")
                                        //  console.log("ILLEGAL>>cell: " + grid_cell+ " == " + ship_loc_list[j] )
                                     return false;
                                 }else { is_legal = true;}
                             }

              } //end ship_list iteration

                    return is_legal;

         } else { return true;} //there are no placed ships, all positions are legal
 }

  this.shipOverlapping = function(prov_ship_loc, placed_ship_loc_array){
    var ship_overlapping;
    var prov_ships = typeof prov_ship_loc != 'undefined';
    var array_full = placed_ship_loc_array.length > 0;
    function sameLocation(location){
        return location == prov_ship_loc[i];
    }
    if( prov_ships & array_full) {
     console.log("overlapping time!")
     for(var i = 0; i < prov_ship_loc.length; i++){
        var overlapping_locs = placed_ship_loc_array.filter(sameLocation) //find overlapping locations
                //console.log("prov_ship_loc [" + i + "]");console.log(overlapping_locs);
        if(overlapping_locs.length > 0) {
        console.log("ship_overlapping: true")
        return true; } else { ship_overlapping = false}
     }
     return ship_overlapping;
    } else { return false;}
  }

  this.insideGrid = function(prov_ship_loc){

    function getCols(location){
         return Number(location.slice(1,3));
    }
    function getRows(location){
         return location.slice(0,1);
    }
    function wrongCol(col){
         return col > 10;
    }
    function wrongRow(row){
        return row.charCodeAt(0) > 74; //check if row is over "J"
    }
    var ship_cols_array = prov_ship_loc.map(getCols);// console.log("ship_cols_array: "); console.log(ship_cols_array);
    var ship_rows_array = prov_ship_loc.map(getRows); //console.log("ship_rows_array: "); console.log(ship_rows_array);

    var illegal_cols = ship_cols_array.filter(wrongCol); //console.log("wrong_cols: "); console.log(illegal_cols);
    var illegal_rows = ship_rows_array.filter(wrongRow); //console.log("wrong_rows: "); console.log(illegal_rows);

    if(illegal_cols.length > 0 || illegal_rows.length > 0) { return false;} else {return true;}

  }

  this.getPlacedShipLocArray = function (placed_ship_list) {
       var placed_ship_loc_array = [];

       var list_full = typeof placed_ship_list != 'undefined'; //make sure ship_list is defined and has data
              if(list_full){
                    for(var i = 0; i < placed_ship_list.length; i++){ //iterating through each ship
                                   var ship_loc_list = placed_ship_list[i]["shipLocations"];

                                   for(var j = 0; j < ship_loc_list.length; j++){ //iterating through all ship locations

                                      placed_ship_loc_array.push(ship_loc_list[j]);
                                   }

                    } //end ship_list iteration

                          return placed_ship_loc_array;

               } else { return placed_ship_loc_array;} //there are no placed ships, all positions are legal

  }

  //this method is legacy. Currently using a better one with less params.
  this.insideGridTwo = function ( prov_ship_loc, ship_align){
            var is_inside;
            //if ship_align == 'H'  ==>> iterate locs. // condition col <= 10
            if(ship_align == 'Horizontal'){
                for(var i = 0; i < prov_ship_loc.length; i++){
                    var loc_col = Number(prov_ship_loc[i].slice(1,3));
                    if ( loc_col <= 10) { is_inside = true;} else { return false;}
                }
                return is_legal;
            }
            //if ship_align == 'V' ===>> iterate locs. // condition row.charCodeAt(0) <= 74 // "J"
            if(ship_align == 'Vertical') {
                for(var i = 0; i < prov_ship_loc.length; i++){
                    var loc_row = prov_ship_loc[i].slice(0,1);
                    var loc_char = loc_row.charCodeAt(0);
                    if ( loc_char <= 74) { is_inside = true;} else { return false;}
                }
                return is_inside;
            }
  }

  this.paintFirstShip = function(prov_ship_loc, cell_data){

          for (var i = 0; i < prov_ship_loc.length; i++){ //iterating through the mouseover ship locations
                               var provisional_ship = typeof prov_ship_loc != 'undefined';
                               if(provisional_ship && prov_ship_loc[i] === cell_data){

                                    return {'background-color': 'lightyellow'}
                                    }
                               }
  }

  /*this.paintNextShip = function (prov_ship_loc, ship_list, cell_data, ship_legal){
          for (var i = 0; i < prov_ship_loc.length; i++){ //iterating through the mouseover ship locations

                   var provisional_ship = typeof prov_ship_loc != 'undefined';
                   if(provisional_ship && prov_ship_loc[i] === cell_data){
                       if (ship_legal){
                        return {'background-color': 'lightyellow'}
                        }else {
                            return {'background-color': 'red'}
                       }
                   }

          }
    }*/

  //returns an array of locations to show in front end, based on cell data & ship data (type, num of cells, alignment)
  this.getProvShipLoc = function(row, col, selected_ship, ship_align){
                            var grid_cell;
                            if(grid_cell != row+col){ //check if the cell is different from the previous one
                                var prov_ship_loc = [];  //resets the global variable for ship locations

                                if(ship_align == "Horizontal"){
                                        for(var i = 1; i <= selected_ship["cells"]; i++){

                                           prov_ship_loc.push(row+(col++));
                                        }
                                    grid_cell = row+col;
                                    return prov_ship_loc;
                                }

                                if (ship_align == "Vertical"){
                                     var char = row.charCodeAt(0);
                                     for(var i = 1; i <= selected_ship["cells"]; i++){
                                           var row_char = String.fromCharCode(char);
                                           prov_ship_loc.push(row_char+col);
                                           char++;
                                        }
                                    grid_cell = row+col;
                                    return prov_ship_loc;
                                }
                            }
                        }

});