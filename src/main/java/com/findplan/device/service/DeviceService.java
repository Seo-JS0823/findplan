package com.findplan.device.service;

import org.springframework.stereotype.Service;

import com.findplan.device.repository.DeviceRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class DeviceService {
	
	private final DeviceRepository deviceRepository;
	
	
}
