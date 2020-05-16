package com.andrew.secondkill.dao;

import com.andrew.secondkill.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * Class
 *
 * @author andrew
 * @date 2020/3/21
 */
@Mapper
public interface UserDao {

    @Select("select * from kill.user where id=#{id};")
    User getUserById(@Param("id")Long id);

    @Update("update kill.user set password=#{password} where id=#{id};")
    void updatePasswordById(User newPasswordUser);
}
