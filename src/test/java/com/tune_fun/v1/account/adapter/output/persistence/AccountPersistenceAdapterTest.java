package com.tune_fun.v1.account.adapter.output.persistence;

import com.tune_fun.v1.account.adapter.output.persistence.device.DeviceJpaEntity;
import com.tune_fun.v1.account.adapter.output.persistence.oauth2.OAuth2AccountJpaEntity;
import com.tune_fun.v1.account.adapter.output.persistence.oauth2.OAuth2AccountRepository;
import com.tune_fun.v1.account.domain.behavior.SaveAccount;
import com.tune_fun.v1.account.domain.behavior.SaveOAuth2Account;
import com.tune_fun.v1.account.domain.value.CurrentAccount;
import com.tune_fun.v1.account.domain.value.RegisteredAccount;
import com.tune_fun.v1.account.domain.value.oauth2.RegisteredOAuth2Account;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ContextConfiguration(classes = {AccountPersistenceAdapter.class})
@ExtendWith(SpringExtension.class)
@DisabledInAotMode
class AccountPersistenceAdapterTest {
    @MockBean
    private AccountMapper accountMapper;

    @Autowired
    private AccountPersistenceAdapter accountPersistenceAdapter;

    @MockBean
    private AccountRepository accountRepository;

    @MockBean
    private OAuth2AccountRepository oAuth2AccountRepository;

    /**
     * Method under test:
     * {@link AccountPersistenceAdapter#loadCustomUserByUsername(String)}
     */
    @Test
    void testLoadCustomUserByUsername() {
        // Arrange
        NotificationConfig notificationConfig = new NotificationConfig();
        HashSet<Role> roles = new HashSet<>();
        LocalDateTime lastLoginAt = LocalDate.of(1970, 1, 1).atStartOfDay();
        LocalDateTime lastLogoutAt = LocalDate.of(1970, 1, 1).atStartOfDay();
        LocalDateTime emailVerifiedAt = LocalDate.of(1970, 1, 1).atStartOfDay();
        LocalDateTime withdrawalAt = LocalDate.of(1970, 1, 1).atStartOfDay();
        LocalDateTime deletedAt = LocalDate.of(1970, 1, 1).atStartOfDay();
        ArrayList<DeviceJpaEntity> devices = new ArrayList<>();
        Optional<AccountJpaEntity> ofResult = Optional.of(new AccountJpaEntity(1L, "01234567-89AB-CDEF-FEDC-BA9876543210",
                "janedoe", "iloveyou", "jane.doe@example.org", "Nickname", "test@test.com",
                notificationConfig, roles, lastLoginAt, lastLogoutAt, emailVerifiedAt, withdrawalAt, deletedAt, devices,
                new ArrayList<>(), true, true, true, true));
        when(accountRepository.findActive(Mockito.<String>any(), Mockito.<String>any(), Mockito.<String>any()))
                .thenReturn(ofResult);

        // Act
        Optional<User> actualLoadCustomUserByUsernameResult = accountPersistenceAdapter.loadCustomUserByUsername("janedoe");

        // Assert
        verify(accountRepository).findActive(eq("janedoe"), isNull(), isNull());
        User getResult = actualLoadCustomUserByUsernameResult.get();
        assertEquals("iloveyou", getResult.getPassword());
        assertEquals("janedoe", getResult.getUsername());
        assertTrue(getResult.isAccountNonExpired());
        assertTrue(getResult.isAccountNonLocked());
        assertTrue(getResult.isCredentialsNonExpired());
        assertTrue(getResult.isEnabled());
        assertEquals(roles, getResult.getAuthorities());
    }

