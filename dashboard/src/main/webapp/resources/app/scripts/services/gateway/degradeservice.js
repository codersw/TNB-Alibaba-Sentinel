var app = angular.module('sentinelDashboardApp');

app.service('GatewayDegradeService', ['$http', function ($http) {
  this.queryMachineRules = function (app, ip, port) {
    var param = {
      app: app,
      ip: ip,
      port: port
    };
    return $http({
      url: '/gateway/degrade/rules',
      params: param,
      method: 'GET'
    });
  };

  this.newRule = function (rule) {
    var param = {
      id: rule.id,
      resource: rule.resource,
      limitApp: rule.limitApp,
      count: rule.count,
      timeWindow: rule.timeWindow,
      grade: rule.grade,
      app: rule.app,
      ip: rule.ip,
      port: rule.port
    };
    return $http({
      url: '/gateway/degrade/new',
      params: param,
      method: 'GET'
    });
  };

  this.saveRule = function (rule) {
    var param = {
      id: rule.id,
      app: rule.app,
      ip: rule.ip,
      port: rule.port,
      resource: rule.resource,
      limitApp: rule.limitApp,
      grade: rule.grade,
      count: rule.count,
      timeWindow: rule.timeWindow,
    };
    return $http({
      url: '/gateway/degrade/save',
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
      url: '/gateway/degrade/delete',
      params: param,
      method: 'GET'
    });
  };

  this.checkRuleValid = function (rule) {
      if (rule.resource === undefined || rule.resource === '') {
          alert('资源名称不能为空');
          return false;
      }
      if (rule.grade === undefined || rule.grade < 0) {
          alert('未知的降级策略');
          return false;
      }
      if (rule.count === undefined || rule.count === '' || rule.count < 0) {
          alert('降级阈值不能为空或小于 0');
          return false;
      }
      if (rule.timeWindow === undefined || rule.timeWindow === '' || rule.timeWindow <= 0) {
          alert('降级时间窗口必须大于 0');
          return false;
      }
      // 异常比率类型.
      if (rule.grade === 1 && rule.count > 1) {
          alert('异常比率超出范围：[0.0 - 1.0]');
          return false;
      }
      return true;
  };
}]);
