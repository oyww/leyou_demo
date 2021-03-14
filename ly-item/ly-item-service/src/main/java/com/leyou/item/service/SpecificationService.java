package com.leyou.item.service;

import com.leyou.common.enums.ExceptionEnums;
import com.leyou.common.exception.LyException;
import com.leyou.item.mapper.SpecGroupMapper;
import com.leyou.item.mapper.SpecParamMapper;
import com.leyou.item.mapper.SpecificationMapper;
import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import com.leyou.item.pojo.Specification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SpecificationService {
    @Autowired
    private SpecGroupMapper specGroupMapper;
    @Autowired
    private SpecParamMapper specParamMapper;

    @Autowired
    private SpecificationMapper specificationMapper;

    /**
     * 根据分类id查询规格组
     *
     * @param cid
     * @return
     */
    public List<SpecGroup> queryGroupByCid(Long cid) {
        SpecGroup group = new SpecGroup();
        group.setCid(cid);
        List<SpecGroup> list = specGroupMapper.select(group);
        if (CollectionUtils.isEmpty(list)) {
            throw new LyException(ExceptionEnums.SPECGROUP_NOT_FOND);
        }
        return list;
    }

    public List<SpecParam> queryParamList(Long gid, Long cid, Boolean searching) {
        SpecParam specParam = new SpecParam();
        specParam.setCid(cid);
        specParam.setGroupId(gid);
        specParam.setSearching(searching);

        List<SpecParam> list = specParamMapper.select(specParam);
        if (CollectionUtils.isEmpty(list)) {
            throw new LyException(ExceptionEnums.SPECPARAM_NOT_FOND);
        }
        return list;
    }

    public Specification queryById(Long id) {
        return this.specificationMapper.selectByPrimaryKey(id);
    }

    public List<SpecGroup> querySpecsByCid(Long cid) {
/*        SpecParam param = new SpecParam();
        groups.forEach(g -> {
            // 查询组内参数
            g.setParams(queryParamList(g.getId(), cid, true));
        });*/
        // 查询规格组
        List<SpecGroup> specGroups = queryGroupByCid(cid);
        //查询当前分类下的参数
        List<SpecParam> specParams = queryParamList(null, cid, null);
        //先把规格参数变成map，map的key是规格组id，只是组下的所有参数
        Map<Long, List<SpecParam>> paramMap = new HashMap<>();
        for (SpecParam param : specParams) {
            if (!paramMap.containsKey(param.getId())) {
                //组id在map中不存在就新增一个新list
                paramMap.put(param.getId(), new ArrayList<>());
            }
            //无论之前有没有list，现在一定有，取出list填充数据
            paramMap.get(param.getId()).add(param);
        }
        //填充param到group中
        for (SpecGroup group : specGroups) {
//            if(group.getId() == 28l){
//                group.setId(13l);
//            }
            group.setParams(paramMap.get(group.getId()));
        }
        return specGroups;
    }
}
