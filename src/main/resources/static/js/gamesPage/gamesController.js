angular.module('GamesPageModule', []).controller('GamesController', ['$scope', '$http', '$window',
function($scope, $http, $window) {
                $scope.guest
                $scope.user
                $scope.player;
                $scope.player_games;
                $scope.response;
                $scope.feedback_style;
                $scope.feedback_message;


              $http.get("/api/games")
                .then(function(response){

                    $scope.games_obj = angular.fromJson(response.data);
                    $scope.games = $scope.games_obj.games;

                    if($scope.games_obj.auth == null){
                        $scope.guest = true;
                        $scope.user = false;
                    }else {
                        $scope.guest = false;
                        $scope.user = true;
                        $scope.player = $scope.games_obj.player;
                        $scope.player_games = $scope.games_obj.player_games;
                    }
                    console.log($scope.games_obj);

                })

              $http.get("/api/scores")
                             .then(function(response){

                                 $scope.scores_obj = angular.fromJson(response.data);
                                 $scope.scores_keys = Object.keys($scope.scores_obj);
                                 console.log($scope.scores_obj);
                                 console.log("scores_obj_keys_array: ")
                                 console.log($scope.scores_keys);

                             })

                $scope.successCallback = function(status){ console.log("Sucess!")}

                $scope.errorCallback = function(status) {console.log("ERROR!")}

                $scope.joinGame = function(game_id){

                    $.post("/api/games/"+game_id+"/players").done(function(response){
                        console.log("response:");
                        console.log(response);
                        $window.location.href = "http://" + $window.location.host + "/game.html?gp="+response.gp_id;
                    })
                    .fail(function(response){
                        console.log("Fail response:");
                        console.log(response);
                        alert(response.responseJSON.backend);

                    })

                }

                $scope.createGame= function(){
                        $.post("/api/games").done(function(response){
                        console.log("game created!");
                        console.log(response)
                        $window.location.href = "http://" + $window.location.host + "/game.html?gp="+response.gp_id;
                        })
                        .fail(function(){console.log("sorry, couldn't create game...")})
                }

               $scope.logMeOut = function (){
                    $.post("/app/logout").done(function() { console.log("logged out");
                       $http.get("/api/games")
                         .then(function(response){
                         $scope.games_obj = angular.fromJson(response.data);
                         $scope.games = $scope.games_obj.games;
                                             })
                                          $scope.guest = true;
                                          $scope.user = false;
                     });

                    }

               $scope.checkEmail = function(email){
                     var pat = /^[a-zA-Z0-9.!#$%&’*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\.[a-zA-Z0-9-]+)*$/;
               if(email.match(pat) == email){ return true; } else {  return false; }
               }

               $scope.postNewUser = function(){
                 $.post("api/players", { 'username': $scope.new_username,
                                             'email': $scope.new_email, 'password': $scope.new_password})
                                             .done(function(response){
                                                 console.log(response);
                                                 alert(response.success);
/* $('#feedback').show();
                                                 $scope.response = response;
                                                 $scope.feedback_style= {textAlign: "center", backgroundColor: "green", color: "white"};
                                                 $scope.feedback_message = response.success;*/

                                             })
                                              .fail(function(response){
                                              console.log(response);
                                              alert(response.responseJSON.error)

/*$('#feedback').show();
                                               $scope.response = response;
                                               $scope.feedback_style= {textAlign: "center", backgroundColor: "red", color: "white"};
                                               $scope.feedback_message = response.responseJSON.error;*/


                                              });

               }

               $scope.signMeUp = function(){

                            if($scope.checkEmail($scope.new_email)){
                                    console.log("email is good!");
                                    $scope.postNewUser()
                            }else {
                                    console.log("give in a correct email adress.");
                                    alert("Please give in a correct e-mail adress.");
                                     $window.location.reload();
                            }


               }
               $scope.logMeIn =  function(){
                            $.post("app/login", { username: $scope.username, password: $scope.password})
                            .done(function(){console.log("logged-in!");
                              $scope.user = true;
                              $scope.guest = false;

                              $http.get("/api/games")
                              .then(function(response){
                              $scope.games_obj = angular.fromJson(response.data);
                              $scope.games = $scope.games_obj.games;
                              $scope.player = $scope.games_obj.player;
                              $scope.player_games = $scope.games_obj.player_games;

                                                                })

                            })
                            .fail(function(){console.log("Sorry login failed, try again...")});




                }

                $scope.loginTest = function () {
                        console.log("Testing");
//                    $http.post('/app/login', {"username": 'Jack', "password": 'iamjack'}, { headers: {'Content-Type': 'application/x-www-form-urlencoded'}})
                $http( {method: 'POST', 
                url: '/app/login',
                data: { "username": "Jack", "password": "iamjack"}, 
                headers: {'Content-Type': 'application/x-www-form-urlencoded'} 
                } ).success(function(status){ console.log("angular login succeeded!");   })
                .error(function(status){   console.log("Sorry, angular login failed!")    console.log(status)    })

                }

    }]);

