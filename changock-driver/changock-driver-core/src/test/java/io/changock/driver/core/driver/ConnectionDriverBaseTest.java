package io.changock.driver.core.driver;


import io.changock.driver.api.driver.ChangeSetDependency;
import io.changock.driver.api.driver.ForbiddenParametersMap;
import io.changock.driver.api.entry.ChangeEntryService;
import io.changock.driver.api.lock.LockManager;
import io.changock.driver.core.lock.LockRepository;
import io.changock.migration.api.exception.ChangockException;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.verification.Times;

import java.util.Set;

public class ConnectionDriverBaseTest {


  @Test
  public void shouldInitializeRepositories() {
    // given
    LockRepository lockRepository = Mockito.mock(LockRepository.class);
    ChangeEntryService changeEntryService = Mockito.mock(ChangeEntryService.class);

    ConnectionDriverBase driver = new ConnectionDriverBaseTestImpl(
        lockRepository,
        changeEntryService,
        Mockito.mock(LockManager.class));
    driver.setLockSettings(4, 3, 3);

    // when
    driver.initialize();

    // then
    Mockito.verify(lockRepository, new Times(1)).initialize();
    Mockito.verify(changeEntryService, new Times(1)).initialize();


  }


  static class ConnectionDriverBaseTestImpl extends ConnectionDriverBase {


    private final LockRepository lockRepository;
    private final ChangeEntryService changeEntryService;
    private final LockManager lockManager;

    ConnectionDriverBaseTestImpl(LockRepository lockRepository,
                                 ChangeEntryService changeEntryService,
                                 LockManager lockManager) {
      this.lockRepository = lockRepository;
      this.changeEntryService = changeEntryService;
      this.lockManager = lockManager;
    }

    @Override
    protected LockRepository getLockRepository() {
      return lockRepository;
    }

    @Override
    public ChangeEntryService getChangeEntryService() {
      return changeEntryService;
    }

    @Override
    public LockManager getLockManager() {
      return lockManager;
    }

    @Override
    public Set<ChangeSetDependency> getDependencies() {
      return null;
    }

    @Override
    public ForbiddenParametersMap getForbiddenParameters() {
      return null;
    }

    @Override
    public void runValidation() throws ChangockException {

    }
  }

}