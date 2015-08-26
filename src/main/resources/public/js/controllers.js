angular.module('secureRestSample', []).
    config(function ($routeProvider, $locationProvider) {
        $locationProvider.html5Mode(false);

        $routeProvider
            .when('/',{ templateUrl: '/templates/play.html', controller: PlayCtrl });
    })

function RouteCtrl($scope, $http, $location) {
    $scope.isAuthenticated = false;
    $scope.user = undefined;

    function setAuthenticationStatus(isAuthenticated, user) {
        $scope.isAuthenticated = isAuthenticated;
        $scope.user = user;
    }

    function checkUserAuthentication() {
        $http.get("/api/authentication/status")
            .success(function (data) {
                setAuthenticationStatus(data.authenticated, data.user);
            })
            .error(function () {
                setAuthenticationStatus(false, undefined);
            });
    }

    checkUserAuthentication();
}




function PlayCtrl($scope, $http, $timeout) {
	$scope.state = "";
	
    $scope.noGame = true;
    $scope.hasGame = false;
    $scope.waitingForPlayer=false;
    $scope.gameUri = null;
    $scope.needToDeal = false;
    $scope.youWon = false;
    $scope.youLost = false;
    $scope.playing = false;
    $scope.roundsToWin = 2;
    
    
    $scope.getGameInfo = function() {
    	$http.get($scope.gameUri)
        .success(function (data, status, headers) {
        	alert(data);
        })
        .error(function (data, status) {
            alert(data);
        });
    };


    var self = this;
    var startPolling = function(){
      function poll(){
    	  $http.get($scope.gameUri)
          .success(function (data, status, headers) {
            $scope.currentGame = data;
        	  $timeout(poll, 1000);
          })
          .error(function (data, status) {
              alert(data);
          });
    	  
    	  
      };
      poll();
    };
    
    
    $scope.createGame = function()
    {
        $http.post("/v1/games", null , {headers: {'SimpleIdentity': $scope.playerName}})
        .success(function (data, status, headers) {
        	$scope.hasGame = true;
        	
        	$scope.gameUri = headers("Location");
            $scope.noGame = false;

            $http.get($scope.gameUri).success(function(data) {
                $scope.currentGame = data;
                startPolling();
            });


        })
        .error(function (data, status) {
            alert(data);
        });
    };
    
    
    $scope.deal = function(choice)
    {
       	$http.post($scope.gameUri, {move: $scope.choice.toLowerCase()}, {headers: {'SimpleIdentity': $scope.playerName}})
        .success(function (data, status, headers) {
        	alert("You've dealt a blow!");
        })
        .error(function (data, status) {
            alert(data);
        });
    };
    
    $scope.join = function() {
        $scope.gameUri = "/v1/games/" + $scope.gameId;
        startPolling();
    }
    
}
