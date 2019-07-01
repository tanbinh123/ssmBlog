package com.we.weblog.mapper;


import com.we.weblog.domain.Post;
import com.we.weblog.mapper.builder.PostSqlBuilder;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.StatementType;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 *  处理博客信息的管理mapper
 */
@Repository
@Mapper
public interface PostMapper {

    /**
     * 查询文章接口
     * @param post
     * @return
     */
    @SelectProvider(type = PostSqlBuilder.class, method = "buildGetPostQuery")
    List<Post> queryPost(Post post);

    /**
     * 分类标签查询 这里 in not null去除空列表
     * @return
     */
    @SelectProvider(type = PostSqlBuilder.class, method = "buildGetCategoryQuery")
    List<String> findAllCategory();

    @SelectProvider(type = PostSqlBuilder.class, method = "buildCountPostQuery")
    int countPost();




    @Update({"update hexo_post set status = #{s} where uid = #{id}"})
    void updateByStatus(@Param("id") Integer postId, @Param("s") Integer status);
    /**
     * 得到博客的总数量
      * @return
     */


    /**
     * todo 这个其实也可以和第一个合并 研究一下
     * 批量查询博客  目前10个一次
     * @param count
     * @return
     */
    @SelectProvider(type = PostSqlBuilder.class, method = "buildRecentPostsQuery")
    List<Post> findRecentPosts(@Param("count") int count);

    /**
     *  插入博客 用于增加博客内容吧
     * @param post
     * @return
     */
    @Insert({"insert into hexo_post " +
            "(article,title,created,tags,md,type,slug,publish,categories) " +
            "values (#{b.article},#{b.title},#{b.created},#{b.tags},#{b.md}" +
            ",#{b.type},#{b.slug},#{b.publish},#{b.categories})"})
    @SelectKey(before=false,keyProperty="b.uid",resultType=Integer.class,
            statementType= StatementType.STATEMENT,statement="SELECT LAST_INSERT_ID() AS id")
    int savePost(@Param("b") Post post);

    /**
     *  删除博客
     * @param id
     * @return
     */
    @Delete({"delete from hexo_post where uid = #{id}"})
    int removeByPostId(@Param("id") int id);


    @Update({" update hexo_post " +
            " set title = #{b.title}," +
            " md = #{b.md}," +
            " slug = #{b.slug}," +
            " categories = #{b.categories},"+
            " tags = #{b.tags},"+
            " article=#{b.article} where uid= #{id}"})
    void updatePostByUid(@Param("b")Post context, @Param("id") int uid);

    @Update({"update hexo_post set hits=#{c.hits} where uid = #{c.uid}"})
    void updateOnePostVisit(@Param("c") Post context);


    @Select({"select uid,title,created,tags from hexo_post " +
            "where type = 'post'  order by created desc limit #{p},12"})
    List<Post> findPostByYearAndMonth(@Param("p") int page);


    @Select({"select uid,title,tags,created from hexo_post " +
            "where uid < #{id} and type = 'post'   order by uid desc limit 1"})
    Post findPreviousPost(@Param("id") int id);

    @Select({"select uid,title,article,tags,created " +
            "from hexo_post where uid > #{id} and type = 'post' order by uid asc limit 1"})
  Post findNextPost(@Param("id") int id);


    /**
     * 标签页面删除相关数据
     * @param categoryName
     */
    @Delete({"update hexo_post set categories = null where categories = #{cate}"})
    int removePostCategory(@Param("cate") String categoryName);


}