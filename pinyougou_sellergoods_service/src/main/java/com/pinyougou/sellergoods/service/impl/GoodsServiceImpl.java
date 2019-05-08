package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.abel533.entity.Example;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import com.pinyougou.pojogroup.Goods;
import com.pinyougou.sellergoods.service.GoodsService;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 业务逻辑实现
 * @author Steven
 *
 */
@Service(timeout = 5000,interfaceClass = GoodsService.class)
@Transactional
public class GoodsServiceImpl implements GoodsService {

	@Autowired
	private TbGoodsMapper goodsMapper;
	@Autowired
	private TbGoodsDescMapper goodsDescMapper;
	@Autowired
	private TbItemMapper itemMapper;
	@Autowired
	private TbBrandMapper brandMapper;
	@Autowired
	private TbItemCatMapper itemCatMapper;
	@Autowired
	private TbSellerMapper sellerMapper;



	/**
	 * 查询全部
	 */
	@Override
	public List<TbGoods> findAll() {
		return goodsMapper.select(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		
		PageResult<TbGoods> result = new PageResult<TbGoods>();
        //设置分页条件
        PageHelper.startPage(pageNum, pageSize);

        //查询数据
        List<TbGoods> list = goodsMapper.select(null);
        //保存数据列表
        result.setRows(list);

        //获取总记录数
        PageInfo<TbGoods> info = new PageInfo<TbGoods>(list);
        result.setTotal(info.getTotal());
		return result;
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbGoods goods) {
		goodsMapper.insertSelective(goods);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(Goods goods){
		goods.getGoods().setAuditStatus("0");
		goodsMapper.updateByPrimaryKeySelective(goods.getGoods());
		goodsDescMapper.updateByPrimaryKeySelective(goods.getGoodsDesc());
		TbItem where = new TbItem();
		where.setGoodsId(goods.getGoods().getId());
		itemMapper.delete(where);
		saveItemList(goods);

	}
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Goods findOne(Long id){
		Goods goods = new Goods();
		TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
		goods.setGoods(tbGoods);
		TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(id);
		goods.setGoodsDesc(tbGoodsDesc);

		//查询商品SKU信息
		TbItem where = new TbItem();
		where.setGoodsId(id);
		List<TbItem> items = itemMapper.select(where);
		goods.setItemList(items);

		return goods;
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		//数组转list
		List longs = Arrays.asList(ids);
		//构建查询条件
		Example example = new Example(TbGoods.class);
		Example.Criteria criteria = example.createCriteria();
		criteria.andIn("id", longs);

		//跟据查询条件删除数据
		//goodsMapper.deleteByExample(example);

		//逻辑删除商品信息
		TbGoods recourd = new TbGoods();
		recourd.setIsDelete("1");  //标识数据已删除
		goodsMapper.updateByExampleSelective(recourd, example);
	}
	
	
	@Override
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
		PageResult<TbGoods> result = new PageResult<TbGoods>();
        //设置分页条件
        PageHelper.startPage(pageNum, pageSize);

        //构建查询条件
        Example example = new Example(TbGoods.class);
        Example.Criteria criteria = example.createCriteria();
		
		if(goods!=null){			
						//如果字段不为空
			if (goods.getSellerId()!=null && goods.getSellerId().length()>0) {
				criteria.andEqualTo("sellerId", goods.getSellerId());
				//criteria.andLike("sellerId", "%" + goods.getSellerId() + "%");
			}
			//如果字段不为空
			if (goods.getGoodsName()!=null && goods.getGoodsName().length()>0) {
				criteria.andLike("goodsName", "%" + goods.getGoodsName() + "%");
			}
			//如果字段不为空
			if (goods.getAuditStatus()!=null && goods.getAuditStatus().length()>0) {
				criteria.andLike("auditStatus", "%" + goods.getAuditStatus() + "%");
			}
			//如果字段不为空
			if (goods.getIsMarketable()!=null && goods.getIsMarketable().length()>0) {
				criteria.andLike("isMarketable", "%" + goods.getIsMarketable() + "%");
			}
			//如果字段不为空
			if (goods.getCaption()!=null && goods.getCaption().length()>0) {
				criteria.andLike("caption", "%" + goods.getCaption() + "%");
			}
			//如果字段不为空
			if (goods.getSmallPic()!=null && goods.getSmallPic().length()>0) {
				criteria.andLike("smallPic", "%" + goods.getSmallPic() + "%");
			}
			//如果字段不为空
			if (goods.getIsEnableSpec()!=null && goods.getIsEnableSpec().length()>0) {
				criteria.andLike("isEnableSpec", "%" + goods.getIsEnableSpec() + "%");
			}
			//如果字段不为空
			/*if (goods.getIsDelete()!=null && goods.getIsDelete().length()>0) {
				criteria.andLike("isDelete", "%" + goods.getIsDelete() + "%");
			}*/
			//查询没删除的数据
			criteria.andIsNull("isDelete");
	
		}

        //查询数据
        List<TbGoods> list = goodsMapper.selectByExample(example);
        //保存数据列表
        result.setRows(list);

        //获取总记录数
        PageInfo<TbGoods> info = new PageInfo<TbGoods>(list);
        result.setTotal(info.getTotal());
		
		return result;
	}

    @Override
    public void add(Goods goods) {
		//设置状态为未审核
        goods.getGoods().setAuditStatus("0");
		goodsMapper.insertSelective(goods.getGoods());

		//设置商品id
		goods.getGoodsDesc().setGoodsId(goods.getGoods().getId());
		goodsDescMapper.insertSelective(goods.getGoodsDesc());

		saveItemList(goods);


	}

	@Override
	public void updateStatus(Long[] ids, String status) {
		//要更新的内容
		TbGoods record = new TbGoods();
		record.setAuditStatus(status);
		//更新条件组装
		Example where = new Example(TbGoods.class);
		Example.Criteria criteria = where.createCriteria();
		List longs = Arrays.asList(ids);
		criteria.andIn("id", longs);
		//执行更新
		goodsMapper.updateByExampleSelective(record, where);

	}

	private void saveItemList(Goods goods) {
		if ("1".equals(goods.getGoods().getIsEnableSpec())){
			for (TbItem item : goods.getItemList()) {
				//计算标题
				String title = goods.getGoods().getGoodsName();
				Map<String,String> specMap =JSON.parseObject(item.getSpec(),Map.class);
				for (String spec : specMap.values()) {
					title += spec + " ";
				}
				item.setTitle(title);  //标题
				//初始化商品sku信息
				setItemValus(goods, item);
				//保存sku
				itemMapper.insertSelective(item);
			}
		}else {
			TbItem item=new TbItem();
			item.setTitle(goods.getGoods().getGoodsName());//商品KPU+规格描述串作为SKU名称
			item.setPrice(goods.getGoods().getPrice());//价格
			item.setStatus("1");//状态
			item.setIsDefault("1");//是否默认
			item.setNum(99999);//库存数量
			item.setSpec("{}");
			//初始化商品sku信息
			setItemValus(goods, item);
			itemMapper.insertSelective(item);
		}
	}


	/**
	 * 初始化商品sku信息
	 * @param goods
	 * @param item
	 */
	private void setItemValus(Goods goods, TbItem item) {
		item.setSellPoint(goods.getGoods().getCaption());  //卖点
		List<Map> imgMap = JSON.parseArray(goods.getGoodsDesc().getItemImages(), Map.class);
		if (imgMap != null && imgMap.size() > 0) {
			//商品图片
			item.setImage(imgMap.get(0).get("url").toString());
		}
		item.setCategoryid(goods.getGoods().getCategory3Id());  //商品分类id
		TbItemCat itemCat = itemCatMapper.selectByPrimaryKey(item.getCategoryid());
		item.setCategory(itemCat.getName());  //分类

		//创建日期
		item.setCreateTime(new Date());
		//更新日期
		item.setUpdateTime(item.getCreateTime());

		//所属SPU-id
		item.setGoodsId(goods.getGoods().getId());
		//所属商家
		item.setSellerId(goods.getGoods().getSellerId());
		TbSeller seller = sellerMapper.selectByPrimaryKey(item.getSellerId());
		item.setSeller(seller.getNickName());

		//品牌信息
		TbBrand brand = brandMapper.selectByPrimaryKey(goods.getGoods().getBrandId());
		item.setBrand(brand.getName());
	}

}
