var app = angular.module('sentinelDashboardApp');

app.service('GatewaySystemService', ['$http', function ($http) {
  this.queryMachineRules = function (app, ip, port) {
    var param = {
      app: app,
      ip: ip,
      port: port
    };
    return $http({
      url: '/gateway/system/rules',
      params: param,
      method: 'GET'
    });
  };

  this.newRule = function (rule) {
    var param = {
      app: rule.app,
      ip: rule.ip,
      port: rule.port
    };
    rule.grade = parseInt(rule.grade);
    if (rule.grade === 0) {// avgLoad
      param.highestSystemLoad = rule.highestSystemLoad;
    } else if (rule.grade === 1) {// avgRt
      param.avgRt = rule.avgRt;
    } else if (rule.grade === 2) {// maxThread
      param.maxThread = rule.maxThread;
    } else if (rule.grade === 3) {// qps
      param.qps = rule.qps;
    } else if (rule.grade === 4) {// cpu
      param.highestCpuUsage = rule.highestCpuUsage;
    }

    return $http({
      url: '/gateway/system/new',
      params: param,
      method: 'GET'
    });
  };

  this.saveRule = function (rule) {
    var param = {
      id: rule.id,
      app: rule.app,
      ip: rule.ip,
      port: rule.port
    };
    if (rule.grade === 0) {// avgLoad
      param.highestSystemLoad = rule.highestSystemLoad;
    } else if (rule.grade === 1) {// avgRt
      param.avgRt = rule.avgRt;
    } else if (rule.grade === 2) {// maxThread
      param.maxThread = rule.maxThread;
    } else if (rule.grade === 3) {// qps
      param.qps = rule.qps;
    } else if (rule.grade === 4) {// cpu
        param.highestCpuUsage = rule.highestCpuUsage;
    }

    return $http({
      url: '/gateway/system/save',
      params: param,
      method: 'GET'
    });
  };

  this.deleteRule = function (rule) {
    var param = {
      id: rule.id,
      app: rule.app
    };

    return $http({
      url: '/gateway/system/delete',
      params: param,
      method: 'GET'
    });
  };
}]);
