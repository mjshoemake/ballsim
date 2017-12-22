
var foodCategories = angular.module('foodCategories', ['ngRoute', 'ngSanitize', 'ngResource']);
 
var urlPrefix = '';

foodCategories.factory('FoodCategoriesFactory', function($resource) {
    return $resource('/foodCategories', {}, {
        query: { method: 'GET', isArray: true }
    })
});

foodCategories.factory('FoodCategoriesFactory', function($resource) {
    return $resource('/foodCategories/:id', {}, {
        show: { method: 'GET' , params: {id: '@id'} },
        update: { method: 'PUT' , params: {id: '@id'} },
        delete: { method: 'DELETE' , params: {id: '@id'} }
    })
});

// Routes
foodCategories.config(function($routeProvider) {
	$routeProvider.when('/adminFoodCategories', {templateUrl: urlPrefix + 'admin-food-categories.html', controller: 'FoodCategoryListController'});
	$routeProvider.when('/adminNewFoodCategory', {templateUrl: urlPrefix + 'admin-food-categories-new.html', controller: 'FoodCategoryAddController'});
	$routeProvider.when('/adminEditFoodCategory/:id', {templateUrl: urlPrefix + 'admin-food-categories-edit.html', controller: 'FoodCategoryEditController'});
});

// Services
foodCategories.service('foodCategoryService', function(FoodCategoriesFactory, $q) {
	var nextPk = 15;
	var allSelected = false;
	var list = undefined;

	this.indexForPK = function(pk) {
		for (var i = 0; i < list.length; i++) {
			var next = list[i];
			if (next.meal_categories_pk == pk) {
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
                itemsToDelete = itemsToDelete + list[i].meal_categories_pk;
				list.splice(i, 1);
                numSelected = numSelected + 1;
			}
		}
        var deferred = $q.defer();
        FoodCategoriesFactory.delete({ id: itemsToDelete });
        console.log("Deleted food categories: " + itemsToDelete);
        deferred.resolve();
        return deferred.promise;
	}
    this.getAll = function() {
        if (list === undefined) {
            list = FoodCategoriesFactory.query(function(results) {
                //data saved. do something here.
                console.log("Food category list response: " + results.length);
            });
        }
        return list;
    }
    this.refreshAll = function() {
        list = FoodCategoriesFactory.query(function(results) {
            //data saved. do something here.
            console.log("Food category list response: " + results.length);
        });
        return list;
    }
    this.getItem = function(pk) {
        return FoodCategoriesFactory.show({ id: pk });
    }
    this.addItem = function(item) {
        var deferred = $q.defer();
        var result = FoodCategoriesFactory.save(item, function(pk) {
            deferred.resolve(pk.primaryKey);
        });
        return deferred.promise;
    }
    this.removeItem = function(pk) {
        var deferred = $q.defer();
        FoodCategoriesFactory.delete({ id: pk }, function(pk) {
            deferred.resolve(pk);
        });
        return deferred.promise;
    }
    this.update = function(item) {
        var deferred = $q.defer();
        FoodCategoriesFactory.update(item, function(pk) {
            deferred.resolve(pk.primaryKey);
        });
        return deferred.promise;
    }
    this.size = function() { return list.length; }
    this.isAllSelected = function() { return list.allSelected; }
});

// Cookbook Controllers
foodCategories.controller('FoodCategoryListController', function ($scope, $rootScope, foodCategoryService, mainService, loginService) {
    $rootScope.headerDisplay = "display: block;";
    $rootScope.bodyBackground = "";
    $rootScope.lastPage = '/adminFoodCategories';
	$scope.foodCategoryList = foodCategoryService.refreshAll();
	$scope.allSelected = foodCategoryService.isAllSelected();

	$scope.edit = function (id) {
		$rootScope.goto('/adminEditFoodCategory/' + id);
	};

	$scope.selectAll = function () {
		foodCategoryService.selectAll($scope.allSelected);
	};
		
	$scope.removeSelected = function () {
		foodCategoryService.removeSelected().then(function(data) {
            mainService.setStatusBarText('Successfully deleted the selected food categories.');
            $rootScope.goto('/adminFoodCategories');
        }, function(error) {
            mainService.setStatusBarText('An error occurred trying to delete the selected food categories. ' + error);
        });
	};
});

foodCategories.controller('FoodCategoryEditController', function ($scope, $rootScope, $routeParams, mainService, foodCategoryService) {
	$scope.foodCategoryToEdit = foodCategoryService.getItem($routeParams.id);
	$scope.backup = angular.copy($scope.foodCategoryToEdit);

	$scope.update = function () {
        foodCategoryService.update($scope.foodCategoryToEdit).then(
        function(pk) {
            console.log("Updated food category: " + $scope.foodCategoryToEdit.name);
            mainService.setStatusBarText('Successfully updated food category "' + $scope.foodCategoryToEdit.name + '".');
            $rootScope.goto('/adminFoodCategories');
        }, function(error) {
            mainService.setStatusBarText('An error occurred trying to update food category "' + $scope.foodCategoryToEdit.name + '".');
            $rootScope.goto('/adminFoodCategories');
        });
	};
	
	$scope.cancel = function () {
		$scope.foodCategoryToEdit.name = $scope.backup.name;
        $rootScope.goto('/adminFoodCategories');
	};
	
    $scope.delete = function () {
        foodCategoryService.removeItem($scope.foodCategoryToEdit.meal_categories_pk).then(
        function(pk) {
            console.log("Removed food category: '" + $scope.foodCategoryToEdit.name + "'.");
            mainService.setStatusBarText('Successfully deleted food category "' + $scope.foodCategoryToEdit.name + '".');
            $rootScope.goto('/adminFoodCategories');
        }, function(error) {
            mainService.setStatusBarText('An error occurred trying to delete food category "' + $scope.foodCategoryToEdit.name + '".');
            $rootScope.goto('/adminFoodCategories');
        });
    };
});

foodCategories.controller('FoodCategoryAddController', function ($scope, $rootScope, $routeParams, mainService, foodCategoryService) {
	$scope.foodCategoryToAdd = {'meal_categories_pk': '', 'name': ''};

	$scope.addItem = function () {
        foodCategoryService.addItem($scope.foodCategoryToAdd).then(
        function(pk) {
            console.log("Added food categoy: " + pk + " - " + $scope.foodCategoryToAdd.name);
            mainService.setStatusBarText('Successfully added food category "' + $scope.foodCategoryToAdd.name + '".');
            $rootScope.goto('/adminFoodCategories');
        }, function(error) {
            mainService.setStatusBarText('An error occurred trying to add food category "' + $scope.foodCategoryToAdd.name + '".');
            $rootScope.goto('/adminFoodCategories');
        });
	};
	
	$scope.cancel = function () {
        $rootScope.goto('/adminFoodCategories');
	};
});

angular.element(document).ready(function() { 
	angular.bootstrap(document.getElementById('foodCategories'), ['foodCategories']);
});
