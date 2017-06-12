var app = angular.module('myApp', [ 'autocomplete' ]);

app.controller('editCtrl', function($scope, $http, $window, $location, $filter) { //, testService) {
	$http({
		method : "GET",
		url : "getData"
	}).then(function SuccessCallback(response) {
		$scope.autoParts = [];
		$scope.autoMats = [];
		$scope.newField = {};
		$scope.newList = [];
		$scope.tmpEntry = {};
		$scope.editing = false;
		
		$scope.myList = response.data;
		$scope.tmpEntry = $scope.myList;
		
		angular.forEach(response.data, function(value, key) {
			// push unique values
			if ($scope.autoParts.indexOf(value.part) == -1)
				$scope.autoParts.push(value.part);
			if ($scope.autoMats.indexOf(value.material) == -1)
				$scope.autoMats.push(value.material);
		});
		
		$scope.update = function() {
			$http({
				method : 'POST',
				url : 'updateSQL',
		        data : $scope.newList,
		        headers: {
		        	'Content-Type': 'application/json'
		        }
			}).then(function SuccessCallback(response2) {
				alert("updated");
				$window.location.reload();
			}, function ErrorCallback(response2) {
			})
			
			// reset updated list to empty array after submit to SQL.
			$scope.newList = [];
		};
		
		$scope.editData = function(field) {
			$scope.editing = $scope.myList.indexOf(field);
			$scope.newField = angular.copy(field);
		};

		$scope.saveField = function(index) {
			if ($scope.editing !== false) {
				$scope.newList[$scope.editing] = $scope.myList[$scope.editing];
				$scope.editing = false;
			}
			$scope.newData = true;
		};

		$scope.cancel = function(index) {
			if ($scope.editing !== false) {
				$scope.myList[$scope.editing] = $scope.newField;
				$scope.editing = false;
			}
		};
		
		
		$scope.autoNames = ["Aaron Lee (AL)", "Yu-Tien Chang (YT)", "Frank Pei (FPe)", "Christopher Tjhen (CT)", "Matt Phuong Pham (MP)", "Ronald Lu (RL)", "William Ge (WG)", "Sukreeti Sehrawat (SS)", "Huy Pham (HP)", "Dan Hang (DH)", "Trieu Pham (TP)", "Jyhkai Hsu (JHs)", "Sekar Mookandi (SM)", "Peter Storz (PS)", "Chyijy Ngan (CN)", "Curtis Walker (CW)", "Dong Kang (DK)", "Fai Jor (FJ)", "Me Me Thin (MT)", "Roy Guo (RGu)", "Brian Nguyen (BN)", "Brian Ziani (BZ)", "Edmund Soo (ES)", "Gary Cheng (GC)", "Hassan Yahya (HY)", "Hiep Tran (HT)", "Johnny Huang (JH)", "Jonathan Cheng (JC)", "Jonathan Tam (JTa)", "Lan Vu (LV)", "Long Nguyen (LN)", "Ma Xiong (MX)", "Michael Sarmento (MS)", "Paul Boghratian (PB)", "Simon Ng (SN)", "Sonny Vuong (SV)", "Tommy Trinh (TT)", "Dan Exon (DE)", "Eric Chung (EC)", "Jason Xia (JX)", "Jennifer Tsai (JTs)", "Grace Liu (GL)", "Jose Santamaria (JS)", "Ken Huang (KH)", "Wilson Tam(WT)", "Mark Duan (MD)", "Milton Cai (MC)", "Kin Yan (KY)", "Steve Chen (SC)", "Terrance Xiao (TX)", "Fabio Lin (FL)", "Hung Lin (HL)", "Jerry Ho (JHo)", "Forest Preston (FPr)", "Pranav Abhyankar (PA)", "Srinivas Bonthu (SB)", "Jinjya Hsu (Jin)", "Jue Fang (JF)", "SungKuang Chung (Max)", "Tee Lee", "Marc Schneider", "Vinh Nguyen"];
		
		var entryInit = function () {
			$scope.nameData = '';
			$scope.partData = '';
			$scope.matData = '';
			$scope.batchData = '';
			$scope.qtyData = '';
		}
		
		entryInit();
		$scope.addEntry = function() {
			var entry = {
									id :		'null',
									name :		$scope.nameData,
									part :		$scope.partData,
									material :	$scope.matData,
									batch :		$scope.batchData,
									qty :		$scope.qtyData
			};
			// add new data to the temp list
			$scope.tmpEntry.push(entry);
			// add new data to new list by using temp list last index
			$scope.newList[$scope.tmpEntry.length-1] = entry;
			console.log($scope.tmpEntry);
			entryInit();
		};
		
		// for delete data
		var previous = [];
		var updatePrevious = function(newPrevious) {
		    angular.copy(newPrevious, previous);
		};
		updatePrevious($scope.myList);

		$scope.$watch( "myList" , function(newValue, oldValue){
	        var checked = $filter("filter")( newValue , {checked:true} );
	        var idxOfChangedItem;
	        
	        if(checked && checked.length){
	            $scope.selected = checked;
	            $scope.delConfirm = true;

	        }else{
	        	$scope.delConfirm = false;
	        }
	    }, true );
		
		$scope.delField = function ($index) {
			console.log($scope.selected);
	        // How to delete rows using selected $scope.selected objects
	        angular.forEach($scope.myList, function (row, index) {
	            if($scope.myList[index].checked) {
	            	$scope.newList[index] = $scope.myList[index];
	            }            
	        });
	        $scope.delConfirm = false;
		};
		}, function ErrorCallback(response) {
	})	
});