    /**
     * Method under test:
     * {@link AccountPersistenceAdapter#loadCustomUserByUsername(String)}
     */
    @Test
    void testLoadCustomUserByUsername2() {
        // Arrange
        AccountJpaEntity accountJpaEntity = mock(AccountJpaEntity.class);
        when(accountJpaEntity.isAccountNonExpired()).thenReturn(true);
        when(accountJpaEntity.isAccountNonLocked()).thenReturn(true);
        when(accountJpaEntity.isCredentialsNonExpired()).thenReturn(true);
        when(accountJpaEntity.isEnabled()).thenReturn(true);
        when(accountJpaEntity.getPassword()).thenReturn("iloveyou");
        when(accountJpaEntity.getUsername()).thenReturn("janedoe");
        Mockito.<Collection<? extends GrantedAuthority>>when(accountJpaEntity.getAuthorities())
                .thenReturn(new ArrayList<>());
        Optional<AccountJpaEntity> ofResult = Optional.of(accountJpaEntity);
        when(accountRepository.findActive(Mockito.<String>any(), Mockito.<String>any(), Mockito.<String>any()))
                .thenReturn(ofResult);

        // Act
        Optional<User> actualLoadCustomUserByUsernameResult = accountPersistenceAdapter.loadCustomUserByUsername("janedoe");

        // Assert
        verify(accountRepository).findActive(eq("janedoe"), isNull(), isNull());
        verify(accountJpaEntity).getAuthorities();
        verify(accountJpaEntity).getPassword();
        verify(accountJpaEntity).getUsername();
        verify(accountJpaEntity).isAccountNonExpired();
        verify(accountJpaEntity).isAccountNonLocked();
        verify(accountJpaEntity).isCredentialsNonExpired();
        verify(accountJpaEntity).isEnabled();
        User getResult = actualLoadCustomUserByUsernameResult.get();
        assertEquals("iloveyou", getResult.getPassword());
        assertEquals("janedoe", getResult.getUsername());
        assertTrue(getResult.getAuthorities().isEmpty());
        assertTrue(getResult.isAccountNonExpired());
        assertTrue(getResult.isAccountNonLocked());
        assertTrue(getResult.isCredentialsNonExpired());
        assertTrue(getResult.isEnabled());
    }

    /**
     * Method under test:
     * {@link AccountPersistenceAdapter#loadCustomUserByUsername(String)}
     */
    @Test
    void testLoadCustomUserByUsername3() {
        // Arrange
        ArrayList<GrantedAuthority> grantedAuthorityList = new ArrayList<>();
        grantedAuthorityList.add(new SimpleGrantedAuthority("Role"));
        AccountJpaEntity accountJpaEntity = mock(AccountJpaEntity.class);
        when(accountJpaEntity.isAccountNonExpired()).thenReturn(true);
        when(accountJpaEntity.isAccountNonLocked()).thenReturn(true);
        when(accountJpaEntity.isCredentialsNonExpired()).thenReturn(true);
        when(accountJpaEntity.isEnabled()).thenReturn(true);
        when(accountJpaEntity.getPassword()).thenReturn("iloveyou");
        when(accountJpaEntity.getUsername()).thenReturn("janedoe");
        Mockito.<Collection<? extends GrantedAuthority>>when(accountJpaEntity.getAuthorities())
                .thenReturn(grantedAuthorityList);
        Optional<AccountJpaEntity> ofResult = Optional.of(accountJpaEntity);
        when(accountRepository.findActive(Mockito.<String>any(), Mockito.<String>any(), Mockito.<String>any()))
                .thenReturn(ofResult);

        // Act
        Optional<User> actualLoadCustomUserByUsernameResult = accountPersistenceAdapter.loadCustomUserByUsername("janedoe");

        // Assert
        verify(accountRepository).findActive(eq("janedoe"), isNull(), isNull());
        verify(accountJpaEntity).getAuthorities();
        verify(accountJpaEntity).getPassword();
        verify(accountJpaEntity).getUsername();
        verify(accountJpaEntity).isAccountNonExpired();
        verify(accountJpaEntity).isAccountNonLocked();
        verify(accountJpaEntity).isCredentialsNonExpired();
        verify(accountJpaEntity).isEnabled();
        User getResult = actualLoadCustomUserByUsernameResult.get();
        assertEquals("iloveyou", getResult.getPassword());
        assertEquals("janedoe", getResult.getUsername());
        assertEquals(1, getResult.getAuthorities().size());
        assertTrue(getResult.isAccountNonExpired());
        assertTrue(getResult.isAccountNonLocked());
        assertTrue(getResult.isCredentialsNonExpired());
        assertTrue(getResult.isEnabled());
    }

    /**
     * Method under test:
     * {@link AccountPersistenceAdapter#loadCustomUserByUsername(String)}
     */
    @Test
    void testLoadCustomUserByUsername4() {
        // Arrange
        Optional<AccountJpaEntity> emptyResult = Optional.empty();
        when(accountRepository.findActive(Mockito.<String>any(), Mockito.<String>any(), Mockito.<String>any()))
                .thenReturn(emptyResult);

        // Act
        Optional<User> actualLoadCustomUserByUsernameResult = accountPersistenceAdapter.loadCustomUserByUsername("janedoe");

        // Assert
        verify(accountRepository).findActive(eq("janedoe"), isNull(), isNull());
        assertFalse(actualLoadCustomUserByUsernameResult.isPresent());
        assertSame(emptyResult, actualLoadCustomUserByUsernameResult);
    }

