angular.module('myService', [])

.factory("myFactoryTest", function(){

     return {

         factoryTest1 : function(){

             console.log("factory test 1" );
             return "Hello";
         },

         factoryTest2 : function () {

            return "factory test 2";
         }

     };


 });

/*
.service('testService', function(){

    this.testMe = function(){

        console.log("test Successful!");
    }


});
*/
ZºW2
ÑÇ´`''''''''23sa`
//pl_view_mod.service('updateGameView', function(){
//
//    this.getShipLocations = function(ships_array){
//
//        var ship_locations = [];
//
//    //looping through all the ships of the game player
//        for(var i = 0; i < ships_array.length; i++){
//                     var ship = ships_array[i].locations;
//                     //looping through each ship's individual location
//                     for(var j = 0; j < ship.length; j++){
//                         ship_locations.push(ship[j]);
//                     }
//        }
//        return ship_locations;
//    }
//
//  this.getEnemies = function (enemies){
//            var all_enemies = [];
//            for (var j= 0; j < enemies.length ; j++){
//                    var enemy_ship = enemies[j].locations;
//                    console.log("enemy_ship"); console.log(enemy_ship)
//                    for(var i = 0; i < enemy_ship.length; i++ ){
//                    all_enemies.push(enemy_ship[i])
//                    }
//
//            }
//            return all_enemies;
//        }
//
//    this.allUserSalvoes = function(salvoes_obj) {
//        var all_salvoes = [];
//         for(turn in salvoes_obj){
//                          var salvo_array = salvoes_obj[turn];
//                          for(var i = 0; i < salvo_array.length; i++){
//                            all_salvoes.push(salvo_array[i]);
//                          }
//         }
//         return all_salvoes;
//    }
//
//    this.getHitsArray = function (all_salvoes, enemies) {
//            var hits = [];
//          //looping through all_salvoes array
//              for(var i = 0; i < all_salvoes.length; i++) {
//    //                console.log("salvo");
//    //                console.log(all_salvoes[i]);
//                for(var j = 0; j < enemies.length; j++){
//    //                console.log("enemy")
//    //                console.log(enemies[j]);
//                    if(all_salvoes[i] == enemies[j]){ console.log("hit!"); hits.push(all_salvoes[i]) }
//                }
//              }
//        return hits;
//    }
//
//    this.getUserPlayer = function (url_gp_id, players_array){
//        var gp_id = players_array[0].gp_id;
//        if(gp_id == url_gp_id){ return players_array[0]}
//        else { return players_array[1]}
//    }
//
//    this.getEnemyPlayer = function (url_gp_id, players_array){
//     var gp_id = players_array[0].gp_id;
//     if(gp_id != url_gp_id){ return players_array[0]}
//     else { return players_array[1]}
//    }
//
//
//  });