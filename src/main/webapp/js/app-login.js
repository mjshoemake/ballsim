
var login = angular.module('login', ['ngRoute', 'ngSanitize', 'ngResource']);
 
var urlPrefix = '';

login.factory('LoginFactory', function($resource) {
    return $resource('/login', {}, {
        create: { method: 'POST', params: {type: 'create'} }
    })
});

// Routes
login.config(function($routeProvider) {
    $routeProvider.when('/adminCookbooks', {templateUrl: urlPrefix + 'admin-cookbooks.html', controller: 'CookbookListController'});
    $routeProvider.when('/login', {templateUrl: urlPrefix + 'login.html', controller: 'LoginController'});
    $routeProvider.otherwise({redirectTo: '/adminCookbooks'});
});

// Login Service
login.service('loginService', function(mainService) {
    var username = '';
    var loginTime = 0;
    var timeoutDuration = 1000 * 60 * 20; // 20 minutes

    this.bootstrapAlert = function (type, message) {
        $('#alertHome').html('<div class="alert alert-' + type + ' alert-dismissable"><button type="button" class="close" data-dismiss="alert" aria-hidden="true">&times;</button>' + message + '</div>')
    }

    this.sessionExpired = function() {
        if (loginTime == 0) {
            username = '';
            mainService.setStatusBarText("Your session has timed out.");
            return true;
        } else {
            var now = new Date().getTime();
            var elapsedTime = now - loginTime;
            if (elapsedTime > timeoutDuration) {
                mainService.setStatusBarText("Your session has timed out.");
                mainService.hideAlert();
                mainService.mainAlert("info", "Your session has timed out. Login again to continue working.");
                username = '';
                return true;
            } else {
                loginTime = new Date().getTime();
                return false;
            }
        }
    }

    this.login = function(pUsername, pPassword) {
        if ((pUsername == 'mjshoemake' && pPassword == 'qwe`123') ||
            (pUsername == 'mjshoemake' && pPassword == 'Iwjptdm1') ||
            (pUsername == 'mrshoemake' && pPassword == 'Iwjptdm1') ||
            (pUsername == 'mrshoemake' && pPassword == 'qwe`123')) {
            // Valid login.
            username = pUsername;
            loginTime = new Date().getTime();
            mainService.hideAlert();
            return true;
        } else {
            username = '';
            loginTime = 0;
            return false;
        }
    }

    this.logout = function() {
        username = '';
        loginTime = 0;
    }
});

// Login Controller
login.controller('LoginController', function ($scope, $rootScope, $route, $location, mainService, loginService) {
    $scope.$route = $route;
    $scope.username = '';
    $scope.password = '';
    $rootScope.headerDisplay = "display: none;";

    $scope.show = function() {
        $rootScope.headerDisplay = "display: none;";
        $rootScope.bodyBackground = "images/background.jpg";
        $scope.password = '';
        $location.path('/login.html');
    }

    $scope.login = function () {
        mainService.setStatusBarText("Validating credentials...");
        var success = loginService.login($scope.username, $scope.password);
        if (success) {
            $rootScope.headerDisplay = "display: blocked;";
            $rootScope.bodyBackground = "";
            //$rootScope.gotoLast();
            mainService.setStatusBarText("You have logged in successfully.  Welcome!");
            $rootScope.gotoLast();
        } else {
            //show();
            mainService.setStatusBarText("Your login credentials do not match our records.  Please try again.");
            $location.path('/login');
        }
    };

});

angular.element(document).ready(function() { 
	angular.bootstrap(document.getElementById('login'), ['login']);
});
