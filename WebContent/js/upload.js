var app = angular.module('myApp', []);

app.directive('fileModel', [ '$parse', function($parse) {
	return {
		restrict : 'A',
		link : function(scope, element, attrs) {
			var model = $parse(attrs.fileModel);
			var modelSetter = model.assign;

			element.bind('change', function() {
				scope.$apply(function() {
					modelSetter(scope, element[0].files[0]);
				});
			});
		}
	};
} ]);

app.service('fileUpload', [ '$http', function($http) {
	this.uploadFileToUrl = function(file, uploadUrl) {
		var fd = new FormData();
		fd.append('file', file);
		return $http({
			url : uploadUrl,
			method : "POST",
			data : fd,
			transformRequest : angular.identity,
			headers : {
				'Content-Type' : undefined
			}
		});
	};
} ]);

app.controller('uploadCtrl', [ '$scope', 'fileUpload',
		function($scope, fileUpload) {
			$scope.uploadFile = function() {
				var file = $scope.myFile;
				
				$scope.upMSG = "File is uploading.";
				console.log('file is ');
				console.dir(file);

				// servlet url
				var uploadUrl = "uploadExcel";
				
				fileUpload.uploadFileToUrl(file, uploadUrl).then(function SuccessCallback(response) {
					// Get response from servlet
					console.log(response.data);
					$scope.doneMSG = response.data;
//					alert(response.data);
				}, function errorCallback(response) {
					// Get response from servlet
					console.log(response);
					$scope.doneMSG = response;
//					alert(response);
				});
			}
		} ]);