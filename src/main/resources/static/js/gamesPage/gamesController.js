angular.module('GamesPageModule', []).controller('GamesController', ['$scope', '$http', '$window', '$interval',
    function ($scope, $http, $window, $interval) {

        // [1] Initialize Variables
        $scope.guest
        $scope.user
        $scope.player;
        $scope.player_games;
        $scope.response;
        $scope.feedback_style;
        $scope.feedback_message;


        $scope.getGames = function () {
            $http.get("/api/games")
                .then(function (response) {

                    $scope.games_obj = angular.fromJson(response.data);
                    $scope.games = $scope.games_obj.games;

                    if ($scope.games_obj.auth == null) {
                        $scope.guest = true;
                        $scope.user = false;
                    } else {
                        $scope.guest = false;
                        $scope.user = true;
                        $scope.player = $scope.games_obj.player;
                        $scope.player_games = $scope.games_obj.player_games;
                    }
                    console.info("$scope.games_obj", $scope.games_obj);

                })
        }

        // [2] Get Objects from Backend
        $scope.getGames();

        $scope.getScores = function () {
            $http.get("/api/scores")
                .then(function (response) {

                    $scope.scores_obj = angular.fromJson(response.data);
                    //$scope.scores_keys = Object.keys($scope.scores_obj);
                    //console.log($scope.scores_obj);


                })
        }

        $scope.getScores();
        //refresh games and scores list every 5 seconds
        $interval(function () {
            $scope.getGames(), $scope.getScores()
        }, 5000)

        // LEGACY: use default Callback logging or remove
        $scope.successCallback = function (status) {
            console.log("Sucess!")
        }

        $scope.errorCallback = function (status) {
            console.log("ERROR!")
        }

        $scope.joinGameById = function () {

            var game_id = prompt("Please enter the id of the game you would like to join", "Game id");

            $scope.joinGame(game_id);


        }

        $scope.joinGame = function (game_id) {

            $.post("/api/games/" + game_id + "/players").done(function (response) {
                console.log("response:");
                console.log(response);
                $window.location.href = "http://" + $window.location.host + "/game.html?gp=" + response.gp_id;
            })
                .fail(function (response) {
                    console.log("Fail response:");
                    console.log(response);
                    alert(response.responseJSON.backend);

                })

        }

        $scope.createGame = function () {
            $.post("/api/games").done(function (response) {
                console.log("game created!");
                console.log(response)
                $window.location.href = "http://" + $window.location.host + "/game.html?gp=" + response.gp_id;
            })
                .fail(function () {
                    console.log("sorry, couldn't create game...")
                })
        }

        $scope.logMeOut = function () {
            $.post("/app/logout").done(function () {
                console.log("logged out");
                $http.get("/api/games")
                    .then(function (response) {
                        $scope.games_obj = angular.fromJson(response.data);
                        $scope.games = $scope.games_obj.games;
                    })
                $scope.guest = true;
                $scope.user = false;
            });
            $scope.new_password = "new password";
            $scope.username = "";
            $scope.password = "";
        }

        $scope.checkEmail = function (email) {
            var pat = /^[a-zA-Z0-9.!#$%&’*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\.[a-zA-Z0-9-]+)*$/;
            if (email.match(pat) == email) {
                return true;
            } else {
                return false;
            }
        }

        $scope.postNewUser = function () {
            $.post("api/players", {
                'username': $scope.new_username,
                'email': $scope.new_email, 'password': $scope.new_password
            })
                .done(function (response) {
                    console.log(response);
                    alert(response.success);
                    /* $('#feedback').show();
                     $scope.response = response;
                     $scope.feedback_style= {textAlign: "center", backgroundColor: "green", color: "white"};
                     $scope.feedback_message = response.success;*/
                    logIn($scope.new_username, $scope.new_password);
                    //reset signup variables
                    $scope.new_username = "";
                    $scope.new_email = "";

                })
                .fail(function (response) {
                    console.log(response);
                    alert(response.responseJSON.error)

                    /*$('#feedback').show();
                   $scope.response = response;
                   $scope.feedback_style= {textAlign: "center", backgroundColor: "red", color: "white"};
                   $scope.feedback_message = response.responseJSON.error;*/

                });

        }

        function logIn(username, password) {

            $.post("app/login", {username: username, password: password})
                .done(function () {
                    console.log("logged-in!");
                    $scope.user = true;
                    $scope.guest = false;
                    // load games again to show logged player games on top
                    $scope.getGames();

                })
                .fail(function () {

                    console.warn("Sorry login failed, try again...");
                    alert("Sorry login failed, try again...")
                });
        }

        $scope.signMeUp = function () {

            if ($scope.checkEmail($scope.new_email)) {
                console.log("email is good!");
                $scope.postNewUser()

                //reset New Player Signup button visibility
                document.getElementById("signup").style.visibility = "hidden";
                document.querySelector("#new-pl-signup button").style.visibility = "visible";


            } else {
                console.log("give in a correct email adress.");
                alert("Please give in a correct e-mail adress.");
                $window.location.reload();
            }

        }


        $scope.logMeIn = function () {
            $.post("app/login", {username: $scope.username, password: $scope.password})
                .done(function () {
                    console.log("logged-in!");
                    $scope.user = true;
                    $scope.guest = false;
                    // load games again to show logged player games on top
                    $scope.getGames();

                })
                .fail(function () {

                    console.warn("Sorry login failed, try again...");
                    alert("Sorry login failed, try again...")
                });


        }

        $scope.loginTest = function () {
            console.log("Testing");
            //    $http.post('/app/login', {"username": 'Jack', "password": 'iamjack'}, { headers: {'Content-Type': 'application/x-www-form-urlencoded'}})
            $http({
                method: 'POST',
                url: '/app/login',
                data: {"username": "Jack", "password": "iamjack"},
                headers: {'Content-Type': 'application/x-www-form-urlencoded'}
            }).success(function (status) {
                console.log("angular login succeeded!");
            })
                .error(function (status) {
                    console.log("Sorry, angular login failed!")
                    console.log(status)
                })

        }


        $scope.showSignup = function () {


            document.getElementById("signup").style.visibility = "visible";

            document.querySelector("#new-pl-signup button").style.visibility = "hidden";
        }


        $scope.gameOver = function(gameOver){
                    if (!gameOver){ return false;}
                    else { return true;}

        }

    }]);

