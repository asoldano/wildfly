/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2014, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.wildfly.extension.wise;

import java.io.File;

import org.jboss.as.server.ServerEnvironment;
import org.jboss.as.server.ServerEnvironmentService;
import org.jboss.as.server.deployment.AttachmentKey;
import org.jboss.as.server.deployment.DeploymentUnit;
import org.jboss.as.web.host.ServletBuilder;
import org.jboss.as.web.host.WebDeploymentBuilder;
import org.jboss.as.web.host.WebDeploymentController;
import org.jboss.as.web.host.WebHost;
import org.jboss.metadata.web.jboss.JBossWebMetaData;
import org.jboss.msc.service.Service;
import org.jboss.msc.service.ServiceBuilder;
import org.jboss.msc.service.ServiceController;
import org.jboss.msc.service.ServiceName;
import org.jboss.msc.service.ServiceTarget;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StartException;
import org.jboss.msc.service.StopContext;
import org.jboss.msc.value.InjectedValue;
import org.jboss.wise.gwt.server.MainServiceImpl;

/**
 *
 * @author alessio.soldano@jboss.com
 * @since 11-Sep-2016
 */
public final class WiseGuiAppService implements Service<Void> {

    public static final ServiceName WISE_SERVICE = ServiceName.JBOSS.append("wise");
    public static final ServiceName WISE_GUI_APP_SERVICE = WISE_SERVICE.append("gui-app");
    public static final AttachmentKey<ClassLoader> CLASSLOADER_KEY = AttachmentKey.create(ClassLoader.class);
    public static final AttachmentKey<JBossWebMetaData> JBOSSWEB_METADATA_KEY = AttachmentKey.create(JBossWebMetaData.class);

    private final InjectedValue<ServerEnvironment> injectedServerEnvironment = new InjectedValue<ServerEnvironment>();
    private final InjectedValue<WebHost> hostInjector = new InjectedValue<WebHost>();

    private WiseGuiAppService() {
        // NOOP
    }

    public InjectedValue<ServerEnvironment> getServerEnvironmentInjector() {
        return injectedServerEnvironment;
    }

    private ServerEnvironment getServerEnvironment() {
        return injectedServerEnvironment.getValue();
    }

    public InjectedValue<WebHost> getHostInjector() {
        return hostInjector;
    }

    private WebDeploymentController startWebApp(WebHost host, DeploymentUnit unit) throws Exception {
        WebDeploymentBuilder deployment = new WebDeploymentBuilder();
        WebDeploymentController handle;
        try {
            JBossWebMetaData jbwebMD = unit.getAttachment(JBOSSWEB_METADATA_KEY);
            deployment.setContextRoot(jbwebMD.getContextRoot());
            File docBase = new File(getServerEnvironment().getHomeDir(), jbwebMD.getContextRoot());
//            Logger.getLogger(this.getClass()).info("*** docbase: " + docBase);
            if (!docBase.exists()) {
                docBase.mkdirs();
            }
            deployment.setDocumentRoot(docBase);
            deployment.setClassLoader(unit.getAttachment(CLASSLOADER_KEY));

            ServletBuilder servletBuilder = new ServletBuilder();
            servletBuilder.setServletName("mainServiceServlet");
            servletBuilder.setServlet(new MainServiceImpl());
            servletBuilder.setServletClass(MainServiceImpl.class);
            servletBuilder.addUrlMapping("/wise/mainService");
            deployment.addServlet(servletBuilder);
            deployment.addWelcomePage("Main.html");

            handle = host.addWebDeployment(deployment);
            handle.create();
        } catch (Exception e) {
            throw e;
        }
        try {
            handle.start();
        } catch (Exception e) {
            throw e;
        }
        return handle;
    }

    @Override
    public Void getValue() throws IllegalStateException, IllegalArgumentException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void start(StartContext context) throws StartException {
        WiseGuiAppDeploymentUnit unit = new WiseGuiAppDeploymentUnit(this.getClass().getClassLoader(), "/wise");
        try {
            startWebApp(hostInjector.getValue(), unit);
        } catch (Exception e) {
            throw new StartException(e);
        }

    }

    @Override
    public void stop(StopContext context) {
        // TODO Auto-generated method stub

    }

    public static ServiceController<?> install(final ServiceTarget serviceTarget) {
        final WiseGuiAppService service = new WiseGuiAppService();
        final ServiceBuilder<?> builder = serviceTarget.addService(WISE_GUI_APP_SERVICE, service);
        builder.addDependency(ServerEnvironmentService.SERVICE_NAME, ServerEnvironment.class,
                service.getServerEnvironmentInjector());
        builder.addDependency(WebHost.SERVICE_NAME.append("localhost"), WebHost.class, service.getHostInjector());
//        return builder.setInitialMode(ServiceController.Mode.ON_DEMAND).install();
        return builder.setInitialMode(ServiceController.Mode.ACTIVE).install();
    }

}
