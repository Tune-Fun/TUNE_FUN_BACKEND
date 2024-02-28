package com.tune_fun.v1.account.adapter.output.persistence;

import com.amazonaws.xray.spring.aop.XRayEnabled;
import com.tune_fun.v1.account.application.port.output.*;
import com.tune_fun.v1.account.domain.behavior.SaveAccount;
import com.tune_fun.v1.account.domain.state.CurrentAccount;
import com.tune_fun.v1.account.domain.state.RegisteredAccount;
import com.tune_fun.v1.common.exception.CommonApplicationException;
import com.tune_fun.v1.common.hexagon.PersistenceAdapter;
import com.tune_fun.v1.common.response.MessageCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@XRayEnabled
@Component
@PersistenceAdapter
@RequiredArgsConstructor
public class AccountPersistenceAdapter implements
        LoadAccountPort, SaveAccountPort,
        RecordLastLoginAtPort, RecordEmailVerifiedAtPort,
        UpdatePasswordPort, UpdateNicknamePort {

    private final AccountRepository accountRepository;
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
    public void recordLastLoginAt(final String username) {
        loadAccountByUsername(username)
                .ifPresent(account -> {
                    AccountJpaEntity updatedAccount = account.toBuilder()
                            .lastLoginAt(LocalDateTime.now()).build();
                    accountRepository.save(updatedAccount);
                });
    }

    @Override
    public void recordEmailVerifiedAt(final String username) {
        loadAccountByUsername(username)
                .ifPresent(account -> {
                    AccountJpaEntity updatedAccount = account.toBuilder()
                            .emailVerifiedAt(LocalDateTime.now()).build();
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
    public CurrentAccount saveAccount(SaveAccount saveAccount) {
        AccountJpaEntity saved = accountRepository.save(accountMapper.fromSaveAccountValue(saveAccount));
        return accountMapper.accountInfo(saved);
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
}
