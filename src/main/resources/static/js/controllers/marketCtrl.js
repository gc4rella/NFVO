var app = angular.module('app').controller('marketCtrl', function ($scope, serviceAPI, $routeParams, $http, $cookieStore, AuthService, $window, $interval, http) {

var url =  $cookieStore.get('URL');
//var defaultUrl = "lore:8082"
var defaultUrl = "marketplace.openbaton.org:8082";
$scope.alerts = [];

$scope.marketUrl = null;
$scope.privatepackages = [];
$scope.publicpackages = [];
$scope.publicNSDs = [];

loadTablePublic();
loadTablePublicNSD();
getMarketURL();



function getMarketURL() {
  $http.get(url + "/configprops")
      .success(function (response) {
          if (response.restVNFPackage.properties.privateip) {
            $scope.marketUrl = response.restVNFPackage.properties.privateip;
            loadTable();
          }
          else {
            return;
          }


          //console.log($scope.marketUrl);
      })
      .error(function (data, status) {
          showError(data, status);
      });


}


function loadTable() {
    //console.log($routeParams.userId);
    $http.get("http://"+ $scope.marketUrl + "/api/v1/vnf-packages")
        .success(function (response) {
            $scope.privatepackages = response;

            //console.log($scope.packages);
        })
        .error(function (data, status) {
            showError(data, status);
        });


}

function loadTablePublic() {
    //console.log($routeParams.userId);
    $http.get("http://"+ defaultUrl + "/api/v1/vnf-packages")
        .success(function (response) {
            $scope.publicpackages = response;

            //console.log($scope.packages);
        })
        .error(function (data, status) {
            showError(data, status);
        });


}

function loadTablePublicNSD() {
    //console.log($routeParams.userId);
    $http.get("http://"+ defaultUrl + "/api/v1/nsds")
        .success(function (response) {
             console.log(response);
            $scope.publicNSDs = response;

           
        })
        .error(function (data, status) {
            showError(data, status);
        });


}


$scope.loadTable = function() {
  loadTable();
};

$scope.closeAlert = function (index) {
    $scope.alerts.splice(index, 1);
};

$scope.download = function(data) {
  $scope.requestlink = {};
  $scope.requestlink['link'] = "http://" + defaultUrl + "/api/v1/vnf-packages/" + data.id + "/tar/";
    console.log($scope.requestlink);
     http.post(url + "/api/v1/vnf-packages/marketdownload", JSON.stringify($scope.requestlink)).success(function (response) {
      showOk("The package is being downloaded");
      })
     .error(function (data, status) {
         showError(data, status);

     });
};

$scope.downloadPrivate = function(data) {
  $scope.requestlink = {};
  $scope.requestlink['link'] = "http://" + $scope.marketUrl + "/api/v1/vnf-packages/" + data.id + "/tar/";
    console.log($scope.requestlink);
     http.post(url + "/api/v1/vnf-packages/marketdownload", JSON.stringify($scope.requestlink)).success(function (response) {
      showOk("The package is being downloaded");
      })
     .error(function (data, status) {
        showError(data, status);

     });
};

$scope.downloadNSD = function(data) {
  $scope.requestlink = {};
  $scope.requestlink['link'] = "http://" + defaultUrl + "/api/v1/nsds/" + data.id + "/json/";
    console.log($scope.requestlink);
     http.post(url + '/api/v1/ns-descriptors/marketdownload', JSON.stringify($scope.requestlink)).success(function (response) {
      showOk("The package is being downloaded");
      })
     .error(function (data, status) {
        showError(data, status);

     });
};


function showError(data, status) {
    $scope.alerts.push({
        type: 'danger',
        msg: 'ERROR: <strong>HTTP status</strong>: ' + status + ' response <strong>data</strong> : ' + JSON.stringify(data)
    });
    $('.modal').modal('hide');
    if (status === 401) {
        //console.log(status + ' Status unauthorized')
        AuthService.logout();
    }
}

function showOk(msg) {
    $scope.alerts.push({type: 'success', msg: msg});
    window.setTimeout(function() { 
    for (i = 0; i < $scope.alerts.length; i++) {
        if ($scope.alerts[i].type == 'success') {
            $scope.alerts.splice(i, 1);
        }
    }
    }, 5000);
    $('.modal').modal('hide');
}

$scope.closeAlert = function (index) {
        $scope.alerts.splice(index, 1);
    };

});
