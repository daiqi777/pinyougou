package com.pinyougou.sellergoods.service;

import com.pinyougou.entity.PageResult;
import com.pinyougou.pojo.TbBrand;

import java.util.List;

public interface BrandService {

    /**
     * 查询所有品牌
     * @return
     */
    public List<TbBrand> findAll();

    /**
     *
     * 分页查询数据,存储在pageresult实体中
     * @param pageNum
     * @param pageSize
     * @return
     */
    public PageResult<TbBrand> findPage(Integer pageNum,Integer pageSize,TbBrand Brand);


    /**
     *
     *增加商品
     * @param brand
     */
    public void saveBrand(TbBrand brand);


    /**
     * 跟据id查询数据
     * @param id
     * @return
     */
    public TbBrand queryById(Long id);

    /**
     * 修改品牌
     * @param brand
     */
    public void updateBrand(TbBrand brand);


    /**
     * 跟据id列表删除数据
     * @param ids
     */
    public void deleteBrand(Long[] ids);


}
