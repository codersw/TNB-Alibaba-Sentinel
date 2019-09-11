var app = angular.module('sentinelDashboardApp');

app.controller('GatewayRoutesCtl', ['$scope', '$stateParams','GatewayRoutesService', 'GatewayApiService', 'ngDialog', 'MachineService',
    function ($scope, $stateParams, GatewayRoutesService, GatewayApiService, ngDialog, MachineService) {
        $scope.app = $stateParams.app;

        $scope.routesPageConfig = {
            pageSize: 10,
            currentPageIndex: 1,
            totalPage: 1,
            totalCount: 0,
        };

        $scope.macsInputConfig = {
            searchField: ['text', 'value'],
            persist: true,
            create: false,
            maxItems: 1,
            render: {
                item: function (data, escape) {
                    return '<div>' + escape(data.text) + '</div>';
                }
            },
            onChange: function (value, oldValue) {
                $scope.macInputModel = value;
            }
        };

        getMachineRules();
        function getMachineRules() {
            if (!$scope.macInputModel) {
                return;
            }

            var mac = $scope.macInputModel.split(':');
            GatewayRoutesService.queryRoutes($scope.app, mac[0], mac[1]).success(
                function (data) {
                    if (data.code == 0 && data.data) {
                        $scope.routes = data.data;
                        $scope.routesPageConfig.totalCount = $scope.routes.length;
                    } else {
                        $scope.routes = [];
                        $scope.routesPageConfig.totalCount = 0;
                    }
                });
        };
        $scope.getMachineRules = getMachineRules;

        getApiNames();
        function getApiNames() {
            if (!$scope.macInputModel) {
                return;
            }

            var mac = $scope.macInputModel.split(':');
            GatewayApiService.queryApis($scope.app, mac[0], mac[1]).success(
                function (data) {
                    if (data.code == 0 && data.data) {
                        $scope.apiNames = [];

                        data.data.forEach(function (api) {
                            $scope.apiNames.push(api["apiName"]);
                        });
                    }
                });
        }

        $scope.intervalUnits = [{val: 0, desc: '秒'}, {val: 1, desc: '分'}, {val: 2, desc: '时'}, {val: 3, desc: '天'}];

        var gatewayRoutesDialog;
        $scope.editRoutes = function (rule) {
            $scope.currentRoutes = angular.copy(rule);
            $scope.gatewayRoutesDialog = {
                title: '编辑路由规则',
                type: 'edit',
                confirmBtnText: '保存'
            };
            gatewayRoutesDialog = ngDialog.open({
                template: '/app/views/dialog/gateway/flow-rule-dialog.html',
                width: 780,
                overlay: true,
                scope: $scope
            });
        };

        $scope.addNewRule = function () {
            var mac = $scope.macInputModel.split(':');
            $scope.currentRoutes = {
                grade: 1,
                app: $scope.app,
                ip: mac[0],
                port: mac[1],
                resourceMode: 0,
                interval: 1,
                intervalUnit: 0,
                controlBehavior: 0,
                burst: 0,
                maxQueueingTimeoutMs: 0
            };

            $scope.gatewayRoutesDialog = {
                title: '新增路由规则',
                type: 'add',
                confirmBtnText: '新增'
            };

            gatewayRoutesDialog = ngDialog.open({
                template: '/app/views/dialog/gateway/flow-rule-dialog.html',
                width: 780,
                overlay: true,
                scope: $scope
            });
        };

        $scope.saveRoutes = function () {

            if ($scope.gatewayFlowRuleDialog.type === 'add') {
                GatewayRoutesService.addRoutes($scope.currentRule);
            } else if ($scope.gatewayFlowRuleDialog.type === 'edit') {
                GatewayRoutesService.updateRoutes($scope.currentRule, true);
            }
        };

        $scope.useRouteID = function() {
            $scope.currentRoutes.resource = '';
        };

        $scope.useCustormAPI = function() {
            $scope.currentRoutes.resource = '';
        };

        $scope.useParamItem = function () {
            $scope.currentRoutes.paramItem = {
                parseStrategy: 0,
                matchStrategy: 0
            };
        };

        $scope.notUseParamItem = function () {
            $scope.currentRoutes.paramItem = null;
        };

        $scope.useParamItemVal = function() {
            $scope.currentRoutes.paramItem.pattern = "";
            $scope.currentRoutes.paramItem.matchStrategy = 0;
        };

        $scope.notUseParamItemVal = function() {
            $scope.currentRoutes.paramItem.pattern = null;
            $scope.currentRoutes.paramItem.matchStrategy = null;
        };

        function addNewRoutes(routes) {
            GatewayRoutesService.addRoutes(routes).success(function (data) {
                if (data.code === 0) {
                    getMachineRules();
                    gatewayRoutesDialog.close();
                } else {
                    alert('新增路由规则失败!' + data.msg);
                }
            });
        };

        function saveRoutes(routes, edit) {
            GatewayRoutesService.updateRoutes(routes).success(function (data) {
                if (data.code === 0) {
                    getMachineRules();
                    if (edit) {
                        gatewayRoutesDialog.close();
                    } else {
                        confirmDialog.close();
                    }
                } else {
                    alert('修改路由规则失败!' + data.msg);
                }
            });
        };

        var confirmDialog;
        $scope.deleteRoutes = function (routes) {
            $scope.currentRoutes = routes;
            $scope.confirmDialog = {
                title: '删除路由规则',
                type: 'delete_routes',
                attentionTitle: '请确认是否删除如下规则',
                attention: '路由 ID: ' + routes.id ,
                confirmBtnText: '删除',
            };
            confirmDialog = ngDialog.open({
                template: '/app/views/dialog/confirm-dialog.html',
                scope: $scope,
                overlay: true
            });
        };

        $scope.confirm = function () {
            if ($scope.confirmDialog.type === 'delete_routes') {
                deleteRule($scope.currentRule);
            } else {
                console.error('error');
            }
        };

        function deleteRoutes(routes) {
            GatewayFlowService.deleteRoutes(routes).success(function (data) {
                if (data.code === 0) {
                    getMachineRules();
                    confirmDialog.close();
                } else {
                    alert('删除路由规则失败!' + data.msg);
                }
            });
        };

        queryAppMachines();

        function queryAppMachines() {
            MachineService.getAppMachines($scope.app).success(
                function (data) {
                    if (data.code == 0) {
                        if (data.data) {
                            $scope.machines = [];
                            $scope.macsInputOptions = [];
                            data.data.forEach(function (item) {
                                if (item.healthy) {
                                    $scope.macsInputOptions.push({
                                        text: item.ip + ':' + item.port,
                                        value: item.ip + ':' + item.port
                                    });
                                }
                            });
                        }
                        if ($scope.macsInputOptions.length > 0) {
                            $scope.macInputModel = $scope.macsInputOptions[0].value;
                        }
                    } else {
                        $scope.macsInputOptions = [];
                    }
                }
            );
        };
        $scope.$watch('macInputModel', function () {
            if ($scope.macInputModel) {
                getMachineRules();
                getApiNames();
            }
        });
    }]
);
