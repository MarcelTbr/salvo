angular.module('PlayerViewModule').service('salvosLogic', function($http){

    this.getSalvosObject = function(gp_id){

        return $http.get("api/games/players/"+gp_id+"/salvos")
        .success(function(response){

                var salvos_dto = response["salvosDTO"];
                                // console.log(" ======= getSalvosObject()======")
                                // console.log(response["salvosDTO"]);
                return salvos_dto;

            })
            .error(function(response){
                console.log("Sorry, request failed...")
                console.log("response: "); console.log(response);
            });

    }

    this.getHitsArray = function(response) {
//                    console.log("getHitsArray()");
//                    console.log("response: "); console.log(response);
                var hits_array = [];
                var salvos_obj = response.data["salvosDTO"]

        for(turn in salvos_obj){
                // console.log("turn: " + turn);
            var turn_hits = salvos_obj[turn]["hits"];
            for(var i=0; i < turn_hits.length; i++){
                // console.log(turn_hits[i]);
                hits_array.push(turn_hits[i]);
            }
        }
        // console.log("hits_array: "); console.log(hits_array);
        return hits_array;
    }


});


