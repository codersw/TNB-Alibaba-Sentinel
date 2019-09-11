var app = angular.module('sentinelDashboardApp');

app.service('GatewayRoutesService', ['$http', function ($http) {
  this.queryRoutes = function (app, ip, port) {
    var param = {
      app: app,
      ip: ip,
      port: port
    };

    return $http({
      url: '/gateway/routes/list',
      params: param,
      method: 'GET'
    });
  };

  this.addRoutes = function (routes) {
    return $http({
      url: '/gateway/routes/add',
      data: routes,
      method: 'POST'
    });
  };

  this.updateRoutes = function (routes) {
    return $http({
      url: '/gateway/routes/update',
      data: routes,
      method: 'POST'
    });
  };

  this.deleteRoutes = function (routes) {
    var param = {
      id: routes.id,
      app: routes.app,
      ip: routes.ip,
      port: routes.port
    };
    return $http({
      url: '/gateway/flow/'+ param.id ,
      params: param,
      method: 'DELETE'
    });
  };

}]);
