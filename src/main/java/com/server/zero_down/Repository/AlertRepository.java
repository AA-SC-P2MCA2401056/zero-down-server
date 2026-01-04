package com.server.zero_down.Repository;

import com.server.zero_down.Modal.Alert;
import com.server.zero_down.Modal.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AlertRepository extends JpaRepository<Alert, String> {
    boolean existsBySensorAndMessageAndResolvedFalse(Sensor sensor, String msg);

    List<Alert> findByResolvedFalseOrderByCreatedAtDesc();

    Alert findFirstBySensorAndMessageAndResolvedFalseOrderByCreatedAtDesc(Sensor sensor, String msg);

    @Modifying
    @Query(
            value = "UPDATE tb_alert SET resolved = 1, resolved_at = NOW() WHERE parent_id = :parentId",
            nativeQuery = true
    )
    void resolveAllByParent(@Param("parentId") String parentId);


    @Modifying
    @Query(
            value = "UPDATE tb_alert SET resolved = 1, resolved_at = NOW() WHERE parent_id = :parentId OR id = :parentId",
            nativeQuery = true
    )
    void resolveAllByParentAndChild(@Param("parentId") String parentId);
}
