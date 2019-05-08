//控制层
app.controller('goodsController', function ($scope, $controller, $location, goodsService, uploadService, itemCatService, typeTemplateService) {

    $controller('baseController', {$scope: $scope});//继承

    //读取列表数据绑定到表单中  
    $scope.findAll = function () {
        goodsService.findAll().success(
            function (response) {
                $scope.list = response;
            }
        );
    }

    //分页
    $scope.findPage = function (page, rows) {
        goodsService.findPage(page, rows).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }

    //查询实体
    $scope.findOne = function (id) {
        var id = $location.search()["id"];
        goodsService.findOne(id).success(
            function (response) {
                //alert(JSON.stringify(response));
                $scope.entity = response;
                //富文本编辑器内容
                editor.html($scope.entity.goodsDesc.introduction);

                //把图片列表的json串转换成对象
                $scope.entity.goodsDesc.itemImages = JSON.parse($scope.entity.goodsDesc.itemImages);
                //把扩展属性列表的json串转换成对象
                $scope.entity.goodsDesc.customAttributeItems = JSON.parse($scope.entity.goodsDesc.customAttributeItems);
                //把规格信息列表的json串转换成对象
                $scope.entity.goodsDesc.specificationItems = JSON.parse($scope.entity.goodsDesc.specificationItems);
                //把商品sku信息中的spec属性转换成对象
                for (var i = 0; i < $scope.entity.itemList.length; i++) {
                    response.itemList[i].spec = JSON.parse($scope.entity.itemList[i].spec);
                }

            }
        );

    }

    //保存
    $scope.save = function () {
        $scope.entity.goodsDesc.introduction = editor.html();
        var serviceObject;//服务层对象
        if ($scope.entity.goods.id != null) {//如果有ID
            //alert(JSON.stringify($scope.entity.goodsDesc.introduction));
            serviceObject = goodsService.update($scope.entity); //修改
        } else {
            serviceObject = goodsService.add($scope.entity);//增加
        }
        serviceObject.success(
            function (response) {
                alert(response.message);
                if (response.success) {
                    //重新查询
                    window.location.href="goods.html";
                }
            }
        );
    }


    //批量删除
    $scope.dele = function () {
        //获取选中的复选框
        goodsService.dele($scope.selectIds).success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();//刷新列表
                }
            }
        );
    }

    $scope.searchEntity = {};//定义搜索对象

    //搜索
    $scope.search = function (page, rows) {
        goodsService.search(page, rows, $scope.searchEntity).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }

    $scope.add = function () {
        $scope.entity.goodsDesc.introduction = editor.html();
        goodsService.add($scope.entity).success(function (response) {

            if (response.success) {
                alert(response.message)
                $scope.entity = {};
                editor.html("");
            } else {
                alert(response.message)
            }
        })
    }

    $scope.image_entity={url:''};
    $scope.uploadFile = function () {
        uploadService.uploadFile().success(function (response) {
            if (response.success) {
                $scope.image_entity.url = response.message;
            } else {
                alert(response.message)
            }
        }).error(function () {
            alert("上传错误")
        })
    }

    //定义页面实体结构
    //{goods:{},goodsDesc:{itemImages:图片列表,specificationItems:规格列表}}
    $scope.entity = {goods: {}, goodsDesc: {itemImages: [], specificationItems: []}};

    $scope.add_image_entity = function () {
        $scope.entity.goodsDesc.itemImages.push($scope.image_entity);
    }
    $scope.remove_image_entity = function (index) {
        $scope.entity.goodsDesc.itemImages.splice(index, 1);
    }


    //查询一级分类
    $scope.selectItemCat1List = function () {
        itemCatService.findByParentId(0).success(function (response) {
            $scope.itemCat1List = response;
        })
    }

    //查询二级分类,监控父ID变化
    $scope.$watch("entity.goods.category1Id", function (newValue, oldValue) {
     if (newValue>0){
         itemCatService.findByParentId(newValue).success(function (response) {
             $scope.itemCat2List = response;
             $scope.itemCat3List = [];
         })
     }

    })

    //查询三级分类,监控父ID变化
    $scope.$watch("entity.goods.category2Id", function (newValue, oldValue) {
        itemCatService.findByParentId(newValue).success(function (response) {
            $scope.itemCat3List = response;

        })
    })

    //选择三级类目后，更新模板id
    $scope.$watch("entity.goods.category3Id", function (newValue, oldValue) {
        itemCatService.findOne(newValue).success(function (response) {
            $scope.entity.goods.typeTemplateId = response.typeId;
        })
    });

    $scope.$watch("entity.goods.typeTemplateId", function (newValue, oldValue) {
        typeTemplateService.findOne(newValue).success(function (response) {
            //把品牌json串转换为品牌列表
            $scope.typeTemplate = response;
            $scope.typeTemplate.brandIds = JSON.parse(response.brandIds);

            //把扩展属性json串转换为列表
            var id = $location.search()["id"];
            if (id == null) {
               // $scope.entity.goodsDesc.customAttributeItems = JSON.parse(response.customAttributeItems);
                $scope.entity.goodsDesc.customAttributeItems = JSON.parse(response.customAttributeItems);
            }

            typeTemplateService.findSpecList(newValue).success(function (response) {
                //alert(JSON.stringify(response));
                $scope.specList = response;
            });


        })
    })

    $scope.updateSpecAttribute = function ($event, specName, optionName) {
        //检查我们的规格名称有没有被点击过
        //var obj = $scope.searchObjectByKey($scope.entity.goodsDesc.specificationItems,'attributeName',specName);
        var obj = $scope.searchObjectByKey($scope.entity.goodsDesc.specificationItems, 'attributeName', specName);
        if (obj == null) {
            $scope.entity.goodsDesc.specificationItems.push(
                {'attributeName': specName, 'attributeValue': [optionName]}
            )
        } else {
            if ($event.target.checked) {
                obj.attributeValue.push(optionName);
            } else {
                var optionIndex = obj.attributeValue.indexOf(optionName);
                obj.attributeValue.splice(optionIndex, 1);
                if (obj.attributeValue.length < 1) {
                    var specIndex = $scope.entity.goodsDesc.specificationItems.indexOf(obj);
                    $scope.entity.goodsDesc.specificationItems.splice(specIndex, 1);
                }
            }
        }
        $scope.createItemList();
    }


    //1.创建$scope.createItemList方法，同时创建一条有基本数据，不带规格的初始数据
    $scope.createItemList = function () {
        //参考: $scope.entity.itemList=[{spec:{},price:0,num:99999,status:'0',isDefault:'0' }]
        $scope.entity.itemList = [{spec: {}, price: 0, num: 99, status: '0', isDefault: '0'}]
        //2.查找遍历所有已选择的规格列表，后续会重复使用它，所以我们可以抽取出个变量items
        var items = $scope.entity.goodsDesc.specificationItems;

        for (var i = 0; i < items.length; i++) {
            //9.回到createItemList方法中，在循环中调用addColumn方法，并让itemList重新指向返回结果;
            $scope.entity.itemList = addColumn($scope.entity.itemList, items[i].attributeName, items[i].attributeValue);
        }
    }
    //3.抽取addColumn(当前的表格，列名称，列的值列表)方法，用于每次循环时追加列

    addColumn = function (list, specName, optionValue) {
        //4.编写addColumn逻辑，当前方法要返回添加所有列后的表格，定义新表格变量newList
        var newList = [];
        //5.在addColumn添加两重嵌套循环，一重遍历之前表格的列表，二重遍历新列值列表
        for (var i = 0; i < list.length; i++) {
            for (var j = 0; j < optionValue.length; j++) {
                // 6.在第二重循环中，使用深克隆技巧，把之前表格的一行记录copy所有属性，
                // 用到var newRow = JSON.parse(JSON.stringify(之前表格的一行记录));
                var newRow = JSON.parse(JSON.stringify(list[i]));
                //7.接着第6步，向newRow里追加一列
                newRow.spec[specName] = optionValue[j];
                //8.把新生成的行记录，push到newList中
                newList.push(newRow);
            }
        }
        return newList;
    }

    $scope.status = ['未审核', '已审核', '审核未通过', '关闭'];//商品状态
    $scope.itemCatList = [];//商品分类列表
    $scope.findItemCatList = function () {
        itemCatService.findAll().success(function (response) {
            for (var i = 0; i < response.length; i++) {
                $scope.itemCatList[response[i].id] = response[i].name;
            }
        })
    }

    /**
     * 识别checkbox是否要勾中
     * @param specName 当前后规格名称
     * @param optionName 当前的选项名称
     */
    $scope.checkAttributeValue = function (specName, optionName) {
        var obj = $scope.searchObjectByKey($scope.entity.goodsDesc.specificationItems, 'attributeName', specName);
        if (obj != null) {
            //如果找到相应的规格选项
            if (obj.attributeValue.indexOf(optionName) > -1) {
                return true;
            }
        }
        return false;
    }


});
