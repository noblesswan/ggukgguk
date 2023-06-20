package com.ggukgguk.api.record.service;

import org.springframework.web.multipart.MultipartFile;

public interface MediaFileService {

	public boolean saveFile(MultipartFile file, String subDir, String saveName);
}
