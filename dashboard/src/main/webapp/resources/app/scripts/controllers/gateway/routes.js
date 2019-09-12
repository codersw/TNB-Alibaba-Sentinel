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

        getMachineRoutes();
        function getMachineRoutes() {
            if (!$scope.macInputModel) {
                return;
            }

            var mac = $scope.macInputModel.split(':');
            GatewayRoutesService.queryRoutes($scope.app, mac[0], mac[1]).success(
                function (data) {
                    if (data.code === 0 && data.data) {
                        $scope.routes = data.data;
                        $scope.routes.forEach( e => {
                            e.predicates.forEach( f => {
                                var value = "";
                                for(var key in f.args){
                                    value += f.args[key] + ",";
                                }
                                f.value = value === "" ? "" : value.substring(0,value.length-1);
                            });
                        });
                        $scope.routesPageConfig.totalCount = $scope.routes.length;
                    } else {
                        $scope.routes = [];
                        $scope.routesPageConfig.totalCount = 0;
                    }
                });
        };
        $scope.getMachineRoutes = getMachineRoutes;

        getApiNames();
        function getApiNames() {
            if (!$scope.macInputModel) {
                return;
            }

            var mac = $scope.macInputModel.split(':');
            GatewayApiService.queryApis($scope.app, mac[0], mac[1]).success(
                function (data) {
                    if (data.code === 0 && data.data) {
                        $scope.apiNames = [];
                        data.data.forEach(function (api) {
                            $scope.apiNames.push(api["apiName"]);
                        });
                    }
                });
        }

        var gatewayRoutesDialog;
        $scope.editRoutes = function (rule) {
            $scope.currentRoutes = angular.copy(rule);
            $scope.gatewayRoutesDialog = {
                title: '编辑路由规则',
                type: 'edit',
                confirmBtnText: '保存'
            };
            gatewayRoutesDialog = ngDialog.open({
                template: '/app/views/dialog/gateway/routes-dialog.html',
                width: 780,
                overlay: true,
                scope: $scope
            });
        };

        $scope.addNewRoutes = function () {
            var mac = $scope.macInputModel.split(':');
            $scope.currentRoutes = {
                app: $scope.app,
                ip: mac[0],
                port: mac[1],
                id: "",
                uri: "",
                predicates: [{
                    name: "Path",
                    value: "",
                    args: {}
                }]
            };

            $scope.gatewayRoutesDialog = {
                title: '新增路由规则',
                type: 'add',
                confirmBtnText: '新增'
            };

            gatewayRoutesDialog = ngDialog.open({
                template: '/app/views/dialog/gateway/routes-dialog.html',
                width: 780,
                overlay: true,
                scope: $scope
            });
        };

        $scope.saveRoutes = function () {

            if ($scope.gatewayRoutesDialog.type === 'add') {
                addNewRoutes($scope.currentRoutes);
            } else if ($scope.gatewayRoutesDialog.type === 'edit') {
                saveRoutes($scope.currentRoutes, true);
            }
        };

        $scope.addNewMatchPattern = function() {
            var total;
            if ($scope.currentRoutes.predicates == null) {
                $scope.currentRoutes.predicates = [];
                total = 0;
            } else {
                total = $scope.currentRoutes.predicates.length;
            }
            $scope.currentRoutes.predicates.splice(total + 1, 0, {name : '', value: ''});
        };

        $scope.removeMatchPattern = function($index) {
            if ($scope.currentRoutes.predicates.length <= 1) {
                // Should never happen since no remove button will display when only one predicateItem.
                alert('至少有一个路由规则');
                return;
            }
            $scope.currentRoutes.predicates.splice($index, 1);
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
            routes.predicates.forEach( e => {
                var v = e.value.replace("|",",").replace("，",",");
                var arr = v.split(",");
                e.args = {};
                arr.forEach( (f,i) =>{
                    e.args["_genkey_"+i] = f;
                });
            });
            var mac = $scope.macInputModel.split(':');
            routes.app = $scope.app;
            routes.ip = mac[0];
            routes.port = mac[1];
            GatewayRoutesService.addRoutes(routes).success(function (data) {
                if (data.code === 0) {
                    getMachineRoutes();
                    gatewayRoutesDialog.close();
                } else {
                    alert('新增路由规则失败!' + data.msg);
                }
            });
        };

        function saveRoutes(routes, edit) {
            routes.predicates.forEach( e => {
                var v = e.value.replace("|",",").replace("，",",");;
                var arr = v.split(",");
                e.args = {};
                arr.forEach( (f,i) =>{
                    e.args["_genkey_"+i] = f;
                });
            });
            var mac = $scope.macInputModel.split(':');
            routes.app = $scope.app;
            routes.ip = mac[0];
            routes.port = mac[1];
            GatewayRoutesService.updateRoutes(routes).success(function (data) {
                if (data.code === 0) {
                    getMachineRoutes();
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
                deleteRoutes($scope.currentRoutes);
            } else {
                console.error('error');
            }
        };

        function deleteRoutes(routes) {
            var mac = $scope.macInputModel.split(':');
            routes.app = $scope.app;
            routes.ip = mac[0];
            routes.port = mac[1];
            GatewayRoutesService.deleteRoutes(routes).success(function (data) {
                if (data.code === 0) {
                    getMachineRoutes();
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
                    if (data.code === 0) {
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
                getMachineRoutes();
                getApiNames();
            }
        });
    }]
);
