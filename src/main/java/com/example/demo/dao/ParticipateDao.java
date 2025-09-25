package com.example.demo.dao;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.example.demo.dto.Participate;

@Mapper
public interface ParticipateDao {

  int insert(@Param("postId") int postId,
             @Param("userId") int userId,
             @Param("status") String status);

  int updateStatus(@Param("postId") int postId,
                   @Param("userId") int userId,
                   @Param("status") String status);

  int delete(@Param("postId") int postId,
             @Param("userId") int userId);

  int exists(@Param("postId") int postId,
             @Param("userId") int userId);

  List<Participate> findByPostAndStatus(@Param("postId") int postId,
                                        @Param("status") String status);

  List<Participate> findByPost(@Param("postId") int postId);
  List<Participate> findByUser(@Param("userId") int userId);

  Integer countByPostAndStatus(@Param("postId") int postId,
                               @Param("status") String status);
}