    /**
     * Method under test:
     * {@link AccountPersistenceAdapter#currentAccountInfo(String)}
     */
    @Test
    void testCurrentAccountInfo() {
        // Arrange
        Optional<AccountJpaEntity> ofResult = Optional.of(new AccountJpaEntity());
        when(accountRepository.findActive(Mockito.<String>any(), Mockito.<String>any(), Mockito.<String>any()))
                .thenReturn(ofResult);
        LocalDateTime createdAt = LocalDate.of(1970, 1, 1).atStartOfDay();
        LocalDateTime emailVerifiedAt = LocalDate.of(1970, 1, 1).atStartOfDay();
        when(accountMapper.accountInfo(Mockito.<AccountJpaEntity>any()))
                .thenReturn(new CurrentAccount(1L, createdAt, emailVerifiedAt, "01234567-89AB-CDEF-FEDC-BA9876543210", "janedoe",
                        "Nickname", "jane.doe@example.org", new HashSet<>()));

        // Act
        accountPersistenceAdapter.currentAccountInfo("janedoe");

        // Assert
        verify(accountRepository).findActive(eq("janedoe"), isNull(), isNull());
        verify(accountMapper).accountInfo(isA(AccountJpaEntity.class));
    }

    /**
     * Method under test:
     * {@link AccountPersistenceAdapter#registeredAccountInfoByUsername(String)}
     */
    @Test
    void testRegisteredAccountInfoByUsername() {
        // Arrange
        Optional<AccountJpaEntity> ofResult = Optional.of(new AccountJpaEntity());
        when(accountRepository.findActive(Mockito.<String>any(), Mockito.<String>any(), Mockito.<String>any()))
                .thenReturn(ofResult);
        HashSet<String> roles = new HashSet<>();
        when(accountMapper.registeredAccountInfo(Mockito.<AccountJpaEntity>any()))
                .thenReturn(new RegisteredAccount(1L, "janedoe", "iloveyou", "a@a.com", "janedoe", "profileImageUrl", roles, new ArrayList<>()));

        // Act
        accountPersistenceAdapter.registeredAccountInfoByUsername("janedoe");

        // Assert
        verify(accountRepository).findActive(eq("janedoe"), isNull(), isNull());
        verify(accountMapper).registeredAccountInfo(isA(AccountJpaEntity.class));
    }

    /**
     * Method under test:
     * {@link AccountPersistenceAdapter#registeredAccountInfoByEmail(String)}
     */
    @Test
    void testRegisteredAccountInfoByEmail() {
        // Arrange
        Optional<AccountJpaEntity> ofResult = Optional.of(new AccountJpaEntity());
        when(accountRepository.findActive(Mockito.<String>any(), Mockito.<String>any(), Mockito.<String>any()))
                .thenReturn(ofResult);
        HashSet<String> roles = new HashSet<>();
        when(accountMapper.registeredAccountInfo(Mockito.<AccountJpaEntity>any()))
                .thenReturn(new RegisteredAccount(1L, "janedoe", "iloveyou", "a@a.com", "janedoe", "profileImageUrl", roles, new ArrayList<>()));

        // Act
        accountPersistenceAdapter.registeredAccountInfoByEmail("jane.doe@example.org");

        // Assert
        verify(accountRepository).findActive(isNull(), eq("jane.doe@example.org"), isNull());
        verify(accountMapper).registeredAccountInfo(isA(AccountJpaEntity.class));
    }

    /**
     * Method under test:
     * {@link AccountPersistenceAdapter#registeredAccountInfoByNickname(String)}
     */
    @Test
    void testRegisteredAccountInfoByNickname() {
        // Arrange
        Optional<AccountJpaEntity> ofResult = Optional.of(new AccountJpaEntity());
        when(accountRepository.findActive(Mockito.<String>any(), Mockito.<String>any(), Mockito.<String>any()))
                .thenReturn(ofResult);
        HashSet<String> roles = new HashSet<>();
        when(accountMapper.registeredAccountInfo(Mockito.<AccountJpaEntity>any()))
                .thenReturn(new RegisteredAccount(1L, "janedoe", "iloveyou", "a@a.com", "janedoe", "profileImageUrl", roles, new ArrayList<>()));

        // Act
        accountPersistenceAdapter.registeredAccountInfoByNickname("Nickname");

        // Assert
        verify(accountRepository).findActive(isNull(), isNull(), eq("Nickname"));
        verify(accountMapper).registeredAccountInfo(isA(AccountJpaEntity.class));
    }

