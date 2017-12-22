
var recipes = angular.module('recipes', ['ngRoute', 'ngSanitize', 'ngResource']);
 
var urlPrefix = '';
var lastPage = '';

recipes.factory('RecipesFactory', function($resource) {
    return $resource('/recipes', {}, {
        query: { method: 'GET', isArray: true }
    })
});

recipes.factory('RecipesFactory', function($resource) {
    return $resource('/recipes/:id', {}, {
        show: { method: 'GET' , params: {id: '@id'} },
        update: { method: 'PUT' , params: {id: '@id'} },
        delete: { method: 'DELETE' , params: {id: '@id'} }
    })
});

// Routes
recipes.config(function($routeProvider) {
	$routeProvider.when('/recipesByLetter/:filter', {templateUrl: urlPrefix + 'recipes-by-letter.html', controller: 'RecipeByLetterController'});
    $routeProvider.when('/recipesAll', {templateUrl: urlPrefix + 'recipes-all.html', controller: 'AllRecipesController'});
    $routeProvider.when('/recipesDetailed', {templateUrl: urlPrefix + 'recipes-details.html', controller: 'DetailedRecipesController'});
    $routeProvider.when('/recipesEdit/:id', {templateUrl: urlPrefix + 'recipes-edit.html', controller: 'RecipeEditController'});
    $routeProvider.when('/recipesNew', {templateUrl: urlPrefix + 'recipes-new.html', controller: 'RecipeAddController'});
    $routeProvider.when('/recipesNewTest', {templateUrl: urlPrefix + 'recipes-new-test.html', controller: 'RecipeAddTestController'});
});

// Services
recipes.service('recipeService', function(RecipesFactory, $http, $q) {
	var nextRecipePk = 20;
	var allSelected = false;
	var list = [];

	this.indexForPK = function(pk) {
		for (var i = 0; i < list.length; i++) {
			var next = list[i];
			if (next.recipes_pk == pk) {
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
                itemsToDelete = itemsToDelete + list[i].recipes_pk;
				list.splice(i, 1);
                numSelected = numSelected + 1;
            }
        }

        var deferred = $q.defer();
        RecipesFactory.delete({ id: itemsToDelete });
        console.log("Deleted recipes: " + itemsToDelete);
        deferred.resolve();
        return deferred.promise;
    }
    this.getAll = function() {
        return RecipesFactory.query();
    }
    this.getAll = function(callback) {
        return RecipesFactory.query(callback);
    }

    this.showThreeColumns = function(result, $scope) {
        $scope.recipeList = result;

        var numRowsCol1
        if (result.length > 15) {
            numRowsCol1 = 15
        } else {
            numRowsCol1 = result.length
        }

        var numRowsCol2
        if (result.length > 30) {
            numRowsCol2 = 15
        } else {
            numRowsCol2 = result.length - 15
        }

        var numRowsCol3
        if (result.length > 45) {
            numRowsCol3 = 15
        } else {
            numRowsCol3 = result.length - 30
        }

        var index = 0
        var col1 = []
        var col2 = []
        var col3 = []

        for (i=0; i <= numRowsCol1-1; i++) {
            col1[i] = result[index]
            index++
        }
        for (i=0; i <= numRowsCol2-1; i++) {
            col2[i] = result[index]
            index++
        }
        for (i=0; i <= numRowsCol3-1; i++) {
            col3[i] = result[index]
            index++
        }
        $scope.col1 = col1
        $scope.col2 = col2
        $scope.col3 = col3
    }


    this.filter = function(filterText, mainService, callback) {
        if (typeof filterText == "obect") {

        }
//        list = RecipesFactory.show(filterText);
        $http({
            method: 'GET',
            url: '/recipes/filter/' + filterText
        }).success(function(result) {
            callback(result);
        }).error(function(reason) {
            mainService.setStatusBarText('Failed to get the filtered list of recipes. "' + reason + '".');
        });
    }
    this.getItem = function(pk) {
        return RecipesFactory.show({ id: pk });
        //var deferred = $q.defer();
        //RecipesFactory.show({ id: pk }, function(loadedRecipe) {
        //    deferred.resolve(loadedRecipe);
        //});
        //return deferred.promise;
    }
    this.addItem = function(item) {
        var deferred = $q.defer();
        var result = RecipesFactory.save(item, function(pk) {
            deferred.resolve(pk.primaryKey);
        });
        return deferred.promise;
    }
    this.removeItem = function(pk) {
        var deferred = $q.defer();

        RecipesFactory.delete({ id: pk }, function(pk) {
            deferred.resolve(pk);
        });
        return deferred.promise;
    }
    this.update = function(item) {
        var deferred = $q.defer();
        RecipesFactory.update(item, function(msg) {
            deferred.resolve(msg);
        });
        return deferred.promise;
    }
    this.size = function() { return list.length; }
    this.isAllSelected = function() { return list.allSelected; }
});

