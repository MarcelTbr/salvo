/* === This is Just a Testing Purpose Service ==== */

angular.module('PlayerViewModule')
.factory("myFactoryTest", function(){

     return {

         factoryTest1 : function(){

             console.log("factory test 1" );
             return "Hello";
         },

         factoryTest2 : function () {

            return "factory test 2";
         },

          showShip : function(row, col){
                                     console.log("Ship Types:")
                                     console.log($scope.ship_types);
                                     var grid_cell;
                                     if(grid_cell != row+col){ //check if the cell is different from the previous one
                                         $scope.prov_ship_loc = [];  //resets the global variable for ship locations
                                         $scope.prov_ship_loc.push(row+col);
                                         if($scope.ship_align == "H"){
                                                 for(var i = 1; i <= $scope.selected_ship["cells"]; i++){

                                                    $scope.prov_ship_loc.push(row+(col++));
                                                 }
                                             grid_cell = row+col;
                                         }
                                     }
                                 }


     };


 });

