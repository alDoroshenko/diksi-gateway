package ru.neoflex.keycloak.storage;

import com.google.auto.service.AutoService;
import org.keycloak.component.ComponentModel;
import org.keycloak.component.ComponentValidationException;
import org.keycloak.connections.jpa.JpaConnectionProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.RealmModel;
import org.keycloak.models.utils.KeycloakModelUtils;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;
import org.keycloak.storage.UserStorageProviderFactory;
import org.keycloak.utils.StringUtil;
import ru.neoflex.keycloak.jpa.repository.UserJPARepository;
import ru.neoflex.keycloak.model.GlobalComponentConfig;
import ru.neoflex.keycloak.jpa.repository.provider.spi.JpaRepositoryProvider;
import ru.neoflex.keycloak.util.Constants;
import ru.neoflex.keycloak.util.Deferred;

import java.util.List;

@AutoService(UserStorageProviderFactory.class)
public class ExteranalUserStorageProviderFactory implements UserStorageProviderFactory<ExternalUserStorageProvider> {
    private static final String PROVIDER_ID = "exteral-user-storage-provider";
    private KeycloakSession session;
    private GlobalComponentConfig globalComponentConfig;


    @Override
    public void postInit(KeycloakSessionFactory factory) {
        this.session = factory.create();
        Deferred.defer(() -> { //   It is required to defer initialization
            try { //                because JpaConnectionProviderFactory might be still uninitialized
                //                  when this.postInit(..) is called
                return session.getProvider(JpaConnectionProvider.class).getEntityManager() != null;
            } catch (NullPointerException exception) {
                return false;
            }
        }, () -> {
            // lateinitGlobalComponentConfig();
            globalComponentConfig.tryUpdateFromDatabase();
        });
    }
    @Override
    public ExternalUserStorageProvider create(KeycloakSession session, ComponentModel model) {
        JpaRepositoryProvider jpaRepositoryProvider = session.getProvider(JpaRepositoryProvider.class, "ofr");
        UserJPARepository userJPARepository = jpaRepositoryProvider
                .getJpaRepository(UserJPARepository.class);
        return new ExternalUserStorageProvider(session, model,userJPARepository);
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public void close() {
        if (session != null) {
            session.close();
        }
    }

  /*  @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return ProviderConfigurationBuilder.create()
                .property(Constants.UserStorage.URL, Constants.UserStorage.URL, "external DB URL", ProviderConfigProperty.STRING_TYPE, "jdbc:postgresql://postgres:5432/postgres", null)
                .property(Constants.UserStorage.USERNAME, Constants.UserStorage.USERNAME, "external DB username", ProviderConfigProperty.STRING_TYPE, "postgres", null)
                .property(Constants.UserStorage.PASSWORD, Constants.UserStorage.PASSWORD, "external DB password", ProviderConfigProperty.PASSWORD, "postgres", null)
                .build();
    }*/

    @Override
    public void onCreate(KeycloakSession session, RealmModel realm, ComponentModel model) {
        // lateinitGlobalComponentConfig();
        globalComponentConfig.update(model);
    }

    @Override
    public void onUpdate(KeycloakSession session, RealmModel realm, ComponentModel oldModel, ComponentModel newModel) {
       // lateinitGlobalComponentConfig();
        globalComponentConfig.update(newModel);
    }


    /*@Override
    public void validateConfiguration(KeycloakSession session, RealmModel realm, ComponentModel config) throws ComponentValidationException {
        if (config.getId() == null) {
            config.setId(KeycloakModelUtils.generateShortId());
        }

        if (StringUtil.isBlank(config.get(Constants.UserStorage.URL))
                || StringUtil.isBlank(config.get(Constants.UserStorage.USERNAME))
                || StringUtil.isBlank(config.get(Constants.UserStorage.PASSWORD))) {
            throw new ComponentValidationException("Required properties weren't filled");
        }
    }*/




/*    private void lateinitGlobalComponentConfig() {
        if (globalComponentConfig == null) {
            globalComponentConfig = new GlobalComponentConfig(
                    session.getProvider(JpaConnectionProvider.class).getEntityManager(),
                    UserStorageProvider.class.getName(),
                    getId(),
                    Set.of(
                            Properties.ClearIncompleteRegistrationsJobEnabled,
                            Properties.ClearIncompleteRegistrationsJobCronExpression,
                            Properties.ClearIncompleteRegistrationsJobRequiredProperties
                    )
            ).setUpdateHandler(this::handleGlobalConfigUpdate);
        }
    }*/
   /* private void handleGlobalConfigUpdate(MultivaluedHashMap<String, String> globalConfig) {
        setupClearIncompleteRegistrationsJob(globalConfig);
    }*/

  /*  private void setupClearIncompleteRegistrationsJob(MultivaluedHashMap<String, String> globalConfig) {
        List<String> requiredProperties = globalConfig.get(Properties.ClearIncompleteRegistrationsJobRequiredProperties);
        setupClearIncompleteRegistrationsJob(
                Boolean.parseBoolean(globalConfig.getFirst(Properties.ClearIncompleteRegistrationsJobEnabled)),
                globalConfig.getFirst(Properties.ClearIncompleteRegistrationsJobCronExpression),
                requiredProperties == null ? Set.of() : Set.copyOf(requiredProperties)
        );
    }*/


    /*@SneakyThrows
    private void setupClearIncompleteRegistrationsJob(
            boolean enabled,
            String cronExpression,
            Set<String> requiredFields
    ) {
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        if (!scheduler.isStarted()) {
            scheduler.start();
        }
        if (enabled) {
            JobDetail jobDetail = scheduler.getJobDetail(ClearIncompleteRegistrationsJob.Key);
            CronTrigger cronTrigger = (CronTrigger) scheduler.getTrigger(ClearIncompleteRegistrationsJob.TriggerKey);
            if (jobDetail == null || cronTrigger == null) {
                jobDetail = JobBuilder.newJob(ClearIncompleteRegistrationsJob.class)
                        .withIdentity(ClearIncompleteRegistrationsJob.Key)
                        .usingJobData(new JobDataMap(Map.of(
                                ClearIncompleteRegistrationsJob.KeycloakSessionData, session,
                                ClearIncompleteRegistrationsJob.RequiredPropertiesData, requiredFields
                        )))
                        .build();
                cronTrigger = TriggerBuilder.newTrigger()
                        .withIdentity(ClearIncompleteRegistrationsJob.TriggerKey)
                        .startNow()
                        .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                        .build();
                scheduler.scheduleJob(jobDetail, cronTrigger);
            } else {
                jobDetail.getJobDataMap().put(ClearIncompleteRegistrationsJob.RequiredPropertiesData, requiredFields);
                scheduler.addJob(jobDetail, true, true);
                if (!StringUtils.equalsIgnoreCase(cronTrigger.getCronExpression(), cronExpression)) {
                    cronTrigger = TriggerBuilder.newTrigger()
                            .withIdentity(ClearIncompleteRegistrationsJob.TriggerKey)
                            .startNow()
                            .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                            .build();
                    scheduler.rescheduleJob(ClearIncompleteRegistrationsJob.TriggerKey, cronTrigger);
                }
            }
        } else {
            scheduler.deleteJob(ClearIncompleteRegistrationsJob.Key);
        }
    }*/

    /*public static class ClearIncompleteRegistrationsJob implements Job {
        public static final JobKey Key = new JobKey(ClearIncompleteRegistrationsJob.class.getName());
        public static final TriggerKey TriggerKey = new TriggerKey(ClearIncompleteRegistrationsJob.class.getName());

        public static final String KeycloakSessionData = "keycloakSession";
        public static final String RequiredPropertiesData = "requiredProperties";

        @Override
        @SuppressWarnings("unchecked")
        public void execute(JobExecutionContext context) {
            JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
            KeycloakSession keycloakSession = (KeycloakSession) jobDataMap.get(KeycloakSessionData);
            Set<String> requiredProperties = (Set<String>) jobDataMap.get(RequiredPropertiesData);

            ClusterProvider clusterProvider = keycloakSession.getProvider(ClusterProvider.class);
            UserJPARepository repository = keycloakSession.getProvider(JpaRepositoryProvider.class, "ofr")
                    .getJpaRepository(UserJPARepository.class);

            clusterProvider.executeIfNotExecuted(this.getClass().getName(), 60, () -> {
                repository.deleteAllByOneOfAllIsNull(requiredProperties);
                return null;
            });
        }
    }*/

    public interface Properties {
        String ClearIncompleteRegistrationsJobEnabled = "ClearIncompleteRegistrationsJobEnabled";
        String ClearIncompleteRegistrationsJobCronExpression = "ClearIncompleteRegistrationsJobCronExpression";
        String ClearIncompleteRegistrationsJobRequiredProperties = "ClearIncompleteRegistrationsJobRequiredProperties";
        String SaltLength = "SaltLength";
        String HashLength = "HashLength";
        String Parallelism = "Parallelism";
        String Memory = "Memory";
        String Iterations = "Iterations";
    }
}
