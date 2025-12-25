package com.findplan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.findplan.entity.Device;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {
	Device findByDeviceId(String deviceId);
}
