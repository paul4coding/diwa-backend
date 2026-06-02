package Tg.OSEOR.DIWA.Backend.security.service;

import Tg.OSEOR.DIWA.Backend.security.model.User;
import Tg.OSEOR.DIWA.Backend.security.model.auth.OtpCode;
import Tg.OSEOR.DIWA.Backend.security.model.auth.OtpCode.OtpType;
import Tg.OSEOR.DIWA.Backend.security.repository.auth.OtpCodeRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
public class OtpService {

    private final OtpCodeRepository otpCodeRepository;

    @Value("${app.otp.expiration-minutes:10}")
    private int expirationMinutes;

    @Value("${app.otp.length:6}")
    private int otpLength;

    private final SecureRandom secureRandom = new SecureRandom();

    public OtpService(OtpCodeRepository otpCodeRepository) {
        this.otpCodeRepository = otpCodeRepository;
    }

    @Transactional
    public String generateAndSaveOtp(User user, OtpType type) {
        otpCodeRepository.invalidateAllForUser(user.getId(), type);

        String code = generateNumericCode(otpLength);

        OtpCode otp = new OtpCode();
        otp.setUser(user);
        otp.setCode(code);
        otp.setType(type);
        otp.setUsed(false);
        otp.setExpiresAt(LocalDateTime.now().plusMinutes(expirationMinutes));

        otpCodeRepository.save(otp);
        return code;
    }

    @Transactional
    public boolean verifyOtp(User user, String code, OtpType type) {
        System.out.println("Vérification OTP pour user: " + user.getEmail() + ", code: " + code + ", type: " + type);
        return otpCodeRepository
            .findValidOtp(user.getId(), code, type, LocalDateTime.now())
            .map(otp -> {
                System.out.println("OTP valide trouvé ! Marquage comme utilisé.");
                otp.setUsed(true);
                otpCodeRepository.save(otp);
                return true;
            })
            .orElseGet(() -> {
                System.out.println("Aucun OTP valide trouvé pour cet utilisateur/code/type.");
                return false;
            });
    }

    @Scheduled(fixedRate = 3600000)
    @Transactional
    public void cleanExpiredOtps() {
        otpCodeRepository.deleteExpiredOtps(LocalDateTime.now());
    }

    private String generateNumericCode(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(secureRandom.nextInt(10));
        }
        return sb.toString();
    }
}
