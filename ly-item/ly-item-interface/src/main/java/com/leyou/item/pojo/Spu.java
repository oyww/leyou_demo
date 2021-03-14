package com.leyou.item.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * @author li
 */
@Table(name = "tb_spu")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Spu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long brandId;
    /**
     * 1级类目
     */
    private Long cid1;
    /**
     * 2级类目
     */
    private Long cid2;
    /**
     * 3级类目
     */
    private Long cid3;
    /**
     * 标题
     */
    private String title;
    /**
     * 子标题
     */
    private String subTitle;
    /**
     * 是否上架
     */
    private Boolean saleable;
    /**
     * 是否有效，逻辑删除使用
     */
    private Boolean valid;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 最后修改时间
     */
    @JsonIgnore
    private Date lastUpdateTime;
    @Transient
    private String bname;

    @Transient
    private String cname;

    @Transient
    /**
     * 商品大文本信息,spu一对一，封装表单数据用
     */
    private SpuDetail spuDetail;
    @Transient
    /**
     * sku集合，封装表单数据用
     */
    private List<Sku> skus;


}