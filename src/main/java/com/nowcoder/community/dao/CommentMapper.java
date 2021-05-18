package com.nowcoder.community.dao;

import com.nowcoder.community.entity.Comment;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author zhang
 * @Description
 * @date 2021/5/10 21:52
 */
@Repository
public interface CommentMapper {

    List<Comment> selectCommentsByEntity(int entityType, int entityId, int offset, int limit);

    int selectCountByEntity(int entityType, int entityId);

    int insertComment(Comment comment);

    Comment selectCommentById(int id);
}
