app.controller("IndexController",function ($scope,contentService) {
    //广告集合
    $scope.contentList=[];

    $scope.findByCategoryId=function () {

        //查询轮播图
        contentService.findByCategoryId(1).success(function (response) {
            $scope.contentList[1]=response;
        })
    }
})