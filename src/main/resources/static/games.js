/* =================== salvo.js =============== */

 $(document).ready(loadData)




  // display text in the output area
  function showOutput(text) {
    $('#game_list').text(text);
  }

  //create an ol and fill it with the game objects
  function makeGameList(json) {

    /*var game_1 = JSON.stringify(json[0], null, 2);
    var game_2 = JSON.stringify(json[1], null, 2);
    var game_3 = JSON.stringify(json[2], null, 2);*/

    var game_date1 = json[0].game_date;
    var game_date2 = json[1].game_date;
    var game_date3 = json[2].game_date;

    var player1 = json[0].game_players[0].player_username;
    var player2 = json[0].game_players[1].player_username;
    var player3 = json[1].game_players[0].player_username;
    var player4 = json[1].game_players[1].player_username;
    var player5 = json[2].game_players[0].player_username;
    var player6 = json[2].game_players[1].player_username;

    var inline_game_1 =
         '   <li> <b>Game:</b> ' + game_date1 +
         ' | <b>Blue Squadron:</b> ' + player1 +
         ' | <b>Red Squadron:</b> ' + player2 + '</li>';

    var inline_game_2 =
             '   <li> <b>Game:</b> ' + game_date2 +
             ' | <b>Blue Squadron:</b> ' + player3 +
             ' | <b>Red Squadron:</b> ' + player4 + '</li>';

    var inline_game_3 =
                   '   <li> <b>Game:</b> ' + game_date3 +
                   ' | <b>Blue Squadron:</b> ' + player5 +
                   ' | <b>Red Squadron:</b> ' + player6 + '</li>';


    $('#games_list').html( inline_game_1 + inline_game_2 + inline_game_3 );

    var game1_title = '<li>' + '<u><b>Game1:</b></u>' + '</li>';
    var game1_list = document.createElement('ul');
    game1_list.setAttribute('id', 'game1_list');



    $('#games_list').append(game1_list);
    $('#game1_list').append(game1_title)
    .append('<li> <b>Creation Date:</b> ' + game_date1)
    .append('<li> <b>Blue Squadron:</b> ' + player1)
    .append('<li> <b>Red Squadron:</b> ' + player2);


    var game2_title = '<li>' + '<u><b>Game2:</b></u>' + '</li>';
    var game2_list = document.createElement('ul');
    game2_list.setAttribute('id', 'game2_list');



    $('#games_list').append(game2_list);
    $('#game2_list').append(game2_title)
    .append('<li> <b>Creation Date:</b> ' + game_date2)
    .append('<li> <b>Blue Squadron:</b> ' + player3)
    .append('<li> <b>Red Squadron:</b> ' + player4);

    var game3_title = '<li>' + '<u><b>Game3:</b></u>' + '</li>';
    var game3_list = document.createElement('ul');
    game3_list.setAttribute('id', 'game3_list');


    $('#games_list').append(game3_list);
    $('#game3_list').append(game3_title)
    .append('<li> <b>Creation Date:</b> ' + game_date3)
    .append('<li> <b>Blue Squadron:</b> ' + player5)
    .append('<li> <b>Red Squadron:</b> ' + player6);


  }


   // load and display JSON sent by server for /players

  function loadData() {
    $.get("/api/games")
    .done(function(data) {
      //showOutput(JSON.stringify(data, null, 2));
      makeGameList(data);

    })
    .fail(function( jqXHR, textStatus ) {
      showOutput( "Failed: " + textStatus );
    });
  }