package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.entity.PageResult;
import com.pinyougou.entity.Result;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("brand")
public class BrandController {
    @Reference
    private BrandService brandService;

    @RequestMapping("findAll")
    public List<TbBrand> findAll(){
        return brandService.findAll();
    }


    @RequestMapping("findPage")
    public PageResult<TbBrand> findPage(Integer pageNum, Integer pageSize,@RequestBody TbBrand brand){
        //System.out.println(brandService.findPage(pageNum, pageSize));
        return  brandService.findPage(pageNum, pageSize,brand);
    }

    @RequestMapping("saveBrand")
    public Result saveBrand(@RequestBody TbBrand brand){
        try {
            brandService.saveBrand(brand);
            return new Result(true,"保存成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Result(false,"保存失败");
    }


    @RequestMapping("queryById")
    public TbBrand queryById(Long id){
       return brandService.queryById(id);

    }


    @RequestMapping("updateBrand")
    public Result updateBrand(@RequestBody TbBrand brand){
        try {
            brandService.updateBrand(brand);
            return new Result(true,"修改品牌成功！");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Result(false,"修改品牌失败！");
    }

    @RequestMapping("delete")
    public Result deleteBrand(Long[] ids){
        try {
            brandService.deleteBrand(ids);
            return new Result(true,"删除品牌成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Result(true,"删除品牌失败");
    }

}
