var IssueApp = angular.module('IssueApp',[]);
IssueApp.controller('IssueAppCtrl',function($scope, issueFactory) {

    $scope.link = "https://github.com/Shippable/support/issues";
    $scope.issueData;
    $scope.getData = function() {
        var data = {};
        data["link"] = $scope.link;
        issueFactory.getData(data).success(function(response) {
            console.log(response);
            $scope.issueData = response;
        });
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

    issueFactory.getData = function(data) {
        return $http.post('/link', data, config);
    };

    return issueFactory;
}]);