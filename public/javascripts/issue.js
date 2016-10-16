var IssueApp = angular.module('IssueApp',['angular-ladda']);
IssueApp.controller('IssueAppCtrl',function($scope, issueFactory, $timeout) {

    $scope.link = "https://github.com/Shippable/support/issues";
    $scope.issueData;
    $scope.countryList = ["INDIA","USA","UK"];

    /**
     * function which sends the link address entered by the user and in response gets the details of the issue
     */
    $scope.getData = function() {
        var data = {};
        $scope.loading = true; // start loading
        $timeout(function() {
            data["link"] = $scope.link;
            issueFactory.getData(data).success(function(response) {
                console.log(response);
                $scope.issueData = response;
                $scope.loading = false; //disabling loading
            });
            $scope.loading = false; // stop loading
        }, 2000);
    };

    $scope.displayResult = function() {

    };
    $scope.getData();
});

IssueApp.factory('issueFactory', ['$http', function($http) {
    var issueFactory = {};

    var config = {
        headers : {
            'Content-Type': 'application/json;charset=utf-8;'
        }
    };

    /**
     * http request which call the api for getting the details
     */
    issueFactory.getData = function(data) {
        return $http.post('/link', data, config);
    };

    return issueFactory;
}]);