    /**
     * Method under test:
     * {@link AccountPersistenceAdapter#registeredOAuth2AccountInfoByEmail(String)}
     */
    @Test
    void testRegisteredOAuth2AccountInfoByEmail() {
        // Arrange
        Optional<OAuth2AccountJpaEntity> ofResult = Optional.of(new OAuth2AccountJpaEntity());
        when(oAuth2AccountRepository.findByEmailAndEnabledTrue(Mockito.<String>any())).thenReturn(ofResult);
        when(accountMapper.registeredOAuth2AccountInfo(Mockito.<OAuth2AccountJpaEntity>any()))
                .thenReturn(new RegisteredOAuth2Account("jane.doe@example.org", "Nickname", "Oauth2 Provider", true));

        // Act
        accountPersistenceAdapter.registeredOAuth2AccountInfoByEmail("jane.doe@example.org");

        // Assert
        verify(accountMapper).registeredOAuth2AccountInfo(isA(OAuth2AccountJpaEntity.class));
        verify(oAuth2AccountRepository).findByEmailAndEnabledTrue(eq("jane.doe@example.org"));
    }

    /**
     * Method under test: {@link AccountPersistenceAdapter#deleteAll()}
     */
    @Test
    void testDeleteAll() {
        // Arrange
        doNothing().when(accountRepository).deleteAll();

        // Act
        accountPersistenceAdapter.deleteAll();

        // Assert that nothing has changed
        verify(accountRepository).deleteAll();
    }

    /**
     * Method under test:
     * {@link AccountPersistenceAdapter#recordLastLoginAt(String)}
     */
    @Test
    void testRecordLastLoginAt() {
        // Arrange
        when(accountRepository.save(Mockito.<AccountJpaEntity>any())).thenReturn(new AccountJpaEntity());
        Optional<AccountJpaEntity> ofResult = Optional.of(new AccountJpaEntity());
        when(accountRepository.findActive(Mockito.<String>any(), Mockito.<String>any(), Mockito.<String>any()))
                .thenReturn(ofResult);

        // Act
        accountPersistenceAdapter.recordLastLoginAt("janedoe");

        // Assert
        verify(accountRepository).findActive(eq("janedoe"), isNull(), isNull());
        verify(accountRepository).save(isA(AccountJpaEntity.class));
    }

    /**
     * Method under test:
     * {@link AccountPersistenceAdapter#recordLastLoginAt(String)}
     */
    @Test
    void testRecordLastLoginAt2() {
        // Arrange
        when(accountRepository.save(Mockito.<AccountJpaEntity>any())).thenReturn(new AccountJpaEntity());
        NotificationConfig notificationConfig = new NotificationConfig();
        HashSet<Role> roles = new HashSet<>();
        LocalDateTime lastLoginAt = LocalDate.of(1970, 1, 1).atStartOfDay();
        LocalDateTime lastLogoutAt = LocalDate.of(1970, 1, 1).atStartOfDay();
        LocalDateTime emailVerifiedAt = LocalDate.of(1970, 1, 1).atStartOfDay();
        LocalDateTime withdrawalAt = LocalDate.of(1970, 1, 1).atStartOfDay();
        LocalDateTime deletedAt = LocalDate.of(1970, 1, 1).atStartOfDay();
        ArrayList<DeviceJpaEntity> devices = new ArrayList<>();
        Optional<AccountJpaEntity> ofResult = Optional.of(new AccountJpaEntity(1L, "01234567-89AB-CDEF-FEDC-BA9876543210",
                "janedoe", "iloveyou", "jane.doe@example.org", "Nickname", "test@test.com",
                notificationConfig, roles, lastLoginAt, lastLogoutAt, emailVerifiedAt, withdrawalAt, deletedAt, devices,
                new ArrayList<>(), true, true, true, true));
        when(accountRepository.findActive(Mockito.<String>any(), Mockito.<String>any(), Mockito.<String>any()))
                .thenReturn(ofResult);

        // Act
        accountPersistenceAdapter.recordLastLoginAt("janedoe");

        // Assert
        verify(accountRepository).findActive(eq("janedoe"), isNull(), isNull());
        verify(accountRepository).save(isA(AccountJpaEntity.class));
    }

    /**
     * Method under test:
     * {@link AccountPersistenceAdapter#recordLastLoginAt(String)}
     */
    @Test
    void testRecordLastLoginAt3() {
        // Arrange
        Optional<AccountJpaEntity> emptyResult = Optional.empty();
        when(accountRepository.findActive(Mockito.<String>any(), Mockito.<String>any(), Mockito.<String>any()))
                .thenReturn(emptyResult);

        // Act
        accountPersistenceAdapter.recordLastLoginAt("janedoe");

        // Assert that nothing has changed
        verify(accountRepository).findActive(eq("janedoe"), isNull(), isNull());
    }

