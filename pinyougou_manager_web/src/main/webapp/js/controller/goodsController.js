 //控制层 
app.controller('goodsController' ,function($scope,$controller,$location,goodsService,itemCatService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){
        var id = $location.search()["id"];
		goodsService.findOne(id).success(
			function(response){
				//alert(JSON.stringify(response));
				$scope.entity= response;
                //填充富文本
                editor.html($scope.entity.goodsDesc.introduction);

                //把图片列表的json串转换成对象
                $scope.entity.goodsDesc.itemImages = JSON.parse($scope.entity.goodsDesc.itemImages);
                //把扩展属性列表的json串转换成对象
                $scope.entity.goodsDesc.customAttributeItems = JSON.parse($scope.entity.goodsDesc.customAttributeItems);
                //把规格信息列表的json串转换成对象
                $scope.entity.goodsDesc.specificationItems = JSON.parse($scope.entity.goodsDesc.specificationItems);

                //把商品sku信息中的spec属性转换成对象
                for(var i = 0; i < response.itemList.length; i++){
                    response.itemList[i].spec = JSON.parse(response.itemList[i].spec);
                }
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=goodsService.update( $scope.entity ); //修改  
		}else{
			serviceObject=goodsService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	$scope.reloadList();//重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}

    $scope.status=['未审核','已审核','审核未通过','关闭'];//商品状态
    $scope.itemCatList=[];//商品分类列表
    //查询商品分类
    $scope.findItemCatList=function(){
        itemCatService.findAll().success(
            function(response){
                for(var i=0;i<response.length;i++){
                    $scope.itemCatList[response[i].id ]=response[i].name;
                }
            }
        );
    }

    //更新状态
    $scope.updateStatus=function (status) {
        goodsService.updateStatus($scope.selectIds,status).success(function (response) {
            alert(response.message);
            //如果修改成功
            if(response.success){
                $scope.reloadList();//刷新列表
                $scope.selectIds=[];//清空ID集合
            }
        })
    }

    //所有的商品分类列表，下标就是我们的商品分类id，类似Map结构
    $scope.itemCatList=[];
    //加载所有商品分类
    $scope.findItemCatList=function () {
        itemCatService.findAll().success(function (response) {
            for(var i = 0; i < response.length; i++){
                $scope.itemCatList[response[i].id] = response[i].name;
            }
        })
    }

    //加载商品分类一级目录
    $scope.selectItemCat1List=function () {
        itemCatService.findByParentId(0).success(function (response) {
            $scope.itemCat1List = response;

        })
    }

    //二级分类加载
    //$watch(监听的变量名，函数(新的值,原来的值))
    $scope.$watch("entity.goods.category1Id",function (newValue,oldValue) {
        itemCatService.findByParentId(newValue).success(function (response) {
            $scope.itemCat2List = response;
            //$scope.entity.goods.category2Id = -1;

            $scope.itemCat3List = [];
        })
    });
    //三级分类加载
    //$watch(监听的变量名，函数(新的值,原来的值))
    $scope.$watch("entity.goods.category2Id",function (newValue,oldValue) {
        itemCatService.findByParentId(newValue).success(function (response) {
            $scope.itemCat3List = response;
        })
    });




});	
