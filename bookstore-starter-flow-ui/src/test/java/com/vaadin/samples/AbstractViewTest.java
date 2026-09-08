package com.vaadin.samples;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.SessionScoped;
import jakarta.enterprise.inject.Produces;

import org.jboss.weld.bootstrap.spi.BeanDiscoveryMode;
import org.jboss.weld.junit5.auto.ActivateScopes;
import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.AddExtensions;
import org.jboss.weld.junit5.auto.AddPackages;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.jboss.weld.junit5.auto.SetBeanDiscoveryMode;
import org.junit.jupiter.api.BeforeEach;

import com.vaadin.browserless.BrowserlessTest;
import com.vaadin.browserless.ViewPackages;
import com.vaadin.browserless.internal.MockVaadin;
import com.vaadin.browserless.mocks.MockedUI;
import com.vaadin.cdi.CdiInstantiator;
import com.vaadin.cdi.CdiVaadinServlet;
import com.vaadin.cdi.VaadinExtension;
import com.vaadin.cdi.util.BeanManagerProvider;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.samples.backend.mock.MockDataGenerator;
import com.vaadin.samples.backend.mock.MockDataService;
import com.vaadin.samples.crud.ProductDataProvider;
import com.vaadin.samples.crud.SampleCrudPresenter;
import com.vaadin.samples.crud.SampleCrudViewImpl;
import com.vaadin.samples.authentication.LoginView;
import com.vaadin.samples.about.AboutView;
import com.vaadin.samples.authentication.BasicAccessControl;
import com.vaadin.samples.authentication.CurrentUser;

@EnableAutoWeld
@SetBeanDiscoveryMode(BeanDiscoveryMode.ALL)
@AddPackages(CdiInstantiator.class)
@ActivateScopes(SessionScoped.class)
@AddBeanClasses({ AdminView.class, MockDataService.class,
        MockDataGenerator.class, LoggerProducer.class, SampleCrudViewImpl.class,
        LoginView.class, BasicAccessControl.class, CurrentUser.class,
        MainLayout.class, Menu.class, ProductDataProvider.class,
        CustomI18NProvider.class, CustomSystemMessagesProvider.class,
        SampleCrudPresenter.class, BookstoreBeforeEnterListener.class,
        BookstoreInitListener.class })
@AddExtensions({ BeanManagerProvider.class, VaadinExtension.class })
@ViewPackages(classes = {})
public abstract class AbstractViewTest extends BrowserlessTest {

    @Produces
    @ApplicationScoped
    private final CdiVaadinServlet vaadinServlet = new CdiVaadinServlet();

    // @BeforeEach is re-added on the override so this hook is registered at
    // the concrete test class level — i.e. after weld-junit5 has started the
    // CDI container. See BrowserlessTest's class documentation.
    @BeforeEach
    @Override
    protected void initVaadinEnvironment() {
        scanTesters();
        // Use the CDI servlet/service so the Vaadin Instantiator is the
        // CdiInstantiator backed by the running Weld container.
        MockVaadin.setup(MockedUI::new, vaadinServlet, lookupServices());
        RouteConfiguration.forApplicationScope()
                .setAnnotatedRoute(AboutView.class);
        RouteConfiguration.forApplicationScope()
                .setAnnotatedRoute(LoginView.class);
        RouteConfiguration.forApplicationScope()
                .setAnnotatedRoute(SampleCrudViewImpl.class);
        RouteConfiguration.forApplicationScope().setRoute(AdminView.VIEW_NAME,
                AdminView.class, MainLayout.class);
    }

    protected void login(String username, String password) {
        navigate(LoginView.class);
        var language = find(Select.class).id("language");
        test(language).selectItem("en_GB");
        test(find(LoginForm.class).single()).login(username, password);
    }

}
