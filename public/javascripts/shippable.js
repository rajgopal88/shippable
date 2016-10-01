var ShippableApp = angular.module('ShippableApp',[]);
ShippableApp.controller('ShippableAppCtrl',function($scope, shippableFactory) {

    $scope.link = "https://github.com/Shippable/support/issues";
    $scope.apiData;
    $scope.getData = function() {
        var data = {};
        data["link"] = $scope.link;
        shippableFactory.getData(data).success(function(response) {
            console.log(response);
            $scope.apidata = response;
        });
    };

    $scope.newData = function() {
        var data = {};
        data["apiData"] = $scope.apidata;
        shippableFactory.getNewData(data).success(function(response) {
           console.log("kkkkkkk");
        });
    };

    $scope.getData();
});
ShippableApp.factory('shippableFactory', ['$http', function($http) {
    var shippableFactory = {};

    var config = {
        headers : {
            'Content-Type': 'application/json;charset=utf-8;'
        }
    };

    shippableFactory.getData = function(data) {
        return $http.post('/link', data, config);
    };

    shippableFactory.getNewData = function(data) {
        return $http.post('', data, config);
    };

    return shippableFactory;
}]);