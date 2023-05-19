package com.yizhi.student.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yizhi.common.utils.R;
import com.yizhi.system.domain.UserDO;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.security.Security;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.yizhi.student.dao.StudentInfoDao;
import com.yizhi.student.domain.StudentInfoDO;
import com.yizhi.student.service.StudentInfoService;
import org.springframework.util.StringUtils;


@Service
public class StudentInfoServiceImpl extends ServiceImpl<StudentInfoDao, StudentInfoDO> implements StudentInfoService {
    //因为此处写操作会多一点而且后台相对并发量不大，所以不建议使用redis缓存
  //    private static final String STUDENT_INFO_KEY = "student_info_key";
  //@Autowired
  //private RedisTemplate redisTemplate;

    /**
     * 表单回显
     * @param id
     * @return
     */
    @Override
    public StudentInfoDO get(Integer id) {
        System.out.println("======service层中传递过来的id参数是：" + id + "======");
        return this.getById(id);
    }

    /**
     * 封装查询条件
     * @param map
     * @return
     */
    private LambdaQueryWrapper conditionalQuery(Map<String, Object> map) {
        LambdaQueryWrapper<StudentInfoDO> queryWrapper = new LambdaQueryWrapper<>();
        String key = (String) map.get("name");
        if (!StringUtils.isEmpty(key)) {
            queryWrapper.and(item -> {
                item.eq(StudentInfoDO::getStudentId, key).or()
                        .like(StudentInfoDO::getStudentName, key)
                        .or().eq(StudentInfoDO::getStudentSex, key)
                        .or().like(StudentInfoDO::getTelephone, key);
            });
        }
        String tocollegeId = (String) map.get("tocollegeId");
        System.out.println("tocollegeId:" + tocollegeId);
        if (!StringUtils.isEmpty(tocollegeId)) {
            queryWrapper.eq(StudentInfoDO::getTocollege, Integer.parseInt(tocollegeId));
        }
        String tomajorId = (String) map.get("tomajorId");
        System.out.println("tomajorId:" + tomajorId);
        if (!StringUtils.isEmpty(tomajorId)) {
            queryWrapper.eq(StudentInfoDO::getTomajor, Integer.parseInt(tomajorId));
        }
        String toclassId = (String) map.get("classId");
        if (!StringUtils.isEmpty(toclassId)) {
                queryWrapper.eq(StudentInfoDO::getClassId, Integer.parseInt(toclassId));
        }
        return queryWrapper;
    }

    /**
     * 获取学生信息列表
     * @param map
     * @return
     */
    @Override
    public List<StudentInfoDO> listStudentInfo(Map<String, Object> map) {

        LambdaQueryWrapper<StudentInfoDO> queryWrapper = conditionalQuery(map);
        List<StudentInfoDO> list = this.list(queryWrapper);
        return list;
    }
    //"===================================================================================="

    /**
     * 获取查询数量
     * @param map
     * @return
     */
    @Override
    public int count(Map<String, Object> map) {
        LambdaQueryWrapper<StudentInfoDO> queryWrapper = new LambdaQueryWrapper<>();
        return (int) this.count(queryWrapper);
    }

    /**
     * 获取当前登录用户的id
     * @return
     */
    private Long getUserId() {
        UserDO user = (UserDO) SecurityUtils.getSubject().getPrincipal();
        Long userId = user.getUserId();
        return userId;
    }
    @Override
    public R updateStudentInfo(StudentInfoDO studentInfo) {
        Long userId = getUserId();
        studentInfo.setEditTime(new Date());
        studentInfo.setEditUserid(userId.intValue());
        boolean b = this.updateById(studentInfo);
        if (b) {
            return R.ok();
        } else {
            return R.error();
        }


    }

    @Override
    public R remove(Integer id) {
        boolean b = this.removeById(id);
        if (b) {
            return R.ok();
        } else {
            return R.error();
        }
    }

    @Override
    public R batchRemoveById(Integer[] ids) {
        boolean b = this.removeByIds(Arrays.asList(ids));
        if (b) {
            return R.ok();
        } else {
            return R.error();
        }
    }
    @Override
    public R saveStudentInfo(StudentInfoDO studentInfoDO) {
        studentInfoDO.setAddTime(new Date());
        Long userId = getUserId();
        studentInfoDO.setAddUserid(userId.intValue());
        boolean b = this.save(studentInfoDO);
        if (b) {
            return R.ok();
        } else {
            return R.error();
        }
    }

}
