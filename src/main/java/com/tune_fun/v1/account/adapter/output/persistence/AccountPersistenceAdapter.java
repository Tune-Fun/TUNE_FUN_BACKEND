package com.tune_fun.v1.account.adapter.output.persistence;

import com.tune_fun.v1.account.adapter.output.persistence.oauth2.OAuth2AccountJpaEntity;
import com.tune_fun.v1.account.adapter.output.persistence.oauth2.OAuth2AccountRepository;
import com.tune_fun.v1.account.application.port.output.*;
import com.tune_fun.v1.account.application.port.output.oauth2.DisableOAuth2AccountPort;
import com.tune_fun.v1.account.application.port.output.oauth2.SaveOAuth2AccountPort;
import com.tune_fun.v1.account.domain.behavior.SaveAccount;
import com.tune_fun.v1.account.domain.behavior.SaveOAuth2Account;
import com.tune_fun.v1.account.domain.value.CurrentAccount;
import com.tune_fun.v1.account.domain.value.MaskedAccount;
import com.tune_fun.v1.account.domain.value.RegisteredAccount;
import com.tune_fun.v1.account.domain.value.oauth2.RegisteredOAuth2Account;
import com.tune_fun.v1.common.stereotype.PersistenceAdapter;
import com.tune_fun.v1.common.util.StringUtil;
import com.tune_fun.v1.interaction.domain.ScrollableArtist;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;


