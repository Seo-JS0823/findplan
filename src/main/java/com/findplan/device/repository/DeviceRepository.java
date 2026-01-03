package com.findplan.device.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.findplan.device.entity.DeviceEntity;

import ua_parser.Device;

@Repository
public interface DeviceRepository extends JpaRepository<DeviceEntity, Long> {
	Device findByDeviceId(String deviceId);
	
	Device findByRefreshToken(String refreshToken);
}
