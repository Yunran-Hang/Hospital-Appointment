package com.nbdeyy.controller.user;

import com.nbdeyy.result.Result;
import com.nbdeyy.service.CategoryService;
import com.nbdeyy.vo.CategoryDepartmentVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/user/category")
@Tag(name = "用户模块", description = "用户端相关接口")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 获得一二级分类列表
     * @return
     */
    @GetMapping
    @Operation(summary = "获得分类和科室列表接口")
    @Cacheable(cacheNames = "categoryDepartments")
    public Result<CategoryDepartmentVO> getCategoryList(){
        log.info("获得一二级分类列表");
        CategoryDepartmentVO categoryDepartmentVO = categoryService.getCategoryList();
        return Result.success(categoryDepartmentVO);
    }
}

// 使用redis缓存
//@RestController
//@Slf4j
//@RequestMapping("/user/category")
//@Tag(name = "用户模块", description = "用户端相关接口")
//public class CategoryController {
//
//    @Autowired
//    private CategoryService categoryService;
//    @Autowired
//    private RedisTemplate redisTemplate;
//
//    /**
//     * 获得一二级分类列表
//     * @return
//     */
//    @GetMapping
//    @Operation(summary = "获得分类和科室列表接口")
//    public Result<CategoryDepartmentVO> getCategoryList(){
//        log.info("获得一二级分类列表");
//        // 构造redis的key
//        String key = "categoryDepartment";
//        CategoryDepartmentVO categoryDepartmentVO = (CategoryDepartmentVO) redisTemplate.opsForValue().get(key);
//        if (categoryDepartmentVO != null){
//            // 如果存在，直接返回，无须再查询数据库
//            log.info("本次从redis中获得一二级分类列表");
//            return Result.success(categoryDepartmentVO);
//        }
//        categoryDepartmentVO = categoryService.getCategoryList();
//        redisTemplate.opsForValue().set(key, categoryDepartmentVO);
//        return Result.success(categoryDepartmentVO);
//    }
//}