@Component
@PersistenceAdapter
@RequiredArgsConstructor
public class AccountPersistenceAdapter implements
        LoadAccountPort, SaveAccountPort,
        SaveOAuth2AccountPort, DisableOAuth2AccountPort,
        DeleteAccountPort, SaveEmailPort,
        RecordLastLoginAtPort, RecordEmailVerifiedAtPort,
        UpdatePasswordPort, UpdateNicknamePort, DisableAccountPort {

    private final AccountRepository accountRepository;
    private final OAuth2AccountRepository oauth2AccountRepository;
    private final AccountMapper accountMapper;

    @Override
    public Optional<User> loadCustomUserByUsername(final String username) {
        return loadAccountByUsername(username)
                .map(account -> new User(account.getUsername(), account.getPassword(),
                        account.isEnabled(), account.isAccountNonExpired(), account.isCredentialsNonExpired(),
                        account.isAccountNonLocked(), account.getAuthorities()));
    }

    @Override
    public Optional<CurrentAccount> currentAccountInfo(final String username) {
        return loadAccountByUsername(username).map(accountMapper::accountInfo);
    }

    @Override
    public Optional<RegisteredAccount> registeredAccountInfoByUsername(final String username) {
        return loadAccountByUsername(username).map(accountMapper::registeredAccountInfo);
    }

    @Override
    public Optional<RegisteredAccount> registeredAccountInfoByEmail(final String email) {
        return findByEmail(email).map(accountMapper::registeredAccountInfo);
    }

    @Override
    public Optional<RegisteredAccount> registeredAccountInfoByNickname(final String nickname) {
        return findByNickname(nickname).map(accountMapper::registeredAccountInfo);
    }

    @Override
    public Optional<RegisteredOAuth2Account> registeredOAuth2AccountInfoByEmail(final String email) {
        return oauth2AccountRepository.findByEmailAndEnabledTrue(email)
                .map(accountMapper::registeredOAuth2AccountInfo);
    }

    @Override
    public Slice<ScrollableArtist> scrollArtist(final Long lastId, final String nickname) {
        PageRequest pageRequest = PageRequest.of(0, 10);
        return accountRepository.scrollArtist(pageRequest, lastId, nickname);
    }

    @Override
    public void deleteAll() {
        accountRepository.deleteAll();
    }

    @Override
    public void saveEmail(final String email, final String username) {
        loadAccountByUsername(username)
                .ifPresent(account -> {
                    AccountJpaEntity updatedAccount = account.toBuilder().email(email).build();
                    accountRepository.save(updatedAccount);
                });
    }

    @Override
    public void clearEmail(String username) {
        loadAccountByUsername(username)
                .ifPresent(account -> {
                    AccountJpaEntity updatedAccount = account.toBuilder().email(null).build();
                    accountRepository.save(updatedAccount);
                });
    }

    @Override
    public void recordLastLoginAt(final String username) {
        loadAccountByUsername(username)
                .ifPresent(account -> {
                    AccountJpaEntity updatedAccount = account.toBuilder().lastLoginAt(LocalDateTime.now()).build();
                    accountRepository.save(updatedAccount);
                });
    }

    @Override
    public void recordEmailVerifiedAt(final String username) {
        loadAccountByUsername(username)
                .ifPresent(account -> {
                    AccountJpaEntity updatedAccount = account.toBuilder().emailVerifiedAt(LocalDateTime.now()).build();
                    accountRepository.save(updatedAccount);
                });
    }

    @Override
    public void clearEmailVerifiedAt(final String username) {
        loadAccountByUsername(username)
                .ifPresent(account -> {
                    AccountJpaEntity updatedAccount = account.toBuilder().emailVerifiedAt(null).build();
                    accountRepository.save(updatedAccount);
                });
    }

    public Optional<AccountJpaEntity> loadAccountByUsername(final String username) {
        return accountRepository.findActive(username, null, null);
    }

    public Optional<AccountJpaEntity> findByEmail(final String email) {
        return accountRepository.findActive(null, email, null);
    }

    public Optional<AccountJpaEntity> findByNickname(final String nickname) {
        return accountRepository.findActive(null, null, nickname);
    }

    @Override
    public CurrentAccount saveAccount(final SaveAccount behavior) {
        AccountJpaEntity saved = accountRepository.save(accountMapper.fromSaveAccountBehavior(behavior));
        return accountMapper.accountInfo(saved);
    }

    @Override
    public void saveOAuth2Account(final SaveOAuth2Account behavior) {
        loadAccountByUsername(behavior.username())
                .ifPresent(account -> {
                    OAuth2AccountJpaEntity updatedAccount = OAuth2AccountJpaEntity.builder()
                            .uuid(StringUtil.uuid())
                            .email(behavior.email())
                            .nickname(behavior.nickname())
                            .oauth2Provider(behavior.oauth2Provider())
                            .account(account)
                            .build();

                    oauth2AccountRepository.save(updatedAccount);
                });
    }

    @Override
    public void disableOAuth2Account(String email) {
        oauth2AccountRepository.findByEmailAndEnabledTrue(email)
                .ifPresent(account -> {
                    account.disable();
                    oauth2AccountRepository.save(account);
                });
    }

    @Override
    public void updatePassword(final String username, final String encodedPassword) {
        loadAccountByUsername(username)
                .ifPresent(account -> {
                    AccountJpaEntity updatedAccount = account.toBuilder()
                            .password(encodedPassword).build();
                    accountRepository.save(updatedAccount);
                });
    }

    @Override
    public void updateNickname(final String username, final String nickname) {
        loadAccountByUsername(username)
                .ifPresent(account -> {
                    AccountJpaEntity updatedAccount = account.toBuilder()
                            .nickname(nickname).build();
                    accountRepository.save(updatedAccount);
                });
    }

    @Override
    public void disableAccount(MaskedAccount maskedAccount) {
        accountRepository.findById(maskedAccount.id())
                .ifPresent(account -> {
                    AccountJpaEntity updatedAccount = account.toBuilder()
                            .username(maskedAccount.username())
                            .password(maskedAccount.password())
                            .email(maskedAccount.email())
                            .nickname(maskedAccount.nickname())
                            .profileImageUrl(maskedAccount.profileImageUrl())
                            .build();

                    updatedAccount.disable();
                    accountRepository.save(updatedAccount);
                });
    }
}
