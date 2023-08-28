package com.blossom.backend.server.article.reference;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blossom.backend.server.article.reference.pojo.ArticleReferenceEntity;
import com.blossom.backend.server.article.reference.pojo.ArticleReferenceReq;
import com.blossom.common.base.util.BeanUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 文章相关引用
 *
 * @author xzzz
 */
@Slf4j
@Service
@AllArgsConstructor
public class ArticleReferenceService extends ServiceImpl<ArticleReferenceMapper, ArticleReferenceEntity> {

    /**
     * 文章引用记录
     */
    @Transactional(rollbackFor = Exception.class)
    public void bind(Long userId, Long sourceId, String sourceName, List<ArticleReferenceReq> references) {
        delete(sourceId);

        // 没有图片, 则不保存
        if (CollUtil.isEmpty(references)) {
            return;
        }
        List<ArticleReferenceEntity> refs = BeanUtil.toList(references, ArticleReferenceEntity.class);
        for (ArticleReferenceEntity ref : refs) {
            ref.setUserId(userId);
            ref.setSourceId(sourceId);
            ref.setSourceName(sourceName);
        }
        baseMapper.insertList(refs);
    }

    /**
     * 删除引用
     *
     * @param articleId 文章ID
     */
    public void delete(Long articleId) {
        LambdaQueryWrapper<ArticleReferenceEntity> where = new LambdaQueryWrapper<>();
        where.eq(ArticleReferenceEntity::getSourceId, articleId);
        baseMapper.delete(where);
    }

    /**
     * 插件图片是否被引用
     *
     * @param url 文章url
     */
    public boolean check(String url) {
        LambdaQueryWrapper<ArticleReferenceEntity> where = new LambdaQueryWrapper<>();
        where.eq(ArticleReferenceEntity::getTargetUrl, url);
        return baseMapper.exists(where);
    }

    /**
     * 查询文章引用关系
     *
     * @param onlyInner 是否只查询内部文章之间的引用
     * @param userId    用户ID
     */
    public Map<String, Object> listAll(boolean onlyInner, Long userId) {
        Map<String, Object> result = new HashMap<>();
        LambdaQueryWrapper<ArticleReferenceEntity> where = new LambdaQueryWrapper<>();
        where.eq(ArticleReferenceEntity::getUserId, userId);
        if (onlyInner) {
            where.in(ArticleReferenceEntity::getType, 11);
        } else {
            where.in(ArticleReferenceEntity::getType, 11, 21);
        }

        List<ArticleReferenceEntity> all = baseMapper.selectList(where);
        if (CollUtil.isEmpty(all)) {
            return result;
        }

        // ======================================== node ========================================
        // sourceName
        Map<Long, List<ArticleReferenceEntity>> source = all.stream().collect(Collectors.groupingBy(ArticleReferenceEntity::getSourceId));
        // targetName, 被引用的同一篇文章可能会有不同名称, 例如 github.com 分别被不同的引用时分别叫 A1,A2
        Map<String, List<ArticleReferenceEntity>> target = all.stream().collect(Collectors.groupingBy(ArticleReferenceEntity::getTargetUrl));

        Set<Node> nodes = new HashSet<>();
        source.forEach((id, list) -> {
            Node node = new Node(list.get(0).getSourceName(), 11);
            node.setInner(true);
            node.setArtId(id);
            nodes.add(node);
        });
        target.forEach((name, list) -> {
            Node node = new Node(list.get(0).getTargetName(), list.get(0).getType());
            if (list.get(0).getType().equals(11)) {
                node.setInner(true);
                node.setArtId(list.get(0).getTargetId());
            } else {
                node.setInner(false);
                node.setArtUrl(list.get(0).getTargetUrl());
            }
            nodes.add(node);
        });

        result.put("nodes", nodes);

        // ======================================== link ========================================
        Set<Link> links = new HashSet<>();
        for (ArticleReferenceEntity ref : all) {
            String sourceName = source.get(ref.getSourceId()).get(0).getSourceName();
            String targetName = target.get(ref.getTargetUrl()).get(0).getTargetName();
            links.add(new Link(sourceName, targetName));
        }

        result.put("links", links);

        return result;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private static class Node {
        private String name;
        private Integer artType;
        private Boolean inner;
        private Long artId; // inner = true
        private String artUrl;// inner = false

        public Node(String name, Integer artType) {
            this.name = name;
            this.artType = artType;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }

            if ((null == o) || (this.getClass() != o.getClass())) {
                return false;
            }

            Node t = (Node) o;

            return this.name.equals(t.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }
    }

    @Data
    @EqualsAndHashCode
    private static class Link {
        private String source;
        private String target;

        public Link(String source, String target) {
            this.source = source;
            this.target = target;
        }
    }

}
