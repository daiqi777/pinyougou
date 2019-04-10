package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.abel533.entity.Example;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.entity.PageResult;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

@Service
public class BrandServiceImpl implements BrandService {

    @Autowired
    private TbBrandMapper brandMapper;


    @Override
    public List<TbBrand> findAll() {

        return brandMapper.select(null);
    }

    @Override
    public PageResult<TbBrand> findPage(Integer pageNum,Integer pageSize,TbBrand brand) {

        PageResult<TbBrand> result = new PageResult<>();
        //设置分页条件
        PageHelper.startPage(pageNum,pageSize);
        //查询数据
        //List<TbBrand> brands = tbBrandMapper.select(null);

        Example example = new Example(TbBrand.class);
        Example.Criteria criteria = example.createCriteria();
        if (brand.getName()!=null && brand.getName().trim().length()>0){
        criteria.andLike("name","%"+brand.getName()+"%" );
        }
        if (brand.getFirstChar()!=null && brand.getFirstChar().trim().length()>0){
        criteria.andEqualTo("firstChar",brand.getFirstChar() );
        }

        List<TbBrand> brands = brandMapper.selectByExample(example);
        //保存数据列表
        result.setRows(brands);

        //获取总记录数
        PageInfo<TbBrand> info = new PageInfo<>(brands);
        result.setTotal(info.getTotal());

        return result;
    }

    @Override
    public void saveBrand(TbBrand brand) {
        brandMapper.insertSelective(brand);
    }

    @Override
    public TbBrand queryById(Long id) {
        return brandMapper.selectByPrimaryKey(id);
    }

    @Override
    public void updateBrand(TbBrand brand) {
        brandMapper.updateByPrimaryKeySelective(brand);
    }

    @Override
    public void deleteBrand(Long[] ids) {

        Example example = new Example(TbBrand.class);
        Example.Criteria criteria = example.createCriteria();

        List longs = Arrays.asList(ids);
        criteria.andIn("id",longs );

        brandMapper.deleteByExample(example);
    }
}