    /**
     * Method under test:
     * {@link AccountPersistenceAdapter#recordEmailVerifiedAt(String)}
     */
    @Test
    void testRecordEmailVerifiedAt() {
        // Arrange
        when(accountRepository.save(Mockito.<AccountJpaEntity>any())).thenReturn(new AccountJpaEntity());
        Optional<AccountJpaEntity> ofResult = Optional.of(new AccountJpaEntity());
        when(accountRepository.findActive(Mockito.<String>any(), Mockito.<String>any(), Mockito.<String>any()))
                .thenReturn(ofResult);

        // Act
        accountPersistenceAdapter.recordEmailVerifiedAt("janedoe");

        // Assert
        verify(accountRepository).findActive(eq("janedoe"), isNull(), isNull());
        verify(accountRepository).save(isA(AccountJpaEntity.class));
    }

    /**
     * Method under test:
     * {@link AccountPersistenceAdapter#recordEmailVerifiedAt(String)}
     */
    @Test
    void testRecordEmailVerifiedAt2() {
        // Arrange
        when(accountRepository.save(Mockito.<AccountJpaEntity>any())).thenReturn(new AccountJpaEntity());
        NotificationConfig notificationConfig = new NotificationConfig();
        HashSet<Role> roles = new HashSet<>();
        LocalDateTime lastLoginAt = LocalDate.of(1970, 1, 1).atStartOfDay();
        LocalDateTime lastLogoutAt = LocalDate.of(1970, 1, 1).atStartOfDay();
        LocalDateTime emailVerifiedAt = LocalDate.of(1970, 1, 1).atStartOfDay();
        LocalDateTime withdrawalAt = LocalDate.of(1970, 1, 1).atStartOfDay();
        LocalDateTime deletedAt = LocalDate.of(1970, 1, 1).atStartOfDay();
        ArrayList<DeviceJpaEntity> devices = new ArrayList<>();
        Optional<AccountJpaEntity> ofResult = Optional.of(new AccountJpaEntity(1L, "01234567-89AB-CDEF-FEDC-BA9876543210",
                "janedoe", "iloveyou", "jane.doe@example.org", "Nickname", "test@test.com",
                notificationConfig, roles, lastLoginAt, lastLogoutAt, emailVerifiedAt, withdrawalAt, deletedAt, devices,
                new ArrayList<>(), true, true, true, true));
        when(accountRepository.findActive(Mockito.<String>any(), Mockito.<String>any(), Mockito.<String>any()))
                .thenReturn(ofResult);

        // Act
        accountPersistenceAdapter.recordEmailVerifiedAt("janedoe");

        // Assert
        verify(accountRepository).findActive(eq("janedoe"), isNull(), isNull());
        verify(accountRepository).save(isA(AccountJpaEntity.class));
    }

    /**
     * Method under test:
     * {@link AccountPersistenceAdapter#recordEmailVerifiedAt(String)}
     */
    @Test
    void testRecordEmailVerifiedAt3() {
        // Arrange
        Optional<AccountJpaEntity> emptyResult = Optional.empty();
        when(accountRepository.findActive(Mockito.<String>any(), Mockito.<String>any(), Mockito.<String>any()))
                .thenReturn(emptyResult);

        // Act
        accountPersistenceAdapter.recordEmailVerifiedAt("janedoe");

        // Assert that nothing has changed
        verify(accountRepository).findActive(eq("janedoe"), isNull(), isNull());
    }

    /**
     * Method under test:
     * {@link AccountPersistenceAdapter#loadAccountByUsername(String)}
     */
    @Test
    void testLoadAccountByUsername() {
        // Arrange
        Optional<AccountJpaEntity> ofResult = Optional.of(new AccountJpaEntity());
        when(accountRepository.findActive(Mockito.<String>any(), Mockito.<String>any(), Mockito.<String>any()))
                .thenReturn(ofResult);

        // Act
        Optional<AccountJpaEntity> actualLoadAccountByUsernameResult = accountPersistenceAdapter
                .loadAccountByUsername("janedoe");

        // Assert
        verify(accountRepository).findActive(eq("janedoe"), isNull(), isNull());
        assertSame(ofResult, actualLoadAccountByUsernameResult);
    }

    /**
     * Method under test: {@link AccountPersistenceAdapter#findByEmail(String)}
     */
    @Test
    void testFindByEmail() {
        // Arrange
        Optional<AccountJpaEntity> ofResult = Optional.of(new AccountJpaEntity());
        when(accountRepository.findActive(Mockito.<String>any(), Mockito.<String>any(), Mockito.<String>any()))
                .thenReturn(ofResult);

        // Act
        Optional<AccountJpaEntity> actualFindByEmailResult = accountPersistenceAdapter.findByEmail("jane.doe@example.org");

        // Assert
        verify(accountRepository).findActive(isNull(), eq("jane.doe@example.org"), isNull());
        assertSame(ofResult, actualFindByEmailResult);
    }

