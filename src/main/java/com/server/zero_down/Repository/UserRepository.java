package com.server.zero_down.Repository;

import com.server.zero_down.Modal.AdmUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<AdmUser, Long> {
    AdmUser findByUserName(String userName);
}
