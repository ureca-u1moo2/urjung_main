package com.eureka.ip.team1.urjung_main.user.repository;

import com.eureka.ip.team1.urjung_main.user.dto.UserPlanResponseDto;
import com.eureka.ip.team1.urjung_main.user.entity.Line;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserPlanRepository extends JpaRepository<Line, String> {

    @Query(
            """
            select new com.eureka.ip.team1.urjung_main.user.dto.UserPlanResponseDto(
                p.id,
                p.name,
                l.phoneNumber,
                p.description,
                l.startDate,
                l.discountedPrice
            )
            from Line l join l.plan p join l.user u
            where u.userId = :userId
            and l.status = 'active'
            order by p.id
            """
    )
    List<UserPlanResponseDto> findAllPlansByUserId(
            @Param("userId") String userId
    );
}