    /**
     * Method under test: {@link AccountPersistenceAdapter#findByNickname(String)}
     */
    @Test
    void testFindByNickname() {
        // Arrange
        Optional<AccountJpaEntity> ofResult = Optional.of(new AccountJpaEntity());
        when(accountRepository.findActive(Mockito.<String>any(), Mockito.<String>any(), Mockito.<String>any()))
                .thenReturn(ofResult);

        // Act
        Optional<AccountJpaEntity> actualFindByNicknameResult = accountPersistenceAdapter.findByNickname("Nickname");

        // Assert
        verify(accountRepository).findActive(isNull(), isNull(), eq("Nickname"));
        assertSame(ofResult, actualFindByNicknameResult);
    }

    /**
     * Method under test: {@link AccountPersistenceAdapter#saveAccount(SaveAccount)}
     */
    @Test
    void testSaveAccount() {
        // Arrange
        when(accountRepository.save(Mockito.<AccountJpaEntity>any())).thenReturn(new AccountJpaEntity());
        when(accountMapper.fromSaveAccountBehavior(Mockito.<SaveAccount>any())).thenReturn(new AccountJpaEntity());
        LocalDateTime createdAt = LocalDate.of(1970, 1, 1).atStartOfDay();
        LocalDateTime emailVerifiedAt = LocalDate.of(1970, 1, 1).atStartOfDay();
        CurrentAccount currentAccount = new CurrentAccount(1L, createdAt, emailVerifiedAt,
                "01234567-89AB-CDEF-FEDC-BA9876543210", "janedoe", "Nickname", "jane.doe@example.org", new HashSet<>());

        when(accountMapper.accountInfo(Mockito.<AccountJpaEntity>any())).thenReturn(currentAccount);

        // Act
        CurrentAccount actualSaveAccountResult = accountPersistenceAdapter
                .saveAccount(new SaveAccount("NORMAL", "01234567-89AB-CDEF-FEDC-BA9876543210", "janedoe", "iloveyou",
                        "jane.doe@example.org", "Nickname", true, true, true));

        // Assert
        verify(accountMapper).accountInfo(isA(AccountJpaEntity.class));
        verify(accountMapper).fromSaveAccountBehavior(isA(SaveAccount.class));
        verify(accountRepository).save(isA(AccountJpaEntity.class));
        assertSame(currentAccount, actualSaveAccountResult);
    }

    /**
     * Method under test:
     * {@link AccountPersistenceAdapter#saveOAuth2Account(SaveOAuth2Account)}
     */
    @Test
    void testSaveOAuth2Account() {
        // Arrange
        Optional<AccountJpaEntity> ofResult = Optional.of(new AccountJpaEntity());
        when(accountRepository.findActive(Mockito.<String>any(), Mockito.<String>any(), Mockito.<String>any()))
                .thenReturn(ofResult);
        when(oAuth2AccountRepository.save(Mockito.<OAuth2AccountJpaEntity>any())).thenReturn(new OAuth2AccountJpaEntity());

        // Act
        accountPersistenceAdapter
                .saveOAuth2Account(new SaveOAuth2Account("jane.doe@example.org", "Nickname", "Oauth2 Provider", "janedoe"));

        // Assert
        verify(accountRepository).findActive(eq("janedoe"), isNull(), isNull());
        verify(oAuth2AccountRepository).save(isA(OAuth2AccountJpaEntity.class));
    }

    /**
     * Method under test:
     * {@link AccountPersistenceAdapter#saveOAuth2Account(SaveOAuth2Account)}
     */
    @Test
    void testSaveOAuth2Account2() {
        // Arrange
        Optional<AccountJpaEntity> emptyResult = Optional.empty();
        when(accountRepository.findActive(Mockito.<String>any(), Mockito.<String>any(), Mockito.<String>any()))
                .thenReturn(emptyResult);

        // Act
        accountPersistenceAdapter
                .saveOAuth2Account(new SaveOAuth2Account("jane.doe@example.org", "Nickname", "Oauth2 Provider", "janedoe"));

        // Assert that nothing has changed
        verify(accountRepository).findActive(eq("janedoe"), isNull(), isNull());
    }