recipes.controller('RecipeByLetterController', function ($scope, $rootScope, $routeParams, mainService, recipeService) {
    $rootScope.headerDisplay = "display: block;";
    $rootScope.bodyBackground = "";
    $rootScope.lastPage = '/recipesByLetter/' + $routeParams.filter;
    $rootScope.recipesPage = $rootScope.lastPage;
    recipeService.filter("name=" + $routeParams.filter + "*", mainService, function(filteredList) {
        var result = filteredList;
        recipeService.showThreeColumns(result, $scope);
    });
    $scope.letter = $routeParams.filter;
    $scope.allSelected = recipeService.isAllSelected();
    $scope.edit = function (id) {
        $rootScope.goto('/recipesEdit/' + id);
    };
});

recipes.controller('AllRecipesController', function ($scope, $rootScope, $routeParams, mainService, recipeService) {
    $rootScope.headerDisplay = "display: block;";
    $rootScope.bodyBackground = "";
    $rootScope.lastPage = '/recipesAll';
    $rootScope.recipesPage = $rootScope.lastPage;
    var promise = recipeService.getAll(function(result) {
        recipeService.showThreeColumns(result, $scope);
    });
    //promise.then(
    //    function(result) {
    //        recipeService.showThreeColumns(result, $scope);
    //    }, function(reason) {
    //        mainService.setStatusBarText('Failed to get recipe list. "' + reason + '".');
    //    }
    //);
    $scope.edit = function (id) {
        $rootScope.goto('/recipesEdit/' + id);
    };
});

recipes.controller('DetailedRecipesController', function ($scope, $rootScope, $routeParams, mainService, recipeService) {
    $rootScope.headerDisplay = "display: block;";
    $rootScope.bodyBackground = "";
    $rootScope.lastPage = '/recipesDetailed';
    $rootScope.recipesPage = $rootScope.lastPage;
    if (! $scope.recipeFilter) {
  	    $scope.recipeFilter = {'name': '', 'cookbook': '', 'meal': '', 'category': '', 'favorite': '', 'calPerServing': ''};
    }
    $scope.recipeList = recipeService.filter("name=*" + $routeParams.filter + "*", $scope);
    //recipeService.filter("name=" + $routeParams.filter + "*", $routeParams.filter, $scope);

    $scope.edit = function (id) {
        $rootScope.goto('/recipesEdit/' + id);
    };
});

recipes.controller('RecipeAddController', function ($scope, $rootScope, $routeParams, mainService, recipeService, mealService, foodCategoryService, cookbookService) {
	$scope.recipeToAdd = {'recipes_pk': '', 'name': '', 'directions': '', 'ingredients': '', 'nutrition': '', 'favorite': 'Yes'};
	$scope.cookbookList = cookbookService.getAll();
	$scope.foodCategoryList = foodCategoryService.getAll();
	$scope.mealList = mealService.getAll();	

	$scope.addItem = function () {
		recipeService.addItem($scope.recipeToAdd).then(
            function(pk) {
                console.log("Added recipe: " + pk + " - " + $scope.recipeToAdd.name);
                mainService.setStatusBarText('Successfully added recipe "' + $scope.recipeToAdd.name + '".');
		        $rootScope.goto($scope.recipesPage);
            }, function(reason) {
                mainService.setStatusBarText('Failed to add recipe. "' + reason + '".');
            }
        );
	};
	
	$scope.cancel = function () {
		$rootScope.goto($scope.recipesPage);
	};
});

recipes.controller('RecipeAddTestController', function ($scope, $rootScope, $routeParams, mainService, recipeService, mealService, foodCategoryService, cookbookService) {
	$scope.recipeToAdd = {'recipes_pk': '', 'name': '', 'directions': '', 'ingredients': '', 'nutrition': '', 'favorite': 'No'};
	$scope.cookbookList = cookbookService.getAll();
	$scope.foodCategoryList = foodCategoryService.getAll();
	$scope.mealList = mealService.getAll();

	$scope.addItem = function () {
		recipeService.addItem($scope.recipeToAdd).then(
            function(pk) {
                console.log("Added test recipe: " + pk + " - " + $scope.recipeToAdd.name);
                mainService.setStatusBarText('Successfully added test recipe "' + $scope.recipeToAdd.name + '".');
		        $rootScope.goto($scope.recipesPage);
            }, function(reason) {
                mainService.setStatusBarText('Failed to add test recipe. "' + reason + '".');
            }
        );
	};

	$scope.cancel = function () {
		$rootScope.goto($scope.recipesPage);
	};
});

