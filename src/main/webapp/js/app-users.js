
var users = angular.module('users', ['ngRoute', 'ngSanitize', 'ngResource']);

var urlPrefix = '';

users.factory('UsersFactory', function($resource) {
    return $resource('/users', {}, {
        query: { method: 'GET', isArray: true }
    })
});

users.factory('UsersFactory', function($resource) {
    return $resource('/users/:id', {}, {
        show: { method: 'GET' , params: {id: '@id'} },
        update: { method: 'PUT' , params: {id: '@id'} },
        delete: { method: 'DELETE' , params: {id: '@id'} }
    })
});

// Routes
users.config(function($routeProvider) {
    $routeProvider.when('/adminUsers', {templateUrl: urlPrefix + 'admin-users.html', controller: 'UserListController'});
    $routeProvider.when('/adminNewUser', {templateUrl: urlPrefix + 'admin-users-new.html', controller: 'UserAddController'});
    $routeProvider.when('/adminEditUser/:id', {templateUrl: urlPrefix + 'admin-users-edit.html', controller: 'UserEditController'});
});

// Services
users.service('userService', function(UsersFactory, $q) {
    var nextPk = 7;
    var allSelected = false;
	var list = undefined;

    this.indexForPK = function(pk) {
        for (var i = 0; i < list.length; i++) {
            var next = list[i];
            if (next.user_pk == pk) {
                return i;
            }
        }
        return -1;
    }
    this.selectAll = function(value) {
        for (var i = 0; i < list.length; i++) {
            list[i].selected = value;
        }
    }
    this.removeSelected = function() {
        numSelected = 0
        var itemsToDelete = ""
        for (var i = list.length - 1; i >= 0; i--) {
            if (list[i].selected) {
                if (numSelected > 0) {
                    itemsToDelete = itemsToDelete + ","
                }
                itemsToDelete = itemsToDelete + list[i].user_pk;
				list.splice(i, 1);
                numSelected = numSelected + 1;
            }
        }
        var deferred = $q.defer();
        UsersFactory.delete({ id: itemsToDelete });
        console.log("Deleted users: " + itemsToDelete);
        deferred.resolve();
        return deferred.promise;
    }
    this.getAll = function() {
        if (list === undefined) {
            list = UsersFactory.query(function(results) {
                //data saved. do something here.
                console.log("User list response: " + results.length);
            });
        }
        return list;
    }
    this.refreshAll = function() {
        list = UsersFactory.query(function(results) {
            //data saved. do something here.
            console.log("User list response: " + results.length);
        });
        return list;
    }
    this.getItem = function(pk) {
        return UsersFactory.show({ id: pk });
    }
    this.addItem = function(item) {
        var deferred = $q.defer();
        var result = UsersFactory.save(item, function(pk) {
            deferred.resolve(pk.primaryKey);
        });
        return deferred.promise;
    }
    this.removeItem = function(pk) {
        var deferred = $q.defer();
        UsersFactory.delete({ id: pk }, function(pk) {
            deferred.resolve(pk);
        });
        return deferred.promise;
    }
    this.update = function(item) {
        var deferred = $q.defer();
        UsersFactory.update(item, function(pk) {
            deferred.resolve(pk.primaryKey);
        });
        return deferred.promise;
    }
    this.size = function() { return list.length; }
    this.isAllSelected = function() { return list.allSelected; }
});

// Cookbook Controllers
users.controller('UserListController', function ($scope, $rootScope, mainService, userService, loginService) {
    $rootScope.headerDisplay = "display: block;";
    $rootScope.bodyBackground = "";
    $rootScope.lastPage = '/adminUsers';
    $scope.userList = userService.refreshAll();
    $scope.allSelected = userService.isAllSelected();

    $scope.edit = function (id) {
        $rootScope.goto('/adminEditUser/' + id);
    };

    $scope.selectAll = function () {
        userService.selectAll($scope.allSelected);
    };

    $scope.removeSelected = function () {
        userService.removeSelected().then(
            function(result) {
                mainService.setStatusBarText('Successfully deleted the selected users.');
                $rootScope.goto('/adminUsers');
            }, function(reason) {
                mainService.setStatusBarText('Failed to delete the selected users. "' + reason + '".');
            }
        );
    };
});

users.controller('UserEditController', function ($scope, $rootScope, $routeParams, mainService, userService) {
    $scope.userToEdit = userService.getItem($routeParams.id);
    $scope.backup = angular.copy($scope.userToEdit);

    $scope.update = function () {
        userService.update($scope.userToEdit).then(
            function(pk) {
                console.log("Updated user: " + $scope.userToEdit.name);
                mainService.setStatusBarText('Successfully updated user "' + $scope.userToEdit.username + '".');
                $rootScope.goto('/adminUsers');
            }, function(reason) {
                mainService.setStatusBarText('Failed to update user. "' + reason + '".');
            }
        );
    };

    $scope.cancel = function () {
        $scope.userToEdit.username = $scope.backup.username;
        $rootScope.goto('/adminUsers');
    };

    $scope.delete = function () {
        userService.removeItem($scope.userToEdit.user_pk).then(
            function(pk) {
                console.log("Removed user: '" + $scope.userToEdit.username + "'.");
                mainService.setStatusBarText('Successfully deleted user "' + $scope.userToEdit.username + '".');
                $rootScope.goto('/adminUsers');
            }, function(reason) {
                mainService.setStatusBarText('Failed to delete user. "' + reason + '".');
            }
        );
    };

});

users.controller('UserAddController', function ($scope, $rootScope, $routeParams, mainService, userService) {
    $scope.userToAdd = {user_pk: '', username: '', fname: '', lname: '', password: '', login_enabled: ''};

    $scope.addItem = function () {
        userService.addItem($scope.userToAdd).then(
            function(pk) {
                console.log("Added user: " + pk + " - " + $scope.userToAdd.username);
                mainService.setStatusBarText('Successfully added user "' + $scope.userToAdd.username + '".');
                $rootScope.goto('/adminUsers');
            }, function(reason) {
                mainService.setStatusBarText('Failed to add user. "' + reason + '".');
            }
        );
    };

    $scope.cancel = function () {
        $rootScope.goto('/adminUsers');
    };
});

angular.element(document).ready(function() {
    angular.bootstrap(document.getElementById('users'), ['users']);
});