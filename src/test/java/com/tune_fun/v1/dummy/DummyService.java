package com.tune_fun.v1.dummy;


import com.tune_fun.v1.account.adapter.output.persistence.AccountJpaEntity;
import com.tune_fun.v1.account.adapter.output.persistence.AccountPersistenceAdapter;
import com.tune_fun.v1.account.adapter.output.persistence.device.DeviceJpaEntity;
import com.tune_fun.v1.account.adapter.output.persistence.device.DevicePersistenceAdapter;
import com.tune_fun.v1.account.application.port.input.command.AccountCommands;
import com.tune_fun.v1.account.application.port.input.usecase.RegisterUseCase;
import com.tune_fun.v1.account.application.port.input.usecase.SendForgotPasswordOtpUseCase;
import com.tune_fun.v1.account.application.port.input.usecase.email.RegisterEmailUseCase;
import com.tune_fun.v1.account.application.port.input.usecase.jwt.GenerateAccessTokenUseCase;
import com.tune_fun.v1.account.application.port.input.usecase.jwt.GenerateRefreshTokenUseCase;
import com.tune_fun.v1.account.domain.behavior.SaveDevice;
import com.tune_fun.v1.base.annotation.IntegrationTest;
import com.tune_fun.v1.common.util.StringUtil;
import com.tune_fun.v1.interaction.adapter.output.persistence.LikeCountPersistenceAdapter;
import com.tune_fun.v1.interaction.application.port.input.command.InteractionCommands;
import com.tune_fun.v1.interaction.application.port.input.usecase.FollowUserUseCase;
import com.tune_fun.v1.otp.adapter.output.persistence.OtpPersistenceAdapter;
import com.tune_fun.v1.otp.domain.behavior.OtpType;
import com.tune_fun.v1.otp.application.port.input.query.OtpQueries;
import com.tune_fun.v1.otp.application.port.input.usecase.VerifyOtpUseCase;
import com.tune_fun.v1.otp.domain.behavior.LoadOtp;
import com.tune_fun.v1.otp.domain.value.CurrentDecryptedOtp;
import com.tune_fun.v1.otp.domain.value.EmailVerifyResult;
import com.tune_fun.v1.otp.domain.value.VerifyResult;
import com.tune_fun.v1.vote.adapter.output.persistence.VoteChoiceJpaEntity;
import com.tune_fun.v1.vote.adapter.output.persistence.VotePaperJpaEntity;
import com.tune_fun.v1.vote.adapter.output.persistence.VotePersistenceAdapter;
import com.tune_fun.v1.vote.application.port.input.command.VotePaperCommands;
import com.tune_fun.v1.vote.application.port.input.usecase.RegisterVotePaperUseCase;
import com.tune_fun.v1.vote.application.port.input.usecase.RegisterVoteUseCase;
import com.tune_fun.v1.vote.application.service.VoteBehaviorMapper;
import com.tune_fun.v1.vote.domain.behavior.SaveVoteChoice;
import com.tune_fun.v1.vote.domain.behavior.SaveVotePaper;
import com.tune_fun.v1.vote.domain.value.RegisteredVotePaper;
import com.tune_fun.v1.vote.domain.value.VotePaperOption;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static com.tune_fun.v1.common.util.StringUtil.ulid;
import static com.tune_fun.v1.otp.domain.behavior.OtpType.FORGOT_PASSWORD;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@IntegrationTest
@Getter
@Service
public class DummyService {

    @Autowired
    private RegisterUseCase registerUseCase;

    @Autowired
    private GenerateAccessTokenUseCase generateAccessTokenUseCase;

    @Autowired
    private GenerateRefreshTokenUseCase generateRefreshTokenUseCase;

    @Autowired
    private SendForgotPasswordOtpUseCase sendForgotPasswordOtpUseCase;

    @Autowired
    private RegisterEmailUseCase registerEmailUseCase;

    @Autowired
    private VerifyOtpUseCase verifyOtpUseCase;

    @Autowired
    private RegisterVotePaperUseCase registerVotePaperUseCase;

    @Autowired
    private RegisterVoteUseCase registerVoteUseCase;

    @Autowired
    private FollowUserUseCase followUserUseCase;

    @Autowired
    private AccountPersistenceAdapter accountPersistenceAdapter;

    @Autowired
    private DevicePersistenceAdapter devicePersistenceAdapter;