    /**
     * Method under test:
     * {@link AccountPersistenceAdapter#disableOAuth2Account(String)}
     */
    @Test
    void testDisableOAuth2Account() {
        // Arrange
        when(oAuth2AccountRepository.save(Mockito.<OAuth2AccountJpaEntity>any())).thenReturn(new OAuth2AccountJpaEntity());
        Optional<OAuth2AccountJpaEntity> ofResult = Optional.of(new OAuth2AccountJpaEntity());
        when(oAuth2AccountRepository.findByEmailAndEnabledTrue(Mockito.<String>any())).thenReturn(ofResult);

        // Act
        accountPersistenceAdapter.disableOAuth2Account("jane.doe@example.org");

        // Assert
        verify(oAuth2AccountRepository).findByEmailAndEnabledTrue(eq("jane.doe@example.org"));
        verify(oAuth2AccountRepository).save(isA(OAuth2AccountJpaEntity.class));
    }

    /**
     * Method under test:
     * {@link AccountPersistenceAdapter#disableOAuth2Account(String)}
     */
    @Test
    void testDisableOAuth2Account2() {
        // Arrange
        OAuth2AccountJpaEntity oAuth2AccountJpaEntity = mock(OAuth2AccountJpaEntity.class);
        doNothing().when(oAuth2AccountJpaEntity).disable();
        Optional<OAuth2AccountJpaEntity> ofResult = Optional.of(oAuth2AccountJpaEntity);
        when(oAuth2AccountRepository.save(Mockito.<OAuth2AccountJpaEntity>any())).thenReturn(new OAuth2AccountJpaEntity());
        when(oAuth2AccountRepository.findByEmailAndEnabledTrue(Mockito.<String>any())).thenReturn(ofResult);

        // Act
        accountPersistenceAdapter.disableOAuth2Account("jane.doe@example.org");

        // Assert that nothing has changed
        verify(oAuth2AccountJpaEntity).disable();
        verify(oAuth2AccountRepository).findByEmailAndEnabledTrue(eq("jane.doe@example.org"));
        verify(oAuth2AccountRepository).save(isA(OAuth2AccountJpaEntity.class));
    }

    /**
     * Method under test:
     * {@link AccountPersistenceAdapter#disableOAuth2Account(String)}
     */
    @Test
    void testDisableOAuth2Account3() {
        // Arrange
        Optional<OAuth2AccountJpaEntity> emptyResult = Optional.empty();
        when(oAuth2AccountRepository.findByEmailAndEnabledTrue(Mockito.<String>any())).thenReturn(emptyResult);

        // Act
        accountPersistenceAdapter.disableOAuth2Account("jane.doe@example.org");

        // Assert that nothing has changed
        verify(oAuth2AccountRepository).findByEmailAndEnabledTrue(eq("jane.doe@example.org"));
    }

    /**
     * Method under test:
     * {@link AccountPersistenceAdapter#updatePassword(String, String)}
     */
    @Test
    void testUpdatePassword() {
        // Arrange
        when(accountRepository.save(Mockito.<AccountJpaEntity>any())).thenReturn(new AccountJpaEntity());
        Optional<AccountJpaEntity> ofResult = Optional.of(new AccountJpaEntity());
        when(accountRepository.findActive(Mockito.<String>any(), Mockito.<String>any(), Mockito.<String>any()))
                .thenReturn(ofResult);

        // Act
        accountPersistenceAdapter.updatePassword("janedoe", "secret");

        // Assert
        verify(accountRepository).findActive(eq("janedoe"), isNull(), isNull());
        verify(accountRepository).save(isA(AccountJpaEntity.class));
    }

    /**
     * Method under test:
     * {@link AccountPersistenceAdapter#updatePassword(String, String)}
     */
    @Test
    void testUpdatePassword2() {
        // Arrange
        when(accountRepository.save(Mockito.<AccountJpaEntity>any())).thenReturn(new AccountJpaEntity());
        NotificationConfig notificationConfig = new NotificationConfig();
        HashSet<Role> roles = new HashSet<>();
        LocalDateTime lastLoginAt = LocalDate.of(1970, 1, 1).atStartOfDay();
        LocalDateTime lastLogoutAt = LocalDate.of(1970, 1, 1).atStartOfDay();
        LocalDateTime emailVerifiedAt = LocalDate.of(1970, 1, 1).atStartOfDay();
        LocalDateTime withdrawalAt = LocalDate.of(1970, 1, 1).atStartOfDay();
        LocalDateTime deletedAt = LocalDate.of(1970, 1, 1).atStartOfDay();
        ArrayList<DeviceJpaEntity> devices = new ArrayList<>();
        Optional<AccountJpaEntity> ofResult = Optional.of(new AccountJpaEntity(1L, "01234567-89AB-CDEF-FEDC-BA9876543210",
                "janedoe", "iloveyou", "jane.doe@example.org", "Nickname", "test@test.com",
                notificationConfig, roles, lastLoginAt, lastLogoutAt, emailVerifiedAt, withdrawalAt, deletedAt, devices,
                new ArrayList<>(), true, true, true, true));
        when(accountRepository.findActive(Mockito.<String>any(), Mockito.<String>any(), Mockito.<String>any()))
                .thenReturn(ofResult);

        // Act
        accountPersistenceAdapter.updatePassword("janedoe", "secret");

        // Assert
        verify(accountRepository).findActive(eq("janedoe"), isNull(), isNull());
        verify(accountRepository).save(isA(AccountJpaEntity.class));
    }

