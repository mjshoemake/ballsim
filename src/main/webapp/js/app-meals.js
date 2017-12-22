
var meals = angular.module('meals', ['ngRoute', 'ngSanitize', 'ngResource']);
 
var urlPrefix = '';

meals.factory('MealsFactory', function($resource) {
    return $resource('/meals', {}, {
        query: { method: 'GET', isArray: true }
    })
});

meals.factory('MealsFactory', function($resource) {
    return $resource('/meals/:id', {}, {
        show: { method: 'GET' , params: {id: '@id'} },
        update: { method: 'PUT' , params: {id: '@id'} },
        delete: { method: 'DELETE' , params: {id: '@id'} }
    })
});

// Routes
meals.config(function($routeProvider) {
	$routeProvider.when('/adminMeals', {templateUrl: urlPrefix + 'admin-meals.html', controller: 'MealListController'});
	$routeProvider.when('/adminNewMeal', {templateUrl: urlPrefix + 'admin-meals-new.html', controller: 'MealAddController'});
	$routeProvider.when('/adminEditMeal/:id', {templateUrl: urlPrefix + 'admin-meals-edit.html', controller: 'MealEditController'});
});

// Services
meals.service('mealService', function(MealsFactory, $q) {
	var nextPk = 7;
	var allSelected = false;
	var list = undefined;

	this.indexForPK = function(pk) {
		for (var i = 0; i < list.length; i++) {
			var next = list[i];
			if (next.meals_pk == pk) {
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
        numSelected = 0;
        var itemsToDelete = "";
        for (var i = list.length - 1; i >= 0; i--) {
            if (list[i].selected) {
                if (numSelected > 0) {
                    itemsToDelete = itemsToDelete + ",";
                }
                itemsToDelete = itemsToDelete + list[i].meals_pk;
				list.splice(i, 1);
                numSelected = numSelected + 1;
            }
        }

        var deferred = $q.defer();
        MealsFactory.delete({ id: itemsToDelete });
        console.log("Deleted meals: " + itemsToDelete);
        deferred.resolve();
        return deferred.promise;
    }
    this.getAll = function() {
        list = MealsFactory.query(function(results) {
            //data saved. do something here.
            console.log("Meal list count: " + results.length);
        });
        return list;

    }
    this.getItem = function(pk) {
        return MealsFactory.show({ id: pk });
    }
    this.addItem = function(item) {
        var deferred = $q.defer();
        var result = MealsFactory.save(item, function(pk) {
            deferred.resolve(pk.primaryKey);
        });
        return deferred.promise;
    }
    this.removeItem = function(pk) {
        var deferred = $q.defer();

        MealsFactory.delete({ id: pk }, function(pk) {
            deferred.resolve(pk);
        });
        return deferred.promise;
    }
    this.update = function(item) {
        var deferred = $q.defer();
        MealsFactory.update(item, function(msg) {
            deferred.resolve(msg);
        });
        return deferred.promise;
    }
    this.size = function() { return list.length; }
    this.isAllSelected = function() { return list.allSelected; }
});

// Cookbook Controllers
meals.controller('MealListController', function ($scope, $rootScope, mainService, mealService, loginService) {
    $rootScope.headerDisplay = "display: block;";
    $rootScope.bodyBackground = "";
    $rootScope.lastPage = '/adminMeals';
	$scope.mealList = mealService.getAll();
	$scope.allSelected = mealService.isAllSelected();

	$scope.edit = function (id) {
		$rootScope.goto('/adminEditMeal/' + id);
	};

	$scope.selectAll = function () {
		mealService.selectAll($scope.allSelected);
	};
		
	$scope.removeSelected = function () {
		mealService.removeSelected().then(
            function(result) {
                mainService.setStatusBarText('Successfully deleted the selected meals.');
                $rootScope.goto('/adminMeals');
            }, function(reason) {
                mainService.setStatusBarText('Failed to delete the selected meals. "' + reason + '".');
            }
        );
	};
});

meals.controller('MealEditController', function ($scope, $rootScope, $routeParams, mainService, mealService) {
	$scope.mealToEdit = mealService.getItem($routeParams.id);
	$scope.backup = angular.copy($scope.mealToEdit);

	$scope.update = function () {
		mealService.update($scope.mealToEdit).then(
            function(pk) {
                console.log("Updated meal: " + $scope.mealToEdit.name);
                mainService.setStatusBarText('Successfully updated meal "' + $scope.mealToEdit.name + '".');
                $rootScope.goto('/adminMeals');
            }, function(reason) {
                mainService.setStatusBarText('Failed to update meal. "' + reason + '".');
        		$rootScope.goto('/adminMeals');
            }
        );
	};
	$scope.cancel = function () {
		$scope.mealToEdit.name = $scope.backup.name;
		$rootScope.goto('/adminMeals');
	};
	
	$scope.delete = function () {
		mealService.removeItem($scope.mealToEdit.meals_pk).then(
            function(pk) {
                console.log("Removed meal: '" + $scope.mealToEdit.name + "'.");
                mainService.setStatusBarText('Successfully deleted meal "' + $scope.mealToEdit.name + '".');
                $rootScope.goto('/adminMeals');
            }, function(reason) {
                mainService.setStatusBarText('Failed to delete meal. "' + reason + '".');
            }
        );
	};
});

meals.controller('MealAddController', function ($scope, $rootScope, $routeParams, mainService, mealService) {
	$scope.mealToAdd = {'meals_pk': '', 'name': ''};

	$scope.addItem = function () {
		mealService.addItem($scope.mealToAdd).then(
            function(pk) {
                console.log("Added meal: " + pk + " - " + $scope.mealToAdd.name);
                mainService.setStatusBarText('Successfully added meal "' + $scope.mealToAdd.name + '".');
                $rootScope.goto('/adminMeals');
            }, function(reason) {
                mainService.setStatusBarText('Failed to add meal. "' + reason + '".');
            }
        );
	};
	
	$scope.cancel = function () {
		$rootScope.goto('/adminMeals');
	};
});

angular.element(document).ready(function() { 
	angular.bootstrap(document.getElementById('meals'), ['meals']);
});
