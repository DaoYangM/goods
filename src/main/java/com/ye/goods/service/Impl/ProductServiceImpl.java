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
import com.ye.goods.utils.Properties.Properties;
import com.ye.goods.vo.product.ProductDetailVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductServiceImpl implements IProductService {

    private ProductMapper productMapper;

    private CategoryMapper categoryMapper;

    private Properties properties;

    @Autowired
    public ProductServiceImpl(ProductMapper productMapper, CategoryMapper categoryMapper, Properties properties) {
        this.productMapper = productMapper;
        this.categoryMapper = categoryMapper;
        this.properties = properties;
    }

    @Override
    public ServerResponse getDetail(Integer id) {
        Product product = productMapper.selectByPrimaryKey(id);

        if (product != null) {
            ProductDetailVO productDetailVO = new ProductDetailVO();
            assembleProductDetailVO(product, productDetailVO);

            return ServerResponse.SUCCESS(productDetailVO);
        }

        return ServerResponse.ERROR("ProductDetailVO doesn't exist!");
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
        PageInfo<Product> pageInfo = new PageInfo<>(productList);
        return ServerResponse.SUCCESS(pageInfo);
    }

    @Override
    public ServerResponse saveOrUpdate(Product product) {
        if (product.getId() != null) {
            Product productExist = productMapper.selectByPrimaryKey(product.getId());
            if (productExist != null) {
                return productMapper.updateByPrimaryKeySelective(product)== 1? ServerResponse.SUCCESS(true):
                        ServerResponse.ERROR("ProductDetailVO update  error!");
            }
        }

        return productMapper.insert(product)== 1? ServerResponse.SUCCESS(true):
                ServerResponse.ERROR("ProductDetailVO save error!");
    }

    @Override
    public ServerResponse<PageInfo> all(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Product> productList = productMapper.all();

        PageInfo<Product> pageInfo = new PageInfo<>(productList);
        return ServerResponse.SUCCESS(pageInfo);
    }

    @Override
    public ServerResponse updateStatus(Integer productId, Integer status) {
        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);

        return productMapper.updateByPrimaryKeySelective(product) == 1?
                ServerResponse.SUCCESS("更新商品状态成功"): ServerResponse.ERROR("更新商品状态失败");

    }

    @Override
    public ServerResponse search(String productName, Integer categoryId, Integer productId, Integer pageNum, Integer pageSize) {

        PageHelper.startPage(pageNum, pageSize);
        List<Product> productList = new ArrayList<>();
        if (StringUtils.isNotBlank(productName) || categoryId != null || productId !=null) {
            productName = "%" + productName + "%";
             productList = productMapper.selectByProductNameAndCategoryId(productName, categoryId, productId);
        } else if (StringUtils.isBlank(productName))
            productList = productMapper.all();

        PageInfo<Product> pageInfo = new PageInfo<>(productList);
        return ServerResponse.SUCCESS(pageInfo);
    }

    private void assembleProductDetailVO(Product product, ProductDetailVO productDetailVO) {
        BeanUtils.copyProperties(product, productDetailVO);
        productDetailVO.setImageHost(properties.getFtp().getImageHost());

        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());

        if (category == null)
            productDetailVO.setParentCategoryId(0);
        else
            productDetailVO.setParentCategoryId(category.getParentId());
    }
}
