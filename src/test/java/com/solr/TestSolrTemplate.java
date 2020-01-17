package com.solr;

import com.solr.entity.Item;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.ScoredPage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:beans.xml")
public class TestSolrTemplate {

    @Autowired
    private SolrTemplate solrTemplate;

    @Test
    public void testAdd () {
        Item item = new Item();
        item.setId(2L);
        item.setBrand("华为");
        item.setCategory("手机");
        item.setGoodsId(10L);
        item.setSeller("华为2号专卖店");
        item.setTitle("荣耀V20");
        item.setPrice("2000");

        solrTemplate.saveBean("collection1", item);
        solrTemplate.commit("collection1");
    }

    @Test
    public void testFindById () {
        Optional<Item> optionalItem = solrTemplate.getById("collection1", "1", Item.class);
        System.out.println(optionalItem.get());
    }
    
    @Test
    public void testDelete () {
        solrTemplate.deleteByIds("collection1", "2");
        solrTemplate.commit("collection1");
    }

    @Test
    public void testAddList(){
        List<Item> list = new ArrayList();
        for(int i = 0; i < 100; i++){
            Item item = new Item();
            item.setId(i+1L)
                .setBrand("华为")
                .setCategory("手机")
                .setGoodsId(1L)
                .setSeller("华为2号专卖店")
                .setTitle("华为Mate" + i)
                .setPrice(2000 + i + "");
            list.add(item);
        }
        solrTemplate.saveBeans("collection1", list);
        solrTemplate.commit("collection1");
    }

    @Test
    public void testPageQuery(){
        Query query = new SimpleQuery("*:*");
        query.setOffset(20L); // 开始索引（默认0）
        query.setRows(20); // 每页记录数(默认10)
        ScoredPage<Item> page = solrTemplate.queryForPage("collection1", query, Item.class);
        System.out.println("总记录数：" + page.getTotalElements());
        System.out.println("总页数：" + page.getTotalPages());
        showList(page.getContent());
    }
    //显示记录数据
    private void showList(List<Item> list){
        for(Item item : list){
            System.out.println(item);
        }
    }

    @Test
    public void testPageQueryMutil(){
        Query query = new SimpleQuery("*:*");
        Criteria criteria = new Criteria("item_title").contains("2");
        criteria = criteria.and("item_title").contains("5");
        query.addCriteria(criteria);
        //query.setOffset(20);//开始索引（默认0）
        //query.setRows(20);//每页记录数(默认10)
        ScoredPage<Item> page = solrTemplate.queryForPage("collection1", query, Item.class);
        System.out.println("总记录数：" + page.getTotalElements());
        List<Item> list = page.getContent();
        showList(list);
    }

    @Test
    public void testDeleteAll () {
        Query query = new SimpleQuery("*:*");
        solrTemplate.delete("collection1", query);
        solrTemplate.commit("collection1");
    }


}