meals.controller('RecipeEditController', function ($scope, $rootScope, $routeParams, mainService, recipeService, mealService, foodCategoryService, cookbookService) {
	$scope.recipeToEdit = recipeService.getItem($routeParams.id);
	//recipeService.getItem($routeParams.id).then(
    //    function(loadedRecipe) {
    //        console.log("Retrieved recipe: " + $routeParams.id);
    //        $scope.recipeToEdit = loadedRecipe;
    //    }, function(reason) {
    //        mainService.setStatusBarText('Failed to add recipe. "' + reason + '".');
    //    }
    //);

	$scope.cookbookList = cookbookService.getAll();
	$scope.foodCategoryList = foodCategoryService.getAll();
	$scope.mealList = mealService.getAll();
	$scope.backup = angular.copy($scope.recipeToEdit);

	$scope.favorite = function () {
	    if ($scope.recipeToEdit.favorite == "Yes") {
	       $scope.recipeToEdit.favorite = "No";
	    } else {
	       $scope.recipeToEdit.favorite = "Yes";
	    }
	};
	$scope.update = function () {
		recipeService.update($scope.recipeToEdit).then(
            function(pk) {
                console.log("Updated recipe: " + $scope.recipeToEdit.name);
                mainService.setStatusBarText('Successfully updated recipe "' + $scope.recipeToEdit.name + '".');
		        $rootScope.goto($scope.recipesPage);
            }, function(reason) {
                mainService.setStatusBarText('Failed to update recipe. "' + reason + '".');
		        $rootScope.goto($scope.recipesPage);
            }
        );
	};
	$scope.cancel = function () {
    	$scope.recipeToEdit = angular.copy($scope.backup);
		$rootScope.goto($scope.recipesPage);
	};

	$scope.delete = function () {
		recipeService.removeItem($scope.recipeToEdit.recipes_pk).then(
            function(pk) {
                console.log("Removed recipe: '" + $scope.recipeToEdit.name + "'.");
                mainService.setStatusBarText('Successfully deleted recipe "' + $scope.recipeToEdit.name + '".');
		        $rootScope.goto($scope.recipesPage);
            }, function(reason) {
                mainService.setStatusBarText('Failed to delete recipe. "' + reason + '".');
            }
        );
	};
});



// Cookbook Controllers
/*
recipes.controller('RecipesController', function ($scope, $rootScope, recipeService, loginService) {
    $rootScope.headerDisplay = "display: block;";
    $rootScope.bodyBackground = "";
    $rootScope.lastPage = '/recipesByLetter';
	$scope.recipeList = recipeService.getAll();
	$scope.allSelected = recipeService.isAllSelected();

    $scope.edit = function (id) {
        $rootScope.goto('/adminEditRecipe/' + id);
    };

    $scope.selectAll = function () {
		recipeService.selectAll($scope.allSelected);
	};
		
	$scope.removeSelected = function () {
		recipeService.removeSelected();
	};
		
	$scope.delete = function (id) {
		recipeService.removeItem(id);
		$scope.recipeList = recipeService.getAll();
	};
		
});
*/

/*
cookbooks.controller('CookbookEditController', function ($scope, $rootScope, $routeParams, mainService, cookbookService) {
	$scope.cookbookToEdit = cookbookService.getItem($routeParams.id);
	$scope.backup = angular.copy($scope.cookbookToEdit);

	$scope.update = function () {
		cookbookService.update($scope.cookbookToEdit);
		mainService.setStatusBarText('Successfully updated cookbook "' + $scope.cookbookToEdit.name + '".');
		$rootScope.goto('/adminCookbooks');
	};
	
	$scope.cancel = function () {
		$scope.cookbookToEdit.name = $scope.backup.name;
		$rootScope.goto('/adminCookbooks');
	};
	
	$scope.delete = function () {
		cookbookService.removeItem($scope.cookbookToEdit.cookbooks_pk);
		mainService.setStatusBarText('Successfully deleted cookbook "' + $scope.cookbookToEdit.name + '".');
		$rootScope.goto('/adminCookbooks');
	};
});

*/

angular.element(document).ready(function() { 
	angular.bootstrap(document.getElementById('recipes'), ['recipes']);
});
