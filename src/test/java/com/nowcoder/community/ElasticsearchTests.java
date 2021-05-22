package com.nowcoder.community;

import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.elasticsearch.DiscussPostRepository;
import com.nowcoder.community.entity.DiscussPost;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;


@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class ElasticsearchTests {

    @Autowired
    private DiscussPostMapper discussMapper;

    @Autowired
    private DiscussPostRepository discussRepository;

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Test
    public void testInsert() {
        discussRepository.save(discussMapper.selectDiscussPostById(241));
        discussRepository.save(discussMapper.selectDiscussPostById(242));
        discussRepository.save(discussMapper.selectDiscussPostById(243));
    }

    @Test
    public void testInsertList() {
        discussRepository.saveAll(discussMapper.selectDiscussPosts(101, 0, 100));
        discussRepository.saveAll(discussMapper.selectDiscussPosts(102, 0, 100));
        discussRepository.saveAll(discussMapper.selectDiscussPosts(103, 0, 100));
        discussRepository.saveAll(discussMapper.selectDiscussPosts(111, 0, 100));
        discussRepository.saveAll(discussMapper.selectDiscussPosts(112, 0, 100));
        discussRepository.saveAll(discussMapper.selectDiscussPosts(131, 0, 100));
        discussRepository.saveAll(discussMapper.selectDiscussPosts(132, 0, 100));
        discussRepository.saveAll(discussMapper.selectDiscussPosts(133, 0, 100));
        discussRepository.saveAll(discussMapper.selectDiscussPosts(134, 0, 100));
    }

    @Test
    public void testUpdate() {
        DiscussPost post = discussMapper.selectDiscussPostById(231);
        post.setContent("我是新人,使劲灌水.");
        discussRepository.save(post);
    }

    @Test
    public void testDelete() {
        // discussRepository.deleteById(231);
        discussRepository.deleteAll();
    }

    @Test
    public void testSearchByRepository() {
        Pageable pageable = PageRequest.of(0, 10);
        NativeSearchQuery searchQuery =
                new NativeSearchQueryBuilder()
                        .withQuery(QueryBuilders.multiMatchQuery("互联网寒冬", "title", "content"))
                        .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                        .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                        .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                        .withPageable(pageable)
                        .withHighlightFields(
                                new HighlightBuilder.Field("title")
                                        .preTags("<em>")
                                        .postTags("</em>"),
                                new HighlightBuilder.Field("content")
                                        .preTags("<em>")
                                        .postTags("</em>"))
                        .build();
        // 底层获取了高亮显示的值，但是没有返回
        // Page<DiscussPost> page = discussPostRepository.search(searchQuery);

        SearchHits<DiscussPost> searchHits =
                elasticsearchRestTemplate.search(searchQuery, DiscussPost.class);
        if (searchHits.getTotalHits() <= 0) {
            return;
        }
        List<DiscussPost> list = new ArrayList<>();
        for (SearchHit<DiscussPost> hit : searchHits) {
            DiscussPost content = hit.getContent();
            DiscussPost post = new DiscussPost();
            BeanUtils.copyProperties(content, post);
            // 处理高亮
            //            Map<String, List<String>> highlightFields = hit.getHighlightFields();
            //            for (Map.Entry<String, List<String>> stringHighlightFieldEntry :
            // highlightFields.entrySet()) {
            //                String key = stringHighlightFieldEntry.getKey();
            //                if(StringUtils.equals(key, "title")){
            //                    List<String> fragments = stringHighlightFieldEntry.getValue();
            //                    StringBuilder sb = new StringBuilder();
            //                    for (String fragment : fragments) {
            //                        sb.append(fragment);
            //                    }
            //                    post.setTitle(sb.toString());
            //                }
            //                if(StringUtils.equals(key, "content")){
            //                    List<String> fragments = stringHighlightFieldEntry.getValue();
            //                    StringBuilder sb = new StringBuilder();
            //                    for (String fragment : fragments) {
            //                        sb.append(fragment);
            //                    }
            //                    post.setContent(sb.toString());
            //                }
            //            }
            // 处理高亮
            List<String> list1 = hit.getHighlightFields().get("title");
            if (list1 != null) {
                post.setTitle(list1.get(0));
            }
            List<String> list2 = hit.getHighlightFields().get("content");
            if (list2 != null) {
                post.setContent(list2.get(0));
            }
            list.add(post);
        }
        //        List<DiscussPost> searchDiscussPost =
        // searchHits.stream().map(SearchHit::getContent).collect(Collectors.toList());
        Page<DiscussPost> page = new PageImpl<>(list, pageable, searchHits.getTotalHits());

        System.out.println(page.getTotalElements());
        System.out.println(page.getTotalPages());
        System.out.println(page.getNumber());
        System.out.println(page.getSize());
        for (DiscussPost post : page) {
            System.out.println(post);
        }
    }

}
