var main = angular.module('main', ['cookbooks', 'foodCategories', 'meals', 'login', 'recipes', 'users', 'ngRoute', 'ngSanitize']);
 
// Main Service
main.service('mainService', function() {
    var statusBarText = 'Welcome to the Shoemake Home Management application.';
	this.setStatusBarText = function(text) {
	    statusBarText = text;
		document.getElementById("footer").innerHTML = '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;' + text;	
	}

	this.getStatusBarText = function() {
	    return statusBarText;
	}

    // Example: bootstrapAlert('info', '<message>')
    // Example: bootstrapAlert('error', '<message>')
    // Example: bootstrapAlert('success', '<message>')
    this.mainAlert = function (type, message) {
        $('#alertHome').html('<div class="alert alert-' + type + ' alert-dismissable"><button type="button" class="close" data-dismiss="alert" aria-hidden="true">&times;</button>' + message + '</div>')
    }

    this.loginAlert = function (type, message) {
        $('#loginAlert').html('<div class="alert alert-' + type + ' alert-dismissable"><button type="button" class="close" data-dismiss="alert" aria-hidden="true">&times;</button>' + message + '</div>')
    }

    this.bootstrapAlert = function (type, message, timeout) {
        $('#alertHome').html('<div class="alert alert-' + type + '">' + message + '</div>')
        if (timeout || timeout === 0) {
            setTimeout(function() {
                $('#alertHome').alert('close');
            }, timeout);
        }
    }

    this.hideAlert = function() {
        var alert = $(".alert");
        if (alert != null) {
            alert.remove();
        }
    }

});

// Main App Controllers
main.controller('AppController', function ($scope, $rootScope, $route, $location, mainService, loginService) {
	$scope.$route = $route;
    $rootScope.statusBarText = mainService.getStatusBarText();
    $rootScope.headerDisplay = "display: blocked;";
    $rootScope.bodyClass = "login";
    $rootScope.lastPage = '/adminCookbooks';

    $scope.showLogin = function() {
        $rootScope.headerDisplay = "display: none;";
        $rootScope.bodyClass = "login";
        $scope.password = '';
        $location.path('/login');
    }

    $scope.logout = function () {
        loginService.logout();
        $scope.showLogin();
        mainService.setStatusBarText("You were logged out successfully.");
        mainService.hideAlert();
        mainService.mainAlert("success", "You were logged out successfully.  Login again to continue working.");
    };

    $scope.setup = function () {
        loginService.logout();
        $scope.showLogin();
    };

    $rootScope.goto = function (path) {
        mainService.hideAlert();
        $rootScope.lastPage = path;
        if (loginService.sessionExpired()) {
            mainService.bootstrapAlert('info', 'Your session has timed out. Please login again.');
            $scope.showLogin();
        } else {
            $rootScope.headerDisplay = "display: blocked;";
            $rootScope.bodyClass = "main";
            $location.path(path);
        }
	};

    $rootScope.gotoLast = function() {
        $rootScope.headerDisplay = "display: blocked;";
        $rootScope.bodyClass = "main";
        //$('#alertHome').alert('close');
        $location.path($rootScope.lastPage);
    };

    $scope.setup();

});
