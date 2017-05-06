var app = angular.module('sqlApp', [ 'clearIconModule' ]);

app.controller('sqlCtrl', function($scope, $http) {
	$http({
		method : "GET",
		url : "getData"
	}).then(function SuccessCallback(response) {
		$scope.myList = response.data;
	}, function ErrorCallback(response) {
	})
});