    @Autowired
    private OtpPersistenceAdapter otpPersistenceAdapter;

    @Autowired
    private VotePersistenceAdapter votePersistenceAdapter;

    @Autowired
    private LikeCountPersistenceAdapter likeCountPersistenceAdapter;

    @Autowired
    private VoteBehaviorMapper voteBehaviorMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private AccountJpaEntity defaultAccount = null;
    private DeviceJpaEntity defaultDevice = null;

    private AccountJpaEntity defaultSecondAccount = null;
    private DeviceJpaEntity defatulSecondDevice = null;

    private AccountJpaEntity defaultArtistAccount = null;
    private DeviceJpaEntity defaultArtistDevice = null;

    private VotePaperJpaEntity defaultVotePaper = null;
    private List<VoteChoiceJpaEntity> defaultVoteChoices = null;

    private String defaultUsername = null;
    private String defaultPassword = null;
    private String defaultEmail = null;

    private String defaultSecondUsername = null;
    private String defaultSecondPassword = null;
    private String defaultSecondEmail = null;

    private String defaultAccessToken = null;
    private String defaultRefreshToken = null;

    private String defaultArtistUsername = null;
    private String defaultArtistPassword = null;
    private String defaultArtistEmail = null;

    private String defaultArtistAccessToken = null;
    private String defaultArtistRefreshToken = null;

    private CurrentDecryptedOtp forgotPasswordOtp = null;

    @Transactional
    public void initAndLogin() throws NoSuchAlgorithmException {
        initAccount();
        login(defaultAccount);
    }

    @Transactional
    public void initArtistAndLogin() throws NoSuchAlgorithmException {
        initArtistAccount();
        loginArtist(defaultArtistAccount);
    }

    @Transactional
    public void initAccount() throws NoSuchAlgorithmException {
        defaultUsername = StringUtil.randomAlphanumeric(10, 15);
        defaultPassword = StringUtil.randomAlphaNumericSymbol(15, 20);
        defaultEmail = StringUtil.randomAlphabetic(7) + "@" + StringUtil.randomAlphabetic(5) + ".com";
        String nickname = StringUtil.randomAlphabetic(5);

        AccountCommands.Notification notification = new AccountCommands.Notification(true, true, true);
        AccountCommands.Register command = new AccountCommands.Register(defaultUsername, defaultPassword, defaultEmail, nickname, notification);

        registerUseCase.register("NORMAL", command);

        defaultAccount = accountPersistenceAdapter.loadAccountByUsername(defaultUsername)
                .orElseThrow(() -> new RuntimeException("initUser 실패"));
    }

    @Transactional
    public void initSecondAccount() throws NoSuchAlgorithmException {
        defaultSecondUsername = StringUtil.randomAlphanumeric(10, 15);
        defaultSecondPassword = StringUtil.randomAlphaNumericSymbol(15, 20);
        defaultSecondEmail = StringUtil.randomAlphabetic(7) + "@" + StringUtil.randomAlphabetic(5) + ".com";
        String nickname = StringUtil.randomAlphabetic(5);

        AccountCommands.Notification notification = new AccountCommands.Notification(true, true, true);
        AccountCommands.Register command = new AccountCommands.Register(defaultSecondUsername, defaultSecondPassword, defaultSecondEmail, nickname, notification);

        registerUseCase.register("NORMAL", command);

        defaultSecondAccount = accountPersistenceAdapter.loadAccountByUsername(defaultSecondUsername)
                .orElseThrow(() -> new RuntimeException("initUser 실패"));
    }

    @Transactional
    public void initArtistAccount() throws NoSuchAlgorithmException {
        defaultArtistUsername = StringUtil.randomAlphanumeric(10, 15);
        defaultArtistPassword = StringUtil.randomAlphaNumericSymbol(15, 20);
        defaultArtistEmail = StringUtil.randomAlphabetic(7) + "@" + StringUtil.randomAlphabetic(5) + ".com";
        String nickname = StringUtil.randomAlphabetic(5);

        AccountCommands.Notification notification = new AccountCommands.Notification(true, true, true);
        AccountCommands.Register command = new AccountCommands.Register(defaultArtistUsername, defaultArtistPassword, defaultArtistEmail, nickname, notification);

        registerUseCase.register("ARTIST", command);

        defaultArtistAccount = accountPersistenceAdapter.loadAccountByUsername(defaultArtistUsername)
                .orElseThrow(() -> new RuntimeException("initUser 실패"));
    }

