/* ===================== angular_app.js ====================== */

//function to return an object from a query string i.e. location.search
function paramObj(search) {
  var obj = {};
  var reg = /(?:[?&]([^?&#=]+)(?:=([^&#]*))?)(?:#.*)?/g;

  search.replace(reg, function(match, param, val) {
    obj[decodeURIComponent(param)] = val === undefined ? "" : decodeURIComponent(val);
  });

  return obj;
}


//Angular Code

var app = angular.module('App', [ 'PlayerViewModule', 'GamesPageModule' ], function($locationProvider){
    $locationProvider.html5Mode(true);
});

 var pl_view_mod = angular.module('PlayerViewModule', []);

