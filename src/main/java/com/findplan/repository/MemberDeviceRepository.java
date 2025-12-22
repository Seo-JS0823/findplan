package com.findplan.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.findplan.entity.MemberDevice;

@Repository
public interface MemberDeviceRepository extends JpaRepository<MemberDevice, Long> {
	List<MemberDevice> findByMemberCode(Long code);
	
	MemberDevice findByDeviceId(String deviceId);
}
