package com.ggukgguk.api.record.service;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

import javax.imageio.ImageIO;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.jpeg.exif.ExifRewriter;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import ws.schild.jave.Encoder;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.encode.AudioAttributes;
import ws.schild.jave.encode.EncodingAttributes;
import ws.schild.jave.encode.VideoAttributes;
import ws.schild.jave.encode.enums.X264_PROFILE;

@Service
public class MediaFileServiceImpl implements MediaFileService {
	
	private Logger log = LogManager.getLogger("base");
	private final int mediaWidth = 1200;

	@Value("${file.baseDir}")
	String baseDir;
	
	/**
	 * 미디어 파일을 저장한다.
	 */
	@Override
	public boolean saveFile(MultipartFile file, String subDir, String saveName) {
		File saveFile = new File(baseDir + "/" + subDir + "/" + saveName);
		
		try {
			file.transferTo(saveFile);
			log.debug("파일 저장 성공");
			
			if (prcessMedia(saveFile, file.getContentType())) {
				log.debug("파일 후처리 성공");
				return true;
			} else {
				log.debug("파일 후처리 실패");
				return false;
			}
		} catch (IllegalStateException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * 미디어 파일을 저장한 후에 포맷 등을 통일하는 작업을 수행한다.
	 * @param file
	 * @param contentType
	 * @return
	 * @throws IOException 
	 */
	private boolean prcessMedia(File file, String contentType) throws IOException {
		String format = contentType.split("/")[0];
		boolean result = false;
		
		if (format != null) {
			switch (format) {
			case "image":
				log.debug("이미지 후처리");
				result = resizeImage(file);
				break;
			case "video":
				log.debug("비디오 후처리");
				result = encodeVideo(file);
				break;
			case "audio":
				log.debug("오디오 후처리");
				result = true;
				// 여기에 후처리 로직을 추가
				break;
			default:
				log.debug("지원하지 않는 미디어 타입은 처리할 수 없습니다.");
				result = false;
			}
		}
		
		return result;
	}
	
	/*
	 * 이미지의 너비가 특정 수준을 넘어서면, 해당 수준에 맞게 리사이징한다.
	 */
	private boolean resizeImage(File imgFile) throws IOException {
		boolean result = false;
		
		// 원본 이미지 로드
		BufferedImage originalImage = null;
		byte[] imageData = null;
		
		try {
			originalImage = ImageIO.read(imgFile);
			imageData  = Files.readAllBytes(imgFile.toPath());
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		// 원본 이미지에서 EXIF 추출
		TiffImageMetadata metadata;
		try {
			metadata = readExifMetadata(imageData);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
        imageData = null; // GC될 수 있도록 NULL 처리
		
		// 원본 이미지 사이즈 불러오기
		int originalWidth = originalImage.getWidth();
		int originalHeight = originalImage.getHeight();
		
		// 원본이 이미 1200 픽셀보다 작으면 리사이징이 필요 없음
		if (originalWidth <= mediaWidth) return true;
		
		// 새 이미지 사이즈 계산
		int newWidth = mediaWidth;
		int newHeight = originalHeight * mediaWidth / originalWidth; // 새 높이 = 기존 높이 * 새 너비(고정) / 기존 너비

		// 리사이징 및 저장
		int imageType = originalImage.getType();
		if(imageType == 0) imageType = 5;
		
		BufferedImage newImage = new BufferedImage(newWidth, newHeight, originalImage.getType());
	    Graphics2D g = newImage.createGraphics();  
	    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
	    RenderingHints.VALUE_INTERPOLATION_BILINEAR);  
	    g.drawImage(originalImage, 0, 0, newWidth, newHeight, 0, 0, originalWidth, originalHeight, null);  
	    g.dispose();
	    
	    try {
			ImageIO.write(newImage, "jpg", imgFile);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	    
	    // 기존 메타데이터 쓰기
	    if (metadata == null) {
	    	// 메타데이터가 없으면 스킵
	    	return true;
	    }
	    
	    FileOutputStream fos = new FileOutputStream(imgFile);
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    try {
		    ImageIO.write(newImage, "jpg", baos);
		    byte[] resizedImgData = baos.toByteArray();
		    new ExifRewriter().updateExifMetadataLossless(resizedImgData, fos, metadata.getOutputSet());
	    	result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
	    	fos.close();
	    	baos.close();
		}
	    
	    return result;
	}
	
    private TiffImageMetadata readExifMetadata(byte[] jpegData) throws ImageReadException, IOException {
        ImageMetadata imageMetadata = Imaging.getMetadata(jpegData);
        if (imageMetadata == null) {
            return null;
        }
        JpegImageMetadata jpegMetadata = (JpegImageMetadata)imageMetadata;
        TiffImageMetadata exif = jpegMetadata.getExif();
        if (exif == null) {
            return null;
        }
        return exif;
    }
	
	private boolean encodeVideo(File videoFile) {
		try {
			log.debug("비디오 인코딩 시작: ", videoFile);
			
			File tmp = new File(videoFile.getCanonicalFile() + "_PROCESSING");
		 	videoFile.renameTo(tmp); // 우선 임시로 이름을 부여한다.
		 	
		 	File source = tmp;
		 	File target = videoFile; // 도착 경로 지정
		 	
		 	VideoAttributes vAttr = new VideoAttributes();
		 	vAttr.setCodec("libx264");
		 	vAttr.setBitRate(2500000);
		 	vAttr.setX264Profile(X264_PROFILE.BASELINE);
		 	
		 	AudioAttributes aAttr = new AudioAttributes();
		 	aAttr.setCodec("aac");
		 	
		 	EncodingAttributes attrs = new EncodingAttributes();                                  
		 	attrs.setVideoAttributes(vAttr);
		 	attrs.setAudioAttributes(aAttr);
		 	attrs.setOutputFormat("mp4");
		 	
		 	Encoder encoder = new Encoder();
		 	encoder.encode(new MultimediaObject(source), target, attrs);
		 	log.debug("비디오 인코딩 성공");
		 	
//		 	tmp.delete();
		} catch (Exception e) {
			log.debug("비디오 인코딩 실패");
			e.printStackTrace();
			return false;
		}
		
		return true;
	}

}
