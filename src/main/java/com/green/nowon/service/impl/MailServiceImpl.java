package com.green.nowon.service.impl;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.function.Supplier;

import javax.imageio.ImageIO;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;

import org.apache.ibatis.javassist.compiler.ast.Member;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.green.nowon.domain.member.MemberEntity;
import com.green.nowon.domain.member.MemberEntityRepository;
import com.green.nowon.service.MailService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j@Service
@AllArgsConstructor
public class MailServiceImpl implements MailService {
	
	private final JavaMailSender sender;
	
	private final MemberEntityRepository mRepo;
	
	private final String senderMail ="velo57234@gmail.com";
	
	private final StringRedisTemplate redisTemplate;
	
	  //script(버튼 누를시 클립보드로 인증번호 자동 저장해주는 기능) 적용을 위한 MIME를 사용한 발송
	  //플랫폼에서 보안상의 이유로 javascript를 제거해서 불가능
	private ByteArrayOutputStream SendMailAndMakeQrImg(String name, String address,String key) {
		 //이메일 내용 세팅
		// button만들어서 클릭시 인증번호 복사하려고 했으나 플랫폼에서 보안상의 이유로 javascript를 메일내용으로 제한하기 때문에 안댐
//		  String emailContent = 
//				  "<script>" +
//                 "function copyVerificationCode() {" +
//                 "  var verificationCode = '" + key + "';" +
//                 "  navigator.clipboard.writeText(verificationCode)" +
//                 "    .then(function() {" +
//                 "      alert('인증번호를 클립보드에 복사했습니다!');" +
//                 "    })" +
//                 "    .catch(function(error) {" +
//                 "      alert('인증번호 복사에 실패하였습니다.', error);" +
//                 "    });" +
//                 "}" +
//                 "</script>"+
//				  "<p>" + name + "님께,</p>" +
//		          "<p>인증코드 : " + key + "</p>" +
//		          "<button onclick=\"copyVerificationCode()\">인증번호를 복사 하려면 클릭하세요</button>" ;


		  //		   단순 텍스트만 보내기 script적용x
//	       SimpleMailMessage message =  new SimpleMailMessage();
//		   message.setTo(address);
//		   message.setFrom(senderMail);
//		   message.setSubject("nowon01 project 인증 메일 발송입니다.");
//		   message.setText(emailContent);
//		   sender.send(message);
	  MimeMessage message = sender.createMimeMessage();
	  ByteArrayOutputStream qrCodeImg =null;
	  try {
		MimeMessageHelper helper = new MimeMessageHelper(message,true,"UTF-8");
		//qr코드 이미지 만들기
		qrCodeImg = generateQRCode(key);
		String emailContent2 =
				  			"<p>" + name + "님 회원 가입을 축하합니다.</p>" +
					        "<p>Verification code: <span id=\"verificationCode\">" + key + "</span></p>" +
					        "<p>인증 번호를 복사하여 사용하거나 qr코드를 사용하세요</p>";
		helper.setTo(address);
		helper.setFrom(senderMail);
		helper.setSubject("nowon01 porject 인증 번호 발송 메일 입니다.");
		//true로 하면 보내는 text가 html로 처리 되어야 함을 의미합니다.
		helper.setText(emailContent2,true);
		sender.send(message);
	} catch (MessagingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	  return qrCodeImg;
	}
	
	//redis에 이메일과 인증번호
	//이메일의 인증횟수를 저장하고 인증번호를 client단으로보내서 qr코드를 만듬
	@Override
	public String getQrCodeData(String address) {
		String value = generateRandomString();
		if(mRepo.findByEmail(address).isEmpty()) {
			long duration = 5L;
			Duration expireTime = Duration.ofMinutes(duration);
			//이메일 + 인증번호를 저장할 객체
			ValueOperations<String, String> ops = redisTemplate.opsForValue();
			//이메일의 인증 횟수를 저장할 redis ops객체 생성 ops2
			String addressCount = address+"count";
			
			ValueOperations<String, String> ops2 = redisTemplate.opsForValue();	
			try {
			if(redisTemplate.hasKey(address)) {
				//인증 시도 한 횟수 가져오기
				String getCount = ops2.get(addressCount);
				Integer count = Integer.parseInt(getCount);
				if(count < 10) {
					ops.set(address, value);
					ops2.increment(addressCount);
				}else {
					throw new IllegalStateException("인증 횟수를 초과 하였습니다. 잠시 뒤에 다시 시도해주세요");
				}
				
			}else {
				ops.set(address, value,expireTime);
				ops2.set(addressCount,"10",expireTime);
			}
			}//try블럭 end
			catch (IllegalStateException e) {
				System.out.println(e);
				throw e;
	        }
		}
		return value;
	}
	//이후 콜백 함수로 처리된 이메일 보내기 처리
	@Override
	public void sendEmail(String name, String address) {		
		 System.out.println("이메일 보내요");
		 ValueOperations<String, String> ops =  redisTemplate.opsForValue();
		 String key = ops.get(address);
		 System.out.println(key);
		 MimeMessage message = sender.createMimeMessage();
		  try {
			MimeMessageHelper helper = new MimeMessageHelper(message,true,"UTF-8");
			//qr코드 이미지 만들기
			String emailContent =
					  			"<p>" + name + "님 회원 가입을 축하합니다.</p>" +
						        "<p>Verification code: <span id=\"verificationCode\">" + key + "</span></p>" +
						        "<p>인증 번호를 복사하여 사용하거나 qr코드를 사용하세요</p>";
			helper.setTo(address);
			helper.setFrom(senderMail);
			helper.setSubject("nowon01 porject 인증 번호 발송 메일 입니다.");
			//true로 하면 보내는 text가 html로 처리 되어야 함을 의미합니다.
			helper.setText(emailContent,true);
			sender.send(message);
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public ByteArrayOutputStream  sendMail(String name, String address) {
		if(mRepo.findByEmail(address).isEmpty()) {
		String key = generateRandomString();
		//db에 발송한 인증 키와 email 저장
//		repo.save(MailEntity.builder()
//				.userKey(key)
//				.address(address)
//				.build());
		//redis로 변경하기 위해서 redis에 저장 하도록 바꿔줌
		//만료 시간 설정 (5분)
		//짧은 시간 안에 10번 이상 보내면 메일 안보내도록 설정할거임
		long duration = 5L;
		Duration expireTime = Duration.ofMinutes(duration);
		//이메일과 인증키를 저장할 redis 저장 객체
		ValueOperations<String, String> ops = redisTemplate.opsForValue();
		
		String addressCount = address+"count";
		//이메일과 인증횟수를 저장할 redis 저장 객체
		ValueOperations<String, String> ops2 = redisTemplate.opsForValue();
		//이미 인증을 시도한 메일일 경우
		if(redisTemplate.hasKey(address)) {
			//인증 시도 한 횟수 가져오기
			String getCount = ops.get(addressCount);
			Integer count = Integer.parseInt(getCount);
			//이메일 인증을 단위 시간 내에 10번 이하로 시도한경우
			if(count <= 10) {
				ops.set(address, key,expireTime);
				//인증 횟수를  1증가 시키는 ValueOperations increment 메소드
				//감소는 decrement 사용 하면됨 
				//뒤의 숫자는 얼마만큼 증가/감소 시킬지 정함 근데 자료형이 숫자와 관련된거여야함
				ops2.increment(addressCount);
				return SendMailAndMakeQrImg(name, address, key);
			}
			//이메일 인증을 단위 시간내에 11번 이상 시도한경우
			if(count >10) {
				System.out.println("횟수 초과");
				return null;
			}
		}
		//인증을 처음 시도하는 메일일 경우
			ops.set(address, key,expireTime);
			ops.set(addressCount, "1",expireTime);
			return SendMailAndMakeQrImg(name, address, key);
		}
		//기능 오류 인경우
		return null;
	   }

	//랜덤 스트링 만들기
	private String generateRandomString() {
		String result = null;
		Supplier<String> sup = ()->{
			// 숫자 0의 아스키 코드 값 = 48 , 9 = 57
			// 영어 A = 65 Z = 90
			// 영어 a = 97 z = 122
			int start = 48;
			int end = 122;
			StringBuilder sb = new StringBuilder();
			String str = new Random().ints(start,end+1)
				  .filter(e->!((e<65 && e>=58) || (e <97 && e>90)))
				  .limit(15)
				  .collect(StringBuilder :: new ,
						  StringBuilder :: appendCodePoint , 
						  StringBuilder :: append)
				  .toString();
			sb.append(str);
			return sb.toString();
		};
		result = sup.get();
		return result;
	}


	//qr코드 이미지 만들기 함수
	//ByteArrayOutputStream 객체를 반환 <- 이 객체는 메모리에 QR코드 이미지 정보를 저장하는데 쓰임
	private ByteArrayOutputStream generateQRCode(String str) {
		//리턴 해줄 ByteArrayOutputStream 생성
		ByteArrayOutputStream ops = new ByteArrayOutputStream();
		try {
			//QRCodeWriter -> QRCode만드는 객체
			QRCodeWriter writer = new QRCodeWriter();
			//QRCode Writer의 encode() 메소드는 
			//qr코드를 찍었을때 표시할 정보 여기서는 매개변수 str , int형 width, int형 hegiht, BarcodeFormat.QR_CODE 를 매개변수로 사용함
			int width =200;
			int height = 200;
			
			BitMatrix bitMatrix = writer.encode(str,BarcodeFormat.QR_CODE , width, height);
			//이미지 생성
			BufferedImage qrImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			for(int x = 0; x<width; x++) {
				for(int y= 0; y<height; y++) {
					qrImage.setRGB(x, y, bitMatrix.get(x,y) ? 0x000000 : 0xFFFFFF);
				}
			}
			//이미지를 ByteArrayOutputStream에 저장
			ImageIO.write(qrImage, "png", ops);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return ops;
	}

	// 입력된 인증번호와 이메일 체크 후 db비우고 인증처리하기 
	@Override
	public Map<String, String> checkMail(String key, String address) {
		Map<String, String> flag = new HashMap<>();
		String addressCount = address+"count";
		Optional<MemberEntity> member = mRepo.findByEmail(address);
		if(member.isEmpty()) {
			if(redisTemplate.hasKey(address)) {
				ValueOperations<String, String> ops = redisTemplate.opsForValue();
				String verificationCode = ops.get(address);
				//인증 로직
				if(verificationCode.equals(key)) {
					//인증에 성공했을경우
					flag.put("result", "인증에 성공하였습니다.");
					//redis의 값이 있으면 가져와서 지우는 메소드 getAndDelete(String key)
					//값이 업으면 null 반환합니다.
					ops.getAndDelete(address);
					ops.getAndDelete(addressCount);
				}else {
					flag.put("result", "인증에 실패하였습니다.");
				}
			}
		}else {
			flag.put("result", "이미 회원가입이 완료된 이메일입니다.");
		}
		return flag;
	}




//  그림 그리는 메소드 위에 삽입함	
//	private BufferedImage toBufferedImage(BitMatrix matrix) {
//		int width = matrix.getWidth();
//		int height = matrix.getHeight();
//		
//		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
//		
//		for(int x = 0; x< width ; x++) {
//			for(int y = 0 ; y<height; y++) {
//				image.setRGB(x, y, matrix.get(x, y) ? 0x000000 : 0xFFFFFF);
//			}
//		}
//		return image;
//	}
}