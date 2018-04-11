package com.ye.goods.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ye.goods.common.Const;
import com.ye.goods.common.ServerResponse;
import com.ye.goods.dao.CategoryMapper;
import com.ye.goods.dao.ProductMapper;
import com.ye.goods.pojo.Category;
import com.ye.goods.pojo.Product;
import com.ye.goods.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductServiceImpl implements IProductService {

    private ProductMapper productMapper;

    private CategoryMapper categoryMapper;

    @Autowired
    public ProductServiceImpl(ProductMapper productMapper, CategoryMapper categoryMapper) {
        this.productMapper = productMapper;
        this.categoryMapper = categoryMapper;
    }

    @Override
    public ServerResponse getDetail(Integer id) {
        Product product = productMapper.selectByPrimaryKey(id);

        return product != null? ServerResponse.SUCCESS(product): ServerResponse.ERROR("Product doesn't exist!");
    }

    @Override
    public ServerResponse<PageInfo> getProductByKeywordCategory(String keyword, Integer categoryId,
                                                                Integer pageNum, Integer pageSize, String orderBy) {
        List<Category> categoryList = categoryMapper.getProductParentId(categoryId);
        List<Integer> categoryIds = new ArrayList<>();

        if (categoryId != null) {
            for (Category category : categoryList) {
                categoryIds.add(category.getId());
            }
        }

        PageHelper.startPage(pageNum, pageSize);
        if (orderBy != null && Const.ProductOrderBy.PRICE_ASC_DESC.contains(orderBy)) {
            String[] orderByList = orderBy.split("_");
            PageHelper.orderBy(orderByList[0] + " " + orderByList[1]);
        }

        List<Product> productList = productMapper.getProductByKeywordCategory(keyword, categoryIds);
        PageInfo pageInfo = new PageInfo<>(productList);
        return ServerResponse.SUCCESS(pageInfo);
    }

    @Override
    public ServerResponse saveOrUpdate(Product product) {
        Product productExist = productMapper.selectByPrimaryKey(product.getId());
        if (productExist != null) {
            return productMapper.updateByPrimaryKeySelective(product)== 1? ServerResponse.SUCCESS(true):
                    ServerResponse.ERROR("Product update  error!");
        }
        return productMapper.insert(product)== 1? ServerResponse.SUCCESS(true):
                ServerResponse.ERROR("Product save error!");
    }

    @Override
    public ServerResponse<PageInfo> all(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Product> productList = productMapper.all();

        PageInfo<Product> pageInfo = new PageInfo<>(productList);
        return ServerResponse.SUCCESS(pageInfo);
    }
}
