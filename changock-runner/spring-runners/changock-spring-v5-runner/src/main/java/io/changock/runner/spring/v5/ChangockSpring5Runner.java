package io.changock.runner.spring.v5;

import io.changock.driver.api.driver.ConnectionDriver;
import io.changock.migration.api.exception.ChangockException;
import io.changock.runner.core.ChangockBase;
import io.changock.runner.core.builder.DriverBuilderConfigurable;
import io.changock.runner.core.builder.RunnerBuilderBase;
import io.changock.runner.spring.util.SpringDependencyManager;
import io.changock.utils.CollectionUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

abstract class ChangockSpring5Runner extends ChangockBase {


    ChangockSpring5Runner(SpringMigrationExecutor executor,
                          ProfiledChangeLogService changeLogService,
                          boolean throwExceptionIfCannotObtainLock,
                          boolean enabled) {
        super(executor, changeLogService, throwExceptionIfCannotObtainLock, enabled);
    }

    public static DriverBuilderConfigurable<Builder, ConnectionDriver> builder() {
        return new ChangockSpring5Runner.Builder();
    }

    /**
     * SpringChangockRunner builder
     */
    public static class Builder extends RunnerBuilderBase<Builder, ConnectionDriver> {

        private static final String DEFAULT_PROFILE = "default";
        private ApplicationContext springContext;

        private Builder() {
        }


        /**
         * Set ApplicationContext from Spring
         *
         * @param springContext org.springframework.context.ApplicationContext object to inject
         * @return Changock builder for fluent interface
         * @see org.springframework.context.annotation.Profile
         */
        public Builder setSpringContext(ApplicationContext springContext) {
            this.springContext = springContext;
            return returnInstance();
        }


        public ChangockSpringApplicationRunner buildApplicationRunner() {
            return new ChangockSpringApplicationRunner(
                    buildExecutorWIthEnvironmentDependency(),
                    buildProfiledChangeLogService(),
                    throwExceptionIfCannotObtainLock,
                    enabled);
        }

        public ChangockSpringInitializingBeanRunner buildInitializingBeanRunner() {
            return new ChangockSpringInitializingBeanRunner(
                    buildExecutorWIthEnvironmentDependency(),
                    buildProfiledChangeLogService(),
                    throwExceptionIfCannotObtainLock,
                    enabled);
        }

        private SpringMigrationExecutor buildExecutorWIthEnvironmentDependency() {
            return new SpringMigrationExecutor(
                    driver,
                    new SpringDependencyManager(this.springContext),
                    lockAcquiredForMinutes,
                    maxTries,
                    maxWaitingForLockMinutes,
                    metadata
            );
        }

        private ProfiledChangeLogService buildProfiledChangeLogService() {
            if (springContext == null) {
                throw new ChangockException("ApplicationContext from Spring must be injected to Builder");
            }
            Environment springEnvironment = springContext.getEnvironment();
            List<String> activeProfiles = springEnvironment != null && CollectionUtils.isNotNullOrEmpty(springEnvironment.getActiveProfiles())
                    ? Arrays.asList(springEnvironment.getActiveProfiles())
                    : Collections.singletonList(DEFAULT_PROFILE);
            return new ProfiledChangeLogService(
                    Collections.singletonList(changeLogsScanPackage),
                    startSystemVersion,
                    endSystemVersion,
                    activeProfiles,
                    annotationProcessor
            );
        }

        @Override
        protected Builder returnInstance() {
            return this;
        }

        @Override
        public void runValidation() {
            super.runValidation();
            if (springContext == null) {
                throw new ChangockException("ApplicationContext from Spring must be injected to Builder");
            }
        }
    }
}