    @Transactional
    public void login(final AccountJpaEntity account) throws NoSuchAlgorithmException {
        defaultAccessToken = generateAccessTokenUseCase.generateAccessToken(account);
        defaultRefreshToken = generateRefreshTokenUseCase.generateRefreshToken(account);

        SaveDevice saveDeviceBehavior = new SaveDevice(account.getUsername(), StringUtil.randomAlphaNumericSymbol(15), StringUtil.randomAlphaNumericSymbol(15));
        devicePersistenceAdapter.saveDevice(saveDeviceBehavior);
        devicePersistenceAdapter.findByFcmTokenOrDeviceToken(saveDeviceBehavior.username(), saveDeviceBehavior.fcmToken(), saveDeviceBehavior.deviceToken())
                .ifPresent(device -> defaultDevice = device);

        User user = new User(account.getUsername(), account.getPassword(), account.getAuthorities());
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Transactional
    public void loginArtist(final AccountJpaEntity account) throws NoSuchAlgorithmException {
        defaultArtistAccessToken = generateAccessTokenUseCase.generateAccessToken(account);
        defaultArtistRefreshToken = generateRefreshTokenUseCase.generateRefreshToken(account);

        SaveDevice saveDeviceBehavior = new SaveDevice(account.getUsername(), StringUtil.randomAlphaNumericSymbol(15), StringUtil.randomAlphaNumericSymbol(15));
        devicePersistenceAdapter.saveDevice(saveDeviceBehavior);
        devicePersistenceAdapter.findByFcmTokenOrDeviceToken(saveDeviceBehavior.username(), saveDeviceBehavior.fcmToken(), saveDeviceBehavior.deviceToken())
                .ifPresent(device -> defaultArtistDevice = device);

        User user = new User(account.getUsername(), account.getPassword(), account.getAuthorities());
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Transactional
    public void clearEmail() throws Exception {
        accountPersistenceAdapter.clearEmail(defaultUsername);
    }

    @Transactional
    public void registerEmail() throws Exception {
        String email = StringUtil.randomAlphabetic(7) + "@" + StringUtil.randomAlphabetic(5) + ".com";
        AccountCommands.SaveEmail command = new AccountCommands.SaveEmail(email);

        registerEmailUseCase.registerEmail(command, getSecurityUser(defaultAccount));
    }

    @Transactional
    public void forgotPasswordOtp() throws Exception {
        AccountCommands.SendForgotPasswordOtp command = new AccountCommands.SendForgotPasswordOtp(defaultUsername);
        assertDoesNotThrow(() -> sendForgotPasswordOtpUseCase.sendOtp(command));
        forgotPasswordOtp = otpPersistenceAdapter.loadOtp(new LoadOtp(defaultUsername, FORGOT_PASSWORD.getLabel()));
    }

    @Transactional
    public void verifyOtp(OtpType otpType, String token) throws Exception {
        OtpQueries.Verify query = new OtpQueries.Verify(defaultUsername, otpType.getLabel(), token);
        VerifyResult verifyResult = verifyOtpUseCase.verify(query);

        if (verifyResult instanceof EmailVerifyResult emailVerifyResult) {
            defaultAccessToken = emailVerifyResult.getAccessToken();
            defaultRefreshToken = emailVerifyResult.getRefreshToken();
        }
    }

    @Transactional
    public void initVotePaper() {
        Set<VotePaperCommands.Offer> offers = Set.of(
                new VotePaperCommands.Offer(ulid(), "Love Lee", ulid(), "AKMU"),
                new VotePaperCommands.Offer(ulid(), "Dolphin", ulid(), "오마이걸")
        );

        LocalDateTime voteStartAt = LocalDateTime.now().plusDays(1);
        LocalDateTime voteEndAt = LocalDateTime.now().plusDays(2);

        VotePaperCommands.Register command = new VotePaperCommands.Register("First Vote Paper", "test",
                VotePaperOption.DENY_ADD_CHOICES, voteStartAt, voteEndAt, offers);

        User user = getSecurityUser(defaultArtistAccount);
        RegisteredVotePaper registeredVotePaper = saveVotePaper(command, user);
        saveVoteChoiceByRegisteredVotePaper(command, registeredVotePaper);

        votePersistenceAdapter.findProgressingVotePaperByAuthor(defaultArtistUsername)
                .ifPresent(votePaper -> defaultVotePaper = votePaper);

        votePersistenceAdapter.initializeStatistics(defaultVotePaper.getId());

        defaultVoteChoices = votePersistenceAdapter.findAllByVotePaperId(defaultVotePaper.getId());
    }

    @Transactional
    public void initVotePaperAllowAddChoices() {
        Set<VotePaperCommands.Offer> offers = Set.of(
                new VotePaperCommands.Offer(ulid(), "KNOCK (With 박문치)", ulid(), "권진아"),
                new VotePaperCommands.Offer(ulid(), "Orange, You're Not a Joke to Me!", ulid(), "스텔라장 (Stella Jang)")
        );

        LocalDateTime voteStartAt = LocalDateTime.now().plusDays(1);
        LocalDateTime voteEndAt = LocalDateTime.now().plusDays(2);

        VotePaperCommands.Register command = new VotePaperCommands.Register("First Vote Paper", "test",
                VotePaperOption.ALLOW_ADD_CHOICES, voteStartAt, voteEndAt, offers);

        User user = getSecurityUser(defaultArtistAccount);
        RegisteredVotePaper registeredVotePaper = saveVotePaper(command, user);
        saveVoteChoiceByRegisteredVotePaper(command, registeredVotePaper);

        votePersistenceAdapter.findProgressingVotePaperByAuthor(defaultArtistUsername)
                .ifPresent(votePaper -> defaultVotePaper = votePaper);

        votePersistenceAdapter.initializeStatistics(defaultVotePaper.getId());

        defaultVoteChoices = votePersistenceAdapter.findAllByVotePaperId(defaultVotePaper.getId());
    }

    public void initVotePaperBatch() {
        Long authorId = defaultArtistAccount.getId();
        String sql = "INSERT INTO vote_paper (author_id, created_at, delivery_at, updated_at, vote_end_at, vote_start_at, content,\n" +
                "                        option, title, uuid, video_url, enabled) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        for (int idx = 0; idx < 1000; idx++) {
            jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(@NotNull PreparedStatement ps, int i) throws SQLException {
                    ps.setLong(1, authorId);
                    ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
                    ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now().plusDays(10)));
                    ps.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
                    ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now().plusDays(3)));
                    ps.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
                    ps.setString(7, "test");
                    ps.setString(8, "DENY_ADD_CHOICES");
                    ps.setString(9, "First Vote Paper");
                    ps.setString(10, StringUtil.uuid());
                    ps.setString(11, "test");
                    ps.setBoolean(12, true);
                }

                @Override
                public int getBatchSize() {
                    return 100;
                }
            });
        }
    }

    public void registerVote() {
        registerVoteUseCase.register(defaultVotePaper.getId(), defaultVoteChoices.get(0).getId(), getSecurityUser(defaultAccount));
    }

    public void expireOtp(OtpType otpType) {
        otpPersistenceAdapter.expire(otpType.getLabel(), defaultUsername);
    }

    @Transactional
    public RegisteredVotePaper saveVotePaper(VotePaperCommands.Register command, User user) {
        SaveVotePaper saveVotePaperBehavior = voteBehaviorMapper.saveVotePaper(command, user);
        return votePersistenceAdapter.saveVotePaper(saveVotePaperBehavior);
    }

    @Transactional
    public void saveVoteChoiceByRegisteredVotePaper(VotePaperCommands.Register command, RegisteredVotePaper registeredVotePaper) {
        Set<SaveVoteChoice> saveVoteChoicesBehavior = voteBehaviorMapper.saveVoteChoices(command.offers());
        votePersistenceAdapter.saveVoteChoice(registeredVotePaper.id(), saveVoteChoicesBehavior);
    }

    @Transactional
    public void likeVotePaper(final Long votePaperId, final String username) {
        votePersistenceAdapter.saveVotePaperLike(votePaperId, username);
        likeCountPersistenceAdapter.incrementVotePaperLikeCount(votePaperId);
    }

    @Transactional
    public void follow() {
        InteractionCommands.Follow command = new InteractionCommands.Follow(defaultSecondAccount.getId());
        followUserUseCase.follow(command, getSecurityUser(defaultAccount));
    }

    private static User getSecurityUser(AccountJpaEntity account) {
        return new User(account.getUsername(), account.getPassword(), account.getAuthorities());
    }
}
