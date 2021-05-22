package com.nowcoder.community.dao.elasticsearch;

import com.nowcoder.community.entity.DiscussPost;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * @author zhang
 * @date 2021/03/25
 */
@Repository
public interface DiscussPostRepository extends ElasticsearchRepository<DiscussPost, Integer> {
}