    /**
     * Method under test:
     * {@link AccountPersistenceAdapter#updatePassword(String, String)}
     */
    @Test
    void testUpdatePassword3() {
        // Arrange
        Optional<AccountJpaEntity> emptyResult = Optional.empty();
        when(accountRepository.findActive(Mockito.<String>any(), Mockito.<String>any(), Mockito.<String>any()))
                .thenReturn(emptyResult);

        // Act
        accountPersistenceAdapter.updatePassword("janedoe", "secret");

        // Assert that nothing has changed
        verify(accountRepository).findActive(eq("janedoe"), isNull(), isNull());
    }

    /**
     * Method under test:
     * {@link AccountPersistenceAdapter#updateNickname(String, String)}
     */
    @Test
    void testUpdateNickname() {
        // Arrange
        when(accountRepository.save(Mockito.<AccountJpaEntity>any())).thenReturn(new AccountJpaEntity());
        Optional<AccountJpaEntity> ofResult = Optional.of(new AccountJpaEntity());
        when(accountRepository.findActive(Mockito.<String>any(), Mockito.<String>any(), Mockito.<String>any()))
                .thenReturn(ofResult);

        // Act
        accountPersistenceAdapter.updateNickname("janedoe", "Nickname");

        // Assert
        verify(accountRepository).findActive(eq("janedoe"), isNull(), isNull());
        verify(accountRepository).save(isA(AccountJpaEntity.class));
    }

    /**
     * Method under test:
     * {@link AccountPersistenceAdapter#updateNickname(String, String)}
     */
    @Test
    void testUpdateNickname2() {
        // Arrange
        when(accountRepository.save(Mockito.<AccountJpaEntity>any())).thenReturn(new AccountJpaEntity());
        NotificationConfig notificationConfig = new NotificationConfig();
        HashSet<Role> roles = new HashSet<>();
        LocalDateTime lastLoginAt = LocalDate.of(1970, 1, 1).atStartOfDay();
        LocalDateTime lastLogoutAt = LocalDate.of(1970, 1, 1).atStartOfDay();
        LocalDateTime emailVerifiedAt = LocalDate.of(1970, 1, 1).atStartOfDay();
        LocalDateTime withdrawalAt = LocalDate.of(1970, 1, 1).atStartOfDay();
        LocalDateTime deletedAt = LocalDate.of(1970, 1, 1).atStartOfDay();
        ArrayList<DeviceJpaEntity> devices = new ArrayList<>();
        Optional<AccountJpaEntity> ofResult = Optional.of(new AccountJpaEntity(1L, "01234567-89AB-CDEF-FEDC-BA9876543210",
                "janedoe", "iloveyou", "jane.doe@example.org", "Nickname", "test@test.com",
                notificationConfig, roles, lastLoginAt, lastLogoutAt, emailVerifiedAt, withdrawalAt, deletedAt, devices,
                new ArrayList<>(), true, true, true, true));
        when(accountRepository.findActive(Mockito.<String>any(), Mockito.<String>any(), Mockito.<String>any()))
                .thenReturn(ofResult);

        // Act
        accountPersistenceAdapter.updateNickname("janedoe", "Nickname");

        // Assert
        verify(accountRepository).findActive(eq("janedoe"), isNull(), isNull());
        verify(accountRepository).save(isA(AccountJpaEntity.class));
    }

    /**
     * Method under test:
     * {@link AccountPersistenceAdapter#updateNickname(String, String)}
     */
    @Test
    void testUpdateNickname3() {
        // Arrange
        Optional<AccountJpaEntity> emptyResult = Optional.empty();
        when(accountRepository.findActive(Mockito.<String>any(), Mockito.<String>any(), Mockito.<String>any()))
                .thenReturn(emptyResult);

        // Act
        accountPersistenceAdapter.updateNickname("janedoe", "Nickname");

        // Assert that nothing has changed
        verify(accountRepository).findActive(eq("janedoe"), isNull(), isNull());
    }
}
