/* ===================== angular_app.js ====================== */

// $(document).ready(loadData)
(function(){
loadData()
}());

var app = angular.module("GamesApp", []);

  app.controller('GamesController', ['$scope', '$http',  function($scope, $http) {

              $http.get("/api/games")
                .then(function(response){

                    $scope.games_obj = angular.fromJson(response.data);
                    $scope.games =  $scope.games_obj; //angularGameList($scope.games_obj);
                    console.log($scope.games_obj);

                })

    }]);

