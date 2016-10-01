var ShippableApp = angular.module('ShippableApp',[]);
ShippableApp.controller('ShippableAppCtrl',function($scope, shippableFactory) {

    $scope.link = "https://github.com/Shippable/support/issues";
    $scope.issueData;
    $scope.getData = function() {
        var data = {};
        data["link"] = $scope.link;
        shippableFactory.getData(data).success(function(response) {
            console.log(response);
            $scope.issueData = response;
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

    return shippableFactory;
}]);