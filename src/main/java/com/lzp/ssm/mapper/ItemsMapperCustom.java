package com.lzp.ssm.mapper;

import com.lzp.ssm.po.ItemsCustom;
import com.lzp.ssm.po.ItemsQueryVo;

import java.util.List;

/**
 * Created by lenovo on 2017/8/11.
 */

public interface ItemsMapperCustom {
    //商品查询列表
    List<ItemsCustom> findItemsList(ItemsQueryVo itemsQueryVo)throws Exception;
}