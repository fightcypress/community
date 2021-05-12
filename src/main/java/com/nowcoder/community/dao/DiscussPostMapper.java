package com.nowcoder.community.dao;

import com.nowcoder.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiscussPostMapper {
    /**
     *
     * @param userId 用户id
     * @param offset 页面从第几条数据开始显示
     * @param limit 每页多少条数据
     * @return
     */
    List<DiscussPost> selectDiscussPosts(int userId,int offset,int limit);

    /**
     *
     * @Param 用于给参数起别名
     * 如果只有一个参数，并且在<if>中使用，必须加别名
     *
     * @param userId
     * @return
     */
    int selectDiscussPostRows(@Param("userId") int userId);


    int insertDiscussPost(DiscussPost discussPost);


    DiscussPost selectDiscussPostById(int id);

    int updateCommentCount(int id, int commentCount);
}
